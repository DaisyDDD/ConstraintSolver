package help;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * A class for Variables in problem solving.
 * 
 * @author 200010781
 *
 */
public class Variable implements Cloneable, Serializable {

    private int id;
    private LinkedList<Integer> domain;

    public Variable(int[] domainBounds) {
        domain = new LinkedList<Integer>();
        int start = domainBounds[0];
        int end = domainBounds[1];
        for (int i = start; i <= end; i++) {
            domain.addLast(i);
        }
    }

    public LinkedList<Integer> getDomain() {
        return domain;
    }

    public void setDomain(LinkedList<Integer> domain) {
        this.domain = domain;
    }

    public int getDomainNum() {
        return domain.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
