package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;


/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Assign extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(Assign.class);
    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass) throws ContextualError {
    
    Type typeLeft = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    Type typeRight = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

    // 1. Types identiques
    if (typeLeft.sameType(typeRight)) {
        this.setType(typeLeft);
        return typeLeft;
    }

    if (typeLeft.isFloat() && typeRight.isInt()) {
        AbstractExpr conv = new ConvFloat(this.getRightOperand());
        conv.verifyExpr(compiler, localEnv, currentClass);
        this.setRightOperand(conv);
        this.setType(typeLeft);
        return typeLeft;
    }

    if (typeLeft.isClass() && typeRight.isNull()) {
        this.setType(typeLeft);
        return typeLeft;
    }

    
    if (typeLeft.isClass() && typeRight.isClass()) {

        if (typeRight.asClassType("err", getLocation())
                .isSubClassOf(typeLeft.asClassType("err", getLocation()))) {
            this.setType(typeLeft);
            return typeLeft;
        }
    }

   
    throw new ContextualError("Type incompatible pour l'affectation : impossible d'affecter " 
            + typeRight + " à " + typeLeft, getLocation());
}
    @Override
    protected String getOperatorName() {
        return "=";
    }



    // debut code ajoute
    @Override
    public void decompile(IndentPrintStream s) {
        
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        
        
    }


    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("Affectation");
        // 1. Générer le code de l'expression de droite (RHS) -> Résultat dans un registre (ex: R2)
        GPRegister reg = compiler.getRegisterHandler().allocate();
        getRightOperand().codeGenExpr(compiler, reg);

        // 2. Stocker le résultat dans l'opérande de gauche (LHS)
        AbstractLValue left = getLeftOperand();
        
        if (left instanceof Identifier) {
            Identifier id = (Identifier) left;
            
            if (id.getDefinition().isField()) {
                // CAS CHAMP : this.x = ...
                // 1. Charger l'adresse de 'this' dans un registre temporaire (R1)
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
                
                // 2. Stocker la valeur (R2) dans le champ (offset(R1))
                FieldDefinition fieldDef = id.getFieldDefinition();
                compiler.addInstruction(new STORE(reg, new RegisterOffset(fieldDef.getIndex(), Register.R1)));
            } else {
                // CAS VARIABLE LOCALE : x = ...
                DAddr addr = id.getExpDefinition().getOperand();
                compiler.addInstruction(new STORE(reg, addr));
            }
        } 
        else if (left instanceof Selection) {
            Selection sel = (Selection) left;
            GPRegister regObj = compiler.getRegisterHandler().allocate();
            sel.getObj().codeGenExpr(compiler, regObj);
            compiler.addInstruction(new CMP(new NullOperand(), regObj));
            compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
            compiler.setHasNullDereference();
            int index = sel.getField().getFieldDefinition().getIndex();
            compiler.addInstruction(new STORE(reg, new RegisterOffset(index, regObj)));
            
            compiler.getRegisterHandler().free(regObj);
        }
                
        compiler.getRegisterHandler().free(reg);
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register) {
        this.getRightOperand().codeGenExpr(compiler, register);
        AbstractLValue left = this.getLeftOperand();
        if (left instanceof Identifier) {
            Identifier id = (Identifier) left;

            // champ implicite (dans une méthode, x signifie this.x)
            if (id.getDefinition().isField()) {
                // On charge l'adresse de 'this' (-2(LB))
                GPRegister regThis = compiler.getRegisterHandler().allocate();
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), regThis));
                int index = id.getFieldDefinition().getIndex();
                compiler.addInstruction(new STORE(register, new RegisterOffset(index, regThis)));
                compiler.getRegisterHandler().free(regThis);
            } 
            // variable locale ou un paramètre 
            else {
                DAddr addr = id.getExpDefinition().getOperand();
                compiler.addInstruction(new STORE(register, addr));
            }
        }
        else if (left instanceof Selection) {
            Selection sel = (Selection) left;
            GPRegister regObj = compiler.getRegisterHandler().allocate();// pour calculer  l'addr de la class
            sel.getObj().codeGenExpr(compiler, regObj);
            compiler.addInstruction(new CMP(new NullOperand(), regObj));
            compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
            compiler.setHasNullDereference();
            int index = sel.getField().getFieldDefinition().getIndex();
            compiler.addInstruction(new STORE(register, new RegisterOffset(index, regObj)));
            compiler.getRegisterHandler().free(regObj);

        }

    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, Label lab, Boolean b, GPRegister register) {
        if (b) {
            this.codeGenExpr(compiler, register);
        }
    }

    // fin du code ajoute
}
