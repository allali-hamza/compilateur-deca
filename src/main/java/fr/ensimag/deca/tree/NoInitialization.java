package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.NullOperand;






/**
 * Absence of initialization (e.g. "int x;" as opposed to "int x =
 * 42;").
 *
 * @author gl56
 * @date 01/01/2026
 */
public class NoInitialization extends AbstractInitialization {

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
    }


    /**
     * Node contains no real information, nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    // debut code ajoute
    protected void codeGenInit(DecacCompiler compiler, GPRegister register) {
        // ne fait rien 
    }
    @Override 
    protected void codeGenInitialization(DecacCompiler compiler, Type t, boolean global){
        if(t.isInt()|| t.isBoolean()){
            // 0 pour int ou boolean
            compiler.addInstruction(new LOAD(new ImmediateInteger(0),Register.R0));
        }
        else if (t.isFloat()){
            compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f),Register.R0));
        }
        else {
            compiler.addInstruction(new LOAD(new NullOperand(),Register.R0));

        }
    }
    // fin code ajoute

}
