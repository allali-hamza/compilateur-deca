package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;






import java.io.PrintStream;
import java.lang.reflect.Parameter;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca Identifier
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Identifier extends AbstractIdentifier {
    private static final Logger LOG = Logger.getLogger(Identifier.class);
    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    //
    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ParamDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a Parameter definition.
     */
    @Override
    public ParamDefinition getParamDefinition() {
        try {
            return (ParamDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a parameer identifier, you can't call getParamDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr : start");
        ExpDefinition def = localEnv.get(this.getName()); //On cherche la définition dans l'environnement

        // Si def est null, ca veut dire que la variable n'existe pas
        if (def == null){
            throw new ContextualError("L'identificateur "+ this.getName()+
            " n'est pas définie", this.getLocation());
        }
        this.setDefinition(def); // On attache la définition à l'AST

        Type type = def.getType(); // On récupère le type et on l'attache aussi
        this.setType(type);

        LOG.debug("verifyExpr : end");

        // On renvoie le type
        return type;
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        TypeDefinition def = compiler.environmentType.defOfType(this.getName());

        if (def == null){
            throw new ContextualError("Type "+ this.getName() +" non définie", getLocation());
        }
        this.setDefinition(def);
        this.setType(def.getType());

        return def.getType();
    }
    
    
    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    // debut code ajoute
    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        DAddr addr;
        if(getDefinition().isParam()){
            addr = getParamDefinition().getOperand(); 
        }
        else if(getDefinition().isField()){
            // Si c'est un champ implicite, il faut le charger via this avant la getoperand.
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            addr = new RegisterOffset(getFieldDefinition().getIndex(), Register.R1);
        }
        else {
            addr = getVariableDefinition().getOperand(); 

        }
        compiler.addInstruction(new LOAD(addr, Register.R1));
        if (getDefinition().getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else {
            if (compiler.isPrintHex()) {
                compiler.addInstruction(new WFLOATX());
            } else {
                compiler.addInstruction(new WFLOAT());
            }
            
        }
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        // champ (ex: x)
        if (getDefinition().isField()) {
            FieldDefinition fieldDef = getFieldDefinition();
            // Pour accéder à un champ, on accède avant ça  à l'objet courant ('this')
            // Dans la pile, 'this' est toujours stocké à l'offset -2 par rapport à LB
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            // Ensuite, on charge la valeur du champ.
            // Adresse du champ = Index(Adresse de l'objet) -> Index(R1)
            compiler.addInstruction(new LOAD(new RegisterOffset(fieldDef.getIndex(), Register.R1), register));
        } 
        // Variable locale ou param
        else {
            DAddr addr;
            if(getDefinition().isVariable()){
                addr = getVariableDefinition().getOperand();
            }
            else{
                addr = getParamDefinition().getOperand(); 
            }
            compiler.addInstruction(new LOAD(addr, register));
        }
    }
    @Override
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        DAddr addr;
        if(getDefinition().isParam()){
            addr = getParamDefinition().getOperand(); 
        }
        else if(getDefinition().isField()){
            // Si c'est un champ implicite, il faut le charger via this avant la getoperand.
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            addr = new RegisterOffset(getFieldDefinition().getIndex(), Register.R1);
        }
        else {
            addr = getVariableDefinition().getOperand(); 

        }
        compiler.addInstruction(new LOAD(addr, register));
        compiler.addInstruction(new CMP(new ImmediateInteger(1), register));
        
        if (b) {
            compiler.addInstruction(new BEQ(lab));
        } else {
            compiler.addInstruction(new BNE(lab));
        }

    }

    // fin code ajoute

}
