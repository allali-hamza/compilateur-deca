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
import fr.ensimag.ima.pseudocode.NullOperand;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

import fr.ensimag.ima.pseudocode.instructions.CMP;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;




public class Null extends AbstractExpr{
   

    public Null(){
        
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{

                //Null est compatible avec tous les types, est elle est simple et null == faux
                Type type = compiler.environmentType.NULL ;

                this.setType(type);
                return type;

            } ;
                
           
    @Override
    public void decompile(IndentPrintStream s){
        //vérifier si c'est vraie
        s.print("null");
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){
        //à vérifier
        //leaf

     };
    @Override
    String prettyPrintNode(){
        return "Null";
    }

    @Override
    protected void iterChildren(TreeFunction f){
        //vérifier
        //leaf
    }
  
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister registre){
        compiler.addInstruction(new LOAD(new NullOperand(), registre));
    }
   

   
}
