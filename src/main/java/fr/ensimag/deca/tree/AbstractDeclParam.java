package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Parameter declaration.
 */
public abstract class AbstractDeclParam extends Tree {


protected abstract Type verifyDeclParam(DecacCompiler compiler) throws ContextualError;
protected abstract void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError;

    public abstract void decompile(IndentPrintStream s);

}