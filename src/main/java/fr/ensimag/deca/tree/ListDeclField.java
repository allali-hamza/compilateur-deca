package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.Label;
// import fr.ensimag.ima.pseudocode.instructions.LOAD;
// import fr.ensimag.ima.pseudocode.instructions.STORE;

// import fr.ensimag.ima.pseudocode.ImmediatInteger;


public class ListDeclField extends TreeList<AbstractDeclField> {
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }

    /**
     * Passe 2 : Déclaration des champs dans l'environnement de la classe.
     */
    void verifyListField(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclField f : getList()) {
            f.verifyField(compiler, currentClass);
        }
    }

    /**
     * Passe 3 : Vérification de l'initialisation des champs.
.
     */
    void verifyListFieldBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclField f : getList()) {
            //  la vérification de l'initialisation à chaque DeclField
            f.verifyFielInit(compiler, currentClass);
        }
    }
    protected void codeGenListField(DecacCompiler compiler) {
        for (AbstractDeclField f : getList()) {
            f.codeGenField(compiler);
        }
    }
}