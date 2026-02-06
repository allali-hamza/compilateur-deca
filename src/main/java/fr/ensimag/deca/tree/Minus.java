package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.SUB;

import fr.ensimag.ima.pseudocode.Label;
/**
 * @author gl56
 * @date 01/01/2026
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }


    @Override
    protected void codeGenSpecific(DecacCompiler compiler, GPRegister nextRegister, GPRegister register) {
        compiler.addInstruction(new SUB(nextRegister, register));
        if(!compiler.isNoCheck()){
            if (this.getType().isFloat()) {
                compiler.addInstruction(new BOV(new Label("overflow_error")));
                compiler.setHasArithOverflow();
            }

        }
       
    }
    
}
