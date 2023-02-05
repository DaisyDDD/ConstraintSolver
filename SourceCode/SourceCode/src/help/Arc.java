package help;

/**
 * A class for Arc between variables in problem solving.
 * 
 * @author 200010781
 *
 */
public class Arc {
    private int first;
    private int second;

    public Arc(int fir, int sec) {
        this.setFirst(fir);
        this.setSecond(sec);
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
