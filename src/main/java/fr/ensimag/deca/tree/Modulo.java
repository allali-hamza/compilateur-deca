package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.BOV;



/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type tLeft = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type tRight = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        // Seul cas valide : Int % Int -> Int
        if (tLeft.isInt() && tRight.isInt()) {
            this.setType(tLeft);
            return tLeft;
        }
        throw new ContextualError("Modulo ne supporte que des entiers. Trouvé : " 
                + tLeft + " % " + tRight, getLocation());
    }


    @Override
    protected String getOperatorName() {
        return "%";
    } 

    protected void codeGenSpecific(DecacCompiler compiler, GPRegister nextRegister, GPRegister register) {
        compiler.addInstruction(new REM(nextRegister, register));
        if(!compiler.isNoCheck()){
            
            //vérification d'overlow
             compiler.addInstruction(new BOV(new Label("division_par_zero"))); 
             compiler.setHasDivisionByZero();
        }
    }
}
