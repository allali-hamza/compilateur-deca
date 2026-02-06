package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;


public abstract class AbstractMethodBody extends Tree {
    protected abstract void codeGenMethodBody(DecacCompiler compiler);

    protected abstract void verifyMethodBody(DecacCompiler compiler, 
            EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError;


    public  ListDeclVar getDeclVariables() {
        return null;
    }

    public int getMaxStackSize(int numParams){
        return 0;
    }
   
}
