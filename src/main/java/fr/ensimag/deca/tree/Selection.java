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
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.deca.tree.AbstractLValue;
import fr.ensimag.ima.pseudocode.NullOperand;

import static org.mockito.Mockito.validateMockitoUsage;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


public class Selection extends AbstractLValue {
    private static final Logger LOG = Logger.getLogger(Selection.class);
    private final AbstractExpr selectExpr;
    private final AbstractIdentifier identifier;

    public Selection(AbstractExpr expr, AbstractIdentifier id){
        Validate.notNull(expr);
        Validate.notNull(id);
        selectExpr = expr;
        identifier = id;
    }
    public AbstractExpr getObj() {
        return selectExpr;
    }

    public AbstractIdentifier getField() {
        return identifier;
    }

 @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        // 1. Vérification du type de l'objet (a dans a.var)
        Type typeCible = selectExpr.verifyExpr(compiler, localEnv, currentClass);

        if (!typeCible.isClass()) {
            throw new ContextualError("Target of selection must be an object, not " + typeCible, getLocation());
        }

        ClassType classTypeCible = (ClassType) typeCible;
        ClassDefinition classDef = classTypeCible.getDefinition();
        
        // 2. Recherche du champ
        ExpDefinition fieldDef = classDef.getMembers().get(identifier.getName());
       
        if (fieldDef == null) {
            throw new ContextualError("Field " + identifier.getName() + " not found in class " + classTypeCible.getName(), getLocation());
        }
        if (!(fieldDef instanceof FieldDefinition)) {
            throw new ContextualError(identifier.getName() + " is not a field in class " + classTypeCible.getName(), getLocation());
        }

        FieldDefinition fieldDefCast = (FieldDefinition) fieldDef;

        // GESTION DU PROTECTED (Règle 3.66) 
        if (fieldDefCast.getVisibility() == Visibility.PROTECTED) {
            
            // Condition 2 : Doit être dans une sous-classe de celle qui définit le champ
            if (currentClass == null) {
                throw new ContextualError("Access to protected field " + identifier.getName() + " outside of a class", getLocation());
            }

            //  récupèrer la classe qui a le champ 
            ClassDefinition definingClass = fieldDefCast.getContainingClass();

          
            if (!currentClass.getType().isSubClassOf(definingClass.getType())) {
                throw new ContextualError("Class " + 
                    " is not a subclass of "  + 
                    ". Access to protected field " + identifier.getName() + " denied.", getLocation());
            }

            
            if (!classTypeCible.isSubClassOf(currentClass.getType())) {
                throw new ContextualError("Target type " + classTypeCible.getName() + 
                    " is not a subclass of current class " + 
                    ". Access to protected field " + identifier.getName() + " denied.", getLocation());
            }
        }
      
        identifier.setDefinition(fieldDefCast);
        identifier.setType(fieldDefCast.getType());

        Type fieldType = fieldDefCast.getType();
        setType(fieldType);
        return fieldType;
    }
                
           
    @Override
    public void decompile(IndentPrintStream s){
        //vérifier si c'est vraie
        selectExpr.decompile(s);
        s.print(".");
        identifier.decompile(s);
    }

    @Override
     protected void prettyPrintChildren(PrintStream s, String prefix){
        //à vérifier
        selectExpr.prettyPrint(s, prefix, false);
        identifier.prettyPrint(s, prefix, true);

     };
    @Override
    String prettyPrintNode(){
        return "Selection";
    }
    @Override
    protected void iterChildren(TreeFunction f){
        //vérifier
        selectExpr.iter(f);
        identifier.iter(f);
    }
    @Override 
    protected void codeGenExpr(DecacCompiler compiler, GPRegister register){
        selectExpr.codeGenExpr(compiler, register);


        compiler.addInstruction(new CMP(new NullOperand(), register));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null"))); 
        compiler.setHasNullDereference();
        int index = identifier.getFieldDefinition().getIndex();
        compiler.addInstruction(new LOAD(new RegisterOffset(index, register), register));

    }

   
}
