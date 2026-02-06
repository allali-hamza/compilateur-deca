package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.RFLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;

import java.io.PrintStream;

/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class ReadFloat extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type floatType = compiler.environmentType.FLOAT; //On recupere le type float de l'envirenement
        this.setType(floatType);
        return floatType;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readFloat()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register){
        compiler.addInstruction(new RFLOAT());
        if (!compiler.isNoCheck() ) {
            compiler.addInstruction(new BOV(new Label("erreur_entre_sortie")));
            compiler.setHasInputOutputError();
        }
        
        
        compiler.addInstruction(new LOAD(Register.R1, register));
        
    }


}
