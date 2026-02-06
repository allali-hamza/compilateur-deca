package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;


/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl56
 * @date 01/01/2026
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type lefType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type righType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (lefType.sameType(righType)){ // int + int = int ; float +foat = float
            this.setType(righType);
            return righType;
        } else if (lefType.isInt() && righType.isFloat()){
            // int + float = float
            AbstractExpr conv = new ConvFloat(this.getLeftOperand());
            conv.setType(righType);
            this.setLeftOperand(conv);
            this.setType(righType);
            return righType;
        } else if (lefType.isFloat() && righType.isInt()) {
            // float + int = float
            AbstractExpr conv = new ConvFloat(this.getRightOperand());
            conv.setType(lefType);
            this.setRightOperand(conv);
            this.setType(lefType);
            return lefType;
        }
        throw new ContextualError("Opération arithmétique invalide sur " + lefType + " et " + righType, getLocation());
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        getLeftOperand().codeGenExpr(compiler, register);
        GPRegister nextRegister = compiler.getRegisterHandler().allocate();
        if (nextRegister != null) {

        getRightOperand().codeGenExpr(compiler, nextRegister);
        codeGenSpecific(compiler, nextRegister, register);
        compiler.getRegisterHandler().free(nextRegister);

        } else {

            compiler.addInstruction(new PUSH(register));
            compiler.getRegisterHandler().incrementStack();

            this.getRightOperand().codeGenExpr(compiler, register);

            compiler.addInstruction(new POP(Register.R0));
            compiler.getRegisterHandler().decrementStack();

            codeGenSpecific(compiler, register, Register.R0);
            
            compiler.addInstruction(new LOAD(Register.R0, register));
        }
    }


    void codeGenSpecific(DecacCompiler compiler, GPRegister nextRegister, GPRegister register) {
        throw new UnsupportedOperationException("Non implémenté pour " + decompile());
    }



}
