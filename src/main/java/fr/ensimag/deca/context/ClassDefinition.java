package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Label;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.util.LinkedHashMap;

import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.DAddr;


/**
 * Definition of a class.
 *
 * @author gl56
 * @date 01/01/2026
 */
public class ClassDefinition extends TypeDefinition {

    private DAddr addrVTable;

    private final Map<Integer, MethodDefinition> methods = new HashMap<>();
    private List<VTableEntry> vTableEntries = new ArrayList<>();

    
    public void setMethod(int index, MethodDefinition method) {
        methods.put(index, method);
    }

    
    public MethodDefinition getMethod(int index) {
        return methods.get(index);
    }

    public List<VTableEntry> getVTableEntries() {
        return vTableEntries;
    }

    public void vTableBuild() {
        vTableEntries = new ArrayList<>();
        Map<String, VTableEntry> methodMap = new LinkedHashMap<>();

        if (superClass != null && !superClass.getType().getName().toString().equals("Object")) {
            for (VTableEntry entry : superClass.getVTableEntries()) {
                methodMap.put(entry.getSimpleMethodName(), entry);
            }
        }

        for (MethodDefinition method : methods.values()) {

            VTableEntry tempEntry = new VTableEntry(method, this);
            String simpleName = tempEntry.getSimpleMethodName();
            methodMap.put(simpleName, tempEntry);
        }

        vTableEntries.addAll(methodMap.values());
    }

    public Map<Integer, MethodDefinition> getMethods() {
        return methods;
    }

    

    public DAddr getAddrVTalbe() {
        return this.addrVTable;
    }

    public void setAddrVTable(DAddr addr) {
        this.addrVTable = addr;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }
    
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    private final EnvironmentExp members;
    private final ClassDefinition superClass; 

    public EnvironmentExp getMembers() {
        return members;
    }

    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }

    public int getTotalNumberOfFields() {
        int total = this.numberOfFields;
        ClassDefinition supClass = this.superClass;
        while (supClass != null &&  !superClass.getType().getName().toString().equals("Object")) {
            total += supClass.getNumberOfFields();
            supClass = supClass.getSuperClass();
        }   
        return total;
    }
    
}
