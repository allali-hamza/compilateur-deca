package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Body of a method defined in a class.
 */
public class MethodBody extends AbstractMethodBody {

    final private ListDeclVar declVariables;
    final private ListInst insts;

    public MethodBody(ListDeclVar declVariables, ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }
    

@Override
protected void verifyMethodBody(DecacCompiler compiler, 
        EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) 
        throws ContextualError {
    
    // 1. Vérification des variables locales
    declVariables.verifyListDeclVariable(compiler, localEnv, currentClass);

    // 2. Vérification des instructions (types, syntaxe interne)
    insts.verifyListInst(compiler, localEnv, currentClass, returnType);

    
}
    // @Override
    // protected void codeGenMethodBody(DecacCompiler compiler) {

    //     insts.codeGenListInst(compiler);
    // }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println(" {");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }

    public ListDeclVar getDeclVariables(){
        return declVariables;
    }

    public int getMaxStackSize(int numParams){
        return declVariables.getList().size() + numParams + 4;
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler){

        // //génere le code pour les variabels
        int currentLBOffset = 1;
        for(AbstractDeclVar var: declVariables.getList()){
            var.codeGenDeclVar(compiler,currentLBOffset);
               currentLBOffset++;
        }

        for(AbstractInst inst : insts.getList()){
            inst.codeGenInst(compiler);
        }
    }
}