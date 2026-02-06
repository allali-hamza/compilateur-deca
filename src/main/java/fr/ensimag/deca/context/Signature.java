package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl56
 * @date 01/01/2026
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }


    
    public boolean isSameAs(Signature other) {
    if (this.size() != other.size()) {
        return false;
    }
    for (int i = 0; i < this.size(); i++) {
        if (!this.paramNumber(i).sameType(other.paramNumber(i))) {
            return false;
        }
    }
    return true;
}

}
