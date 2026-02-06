package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.BGE;



/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }

    // debut code ajoute
    protected Instruction getSautDirect(Label lb) {
        Instruction saut = new BLT(lb);
        return saut;
    }
    protected Instruction getSautInsverse(Label lb){
        Instruction saut = new BGE(lb);
        return saut;
    }

    // fin code ajoute

}
