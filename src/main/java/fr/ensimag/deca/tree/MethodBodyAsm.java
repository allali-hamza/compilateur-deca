package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tree.StringLiteral;
import fr.ensimag.ima.pseudocode.AsmLine;
import fr.ensimag.ima.pseudocode.Label;






public class MethodBodyAsm extends AbstractMethodBody {
    
    private StringLiteral asmCode;


    public MethodBodyAsm(StringLiteral asmCode) {
        Validate.notNull(asmCode);
        this.asmCode = asmCode;
    }

 @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, 
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
       
        asmCode.verifyExpr(compiler, localEnv, currentClass);
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("asm(");
        s.print("\"" + asmCode + "\"");
        s.print(");");
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler) {
        String asmStr = asmCode.getValue();
        
        asmStr = asmStr.substring(1, asmStr.length() - 1);//enlever les guillemetes

        asmStr =asmStr.replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\");

        String[] lignes = asmStr.split("\n");
        for( String ligne : lignes){
            ligne = ligne.trim();
            if(!ligne.isEmpty()){
                //UTILISATION DE lINE
                if (ligne.startsWith(";")){
                    compiler.addComment(ligne.substring(1).trim());
                }
                else if (ligne.endsWith(":")) {
                    compiler.addLabel(new Label(ligne.substring(0, ligne.length()-1)));
                    
                }
                else{
                    compiler.add(new AsmLine(ligne));
                }
            }
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        asmCode.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        asmCode.prettyPrint(s,prefix,true);
    }


    protected void displaySpecificAttributes(PrintStream s) {
        s.print(" ");
        s.print(asmCode);
    }

    public int getMaxStackSize(int numParams) {
        return 0;
    }
}