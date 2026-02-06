package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;


/**
 * Class declaration.
 *
 * @author gl56
 * @date 01/01/2026
 */

public abstract class AbstractDeclClass extends Tree {

   
        protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

   
        protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

   
        protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;


        protected abstract void codeGenDeclClass(DecacCompiler compiler);


        protected abstract void codeGenVTable(DecacCompiler compiler);

        public abstract ListDeclMethod getMethods();

        public abstract ClassDefinition getClassDefinition();

        


}
