package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tree.*;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;

import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.ima.pseudocode.ImmediateFloat;

public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier className;
    private AbstractIdentifier superClass;
    private ListDeclField fields;
    private ListDeclMethod methods;
    private ClassDefinition classDefinition; 

    public DeclClass(AbstractIdentifier className, AbstractIdentifier superClass,
                    ListDeclField fields, ListDeclMethod methods) {
        this.className = className;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
        this.classDefinition = className.getClassDefinition();
    }

    public ClassDefinition getClassDefinition(){
        return classDefinition;
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        EnvironmentType env = compiler.getEnvironmentType(); 
        if(env.defOfType(className.getName()) != null){
            throw new ContextualError("La classe " + className.getName()+ " est déja définie ", className.getLocation());
        }

        TypeDefinition superDef = env.defOfType(superClass.getName());
        if (superDef == null) {
            throw new ContextualError("La super-classe " + superClass.getName() + " n'est pas définie", superClass.getLocation());
        }

        if (!superDef.isClass()) {
            throw new ContextualError("L'identificateur " + superClass.getName() 
                + " n'est pas une classe", superClass.getLocation());
        }
        ClassDefinition superIdDef = (ClassDefinition) superDef;

        ClassType classType = new ClassType(className.getName(), className.getLocation(), superIdDef);
        ClassDefinition classDef = new ClassDefinition(classType,className.getLocation(),superIdDef);

        
        classType.setDefinition(classDef);

        env.declareClass(className.getName(), classDef);
        //
        this.classDefinition = classDef; 
        className.setDefinition(classDef);
        className.setType(classType);
        //
        superClass.setDefinition(superIdDef);
        superClass.setType(superIdDef.getType());
    }

    public ListDeclMethod getMethods(){
        return methods;
    }
    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        ClassDefinition currentClass = className.getClassDefinition();
        
        // On vérifie les champs (Passe 2)
        fields.verifyListField(compiler, currentClass);
        // On vérifie les méthodes (Passe 2 : Signatures)
        methods.verifyListMethod(compiler, currentClass);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
    ClassDefinition currentClass = this.className.getClassDefinition();
    
    // Passe 3 pour les champs (initialisations)
    this.fields.verifyListFieldBody(compiler, currentClass);
    
    // Passe 3 pour les méthodes (corps)
    this.methods.verifyListMethodBody(compiler, currentClass);
}

    public AbstractIdentifier getClassName() {
        return this.className;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //{ r := ’class ’.name.’ extends ’.super.’ {’.fields.methods.’}’}
        s.print("class ");
        className.decompile(s);
        if(!superClass.getName().toString().equals("Object")){
            s.print(" extends ");
            superClass.decompile(s);
            
        }
        s.print(" {");
        s.print("\n\t");
        s.indent();
        
        fields.decompile(s);
       
       
        methods.decompile(s);
        s.unindent();
        s.print("}");



    }

    // @Override
    protected void iterChildren(TreeFunction f) {
        // className.iter(f);
        // superClass.iter(f);
        // if (fields != null) {
        //     fields.iter(f);
        // }
        // if (methods != null) {
        //     methods.iter(f);
        // }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s,prefix,false);
        superClass.prettyPrint(s,prefix,false);
        fields.prettyPrint(s,prefix,false);
        methods.prettyPrint(s,prefix,true);
    }

    protected void codeGenDeclClass(DecacCompiler compiler) {
        //
        compiler.addComment("Initialisation des champs de " + className.getName().getName());        
        compiler.addComment("Code des classes");
        String labelName = "init." + getClassName().getName();
        Label lab = new Label(labelName);
        compiler.addLabel(lab);

        

        compiler.getRegisterHandler().reset();
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));

        for (AbstractDeclField field : fields.getList()) {
            DeclField f = (DeclField) field;
            int index = f.getFieldName().getFieldDefinition().getIndex();
            Type t = f.getFieldName().getDefinition().getType();
            if (t.isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), Register.R0));
            } 
            else if (t.isInt() || t.isBoolean()) {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
            } 
            else {
                compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
            }
            
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index, Register.R1)));
        }        

        // //Code de la sauvegarde des registres
        // compiler.addInstruction(new PUSH(Register.getR(2)));
        // compiler.addInstruction(new PUSH(Register.getR(3)));
        //compPileCourante += 2;
        // Toute classe doit appeler l'initialisation de sa classe mère pour que les champs hérités soient initialisés
        if (!superClass.getName().toString().equals("Object")) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            compiler.addInstruction(new BSR(new Label("init." + superClass.getName())));
            compiler.addInstruction(new SUBSP(new ImmediateInteger(0))); 
        }
        // Code de l'initialisation des champs
        fields.codeGenListField(compiler);

        //Code de la restauration des registres
        int maxReg = compiler.getRegisterHandler().getMaxRegisterUsed();
        if (maxReg < 2){
            maxReg = 2; // R0 et R1 exclus
        }

        for (int i = maxReg ; i >= 2 ;i--){
            compiler.addInstruction(new POP(Register.getR(i)));
        }
        //RTS
        compiler.addInstruction(new RTS());


        int nbSavReg = 0;
        for (int i = maxReg ; i >= 2 ;i--){
            compiler.addFirstInBlock(lab, new PUSH(Register.getR(i)), null);
            nbSavReg++;
        }

        int maxPile = compiler.getRegisterHandler().getMaxStackDepth();
        int maxAppel = (!superClass.getName().toString().equals("Object")) ? 2:0;

        int d = nbSavReg + Math.max(maxAppel,maxPile); 
        compiler.addFirstInBlock(lab, new BOV(new Label("pile_pleine")), null);
        compiler.addFirstInBlock(lab,new TSTO(new ImmediateInteger(d)),"");
    }

    protected void codeGenVTable(DecacCompiler compiler) {
        ClassDefinition def = this.className.getClassDefinition();
        String name = getClassName().getName().toString();
        compiler.addComment("Table des méthodes de " + name);

        DAddr addr;
        if (superClass.getName().toString().equals("Object")) {
            addr = new RegisterOffset(1, Register.GB);
        } else {
            addr = superClass.getClassDefinition().getAddrVTalbe();
        }
        compiler.addInstruction(new LEA(addr, Register.R0));
        
        int nextOffest = compiler.getMemoryManager().allocateVariable();
        DAddr nextAddr = new RegisterOffset(nextOffest, Register.GB);
        compiler.addInstruction(new STORE(Register.R0, nextAddr));


        def.setAddrVTable(nextAddr);
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")),Register.R0));
        nextOffest = compiler.getMemoryManager().allocateVariable();
        nextAddr = new RegisterOffset(nextOffest, Register.GB);
        compiler.addInstruction(new STORE(Register.R0, nextAddr));

        for (VTableEntry entry : def.getVTableEntries()) {

            String methodName = entry.getMethodName();

            Label lab = new Label(methodName);
            compiler.addInstruction(new LOAD(new LabelOperand(lab), Register.R0));

            nextOffest = compiler.getMemoryManager().allocateVariable();
            DAddr addrSuiv = new RegisterOffset(nextOffest, Register.GB);
            compiler.addInstruction(new STORE(Register.R0, addrSuiv));
        }
        

        // AbstractIdentifier tempoClass = className;
        // while (!tempoClass.getName().toString().equals("Object")) {

        //     for (AbstractDeclMethod method : methods.getList()) {

        //         String methodName = method.getMethodName();
        //         Label lab = new Label("code." + name + "." + methodName);
        //         compiler.addInstruction(new LOAD(new LabelOperand(lab), Register.R0));

        //         nextOffest = compiler.getMemoryManager().allocateVariable();
        //         DAddr addrSuiv = new RegisterOffset(nextOffest, Register.GB);
        //         compiler.addInstruction(new STORE(Register.R0, addrSuiv));
        //     }
            
        
        // }
        
        

        // LOAD code.A.m, R0
        // STORE R0, 5(GB)
        //LOAD code.Object.equals, R0
        // NullOperand no = new NullOperand()
        // compiler.addInstruction(new LOAD(no, Register.R0));

        // int offset = compiler.getMemoryManager().allocateVariable();
        // DAddr addr = new RegisterOffset(offset, Register.GB);
        
        // compiler.addInstruction(new STORE(Register.R0, addr));

        // LabelOperand labelEquals = new LabelOperand(new Label("code.Object.equals"));

        // compiler.addInstruction(new LOAD(labelEquals, Register.R0));

        // int nextOffset = compiler.getMemoryManager().allocateVariable();
        // DAddr nextAddr = new RegisterOffset(nextOffset, Register.GB);

        // compiler.addInstruction(new STORE(Register.R0, nextAddr));
    }
}