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
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;


import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;




public class InstanceOf extends AbstractExpr {
    private final AbstractExpr expr;
    private final AbstractIdentifier type;

    public InstanceOf(AbstractExpr expression, AbstractIdentifier typeExpr){
        Validate.notNull(typeExpr);
        Validate.notNull(expression);
        expr = expression;
        type = typeExpr;

    }
    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
                Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
                Type typeExprType = type.verifyType(compiler);
                //verifier que c'est vraiement une classe
                if(!exprType.isClass() && !exprType.isNull()){
                    throw new ContextualError("Left Hand of instanceof must be a reference type, found:" + exprType, getLocation());
                }
                //RHS doit etre une classe
                if(!typeExprType.isClass()){
                    throw new ContextualError("Right Hand of instanceof must be a class type, found:" + exprType, getLocation());
                }

                //set type to booleean
                Type booleanType = compiler.environmentType.BOOLEAN;
                setType(booleanType);

                return booleanType;
            } ;
                
           
    @Override
    public void decompile(IndentPrintStream s){
        s.print("(");
        expr.decompile(s);
        s.print("instanceof");
        type.decompile(s);
        s.print(")");
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){

        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);

     }

    @Override
    protected void iterChildren(TreeFunction f){
        expr.iter(f);
        type.iter(f);
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register){

        expr.codeGenExpr(compiler, register);

        
        ClassDefinition classDefCible =  (ClassDefinition) type.getClassDefinition();

        if(classDefCible == null){
            throw new DecacInternalError("Class definition not found: no " + type);

        }

        DAddr VTableAddr = classDefCible.getAddrVTalbe();
        if (VTableAddr == null){
            if (type.getName().toString().equals("Object")){
                VTableAddr = new RegisterOffset(1, Register.GB);
            }
            else {
                throw new DecacInternalError("VtableAddr est nulle pour la classe " + type.getName());
            }
        }
        GPRegister vTableReg =compiler.getRegisterHandler().allocate();
        GPRegister RegBis = compiler.getRegisterHandler().allocate();
        compiler.addInstruction(new LEA(VTableAddr, RegBis));
        int id = compiler.getLabelCounter();
        Label trueLabel = new Label("instanceof_true" + id);
        Label falseLabel = new Label("instanceof_false" + id );
        Label loop = new Label("instanceof_loop" + id);
        Label end = new Label("instanceof_end" + id);
        //null
        compiler.addInstruction(new CMP(new NullOperand(), register));
        
        compiler.addInstruction(new BEQ(falseLabel));
        //Get VTable pointer

        compiler.addInstruction(new LOAD(new RegisterOffset(0, register), vTableReg));

        //chaine d heritage

        compiler.addLabel(loop);
        //get Vtable and compare if instance of
        compiler.addInstruction(new CMP(RegBis,vTableReg));
        compiler.addInstruction(new BEQ(trueLabel));
        //get parent vtable
        compiler.addInstruction(new LOAD(new RegisterOffset(0, vTableReg), vTableReg));
        //si on est dans Objet
        compiler.addInstruction(new CMP(new NullOperand(), vTableReg));
        compiler.addInstruction(new BEQ(falseLabel));

        //MOVE to parent

        compiler.addInstruction(new BRA(loop));

        //Resultats
        compiler.addLabel(trueLabel);
     
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), register));
        compiler.addInstruction(new BRA(end));

        //false
        compiler.addLabel(falseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));

        compiler.addLabel(end);
        compiler.getRegisterHandler().free(vTableReg);
        compiler.getRegisterHandler().free(RegBis);

    }

    protected void codeGenBool(DecacCompiler compiler,  Label lab, Boolean b, GPRegister register){
        codeGenExpr(compiler, register);
        compiler.addInstruction(new CMP(new ImmediateInteger(b?1 :0), register));
        compiler.addInstruction(new BEQ(lab));
       }
    

}

    // fin code ajoute

