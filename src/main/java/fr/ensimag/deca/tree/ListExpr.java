package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl56
 * @date 01/01/2026
 */
public class ListExpr extends TreeList<AbstractExpr> {


    @Override
    public void decompile(IndentPrintStream s) {
        java.util.Iterator<AbstractExpr> iter = getList().iterator();
        while (iter.hasNext()){
            iter.next().decompile(s);
            if (iter.hasNext()){
                s.print(", ");
            }
        }
    }
}
