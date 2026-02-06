package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;

/**
 * Assembly line, printed as-is.
 * Used for asm("...") method bodies.
 */
public class AsmLine extends AbstractLine {

    private final String asm;

    public AsmLine(String asm) {
        super();
        this.asm = asm;
    }

    @Override
    void display(PrintStream s) {
        s.println(asm);
    }
}
