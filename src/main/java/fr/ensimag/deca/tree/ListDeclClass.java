package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.NullOperand;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.deca.context.ClassDefinition;


import org.apache.log4j.Logger;

/**
 *
 * @author gl56
 * @date 01/01/2026
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    // Passe 1 : Déclaration des classes et hiérarchie
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);
        }
    }

    // Passe 2 : Déclaration des champs et signatures des méthodes
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler);
        }
    }
    
    // Passe 3 : Vérification des initialisations et corps de méthodes
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler);
        }
    }

    protected void codeGenListDeclClass(DecacCompiler compiler) {
        Label lab = new Label("init.object");
        compiler.addLabel(lab);
        compiler.addInstruction(new RTS());
        for (AbstractDeclClass c : getList()) {
            c.codeGenDeclClass(compiler);
        }
    }

    protected void codeGenListVTable(DecacCompiler compiler) {


        compiler.addComment("Table des méthodes de Object");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));

        int offset = compiler.getMemoryManager().allocateVariable();
        DAddr addr = new RegisterOffset(offset, Register.GB);

        ClassDefinition objectDef = compiler.environmentType.OBJECT.getDefinition();
        objectDef.setAddrVTable(addr);

        compiler.addInstruction(new STORE(Register.R0, addr));


        LabelOperand labelEquals = new LabelOperand(new Label("code.Object.equals"));

        compiler.addInstruction(new LOAD(labelEquals, Register.R0));
        int nextOffset = compiler.getMemoryManager().allocateVariable();
        DAddr nextAddr = new RegisterOffset(nextOffset, Register.GB);

        compiler.addInstruction(new STORE(Register.R0, nextAddr));

        for (AbstractDeclClass c : getList()) {
            c.codeGenVTable(compiler);
        }
    }
}