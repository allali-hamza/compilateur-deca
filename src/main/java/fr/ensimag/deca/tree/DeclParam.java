package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import fr.ensimag.deca.context.*;
/**
 * List of declarations of parameters.
 */public class DeclParam extends AbstractDeclParam {
    private final AbstractIdentifier type;
    private final AbstractIdentifier parmName;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier parmName){
        this.type = type;
        this.parmName = parmName;
    }
    public AbstractIdentifier getName() {
        return parmName;
    }
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        parmName.decompile(s);
    }

    /**
     * Passe 2 : On extrait le type pour la signature (Passe 2).
     */
    @Override
    protected Type verifyDeclParam(DecacCompiler compiler) throws ContextualError {
        Type t = type.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Un paramètre ne peut pas être de type void", getLocation());
        }
        parmName.setType(t); // On pré-décore l'identificateur
        return t;
    }

    /**
     * Passe 3 : On déclare physiquement le paramètre dans la portée de la méthode.
     */
    @Override
    protected void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp localEnv)
            throws ContextualError {
        // On crée la définition du paramètre
        ParamDefinition def = new ParamDefinition(type.getType(), getLocation());
        try {
            // C'est ICI que le paramètre devient "vivant" pour les instructions
            localEnv.declare(parmName.getName(), def);
            parmName.setDefinition(def);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Nom de paramètre déjà utilisé : " + parmName.getName(), getLocation());
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix){
        type.prettyPrint(s, prefix, false);
        parmName.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f){
        type.iter(f);
        parmName.iter(f);
    }


}