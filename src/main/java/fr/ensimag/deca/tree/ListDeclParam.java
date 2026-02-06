package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        int i = 0;
        for (AbstractDeclParam p : getList()) {
            p.decompile(s);
            if (i < getList().size() - 1) s.print(", ");
            i++;
        }
    }

    /**
     * Passe 2 : vérification de la  Signature.
     */
    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();
        for (AbstractDeclParam p : getList()) {
            sig.add(p.verifyDeclParam(compiler));
        }
        return sig;
    }

    /**
     * Passe 3 : Appelé par DeclMethod juste AVANT de lancer le MethodBody.
     */
    public void verifyListDeclParam(DecacCompiler compiler, EnvironmentExp localEnv)
            throws ContextualError {
        for (AbstractDeclParam p : getList()) {
            p.verifyDeclParamBody(compiler, localEnv);
        }
    }
}