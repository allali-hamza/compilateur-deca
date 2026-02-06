package fr.ensimag.deca.tree;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.OPP;


/**
 * @author gl56
 * @date 01/01/2026
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
       
        Type typeOp = this.getOperand().verifyExpr(compiler, localEnv, currentClass);

      
        if (!typeOp.isInt() && !typeOp.isFloat()){
            throw new ContextualError("L'opérateur '-' unaire ne s'applique qu'aux types int ou float (obtenu : " + typeOp + ")", getLocation());
        }

        this.setType(typeOp);
        return typeOp;
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }
    @Override 
    public void decompile(IndentPrintStream s){
        s.print("-(");
        getOperand().decompile(s);
        s.print(")"); 

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        this.getOperand().codeGenExpr(compiler, register);
        compiler.addInstruction(new OPP(register, register));
    }
}