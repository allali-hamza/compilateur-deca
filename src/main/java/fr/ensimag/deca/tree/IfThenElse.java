package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl56
 * @date 01/01/2026
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        
        Type condType = condition.verifyExpr(compiler, localEnv, currentClass);

        
        if (!condType.isBoolean()) {
            throw new ContextualError(
                "La condition d'un if doit être de type boolean",
                condition.getLocation()
            );
        }

        // Vérifier les instructions du then
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);

       
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);

                
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //throw new UnsupportedOperationException("not yet implemented");
        // debut code ajoute
        int labelNumber = compiler.getLabelCounter();
        compiler.addLabelCounter(); // un autre block if doit avoir des labes !=

        Label labelSiNon = new Label("SiNon" + labelNumber);
        Label labelFin = new Label("Fin" + labelNumber);

        GPRegister register = compiler.getRegisterHandler().allocate();
        condition.codeGenBool(compiler, labelSiNon, false, register);
        compiler.getRegisterHandler().free(register);

        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(labelFin));

        compiler.addLabel(labelSiNon);
        elseBranch.codeGenListInst(compiler);

        compiler.addLabel(labelFin);


        // fin code ajoute
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //{ r := ’if(’.cond.’){’.thens .’} else {’elses .’}’}
        s.print("if(");
        condition.decompile(s);
        s.println(") {");

        // l'indentation du second bloc
        s.indent();
        thenBranch.decompile(s);
        s.unindent();

        // l'indentation du troisième bloc
        s.println("} else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        //
        s.println("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
