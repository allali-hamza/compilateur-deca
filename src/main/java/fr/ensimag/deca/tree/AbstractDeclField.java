package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;

public abstract class AbstractDeclField extends Tree {
        protected abstract void verifyField(DecacCompiler compiler, ClassDefinition currentClass) 
                throws ContextualError;
        protected abstract void verifyFielInit(DecacCompiler compiler, ClassDefinition currentClass) 
                throws ContextualError;
        /**
          Génère le code ass pour l'initialisation du champ.
         */
        protected abstract void codeGenField(DecacCompiler compiler);
   
}