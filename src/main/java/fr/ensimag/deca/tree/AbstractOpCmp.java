package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Opérateurs de comparaison : <, >, <=, >=, ==, !=
 * * @author gl56
 * @date 01/01/2026
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        // 1. Vérifier le type des deux opérandes
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        Type resultType = null;

        // 2. Cas des comparaisons numériques =
        if ((leftType.isInt() || leftType.isFloat()) && (rightType.isInt() || rightType.isFloat())) {
            
            
            if (leftType.isInt() && rightType.isFloat()) {
                ConvFloat conv = new ConvFloat(getLeftOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setLeftOperand(conv);
            } else if (leftType.isFloat() && rightType.isInt()) {
                ConvFloat conv = new ConvFloat(getRightOperand());
                conv.verifyExpr(compiler, localEnv, currentClass);
                setRightOperand(conv);
            }
            resultType = compiler.environmentType.BOOLEAN;
            
        } 
      
        else if (this instanceof AbstractOpExactCmp) {
            
            if (leftType.isBoolean() && rightType.isBoolean()) {
                resultType = compiler.environmentType.BOOLEAN;
            } 
           
            else if ((leftType.isClass() || leftType.isNull()) && (rightType.isClass() || rightType.isNull())) {
                resultType = compiler.environmentType.BOOLEAN;
            }
        }

        
        if (resultType == null) {
            throw new ContextualError("Comparaison impossible entre le type " + leftType + 
                " et le type " + rightType, getLocation());
        }

        this.setType(resultType);
        return resultType;
    }
    
    @Override
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {

        this.getLeftOperand().codeGenExpr(compiler, register);
        GPRegister nextRegister = compiler.getRegisterHandler().allocate();
        this.getRightOperand().codeGenExpr(compiler, nextRegister);

        compiler.addInstruction(new CMP(nextRegister, register));

        if (b) {
            compiler.addInstruction(getSautDirect(lab));
        } else {
            compiler.addInstruction(getSautInsverse(lab));
        }
        compiler.getRegisterHandler().free(nextRegister);
    }

    abstract Instruction getSautDirect(Label lb);
    abstract Instruction getSautInsverse(Label lb);

    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        Label labelTrue = new Label("labelTrue" + compiler.getLabelCounter());
        Label labelFin = new Label("labelFin" + compiler.getLabelCounter());
        compiler.addLabelCounter();

        this.codeGenBool(compiler, labelTrue, true, register);

        compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));
        compiler.addInstruction(new BRA(labelFin));

        compiler.addLabel(labelTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), register));

        compiler.addLabel(labelFin);
    }



}