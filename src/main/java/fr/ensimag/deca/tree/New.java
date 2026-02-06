package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.BSR;

import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.DAddr;

import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;


import java.io.PrintStream;
import org.apache.commons.lang.Validate;

public class New extends AbstractExpr {
   
    private final AbstractIdentifier nomClasse;

    public New(AbstractIdentifier nom){
        Validate.notNull(nom);
        this.nomClasse = nom;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        Type type = nomClasse.verifyType(compiler);
        
        if (!type.isClass()) {
            throw new ContextualError("Le type " + type.getName() + " n'est pas une classe. Impossible d'instancier.", getLocation());
        }

        this.setType(type);
        return type;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        nomClasse.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        nomClasse.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        nomClasse.iter(f);
    }

   @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {

        String className = nomClasse.getClassDefinition().getType().getName().toString();

        compiler.addComment("début de NEW " + className);
        int nbFields = nomClasse.getClassDefinition().getTotalNumberOfFields();

        compiler.addInstruction(new NEW(new ImmediateInteger(nbFields + 1), register));

        if (!compiler.getCompilerOptions().getNoCheck()) {
            compiler.addInstruction(new BOV(new Label("tas_plein")));
            compiler.setHasHeapOverflow();
        }

        DAddr addr = nomClasse.getClassDefinition().getAddrVTalbe();
        compiler.addInstruction(new LEA(addr, Register.R0), "On stocke l'adresse de la table des méthodes");
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, register)));

        compiler.addInstruction(new PUSH(register));
        compiler.addInstruction(new BSR(new Label("init." + className)));
        compiler.addInstruction(new POP(register));

    }


}