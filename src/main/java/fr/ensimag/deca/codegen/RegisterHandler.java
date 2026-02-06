package fr.ensimag.deca.codegen;

import java.util.Stack;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegisterHandler {
    private int maxRegister = 15;   
    private int currentStackDepth = 0;
    private int maxStackDepth = 0;
    private int maxRegisterUsed = 2;



    private boolean[] registerInUse = new boolean[16];
    private Stack<Integer> allocatedRegisters = new Stack<>();

    public int getMaxRegisterUsed() {
        return this.maxRegisterUsed;
    }
    public int getMaxRegisters(){
        return allocatedRegisters.size();
    }

    public void initializeRegisters() {
        for (int i = 2; i < maxRegister ; i++) {
            registerInUse[i] =  false;
        }
    }

    public RegisterHandler(int max) {
        this.maxRegister = max;
        initializeRegisters();
    }

    public GPRegister allocate() {
        // on cherche le premier register libre de R2 à maxRegister
        for (int i = 2; i <= maxRegister; i++) {
            if (!registerInUse[i]) {
                registerInUse[i] =true;
                allocatedRegisters.push(i);
                if (i > maxRegisterUsed){ 
                    maxRegisterUsed = i;
                }
                return Register.getR(i);
            }
        }
        return null;
    }



    public void free(GPRegister reg) {
        int regNum = reg.getNumber();
        if(regNum >= 2 && regNum <= maxRegister) {
            if (!registerInUse[regNum]) {
                throw new IllegalStateException("on a essayé de libérer un registe non alloué R" + regNum);
            }
            registerInUse[regNum] = false;

            if (!allocatedRegisters.isEmpty() && allocatedRegisters.peek() == regNum) {
                allocatedRegisters.pop();
            }
        }
    }




    public void setMaxRegister(int max) {
        this.maxRegister = max;
    }

    public void incrementStack() {
        currentStackDepth++;
        if (currentStackDepth > maxStackDepth) {
            maxStackDepth = currentStackDepth;
        }
    }

    public void decrementStack() {
        currentStackDepth--;
    }

    public int getMaxStackDepth() {
        return maxStackDepth;
    }
    public int getCurrentStackDepth(){
        return this.currentStackDepth;
    }
    
    public void reset() {
        this.maxRegisterUsed = 2; 
        this.currentStackDepth = 0;
        this.maxStackDepth = 0;
        initializeRegisters(); 
    }


}