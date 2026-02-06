package fr.ensimag.deca.codegen;


public class MemoryManager {
    private int currentOffset = 0;
    private int currentLBOffset = 0;

    public void resetLB(){
        currentLBOffset = 0;
    }

    public int allocatLocalVariable(){
        currentLBOffset++;
        return currentLBOffset;
    }

    public int getCurrentLb(){
        return currentLBOffset;
    }
    public int allocateVariable() { 
        currentOffset++;
        return currentOffset;
    }

    public int getNbVariables() {
        return currentOffset;
    }

    public void resetGB() {
        currentOffset = 0; 
    }
}