package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.MUL;

import fr.ensimag.ima.pseudocode.Label;


/**
 * @author gl56
 * @date 01/01/2026
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void codeGenSpecific(DecacCompiler compiler, GPRegister nextRegister, GPRegister register) {
        compiler.addInstruction(new MUL(nextRegister, register));
        if(!compiler.isNoCheck()){
            
            if (this.getType().isFloat()) {
                compiler.addInstruction(new BOV(new Label("overflow_error")));
                compiler.setHasArithOverflow();
            }
    
        }
        
    }


}
