package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;




public class Return extends AbstractInst {
    
    private  AbstractExpr returnExp; 
  

    public Return(AbstractExpr exp) {
        Validate.notNull(exp);
      
        this.returnExp = exp;     
    }

 @Override
protected void verifyInst(DecacCompiler compiler,
        EnvironmentExp localEnv, ClassDefinition currentClass, Type retType)
        throws ContextualError {
    
    
    Type exprType = returnExp.verifyExpr(compiler, localEnv, currentClass);

    
    boolean compatible = false;

    if (exprType.sameType(retType)) {
        compatible = true;
    } else if (retType.isFloat() && exprType.isInt()) {
        ConvFloat conv = new ConvFloat(returnExp);
        conv.verifyExpr(compiler, localEnv, currentClass);
        this.returnExp = conv;
        compatible = true;
    } else if (retType.isClass() && exprType.isNull()) {
        
        compatible = true;
    } else if (retType.isClass() && exprType.isClass()) {
        
        if (exprType.asClassType("err", getLocation()).isSubClassOf(retType.asClassType("err", getLocation()))) {
            compatible = true;
        }
    }

    if (!compatible) {
        throw new ContextualError(
            "Return type mismatch: expected " + retType + ", got " + exprType, getLocation());
    }
}
           

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        
        if (returnExp != null) {
            GPRegister reg = compiler.getRegisterHandler().allocate();
            returnExp.codeGenExpr(compiler,reg);

            compiler.addInstruction(new LOAD(reg, Register.R0));

            compiler.getRegisterHandler().free(reg);
        }
        
        compiler.addInstruction(new BRA(compiler.getReturnLabel()));
        // int labelNumber = compiler.getLabelCounter();
        // compiler.addLabelCounter(); // un autre block if doit avoir des labes !=

        // Label labelSiNon = new Label("SiNon" + labelNumber);
        // Label labelFin = new Label("Fin" + labelNumber);

        // GPRegister register = compiler.getRegisterHandler().allocate();
        // returnExp.codeGenBool(compiler, labelSiNon, false, register);
        // compiler.getRegisterHandler().free();

       

        // fin code ajoute
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        returnExp.decompile(s);
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        returnExp.iter(f);
       
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnExp.prettyPrint(s, prefix, true);
   
    }
    @Override
    String prettyPrintNode(){
        return "Return";
    }


}
