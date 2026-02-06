package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Super-classe pour la visibilité (Public ou Protected).
 */
public abstract class AbstractVisibility extends Tree {
    
    public abstract void decompile(IndentPrintStream s);
    
 

}