package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.VTableEntry;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.Label;





import org.apache.log4j.Logger;
/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;
        @Override
        public void verifyProgram(DecacCompiler compiler) throws ContextualError {
            LOG.debug("verify program: start");

            this.classes.verifyListClass(compiler);


            this.classes.verifyListClassMembers(compiler);

          
            this.classes.verifyListClassBody(compiler);
            this.main.verifyMain(compiler);

            for (AbstractDeclClass cls : classes.getList()) {
                cls.getClassDefinition().vTableBuild();
            }

            LOG.debug("verify program: end");
        }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code

        classes.codeGenListVTable(compiler);
        
        // compiler.getMemoryManager().resetGB();  // a changer si on a le temps
        
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        compiler.addComment("end main program");

        int d2 = compiler.getMemoryManager().getNbVariables();
        int s = compiler.getRegisterHandler().getMaxStackDepth();
        int d1 = d2 + s;

        
        


        classes.codeGenListDeclClass(compiler);
        for(AbstractDeclClass cls : classes.getList()){
            ClassDefinition classDef = cls.getClassDefinition();
            String className = cls.getClassDefinition().getType().getName().toString();
            cls.getMethods().CodeGenListDeclMethod(compiler, className);
            //cls.getMethods().CodeGenListDeclMethod(compiler, cls);
        }


        // pour déboguer (Hamza: A supprimer les println si j'oublie!)
        // System.out.println("le nombre de variables est " + d2);
        // System.out.println("le nombre maximal des temporaires " + s);

        if (!compiler.isNoCheck()) {
            compiler.addFirst(new ADDSP(d2), "Allocation de l'espace pour les variables locales ");
            compiler.addFirst(new BOV(new Label("pile_pleine")), "Saut si dépassement de pile");
            compiler.addFirst(new TSTO(d1), "Vérification de la taille de la pile");
            compiler.setHasStackOverflow();

        }
        compiler.addFirstComment("Main program");
        codeGenObjectEquals(compiler);

        compiler.addErrorMessages();
    }   

    public void codeGenObjectEquals(DecacCompiler compiler) {
        compiler.addLabel(new Label("code.Object.equals"));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.R1));

        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        compiler.addInstruction(new SEQ(Register.R0));
        compiler.addInstruction(new RTS());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
