package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * @author gl56
 * @date 01/01/2026
 */
public class Print extends AbstractPrint {
    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printx)
     */
    public Print(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    String getSuffix() {
        return "Print";
    }
    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not yet implemented");
        // { r := (x ? ’printx(’ : ’print(’).es .’);’}
        if(getPrintHex()){
            s.print("printx(");
        }
        else{
            s.print("print(");

        }
        getArguments().decompile(s);
        s.print(");");
    }
}
