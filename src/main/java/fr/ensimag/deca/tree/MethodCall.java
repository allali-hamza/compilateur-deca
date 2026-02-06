package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.ListExpr;
import fr.ensimag.deca.tree.TreeFunction;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import java.rmi.registry.Registry;
import java.security.Signature;

import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.NullOperand;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.ImmediateInteger;


import java.util.List;
import java.util.ArrayList;


import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


/**
 * Deca InstanceOf
 *
 * @author gl56
 * @date 01/01/2026
 */
public class MethodCall extends AbstractExpr{
    private final AbstractExpr expression;
    private final AbstractIdentifier methodName;
    private final ListExpr args;

    public MethodCall(AbstractExpr exp, AbstractIdentifier id, ListExpr args){
        expression = exp;
        methodName = id;
        this.args = args;
    }
   @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

        Type typeTarget = null;
        ClassDefinition targetClass = null;

        if (expression != null) {
          
            typeTarget = expression.verifyExpr(compiler, localEnv, currentClass);

            if (!typeTarget.isClass()) {
                throw new ContextualError(
                        "type is non object and cannot method call: " + typeTarget, getLocation());
            }

            ClassType classType = (ClassType) typeTarget;
            targetClass = classType.getDefinition();

            if (targetClass == null) {
                throw new ContextualError(
                        "undefined class: " + classType.getName(), getLocation());
            }

            ExpDefinition methodDef = targetClass.getMembers().get(methodName.getName());
            if (methodDef == null) {
                throw new ContextualError(
                        "Method " + methodName.getName() + " not found in " + classType.getName(), getLocation());
            }

            if (!(methodDef instanceof MethodDefinition)) {
                throw new ContextualError(
                        methodName.getName() + " is not a method of " + classType.getName(), getLocation());
            }

            MethodDefinition methDef = (MethodDefinition) methodDef;
            fr.ensimag.deca.context.Signature sign = methDef.getSignature();

            if (args.size() != sign.size()) {
                throw new ContextualError(
                        "wrong number of Arguments in " + methodName.getName(), getLocation());
            }

           
            for (int i = 0; i < args.size(); i++) {
                AbstractExpr paramExpr = args.getList().get(i);
                Type paramType = paramExpr.verifyExpr(compiler, localEnv, currentClass);
                Type typePrevue = sign.paramNumber(i);

               
                if (!compiler.environmentType.assignCompatible(typePrevue, paramType)) {
                    throw new ContextualError("Type mismatch for parameter " + (i + 1) + " of method " + methodName.getName(),
                            getLocation());
                }

                // Conversion 
                if (typePrevue.isFloat() && paramType.isInt()) {
                    fr.ensimag.deca.tree.ConvFloat conv = new ConvFloat(paramExpr);
                    conv.verifyExpr(compiler, localEnv, currentClass);
                    args.set(i, conv);
                }
            }

            methodName.setDefinition(methDef);
            methodName.setType(methDef.getType());
            Type rType = methDef.getType();
            setType(rType);
            return rType;

        } else {
            // Cas : method(args)  sans objet explicite
            String nom = methodName.getName().toString();
            boolean estPredefini = nom.equals("println") || nom.equals("printxln") ||
                    nom.equals("readInt") || nom.equals("readFloat") ||
                    nom.equals("print") || nom.equals("printx");

            if (estPredefini) {
                if (nom.startsWith("print")) {
                    for (AbstractExpr param : args.getList()) {
                        param.verifyExpr(compiler, localEnv, currentClass);
                    }
                    setType(compiler.environmentType.VOID);
                    return getType();
                } else if (nom.equals("readInt")) {
                    if (args.size() != 0) {
                        throw new ContextualError("readInt() takes no argument", getLocation());
                    }
                    setType(compiler.environmentType.INT);
                    return getType();
                } else if (nom.equals("readFloat")) {
                    if (args.size() != 0) {
                        throw new ContextualError("readFloat() takes no argument", getLocation());
                    }
                    setType(compiler.environmentType.FLOAT);
                    return getType();
                }
                throw new ContextualError("Unknown predefined: " + nom, getLocation());
            }

            if (currentClass == null) {
                throw new ContextualError("cannot call " + methodName.getName() + " in static context ", getLocation());
            }

            ExpDefinition methodDef = currentClass.getMembers().get(methodName.getName());
            if (methodDef == null) {
                throw new ContextualError(
                        "Method " + methodName.getName() + " not found in current class", getLocation());
            }
            if (!(methodDef instanceof MethodDefinition)) {
                throw new ContextualError(
                        methodName.getName() + " is not a method", getLocation());
            }

            MethodDefinition methDef = (MethodDefinition) methodDef;
            fr.ensimag.deca.context.Signature sign = methDef.getSignature();

            if (args.size() != sign.size()) {
                throw new ContextualError(
                        "wrong number of Arguments in " + methodName.getName(), getLocation());
            }


            for (int i = 0; i < args.size(); i++) {
                AbstractExpr paramExpr = args.getList().get(i);
                Type paramType = paramExpr.verifyExpr(compiler, localEnv, currentClass);
                Type typePrevue = sign.paramNumber(i);

                if (!compiler.environmentType.assignCompatible(typePrevue, paramType)) {
                    throw new ContextualError("Type mismatch for parameter " + (i + 1) + " of method " + methodName.getName(),
                            getLocation());
                }

                if (typePrevue.isFloat() && paramType.isInt()) {
                    fr.ensimag.deca.tree.ConvFloat conv = new ConvFloat(paramExpr);
                    conv.verifyExpr(compiler, localEnv, currentClass);
                    args.set(i, conv);
                }
            }

            methodName.setDefinition(methDef);
            methodName.setType(methDef.getType());
            Type rType = methDef.getType();
            setType(rType);
            return rType;
        }
    }
          
    
    @Override 
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        int paramCount = args.size() + 1; 

        //GPRegister reg = compiler.getRegisterHandler().allocate();
        
        compiler.addInstruction(new ADDSP(paramCount));

        if (expression != null) {
            expression.codeGenExpr(compiler, register);
            compiler.addInstruction(new STORE(register, new RegisterOffset(0, Register.SP)));

        } else {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), register));
            compiler.addInstruction(new STORE(register, new RegisterOffset(0, Register.SP)));
        }

        //compiler.getRegisterHandler().free(reg);

        for (int i = 0; i < args.size() ; i++) {
            //GPRegister regArg = compiler.getRegisterHandler().allocate();
            args.getList().get(i).codeGenExpr(compiler, register);
            compiler.addInstruction(new STORE(register, new RegisterOffset(-(i+1), Register.SP)));
            //compiler.getRegisterHandler().free(regArg);
        }

        GPRegister rObj = compiler.getRegisterHandler().allocate();
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), rObj));
        compiler.addInstruction(new CMP(new NullOperand(), rObj));
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
            compiler.addComment("test test");
            compiler.setHasNullDereference();
        }

        compiler.addInstruction(new LOAD(new RegisterOffset(0, rObj), rObj));
        int index = methodName.getMethodDefinition().getIndex();
        compiler.addInstruction(new BSR(new RegisterOffset(index, rObj)));

        compiler.addInstruction(new SUBSP(paramCount));
        compiler.addInstruction(new LOAD(Register.R0, register));

        compiler.getRegisterHandler().free(rObj);

        // compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.R1));
        // compiler.addInstruction(new CMP(new NullOperand(), Register.R1));
        
        // compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
        // compiler.setHasNullDereference();

        // compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));
        // compiler.addInstruction(new BSR(new RegisterOffset(VTableIndex, Register.R1)));

        // compiler.addInstruction(new SUBSP(paramCount));

        // if (!register.equals(Register.R0)) {
        //     compiler.addInstruction(new LOAD(Register.R0, register));
        // }
    }    
    
    @Override
    public void decompile(IndentPrintStream s){
        //vérifier si c'est vraie
        if (expression != null){
            expression.decompile(s);
            s.print(".");
        }

        methodName.decompile(s);
        s.print("(");
        args.decompile(s);
        s.print(")");
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){
        
        boolean premier = true;
        if (expression != null){
            expression.prettyPrint(s, prefix, true);
            premier = false;
        }
        methodName.prettyPrint(s, prefix, !premier);
        args.prettyPrint(s, prefix, true);

     };

    @Override
    protected void iterChildren(TreeFunction f){
        
        expression.iter(f);
        methodName.iter(f);
        args.iter(f);
    }
  

   protected void codeGenInst(DecacCompiler compiler) {
        GPRegister reg = compiler.getRegisterHandler().allocate();
        codeGenExpr(compiler, reg); 
        compiler.getRegisterHandler().free(reg);
    }

   @Override
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        this.codeGenExpr(compiler, register);
        
        compiler.addInstruction(new CMP(new ImmediateInteger(b ? 1 : 0), register));
        
        compiler.addInstruction(new BEQ(lab));
    }
}