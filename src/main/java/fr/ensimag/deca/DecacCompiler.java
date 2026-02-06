package fr.ensimag.deca;

import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.MemoryHandler;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Logger;

import fr.ensimag.deca.codegen.MemoryManager;
import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.RegisterOffset;




/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl56
 * @date 01/01/2026
 */
public class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
    // private boolean trigExtensionUsed = false;
    
    // public void setTrigExtensionUsed(boolean util){
    //     this.trigExtensionUsed = util;
    //     if(util){
    //         LOG.debug("Trigo extension enabled");
    //     }
    // }

    // public boolean isTrigExtensionUsed(){
    //     return trigExtensionUsed;
    // }

    // public void generateCordicCode(){
    //     addComment("Adding CORDIC tables");
    //     generateCordicTables();
    //     addComment("Impementation of funtion");
    //     generateCordicFunctions();
    // }
    // public void generateCordicTables(){
    //     //arctan des 2^-i jusqu'à i=23
    // }
    // public void generateCordicFunctions(){
    //     generateCordicSin();
    //     generateCordicCos();
       
    //     generateComputeUlp();
    //     generateCordicAsin();
    //     generateCordicAtan();
    // }
    // public void generateCordicSin(){
    //     addLabel(new Label("cordic_sin"));
    //     addComment("Mode rotation -sin");

    //     addComment("R0: angle en radians");
    //     addComment("R1: resultat");
    //     float K = 0.6072529350088812561694f;
    //     float PI = 3.14159265358979323846f;
    //     float TWO_PI = 2.0f * PI;
    //     float HALF_PI = PI / 2.0f;
    //     //Registres à utiliser
    //     addInstruction(new PUSH(Register.getR(2)));//x
    //     addInstruction(new PUSH(Register.getR(3)));//y
    //     addInstruction(new PUSH(Register.getR(4)));//z
    //     addInstruction(new PUSH(Register.getR(5)));//i
    //     addInstruction(new PUSH(Register.getR(6)));//angme_ptr

    //     addInstruction(new PUSH(Register.getR(7)));//temp
    //     addInstruction(new PUSH(Register.getR(8)));//const_temp
    //     addInstruction(new PUSH(Register.getR(9)));//pour shift et comparaison
    //     addInstruction(new PUSH(Register.getR(10)));//signe de reduction
    //     addInstruction(new PUSH(Register.getR(11)));//sauvegarde de x
    

    //     //copier l'angle R0 vers R4
    //     addInstruction(new LOAD(Register.R0,Register.getR(4)));

    //     //Réduction
    //     addComment("Basic angle reduction");
    //     Label reduceLoop = new Label("sin_reduce_loop" + getLabelCounter());
    //     Label reduceNeg = new Label("sin_reduce_neg");
    //     Label reduceFait = new Label("sin_reduce_done");
    //     addLabel(reduceLoop);
    //     addInstruction(new LOAD(new ImmediateFloat(0.0f),Register.getR(8)));
    //     addInstruction(new CMP(Register.getR(8), Register.getR(4)));
    //     addInstruction(new BLT(reduceNeg));//si angle <0

    //     //angle >=0
    //     addInstruction(new LOAD(new ImmediateFloat(PI),Register.getR(8)));
    //     addInstruction(new CMP(Register.getR(8), Register.getR(4)));
    //     addInstruction(new BLE(reduceFait));
    //     addInstruction(new LOAD(new ImmediateFloat(TWO_PI),Register.getR(8)));
    //     addInstruction(new SUB(Register.getR(8), Register.getR(4)));
    //     addInstruction(new BRA(reduceLoop));
    //     //angle <0
    //     addLabel(reduceNeg);
    //     addInstruction(new LOAD(new ImmediateFloat(-PI),Register.getR(8)));
    //     addInstruction(new CMP(Register.getR(8), Register.getR(4)));
    //     addInstruction(new BLE(reduceFait));
    //     addInstruction(new LOAD(new ImmediateFloat(TWO_PI),Register.getR(8)));
    //     addInstruction(new ADD(Register.getR(8), Register.getR(4)));
    //     addInstruction(new BRA(reduceLoop));

    //     addLabel(reduceFait);

    //     addInstruction(new LOAD(1, Register.getR(10)));//regi
    //     addInstruction(new LOAD(new ImmediateFloat(HALF_PI), Register.getR(8)));
        
    //     addInstruction(new CMP(Register.getR(8), Register.getR(4)));

    //     addInstruction(new BLE(new Label("check_neg"))); //si angle inféreure à pi/2

    //     addInstruction(new LOAD(-1, Register.getR(10)));

    //     addInstruction(new LOAD(new ImmediateFloat(PI), Register.getR(8)));
    //     addInstruction(new SUB( Register.getR(8),Register.getR(4)));//calcul de sin de z + pi avec -
    //     addInstruction(new BRA(new Label("toute_reduction_faite")));

    //     addLabel(new Label("check_neg"));

    //     addInstruction(new LOAD(new ImmediateFloat(-HALF_PI), Register.getR(8)));
    //     addInstruction(new CMP(Register.getR(8), Register.getR(4)));

    //     addInstruction(new BGE(new Label("toute_reduction_faite")));
    //     addInstruction(new LOAD(-1, Register.getR(10)));
    //     addInstruction(new ADD( new ImmediateFloat(PI), Register.getR(4)));//cancul de sin z + pi avec -

    //     addLabel(new Label("toute_reduction_faite"));
        


        
    //     // float[] gainTable = {
    //     //     0.7853981633974483f, 0.4636476090008061f, 0.24497866312686414f,
    //     //     0.12435499454676144f, 0.06241880999596435f, 0.031239833430268277f,
    //     //     0.015623728620476831f, 0.007812341060101111f, 0.0039062301319669718f,
    //     //     0.0019531225164788188f, 0.0009765621895593195f, 0.0004882812501948913f,
    //     //     0.00024414062514936177f, 0.0001220703121063298f, 0.00006103515632579103f,
    //     //     0.000030517578134473872f, 0.000015258789063548813f, 0.00000762939453110197f,
    //     //     0.000003814697265606496f, 0.000001907348632810187f, 0.0000009536743164059602f,
    //     //     0.0000004768371582030886f, 0.0000002384185791015573f
    //     // };

    //     addInstruction(new LOAD(new ImmediateFloat(K),Register.getR(2)));//x = K, pour compenser la précision
    //     addInstruction(new LOAD(new ImmediateFloat(0.0f),Register.getR(3)));//y =0

    //     addInstruction(new LOAD(new LabelOperand(new Label("cordic_angles")), Register.getR(6)));

    //     addComment("Cordic iterations");
    //     addInstruction(new LOAD(0, Register.getR(5)));//i=0
    //     Label loop = new Label("sin_cordic_loop" + getLabelCounter());
    //     Label endLoop = new Label("cordic_end" + getLabelCounter());

    //     addLabel(loop);
    //     addInstruction(new CMP(24, Register.getR(5)));//24 itérations
    //     addInstruction(new BEQ(endLoop));


    //     addInstruction(new LOAD(new RegisterOffset(0, Register.getR(6)), Register.getR(8)));
    //     //si z<0
    //     Label sinRotNeg = new Label("sin_cordic_rot_neg" + getLabelCounter());
    //     addInstruction(new LOAD(new ImmediateFloat(0.0f), Register.getR(9)));
    //     addInstruction(new CMP(Register.getR(9), Register.getR(4)));
    //     addInstruction(new BGE(sinRotNeg));
        
    //     Label shift_y_1 = new Label("shift_y_1" + getLabelCounter());
    //     Label shift_x_1 = new Label("shift_x_1" + getLabelCounter());
    //     Label shift_end_x_1 = new Label("shift_end_x_1" + getLabelCounter());
    //     Label shift_end_y_1 = new Label("shift_end_y_1" + getLabelCounter());
        
    //     addInstruction(new LOAD(Register.getR(3), Register.getR(7)));
    //     addInstruction(new LOAD(Register.getR(2), Register.getR(11)));//sauvegarder x

    //     addInstruction(new LOAD(Register.getR(5), Register.getR(9)));   
    //     addLabel(shift_y_1);
    //     addInstruction(new CMP(0, Register.getR(9)));
    //     addInstruction(new BLE(shift_end_y_1));
    //     addInstruction(new SHR(Register.getR(7)));
    //     addInstruction(new SUB(new ImmediateInteger(1), Register.getR(9)));
    //     addInstruction(new BRA(shift_y_1));
    //     addLabel(shift_end_y_1);;
        
        
        
        
    //     //Rotation positive

    //     addInstruction(new ADD(Register.getR(7), Register.getR(2)));

    //     addInstruction(new LOAD(Register.getR(11), Register.getR(7)));
    //     addInstruction(new LOAD(Register.getR(5), Register.getR(9)));   
    //     addLabel(shift_x_1);
    //     addInstruction(new CMP(0, Register.getR(9)));
    //     addInstruction(new BLE(shift_end_x_1));
    //     addInstruction(new SHR(Register.getR(7)));
    //     addInstruction(new SUB(new ImmediateInteger(1), Register.getR(9)));
    //     addInstruction(new BRA(shift_x_1));
    //     addLabel(shift_end_x_1);

    //     addInstruction(new SUB(Register.getR(7), Register.getR(3)));
        
    //     addInstruction(new ADD(Register.getR(8), Register.getR(4)));//z = z+atan(2^-i)
    //     addInstruction(new ADD(new ImmediateInteger(1), Register.getR(5)));//i++
    //     addInstruction(new ADD(new ImmediateInteger(4), Register.getR(6)));
    //     addInstruction(new BRA(loop));
    //     //Rotation negative
    //     addLabel(sinRotNeg);
    //     Label shift_y_2 = new Label("shift_y_2" + getLabelCounter());
    //     Label shift_x_2 = new Label("shift_x_2" + getLabelCounter());
    //     Label shift_end_x_2 = new Label("shift_end_x_2" + getLabelCounter());
    //     Label shift_end_y_2 = new Label("shift_end_y_2" + getLabelCounter());
        
    //     addInstruction(new LOAD(Register.getR(3), Register.getR(7)));
    //            addInstruction(new LOAD(Register.getR(2), Register.getR(11)));//sauvegarder x
    //     addInstruction(new LOAD(Register.getR(5), Register.getR(9)));   
    //     addLabel(shift_y_2);
    //     addInstruction(new CMP(0, Register.getR(9)));
    //     addInstruction(new BLE(shift_end_y_2));
    //     addInstruction(new SHR(Register.getR(7)));
    //     addInstruction(new SUB(new ImmediateInteger(1), Register.getR(9)));
    //     addInstruction(new BRA(shift_y_2));
    //     addLabel(shift_end_y_2);;
        
        
        
        
    //     //Rotation

    //     addInstruction(new SUB(Register.getR(7), Register.getR(2)));

    //     addInstruction(new LOAD(Register.getR(11), Register.getR(7)));
    //     addInstruction(new LOAD(Register.getR(5), Register.getR(9)));   
    //     addLabel(shift_x_2);
    //     addInstruction(new CMP(0, Register.getR(9)));
    //     addInstruction(new BLE(shift_end_x_2));
    //     addInstruction(new SHR(Register.getR(7)));
    //     addInstruction(new SUB(new ImmediateInteger(1), Register.getR(9)));
    //     addInstruction(new BRA(shift_x_2));
    //     addLabel(shift_end_x_2);

    //     addInstruction(new ADD(Register.getR(7), Register.getR(3)));

    //     addInstruction(new SUB(Register.getR(8), Register.getR(4)));//z = z-atan(2^-i)
    //     addInstruction(new ADD(new ImmediateInteger(1), Register.getR(5)));//i++
    //     addInstruction(new ADD(new ImmediateInteger(4), Register.getR(6)));
    //     addInstruction(new BRA(loop));

    //     addLabel(endLoop);

    //     addInstruction(new CMP(1, Register.getR(10)));
    //     addInstruction(new BEQ(new Label("skip_sign" + getLabelCounter())));

    //     //if signe = -1
    //     addInstruction(new LOAD(new ImmediateFloat(0.0f), Register.getR(8)));
    
    //     addInstruction(new SUB(Register.getR(3), Register.getR(8) ));
    //     addInstruction(new LOAD(Register.getR(8), Register.getR(3)));
    //     //RÉSULTATS DANS R1
        
    //     addLabel(new Label("skip_sign" + getLabelCounter()));
    //     addInstruction(new LOAD(Register.getR(3), Register.R1));
    //     //Restaurer les registres
    //     addInstruction(new POP(Register.getR(11)));
    //     addInstruction(new POP(Register.getR(10)));
    //     addInstruction(new POP(Register.getR(9)));
    //     addInstruction(new POP(Register.getR(8)));
    //     addInstruction(new POP(Register.getR(7)));          
    //     addInstruction(new POP(Register.getR(6)));
    //     addInstruction(new POP(Register.getR(5)));
    //     addInstruction(new POP(Register.getR(4)));
    //     addInstruction(new POP(Register.getR(3)));
    //     addInstruction(new POP(Register.getR(2)));
    //     addInstruction(new RTS());


    // // }
    // public void generateCordicCos(){
    //     addLabel(new Label("cordic_cos"));

    // }
  
    // public void generateComputeUlp(){
    //     addLabel(new Label("compute_ulp"));
    // }
    // public void generateCordicAsin(){
    //     addLabel(new Label("cordic_asin"));
    // }
    // public void generateCordicAtan(){
    //     addLabel(new Label("cordic_atan2"));
    // }   
    // public void generateMethodCall(String methName, GPRegister[] args, GPRegister res){
    //     switch(methName){
    //         case "sin" :
    //             addInstruction(new LOAD(args[0], Register.R0));
    //             addInstruction(new BSR(new Label("cordic_sin")));
    //             addInstruction(new LOAD(Register.R1, res));
    //             break;
    //         case "cos" :
    //             addInstruction(new LOAD(args[0], Register.R0));
    //             addInstruction(new BSR(new Label("cordic_cos")));
    //             addInstruction(new LOAD(Register.R1, res));
    //             break;
    //         case "ulp" :
    //             addInstruction(new LOAD(args[0], Register.R0));
    //             addInstruction(new BSR(new Label("cordic_ulp")));
    //             addInstruction(new LOAD(Register.R1, res));
    //             break;   
    //         case "asin" :
    //             addInstruction(new LOAD(args[0], Register.R0));
    //             addInstruction(new BSR(new Label("cordic_asin")));
    //             addInstruction(new LOAD(Register.R1, res));
    //             break;
    //         case "atan" :
    //             addInstruction(new LOAD(args[0], Register.R0));
    //             addInstruction(new BSR(new Label("cordic_atan2")));
    //             addInstruction(new LOAD(Register.R1, res));
    //             break;
                
    //     }
    // }  

    public void addFistInBlock(Label lab, Instruction instruction, String comment) {
        program.addFirstInBlock(lab, instruction, comment);

    }

    /**
     * Portable newline character.
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;

    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        program.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        program.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        program.addLabel(label);
    }

    // mon code pour essayer
    public void addFirst(Instruction instruction, String comment) {
        program.addFirst(instruction, comment);
    }

    public void addFirstComment(String comment) {
        program.addFirst(new Line(comment));
    }
    // fin de mon code

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        program.addInstruction(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        program.addInstruction(instruction, comment);
    }
    
    /**
     * @see 
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }

    // debut code ajoute

    private int labelCounter = 0;  // pour distinguer entre labels
    

    public int getLabelCounter() {
        return labelCounter;
    }

    public void addLabelCounter() {
        this.labelCounter++;
    }

    private boolean hasInputOutputError = false;
    private boolean hasDivisionByZero = false;
    private boolean hasArithOverflow = false;
    private boolean hasStackOverflow = false;  
    private boolean hasNullDereference = false;
    private boolean hasHeapOverflow = false;


    public void setHasInputOutputError() {
        this.hasInputOutputError = true;
    }

    public void setHasDivisionByZero() { 
        this.hasDivisionByZero = true;
     }

    public void setHasArithOverflow() {
        this.hasArithOverflow = true; 
    }

    public void setHasStackOverflow() {
        this.hasStackOverflow = true;
        }

    public void setHasNullDereference() {
        this.hasNullDereference = true;
    }
    
    public void setHasHeapOverflow() {
        this.hasHeapOverflow = true;
    }
    // on définit RegisterHandler
    private final RegisterHandler registerHandler = new RegisterHandler(16);

    public RegisterHandler getRegisterHandler(){
        return registerHandler;
    }

    private final MemoryManager memoryManager = new MemoryManager();

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    private boolean printHex = false;

    public void setPrintHex(boolean printHex) {
        this.printHex = printHex;
    }

    public boolean isPrintHex() {
        return this.printHex;
    }


    public void addErrorMessages() {
        // Erreur de débordement de pile
        // if(trigExtensionUsed){
            
        //     addComment("TRIGO extension error handling");

        //     //domaine asin ou acos ou abd(x)>1
        //     addLabel(new Label("math_domaine_error"));
        //     addInstruction(new WSTR("Erreur : Dehors de domaine"));
        //     addInstruction(new WNL());
        //     addInstruction(new ERROR());

        //     //Tangente infinie
        //     addLabel(new Label("math_tan_error"));
        //     addInstruction(new WSTR("Erreur : tan indéfinie pur cette valeur"));

        //     addInstruction(new WNL());
        //     addInstruction(new ERROR());
        // }
        if (hasStackOverflow) {
            this.addLabel(new Label("pile_pleine"));
            this.addInstruction(new WSTR("Erreur : Debordement de la pile"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());

        }

        // ReadInt et ReadFloat
        if (hasInputOutputError) {
            this.addLabel(new Label("erreur_entre_sortie"));
            this.addInstruction(new WSTR("Erreur : Saisie invalide"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());

        }

        // Erreur de division par zéro
        if (hasDivisionByZero) {
            this.addLabel(new Label("division_par_zero"));
            this.addInstruction(new WSTR("Erreur : Division par zero"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());
        }

        //Erreur d'overflow arithmetique 
        if (hasArithOverflow) {
            this.addLabel(new Label("overflow_error"));
            this.addInstruction(new WSTR("Erreur : Il y a overflow"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());

        }
        if (hasNullDereference) {
            this.addLabel(new Label("dereferencement.null"));
            this.addInstruction(new WSTR("Erreur : dereferencement de null"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());
        }

        // Erreur de débordement du tas
        if (hasHeapOverflow) {
            this.addLabel(new Label("tas_plein"));
            this.addInstruction(new WSTR("Erreur : tas plein"));
            this.addInstruction(new WNL());
            this.addInstruction(new ERROR());

        }



    }

    // fin code ajoute



    
    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The main program. Every instruction generated will eventually end up here.
     */
    private final IMAProgram program = new IMAProgram();
 

    /** The global environment for types (and the symbolTable) */
    public final SymbolTable symbolTable = new SymbolTable();
    public final EnvironmentType environmentType = new EnvironmentType(this);

    public Symbol createSymbol(String name) {
        //return null; // A FAIRE: remplacer par la ligne en commentaire ci-dessous
        return symbolTable.create(name);
    }
    public EnvironmentType getEnvironmentType(){
        return environmentType;
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String destFile = null;
        // A FAIRE: calculer le nom du fichier .ass à partir du nom du
        // A FAIRE: fichier .deca.
        // debut du code ajouté
        if (!sourceFile.endsWith(".deca")) {
            throw new RuntimeException("Le fichier source doit être en .deca");
        }
        destFile = sourceFile.substring(0, sourceFile.length() - 5) + ".ass";

        // fin du code ajouté

        PrintStream err = System.err;
        PrintStream out = System.out;
        LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile
                    + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    public boolean isNoCheck(){
        return compilerOptions.getNoCheck();
    };

    //Label
    private Label retLabel = null;

    //getter
    public Label getReturnLabel(){
        if (retLabel == null){
            retLabel = new Label("return_" + System.currentTimeMillis());
        }
        return retLabel;
    }
    //reset
    public void resetRetLabel(){
        retLabel = null;
    }

    public void setRetLabel(Label lab) {
        retLabel = lab;
    }

    //add label si on a besoin
    public void addReturnLabel(){
        if (retLabel != null){
            addLabel(retLabel);
        }
    }
    private boolean doCompile(String sourceName, String destName,
            PrintStream out, PrintStream err)
            throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);

        if (prog == null) {
            LOG.info("Parsing failed");
            return true;
        }
        //code trigo
        // if(trigExtensionUsed){
        //     LOG.info("TRIGO extension detected, ajoutant le support Math");

        // }
        // debut code ajoute
        // On met à jour la limite des registres pour l'option -r X
        this.registerHandler.setMaxRegister(compilerOptions.getRegisters() - 1);

        if (compilerOptions.getParse()) {
            prog.decompile(out);
            System.exit(0);
        }

        // fin code ajoute



        assert(prog.checkAllLocations());


        prog.verifyProgram(this);
        assert(prog.checkAllDecorations());

        // debut code ajoute
        if (compilerOptions.getVerification()) {
            System.exit(0);
        }
        // if(trigExtensionUsed){
        //     generateCordicCode();
        // }
        
        // fin code ajoute

        addComment("start main program");
        prog.codeGenProgram(this);
        addComment("end main program");
        LOG.debug("Generated assembly code:" + nl + program.display());
        LOG.info("Output file assembly file is: " + destName);

        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }

        LOG.info("Writing assembler file ...");

        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws DecacInternalError When an inconsistency was detected in the
     * compiler.
     * @throws LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err)
            throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);
    }
    public void addFirstInBlock(Label lab, Instruction instruction, String comment) {
        program.addFirstInBlock(lab, instruction, comment);
    }

}
