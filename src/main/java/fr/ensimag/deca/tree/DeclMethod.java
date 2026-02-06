package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.ima.pseudocode.instructions.POP;


import fr.ensimag.ima.pseudocode.instructions.ERROR;



public class DeclMethod extends AbstractDeclMethod {

    private AbstractIdentifier type;
    private AbstractIdentifier methodName;
    private ListDeclParam params;
    private AbstractMethodBody methodBody;
    

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName,
                     ListDeclParam params, AbstractMethodBody methodBody) {
        this.type = type;
        this.methodName = methodName;
        this.params = params;
        this.methodBody = methodBody;
    }

@Override
protected void verifyMethod(DecacCompiler compiler, ClassDefinition currentClass)
        throws ContextualError {
    Type returnType = type.verifyType(compiler);

    Signature sig = new Signature();
    for (AbstractDeclParam p : params.getList()) {
        Type paramType = p.verifyDeclParam(compiler);
        sig.add(paramType);
    }

    // --- LOGIQUE DE REDÉFINITION (OVERRIDE) ---
    // On cherche si la méthode existe dans la classe parente
    ExpDefinition superDef = currentClass.getSuperClass().getMembers().get(methodName.getName());
    MethodDefinition overrideDef = null;

    if (superDef != null) {
        // On vérifie que c'est une méthode et non un champ
        overrideDef = superDef.asMethodDefinition(
            "Redéfinition invalide : " + methodName.getName() + " est un champ dans la super-classe", 
            methodName.getLocation());

        // VÉRIFICATION DE LA SIGNATURE (C'est ici que signature.deca va échouer !)
        if (!sig.isSameAs(overrideDef.getSignature())) {
            throw new ContextualError("La signature de la méthode " + methodName.getName() 
                + " ne correspond pas à la super-classe", methodName.getLocation());
        }

        boolean returnCompatible = false;
        if (returnType.sameType(overrideDef.getType())) {
            returnCompatible = true;
        } else if (returnType.isClass() && overrideDef.getType().isClass()) {
            // Cast sécurisé vers ClassType pour utiliser isSubClassOf
            ClassType ct1 = (ClassType) returnType;
            ClassType ct2 = (ClassType) overrideDef.getType();
            if (ct1.isSubClassOf(ct2)) {
                returnCompatible = true;
            }
        }

        if (!returnCompatible) {
            throw new ContextualError("Type de retour incompatible pour " + methodName.getName(), 
                methodName.getLocation());
        }
    }

    int index;
    if (overrideDef != null) {
        index = overrideDef.getIndex(); 
    } else {
        int nbMethodsParent = currentClass.getSuperClass().getNumberOfMethods();
        if (currentClass.getNumberOfMethods() < nbMethodsParent) {
            currentClass.setNumberOfMethods(nbMethodsParent);
        }
        index = currentClass.getNumberOfMethods() + 1; 
        currentClass.setNumberOfMethods(index);
    }
    
    MethodDefinition def = new MethodDefinition(returnType, methodName.getLocation(), sig, index);
    def.setLabel(new Label("code." + currentClass.getType().getName() + "." + methodName.getName()));

    try {
        currentClass.getMembers().declare(methodName.getName(), def);
    } catch (EnvironmentExp.DoubleDefException e) {
        throw new ContextualError("Méthode déjà définie", methodName.getLocation());
    }
    
    currentClass.setMethod(index, def);
    methodName.setDefinition(def);
}
   

@Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        // Passe 3 : On crée un nouvel environnement pour les paramètres de la méthode
        EnvironmentExp envParams = new EnvironmentExp(currentClass.getMembers());

        // On vérifie les paramètres et on les ajoute à l'environnement local
        params.verifyListDeclParam(compiler, envParams);
        
        // On vérifie le corps de la méthode (variables locales et instructions)
        methodBody.verifyMethodBody(compiler, envParams, currentClass, type.getType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") ");
        methodBody.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        params.iter(f);
        methodBody.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        methodBody.prettyPrint(s, prefix, true);
    }

    public String getMethodName(){
        return this.methodName.getName().toString();
    }

    // protected void CodeGenDeclMethod(DecacCompiler compiler, ClassDefinition currentClass){

    //     String className = currentClass.getType().getName().toString();

    //     compiler.addLabel(new Label("code." + className + "." + getMethodName()));
    //     compiler.addInstruction(new TSTO(1));
    //     compiler.addInstruction(new BOV( new Label("pile_pleine")));
    //     compiler.addInstruction(new ADDSP(params.getList().size()));
    //     for(int i=2; i< 16; i++){//pas optimisé pour le moment
    //         // compiler.addInstruction(new PUSH())   // rani drt commentaire bach n compiler
    //     }
    //     compiler.addInstruction(new ERROR());

    //     compiler.addLabel(new Label("fin." + className + "." + getMethodName()));
    //     compiler.addComment("Code restoratioin des registres");
    //     compiler.addInstruction(new RTS());


    // }


    protected void CodeGenDeclMethod(DecacCompiler compiler, String className){
        compiler.getRegisterHandler().reset();
        compiler.resetRetLabel();
        Label lab = new Label("code." + className + "." + methodName.getName().toString());
        compiler.addLabel(lab);

        int indexParam = -3; 
        for (AbstractDeclParam p : params.getList()) {
            DeclParam param = (DeclParam) p;
            param.getName().getParamDefinition().setOperand(new RegisterOffset(indexParam, Register.LB));
            indexParam--;
        }
        int d1 = 0;
        if (methodBody.getDeclVariables() != null){
            d1 = methodBody.getDeclVariables().getList().size();
        }
        compiler.setRetLabel(new Label("fin." + className + "." + methodName.getName().toString()));
        methodBody.codeGenMethodBody(compiler);
        if (!type.getType().isVoid()) {
            // meme label
            compiler.addInstruction(new BRA(new Label("err_exit." + className + "." + methodName.getName().toString())));
        }

        int maxReg = compiler.getRegisterHandler().getMaxRegisterUsed();
        if (maxReg < 2){
            maxReg = 2; // R0 et R1 exclus
        }

        compiler.addReturnLabel();
        
        compiler.addComment("Code restoratioin des registres");
        for (int i = maxReg ; i >= 2 ;i--){
            compiler.addInstruction(new POP(Register.getR(i)));
        }
        compiler.addInstruction(new RTS());
        int nbSavReg = 0;
        for (int i = maxReg ; i >= 2 ;i--){
            compiler.addFirstInBlock(lab, new PUSH(Register.getR(i)), null);
            nbSavReg++;
        }

        int maxPile = compiler.getRegisterHandler().getMaxStackDepth();

        int d = nbSavReg + maxPile + d1; 
        compiler.addFirstInBlock(lab, new ADDSP(new ImmediateInteger(d1)), "");
        compiler.addFirstInBlock(lab, new BOV(new Label("pile_pleine")), "");
        compiler.addFirstInBlock(lab,new TSTO(new ImmediateInteger(d)),"");
        



        //error label our les méthodes non void
        if(!type.getType().isVoid()){
            compiler.addLabel(new Label("err_exit." + className + "." + methodName.getName().toString()));
            compiler.addInstruction(new WSTR("\"Erreur : sortie de la methode " + className + "." + methodName.getName().toString() + " sans return\""));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
        
    }



}