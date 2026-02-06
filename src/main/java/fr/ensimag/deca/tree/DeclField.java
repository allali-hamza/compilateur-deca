package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.tree.Visibility;



public class DeclField extends AbstractDeclField {

    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier fieldName;
    final private AbstractInitialization initialization;


    public DeclField(Visibility visibility, AbstractIdentifier type,
                     AbstractIdentifier fieldName, AbstractInitialization initialization) {
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }
    public AbstractIdentifier getFieldName() {
        return fieldName;
    }

    protected void codeGenField(DecacCompiler compiler) {
        

        compiler.addComment("Initialisation de " + fieldName.getName());        

        // ; Initialisation de x
        this.initialization.codeGenInitialization(compiler, type.getType(), false);
        // LOAD #0, R0
        // LOAD -2(LB), R1 ; R1 contient l'adresse de l

        //compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1)) ;       
        //STORE R0, 1(R1) ; 1(R1) est l'adresse de x
        // On récupère l'index (1, 2, 3...) stocké dans la définition du champ
        int index = fieldName.getFieldDefinition().getIndex();
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index, Register.R1)));
    }

    @Override
    protected void verifyField(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {

        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Un champ ne peut pas être de type void", type.getLocation());
        }
        
        Visibility visNode = (Visibility) this.visibility;
        
        int index;
        ClassDefinition superClass = currentClass.getSuperClass();
        if (superClass != null && !superClass.getType().getName().toString().equals("Object")) {
            index = superClass.getNumberOfFields() + currentClass.getNumberOfFields() + 1;
        } else {
            index = currentClass.getNumberOfFields() + 1;
        }
        
        FieldDefinition def = new FieldDefinition(
            t, 
            fieldName.getLocation(), 
            visNode, 
            currentClass, 
            index
        );
        
        try {
            currentClass.getMembers().declare(fieldName.getName(), def);
            currentClass.setNumberOfFields(index);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Champ " + fieldName.getName() + " déjà défini", fieldName.getLocation());
        }
        
        fieldName.setDefinition(def);
        fieldName.setType(t);
    }

    
    @Override
    protected void verifyFielInit(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
        this.initialization.verifyInitialization(compiler, type.getType(), 
                currentClass.getMembers(), currentClass);
    }

    @Override
    public void decompile(IndentPrintStream s) {

        type.decompile(s);      
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }
}