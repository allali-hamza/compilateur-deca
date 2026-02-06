package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BEQ;


/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }    

    // debut code ajoute
    protected Instruction getSautDirect(Label lb) {
        Instruction saut = new BEQ(lb);
        return saut;
    }
    protected Instruction getSautInsverse(Label lb){
        Instruction saut = new BNE(lb);
        return saut;
    }

    // fin code ajoute

}
