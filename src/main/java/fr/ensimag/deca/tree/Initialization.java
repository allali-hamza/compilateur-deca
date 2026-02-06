package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl56
 * @date 01/01/2026
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }
@Override
protected void verifyInitialization(DecacCompiler compiler, Type t,
        EnvironmentExp localEnv, ClassDefinition currentClass)
        throws ContextualError {
    
    
    Type tp = this.expression.verifyExpr(compiler, localEnv, currentClass);

    
    boolean compatible = false;

    if (tp.sameType(t)) {
        compatible = true;
    } else if (t.isFloat() && tp.isInt()) {
        
        compatible = true;
        ConvFloat conv = new ConvFloat(expression);
        conv.verifyExpr(compiler, localEnv, currentClass);
        this.setExpression(conv);
    } else if (t.isClass() && tp.isNull()) {
        // Cas Classe o = null
        compatible = true;
    } else if (t.isClass() && tp.isClass()) {
       
        if (tp.asClassType("Erreur interne", getLocation()).isSubClassOf(t.asClassType("Erreur interne", getLocation()))) {
            compatible = true;
        }
    }


if (!compatible) {
    throw new ContextualError("Type incompatible : impossible d'affecter " 
        + tp + " à " + t, getLocation());
}
    
}
   
@Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not yet implemented");
        if (getExpression() == null) {
            System.exit(0);
        }
        s.print(" = ");
        getExpression().decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    protected void codeGenInit(DecacCompiler compiler, GPRegister register) {
        expression.codeGenExpr(compiler, register);
    }
    @Override 
    protected void codeGenInitialization(DecacCompiler compiler, Type t, boolean global){
        GPRegister register = compiler.getRegisterHandler().allocate();
        expression.codeGenExpr(compiler, register);
        compiler.addInstruction(new LOAD(register, Register.R0));
        compiler.getRegisterHandler().free(register);
        // le res sera stock dans R0 par conv
    }

}
