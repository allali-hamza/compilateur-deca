package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl56
 * @date 01/01/2026
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        // 1. Types de base
        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);

        // 2. Initialisation de la classe Object
        Symbol objectSymb = compiler.createSymbol("Object");
        ClassType objectType = new ClassType(objectSymb, Location.BUILTIN, null);
        ClassDefinition objectDef = new ClassDefinition(objectType, Location.BUILTIN, null);
        objectType.setDefinition(objectDef);
        OBJECT = objectType;

        try {
            Symbol equalsSymb = compiler.createSymbol("equals");
            Signature equalsSig = new Signature();
            equalsSig.add(OBJECT);
            MethodDefinition equalsMethod = new MethodDefinition(BOOLEAN, Location.BUILTIN, equalsSig, 1);

            objectDef.getMembers().declare(equalsSymb, equalsMethod);
            objectDef.setMethod(1, equalsMethod);
            objectDef.setNumberOfMethods(1);

        } catch (EnvironmentExp.DoubleDefException e) {
        throw new RuntimeException("Internal error: equals already defined in Object", e);
    }


        
        


        // on tisse les liens 
        envTypes.put(objectSymb,objectDef);
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public void declareClass(Symbol s, TypeDefinition def) {
        if (!envTypes.containsKey(s)){
            envTypes.put(s, def);
        }
    }

    // Types prédéfinis
    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType   OBJECT;
    public final NullType    NULL;

    // --- Méthodes de compatibilité ---

    public boolean castCompatible(Type type1, Type type2) {
        if (type2.isVoid()) {
            return false;
        }
        return assignCompatible(type2, type1) || assignCompatible(type1, type2);
    }

    public boolean assignCompatible(Type type1, Type type2) {
        if (type1.isFloat() && type2.isInt()) {
            return true; 
        }
        if (type1.sameType(type2)) {
            return true;
        }
        if (type1.isClass() && type2.isNull()) {
            return true;
        }
        if (type1.isClass() && type2.isClass()) {
            return subType(type2, type1);
        }
        return false;
    }

    public boolean subType(Type type1, Type type2) {
        if (type1.sameType(type2)) {
            return true;
        }
        if (type1.isClass() && type2.isClass()) {
            ClassType c1 = (ClassType) type1;
            return c1.isSubClassOf((ClassType) type2);
        }
        return false;
    }
}