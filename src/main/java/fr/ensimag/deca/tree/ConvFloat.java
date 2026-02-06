package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;


/**
 * Conversion of an int into a float. Used for implicit conversions.
 * * @author gl56
 * @date 01/01/2026
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // 1. On vérifie d'abord l'opérande (normalement déjà fait par l'appelant, 
        // mais nécessaire pour définir son type interne)
        Type typeOperand = getOperand().verifyExpr(compiler, localEnv, currentClass);

        // 2. On s'assure que l'opérande est bien un entier
        if (!typeOperand.isInt()) {
            throw new ContextualError("La conversion flottante (ConvFloat) ne peut s'appliquer qu'à un entier.",
                    getLocation());
        }

        // 3. On définit le type de cette expression comme étant float
        Type floatType = compiler.environmentType.FLOAT;
        this.setType(floatType);
        
        return floatType;
    }

    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        if(!compiler.isNoCheck()){
           //pas d'overflow ici  
        }
        this.getOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new FLOAT(register, register));
    }
}

