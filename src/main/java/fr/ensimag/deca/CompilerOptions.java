package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl56
 * @date 01/01/2026
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    // methodes ajoutees
    public boolean getParse() {
        return parse;
    }
    
    public boolean getVerification() {
        return verification;
    }

    public boolean getNoCheck() {
        return noCheck;
    }
    
    public int getRegisters(){
        return registers;  // pour l'option -r
    }
    // fin des methodes ajoutees

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    // ajoutee
    private boolean parse = false;        
    private boolean verification = false;  
    private boolean noCheck = false; 

    private int registers = 16;

    // fin ajoutee      
    private List<File> sourceFiles = new ArrayList<File>();
    
    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        // debut du code ajouté
        int i = 0;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                switch(arg) {
                    case "-b" :
                        this.printBanner = true;
                        break;
                    case "-p" :
                        this.parse = true;
                        break;
                    case "-v" :
                        this.verification = true;
                        break;
                    case "-n" :
                        this.noCheck = true;
                        break;
                    case "-P" :
                        this.parallel = true;
                        break;
                    case "-r" :
                        i++;
                        if (i >=args.length) {
                            throw new CLIException("l'option -r attend un nombre de registre entre 4 et 16");   
                        }
                        try {
                            int r = Integer.parseInt(args[i]);
                            if (r < 4 || r > 16) {
                                throw new CLIException("Le nombre de registres doit être compris entre 4 et 16");
                            }
                            this.registers = r;
                        } catch(NumberFormatException e) {
                            throw new CLIException("l'argument doit être un entier");
                        }
                        break;
                    case "-d":
                        this.debug++;
                        break;
                    default:
                       throw new CLIException("Option inconnue : " + arg);

                }
            }
            else {
                // si ce n'est pas un argument, alors c'est un fichier source
                sourceFiles.add(new File(arg));
            }
        }

        if (parse && verification) {
            throw new CLIException("Les options -p et -v sont incompatibles");
        }
        // fin du code ajouté
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

    }

    protected void displayUsage() {
        // debut du code ajouté
        System.out.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -b (banner)      : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("  -p (parse)       : arrête decac après l'étape de construction de l'arbre,");
        System.out.println("                     et affiche la décompilation de ce dernier");
        System.out.println("  -v (verification): arrête decac après l'étape de vérifications");
        System.out.println("  -n (no check)    : supprime les tests à l'exécution");
        System.out.println("  -r X (registers) : limite les registres banalisés disponibles à");
        System.out.println("                     R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("  -d (debug)       : active les traces de debug. Répéter l'option");
        System.out.println("                     plusieurs fois pour avoir plus de traces.");
        // je vais ajouter les autres apres
        // fin du code ajouté
    }
} 
