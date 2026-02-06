package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.CMP;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


public class This extends AbstractExpr{
   
    //Si implicit == false on affiche "this", sinon on n'affiche pas
    
    private final boolean implicit; 

    public This(boolean implicit){
        this.implicit = implicit;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
                if (currentClass == null){
                    throw new ContextualError("'this' doit être utilisé dans le  non statique", getLocation());

                }
                Type type = currentClass.getType();
                this.setType(type);
                return type;
            } 
                
           
    @Override
    public void decompile(IndentPrintStream s){
        if (!implicit){
            s.print("this");
        }
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){

     }

    @Override
    String prettyPrintNode(){
        return "this";
    }

    @Override
    protected void iterChildren(TreeFunction f){

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister registre){
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, registre.LB), registre));
       
    }

    // @Override 
    // protected void codeGenBool(DecacCompiler compiler, Label label, boolean jumpOnTrue, GPRegister register){
        
    //     codeGenExpr(compiler, register);
    //     //comarer avec 0
    //     compiler.addInstruction(new CMP(new ImmediateInteger(0), register));
    // }  
  

   

   
}
