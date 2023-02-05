package solver;

import java.util.ArrayList;
import java.util.LinkedList;

import help.Help;
import help.Variable;
import input.BinaryConstraint;
import input.BinaryTuple;

/**
 * A class for FC method.
 * 
 * @author 200010781
 *
 */
public class FC {

    private ArrayList<Variable> varListDomain; // record variables and their domain
    private static ArrayList<BinaryConstraint> constraints;// constraints of variables
    private ArrayList<Integer> unassigned = new ArrayList();// record id of variables that are unassigned
    private ArrayList<Integer> assigned = new ArrayList();// record id of variables that are assigned
    private int nodes = 0;// record searching nodes number
    private int revisionNum = 0;// record arc revision number
    private long timeStart = 0;// time for starting searching
    private long timeEnd = 0;// time for end searching

    /**
     * A constructor for starting FC searching.
     */
    public FC(ArrayList<Variable> varListDomain, ArrayList<Integer> unassigned,
            ArrayList<BinaryConstraint> constraints) {
        this.varListDomain = varListDomain;
        this.unassigned = unassigned;
        this.constraints = constraints;
        timeStart = System.currentTimeMillis();

        fowardChecking(this.varListDomain);
        // If no solutions found after all searching nodes
        // print No Solutions
        System.out.print("Sorry! No solutions found!");
    }

    /**
     * A method for supporting FC method.
     */
    private void fowardChecking(ArrayList<Variable> varList) {
        nodes++;

        if (completeAssignment()) {
            printSolution();
        }
        Variable var = selectVar(varList);// select variable
        int val = selectVal(var);// select value
        branchFCLeft(varList, var, val);
        branchFCRight(varList, var, val);

    }

    /**
     * A method for selecting a variable in ascending or smallest domain order.
     */
    private Variable selectVar(ArrayList<Variable> varList) {
        // Ascending or Small-domain first
        Variable smallestVar = varList.get(0);
        int smallDomainNum = smallestVar.getDomainNum();

        for (int i = 0; i < varList.size(); i++) {
            // System.out.print("varList: " + varList.get(i).getId() + " ");
            if (varListDomain.get(varList.get(i).getId()).getDomainNum() < smallDomainNum) {
                smallestVar = varListDomain.get(varList.get(i).getId());

                smallDomainNum = smallestVar.getDomainNum();
            }
        }
        return smallestVar;
    }

    /**
     * A method for selecting a value in ascending order.
     *
     */
    private int selectVal(Variable var) {
        this.varListDomain.get(var.getId()).getDomain().sort(null);
        // ascending value ordering
        int val = this.varListDomain.get(var.getId()).getDomain().get(0);

        return val;
    }

    /**
     * A method for supporting left branch searching.
     */
    private void branchFCLeft(ArrayList<Variable> varList, Variable var, int val) {
        ArrayList<Variable> originVarList = Help.copy(varListDomain);
        // System.out.println("Left branching...");
        assign(var, val);

        if (reviseFutureArcs(varList, var)) {
            ArrayList<Variable> newVarList = Help.remove(varList, var);
            fowardChecking(newVarList);
        }
        varListDomain = originVarList;// undo pruning
        unassign(var, val);
    }

    /**
     * A method for supporting right branch searching.
     */
    private void branchFCRight(ArrayList<Variable> varList, Variable var, int val) {
        // System.out.println("Right branching...");
        deleteValue(var, val);

        if (varListDomain.get(var.getId()).getDomainNum() != 0) {
            ArrayList<Variable> originVarList = Help.copy(varListDomain);
            if (reviseFutureArcs(varList, var)) {
                fowardChecking(varList);
            }
            varListDomain = originVarList;
        }
        restoreValue(var, val);
    }

    /**
     * A method to restore value of a variable.
     */
    private void restoreValue(Variable var, int val) {
        this.varListDomain.get(var.getId()).getDomain().add(val);
    }

    /**
     * A method to delete value of a variable in right branching.
     */
    private void deleteValue(Variable var, int val) {
        int index = this.varListDomain.get(var.getId()).getDomain().indexOf(val);
        this.varListDomain.get(var.getId()).getDomain().remove(index);
    }

    /**
     * A method to revise future arcs.
     */
    private boolean reviseFutureArcs(ArrayList<Variable> varList, Variable var) {

        boolean consistent = true;
        for (int i = 0; i < varList.size(); i++) { // *
            Variable futureVar = varList.get(i);

            if (!assigned.contains(futureVar.getId()) && futureVar.getId() != var.getId()) {
                consistent = revise(futureVar, var);
                if (!consistent)
                    return false;
            }
        }
        return true;
    }

    /**
     * A method for revising future variables.
     */
    private boolean revise(Variable futureVar, Variable var) {
        boolean changed = false;
        revisionNum++;
        ArrayList<Integer> futureVarValues = new ArrayList<Integer>(); // record supported values of future variable

        for (BinaryConstraint constraint : constraints) {
            if (constraint.getFirstVar() == var.getId() && constraint.getSecondVar() == futureVar.getId()) {
                changed = true;
                for (BinaryTuple tuple : constraint.getTuples()) {
                    for (int i = 0; i < varListDomain.get(var.getId()).getDomainNum(); i++) {
                        if (tuple.getFirstVal() == varListDomain.get(var.getId()).getDomain().get(i)) {
                            futureVarValues.add(tuple.getSecondVal());

                        }
                    }
                }
            } else if (constraint.getFirstVar() == futureVar.getId() && constraint.getSecondVar() == var.getId()) {
                changed = true;
                for (BinaryTuple tuple : constraint.getTuples()) {
                    for (int i = 0; i < varListDomain.get(var.getId()).getDomainNum(); i++) {
                        if (tuple.getSecondVal() == varListDomain.get(var.getId()).getDomain().get(i)) {
                            futureVarValues.add(tuple.getFirstVal());

                        }
                    }
                }
            }
        }

        if (changed) {
            LinkedList<Integer> futureDomain = (LinkedList<Integer>) varListDomain.get(futureVar.getId()).getDomain()
                    .clone();
            for (int i = 0; i < varListDomain.get(futureVar.getId()).getDomainNum(); i++) {// *
                if (!futureVarValues.contains(varListDomain.get(futureVar.getId()).getDomain().get(i))) {
                    // if there exists unsupported values, remove it.
                    int index = futureDomain.indexOf(varListDomain.get(futureVar.getId()).getDomain().get(i));// *
                    futureDomain.remove(index);// *
                }
            }

            varListDomain.get(futureVar.getId()).setDomain(futureDomain);// update future variable's domain
            if (futureDomain.isEmpty()) {
                // System.out.println("revise future var: " + futureVar.getId() + "futureDomain
                // empty!");
                return false;
            } else {
                return true;
            }
        } else
            return true;

    }

    /**
     * A method to assign a value to a variable.
     * 
     */
    private void assign(Variable var, int val) {
        assigned.add(var.getId());

        varListDomain.get(var.getId()).getDomain().clear();
        varListDomain.get(var.getId()).getDomain().add(val);
    }

    /**
     * A method to unassign a value of a variable.
     * 
     */
    private void unassign(Variable var, int val) {
        int index = assigned.indexOf(var.getId());
        assigned.remove(index);
        // System.out.println("Unassigned Var : " + var.getId() + ", Unassigned Value: "
        // + val);
    }

    /**
     * A method for printing solutions.
     */
    private void printSolution() {
        timeEnd = System.currentTimeMillis();
        System.out.println();
        System.out.println("....Solution Found.....");
        for (int i = 0; i < varListDomain.size(); i++) {
            System.out.print("Var " + i + ": " + varListDomain.get(i).getDomain() + "\n");
        }
        System.out.println("Total Nodes: " + nodes);
        System.out.println("Arc revisions: " + revisionNum);
        System.out.println("Time spend: " + (timeEnd - timeStart) + " ms");
        System.exit(0);

    }

    /**
     * A method to check if assignment is finished or not.
     */
    private boolean completeAssignment() {
        if (assigned.size() == varListDomain.size()) {

            return true;
        } else {
            return false;
        }
    }
}
