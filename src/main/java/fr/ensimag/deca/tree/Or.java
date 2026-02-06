package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        if (b) {
            this.getLeftOperand().codeGenBool(compiler, lab, true, register); 
            this.getRightOperand().codeGenBool(compiler, lab, true, register);
        } else {
            int labelNumber = compiler.getLabelCounter();
            compiler.addLabelCounter();
            Label finOr = new Label("finOr" + labelNumber);

            this.getLeftOperand().codeGenBool(compiler, finOr, true, register);
            this.getRightOperand().codeGenBool(compiler, lab, false, register);

            compiler.addLabel(finOr);

        }
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        int labelNumber = compiler.getLabelCounter();
        compiler.addLabelCounter();
        
        Label trueLabel = new Label("true_or" + labelNumber);
        Label endLabel = new Label("end_or" + labelNumber);
        
        this.getLeftOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
        compiler.addInstruction(new BEQ(trueLabel)); 
        
        this.getRightOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
        compiler.addInstruction(new BEQ(trueLabel)); 
        
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));
        compiler.addInstruction(new BRA(endLabel));
        
        compiler.addLabel(trueLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), register));
        
        compiler.addLabel(endLabel);
    }
}
