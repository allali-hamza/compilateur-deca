package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        if (b) {
            // sauter si vrai
            int labelNumber = compiler.getLabelCounter();
            compiler.addLabelCounter();
            Label endAnd = new Label("end_and." + labelNumber);  
            this.getLeftOperand().codeGenBool(compiler, endAnd, false, register);
            this.getRightOperand().codeGenBool(compiler, lab, true, register);
            compiler.addLabel(endAnd);

        } else {
            this.getLeftOperand().codeGenBool(compiler, lab, false, register);
            this.getRightOperand().codeGenBool(compiler, lab, false, register);

        }
        
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        int labelNumber = compiler.getLabelCounter();
        compiler.addLabelCounter();
        
        Label falseLabel = new Label("false_and" + labelNumber);
        Label endLabel = new Label("end_and" + labelNumber);
        
        this.getLeftOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
        compiler.addInstruction(new BNE(falseLabel));
        
        this.getRightOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
        compiler.addInstruction(new BNE(falseLabel)); 
        
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), register));
        compiler.addInstruction(new BRA(endLabel));
        
        compiler.addLabel(falseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));
        
        compiler.addLabel(endLabel);
    }

}
