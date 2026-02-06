


package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
            s.println();
        }
    }

    /**
     * Passe 2 : Déclaration des méthodes dans l'environnement de la classe.
     */
    void verifyListMethod(DecacCompiler compiler, ClassDefinition currentClass) 
            throws ContextualError {
        for (AbstractDeclMethod m : getList()) {
            m.verifyMethod(compiler, currentClass);
        }
    }
    public void verifyListMethodBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
            for (AbstractDeclMethod m : getList()) { // Utilise getList() qui est défini dans TreeList
                m.verifyMethodBody(compiler, currentClass);
            }
        }
    
    protected void CodeGenListDeclMethod(DecacCompiler compiler, String className){
        for(AbstractDeclMethod method  : getList()){
            method.CodeGenDeclMethod(compiler, className);
        }
    }
}