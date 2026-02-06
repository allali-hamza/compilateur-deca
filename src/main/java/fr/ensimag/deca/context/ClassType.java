package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import org.apache.commons.lang.Validate;

/**
 * Type defined by a class.
 *
 * @author gl56
 * @date 01/01/2026
 */
public class ClassType extends Type {
    
    protected ClassDefinition definition;
    
    public ClassDefinition getDefinition() {
        return this.definition;
    }
    public void  setDefinition(ClassDefinition def){
        this.definition = def;

    }
            
    @Override
    public ClassType asClassType(String errorMessage, Location l) {
        return this;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    /**
     * Standard creation of a type class.
     */
    public ClassType(Symbol className, Location location, ClassDefinition superClass) {
        super(className);
        this.definition = null;
    }

    /**
     * Creates a type representing a class className.
     * (To be used by subclasses only)
     */
    protected ClassType(Symbol className) {
        super(className);
    }
    

   @Override
        public boolean sameType(Type otherType) {
           
            if (!otherType.isClass()) {
                return false;
            }
           
            return this.getName().equals(otherType.getName());
        }

    /**
     * Return true if potentialSuperClass is a superclass of this class.
     */
    public boolean isSubClassOf(ClassType potentialSuperClass) {
        if (this.sameType(potentialSuperClass)) {
            return true;
        }

     
        if (this.definition == null) {
            return false;
        }
        ClassDefinition superClassDef = this.definition.getSuperClass();
        if (superClassDef == null) {
            return false;
        }
        return superClassDef.getType().isSubClassOf(potentialSuperClass);
    }


}
