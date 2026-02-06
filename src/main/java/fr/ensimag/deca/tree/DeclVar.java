package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;

import java.io.PrintStream;
import java.rmi.registry.Registry;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


/**
 * @author gl56
 * @date 01/01/2026
 */
public class DeclVar extends AbstractDeclVar {
    private static final Logger LOG = Logger.getLogger(DeclVar.class);
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.debug("verifyDeclVar : start");

        Type tp = this.type.verifyType(compiler);
        if (tp == null) {
            throw new ContextualError("Le type de la variable "+ varName.getName()+ " est null", getLocation());
        }
        if (tp.isVoid()){
            throw new ContextualError("La variable est de type void", getLocation());
        }
        this.initialization.verifyInitialization(compiler,tp, localEnv, currentClass);
        try {
            VariableDefinition def = new VariableDefinition(tp, getLocation());// On crée une définition de variable
            localEnv.declare(this.varName.getName(), def);// On l'ajoute dans l'environnement local
            this.varName.setDefinition(def);// On lie l'identificateur à sa définition

            LOG.debug("verifyDeclVar : end");
        }catch (EnvironmentExp.DoubleDefException e){
            throw new ContextualError("La variable " + this.varName.getName() + 
            " est déjà déclarée", this.getLocation());
        }
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("not yet implemented");

        //debut code ajoute

        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");


        // fin code ajoute
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    
    //debut code ajoute
    protected void codeGenDeclVar(DecacCompiler compiler) {
        GPRegister register = compiler.getRegisterHandler().allocate();
        if (initialization instanceof NoInitialization){
            Type varType = type.getType();
            if (varType.isInt() || varType.isBoolean()) {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));
            } else if (varType.isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), register));
            } else {
                compiler.addInstruction(new LOAD(new NullOperand(), register));
            }
        }
        else{
            initialization.codeGenInit(compiler, register);
        }

        int offset = compiler.getMemoryManager().allocateVariable();


        DAddr addr = new RegisterOffset(offset, Register.GB);
        this.varName.getVariableDefinition().setOperand(addr);
    
        compiler.addInstruction(new STORE(register, addr));
        compiler.getRegisterHandler().free(register);
    }
     // debut code ajoute
     

    protected void codeGenDeclVar(DecacCompiler compiler, int currentLBOffset) {
        DAddr addr = new RegisterOffset(currentLBOffset, Register.LB);
        this.varName.getVariableDefinition().setOperand(addr);

        GPRegister register = compiler.getRegisterHandler().allocate();

        if (initialization instanceof NoInitialization) {
            Type varType = type.getType();
            if (varType.isInt() || varType.isBoolean()) {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));
            } else if (varType.isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), register));
            } else {
                compiler.addInstruction(new LOAD(new NullOperand(), register));
            }
        } else {
            initialization.codeGenInit(compiler, register);
        }

        compiler.addInstruction(new STORE(register, addr));
        compiler.getRegisterHandler().free(register);
    }
    //fin code ajoute
}
