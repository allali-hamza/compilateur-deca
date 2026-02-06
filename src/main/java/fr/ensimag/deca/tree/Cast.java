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
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;




public class Cast extends AbstractExpr{
    private static int labelCounter = 0;
    private final AbstractExpr expr;
    private final AbstractIdentifier type;

    public Cast(AbstractExpr expression, AbstractIdentifier ident){
        expr = expression;
        type = ident;
        
    }





 

    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        
    
        Type targetType = type.verifyType(compiler);

        
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);

        
        if (!compiler.environmentType.castCompatible(exprType, targetType)) {
            throw new ContextualError("Cast impossible de " + exprType + " vers " + targetType, 
                                    getLocation());
        }

        
        this.setType(targetType);

        
        return targetType;
    }

    protected void codeGenExpr(DecacCompiler compiler, GPRegister register){
        //codeGen d'expr
        expr.codeGenExpr(compiler, register);

        //
        Type exprType = expr.getType();
        Type castType = type.getType();

        //cas 1 
        if(exprType.sameType(castType)){
            //rien à faire
            return;
        }
        //cas 2
        if(exprType.isInt() && castType.isFloat()){
            compiler.addInstruction(new FLOAT(register,register));
            return;
        }

        //cas 3: float to int
        if(exprType.isFloat() && castType.isInt()){
            compiler.addInstruction(new INT(register, register));
            return;
        }

        //cas 4 : class à une autre

        if(exprType.isClass() && castType.isClass()){
            ClassDefinition castClassDef = (ClassDefinition) type.getClassDefinition();

            DAddr castVTable = castClassDef.getAddrVTalbe();


            //Labels
            labelCounter++;
            Label succes = new Label("cast_succes" + labelCounter);
            Label fail = new Label("cast_error" + labelCounter);
            Label end = new Label("cast_end" + labelCounter);
            //null check
            compiler.addInstruction(new CMP(new NullOperand(), register));
            compiler.addInstruction(new BEQ(succes));

            GPRegister vtableReg = compiler.getRegisterHandler().allocate();
            compiler.addInstruction(new LOAD(new RegisterOffset(0, register), vtableReg));
            GPRegister RegDest = compiler.getRegisterHandler().allocate();
            compiler.addInstruction(new LEA(castVTable, RegDest));
            //voir la chaîne d heritage
            Label check = new Label("cast_check" + labelCounter);
            compiler.addLabel(check);

            //COMPARER LES VTABLES
            compiler.addInstruction(new CMP(RegDest, vtableReg));
            compiler.addInstruction(new BEQ(succes));

            //sinon move to parent

            compiler.addInstruction(new LOAD(new RegisterOffset(0, vtableReg), vtableReg));

            //voir si on est dans Object
            compiler.addInstruction(new CMP(new NullOperand(), vtableReg));
            compiler.addInstruction(new BEQ(fail));

            //retour au boucle
            compiler.addInstruction(new BRA(check));
            //succes
            compiler.addLabel(succes);
           

            compiler.addInstruction(new BRA(end));

            compiler.addLabel(fail);
            
            compiler.addInstruction(new WSTR("ERROR: Cast incorrect"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());  
            compiler.addLabel(end);
            compiler.getRegisterHandler().free(vtableReg);
            compiler.getRegisterHandler().free(RegDest);






            return;
        }

        //cas 5: null

        if(exprType.isNull() && castType.isClass()){
            return;
        }
        throw new DecacInternalError("Invalid cast from "+  castType  +" to " + castType);



    }
            
                

    @Override
    public void decompile(IndentPrintStream s){
        s.print("(");
        expr.decompile(s);
        s.print(")");
        s.print("(");
        type.decompile(s);
        s.print(")");
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){
        expr.prettyPrint(s,prefix,false);
        type.prettyPrint(s,prefix,true);

     }

    @Override
    protected void iterChildren(TreeFunction f){
        expr.iter(f);
        type.iter(f);
    }   
}