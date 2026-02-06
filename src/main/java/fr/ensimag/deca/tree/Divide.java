package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.QUO;

import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;



/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    // debut code ajoute
    @Override
    protected void codeGenSpecific(DecacCompiler compiler, GPRegister nextRegister, GPRegister register) {
        Type typeVar = this.getType();
        if (typeVar.isInt()) {
            compiler.addInstruction(new QUO(nextRegister, register));
            if (!compiler.isNoCheck()) {
                compiler.addInstruction(new BOV(new Label("division_par_zero")));
                compiler.setHasDivisionByZero();
            }
            
        } else {
            compiler.addInstruction(new DIV(nextRegister, register));
            if(!compiler.isNoCheck()){
            
                compiler.addInstruction(new BOV(new Label("overflow_error")));    
                compiler.setHasArithOverflow();  
            }
        }
        
    }
    
    // fin code ajoute
}
