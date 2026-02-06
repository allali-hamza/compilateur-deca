package fr.ensimag.deca.context;

public class VTableEntry {
    public final MethodDefinition method;
    public final ClassDefinition ownerClass;


    public VTableEntry(MethodDefinition method, ClassDefinition ownerClass) {
        this.method = method;
        this.ownerClass = ownerClass;
    }

    public MethodDefinition getMethod() {
        return method;
    }

    public String getSimpleMethodName() {
        String fullLabel = method.getLabel().toString();
        int lastDot = fullLabel.lastIndexOf('.');
        if (lastDot != -1) {
            return fullLabel.substring(lastDot + 1);
        }
        return fullLabel;
    }
    public ClassDefinition getOwnerClass() {
        return ownerClass;
    }

    public String getMethodName() {
        return method.getLabel().toString();
    }

    public String getOwnerClassName() {
        return ownerClass.getType().getName().toString();
    }

}