package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.OPP;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl56
 * @date 01/01/2026
 */
//Not : !x
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // La methode vérifie si l'operande est de type boolean
        Type typeOp = this.getOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!typeOp.isBoolean()) {
            throw new ContextualError("L'opérande n'est pas de type boolean", getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override 
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        this.getOperand().codeGenBool(compiler, lab, !b, register);
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        this.getOperand().codeGenExpr(compiler, register);
        
        compiler.addInstruction(new OPP(register, register)); 
        compiler.addInstruction(new ADD(new ImmediateInteger(1), register));
    }

}
