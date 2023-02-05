package solver;

import java.util.ArrayList;
import java.util.LinkedList;

import help.Arc;
import help.Help;
import help.Variable;
import input.BinaryConstraint;
import input.BinaryTuple;

/**
 * A class for MAC method.
 * 
 * @author 200010781
 *
 */
public class MAC {
    private ArrayList<Integer> assigned = new ArrayList<Integer>(); // record id of variables that are assigned
    private ArrayList<Integer> unassigned = new ArrayList<Integer>(); // record id of variables that are unassigned
    private LinkedList<Arc> queue = new LinkedList<>(); // record the arc that need to be checked
    private static ArrayList<BinaryConstraint> constraints;// constraints of variables
    private ArrayList<Variable> varListDomain; // record variables and their domain
    private boolean emptyDomain = false; // check whether arc revising leads to empty domain of a variable
    private int nodes = 0; // record searching nodes number
    private int revisionNum = 0;// record arc revision number
    private long timeStart = 0; // time for starting searching
    private long timeEnd = 0; // time for end searching

    /**
     * A constructor for starting MAC searching.
     */
    public MAC(ArrayList<Variable> varListDomain, ArrayList<Integer> unassigned,
            ArrayList<BinaryConstraint> constraints) {
        this.varListDomain = varListDomain;
        this.unassigned = unassigned;
        this.constraints = constraints;

        initualQueue();// Initial the Queue
        AC3();// Reach Arc-Consistency before MAC searching

        timeStart = System.currentTimeMillis();
        matainArcConsistency(this.varListDomain);
        // If no solutions found after all searching nodes
        // print No Solutions
        System.out.print("Sorry! No solutions found!");

    }

    /**
     * A method for supporting MAC method.
     */
    private void matainArcConsistency(ArrayList<Variable> varList) {
        if (completeAssignment()) {
            printSolution();
        }
        nodes++;
        emptyDomain = false;

        Variable var = selectVar(varList);// select variable
        int val = selectVal(var);// select value

        // .....Left Branching.....
        assign(var, val);

        ArrayList<Variable> originVarListLeft = Help.copy(varListDomain);// clone to undoPruning
        if (AC3Left(var.getId(), val)) {
            ArrayList<Variable> newVarList = Help.remove(varList, var);
            matainArcConsistency(newVarList);
        }
        varListDomain = originVarListLeft;// undo pruning
        unassign(var, val);

        // .....Right Branching.....
        int index = varListDomain.get(var.getId()).getDomain().indexOf(val);
        varListDomain.get(var.getId()).getDomain().remove(index);

        emptyDomain = false;
        if (varListDomain.get(var.getId()).getDomainNum() != 0) {
            ArrayList<Variable> originVarListRight = Help.copy(varListDomain);// clone to undoPruning
            if (AC3Right(var.getId())) {
                matainArcConsistency(varList);
            }
            varListDomain = originVarListRight;// undo pruning
        }
        varListDomain.get(var.getId()).getDomain().add(val);
    }

    /**
     * A method for supporting right branch AC3.
     * 
     */
    private boolean AC3Right(int varId) {

        AC3Start(varId);
        AC3();
        if (emptyDomain) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * A method for supporting left branch AC3.
     */
    private boolean AC3Left(int varId, int val) {

        if (varListDomain.get(varId).getDomainNum() == 1) {
            return true;
        }
        varListDomain.get(varId).getDomain().clear();
        varListDomain.get(varId).getDomain().addLast(val);

        AC3Start(varId);// add initial arcs into Queue
        AC3();// arc revising

        if (emptyDomain) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * A method for add initial arcs into Queue for left and right branching.
     * 
     */
    private void AC3Start(int varId) {
        for (BinaryConstraint constraint : constraints) {
            int firstConId = constraint.getFirstVar();
            int secondConId = constraint.getSecondVar();

            if (firstConId == varId) {
                Arc arc = new Arc(secondConId, firstConId);
                if (!checkContains(arc) && !assigned.contains(secondConId)) {
                    queue.addLast(arc);
                }
            } else if (secondConId == varId) {
                Arc arc = new Arc(firstConId, secondConId);
                if (!checkContains(arc) && !assigned.contains(firstConId)) {
                    queue.addLast(arc);
                }

            }
        }

    }

    /**
     * A method for doing arc consistency.
     */
    private void AC3() {

        while (queue.size() != 0) {
            if (emptyDomain) {
                return;
            }

            Arc head = queue.remove();

            if (revise(head)) { // if there is changing happens

                int firstHeadId = head.getFirst();// target
                int secondHeadId = head.getSecond();
                for (BinaryConstraint constraint : constraints) {
                    int firstConId = constraint.getFirstVar();
                    int secondConId = constraint.getSecondVar();

                    if (firstConId == firstHeadId) {
                        if (secondConId != secondHeadId) {
                            Arc arc = new Arc(secondConId, firstConId);
                            if (!checkContains(arc) && !assigned.contains(secondConId)) {
                                queue.addLast(arc);

                            }
                        }
                    } else if (secondConId == firstHeadId) {
                        if (firstConId != secondHeadId) {
                            Arc arc = new Arc(firstConId, secondConId);
                            if (!checkContains(arc) && !assigned.contains(firstConId)) {
                                queue.addLast(arc);

                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * A method for initial Queue before MAC searching.
     */
    private void initualQueue() {
        for (BinaryConstraint constraint : constraints) {
            Arc arc1 = new Arc(constraint.getFirstVar(), constraint.getSecondVar());
            Arc arc2 = new Arc(constraint.getSecondVar(), constraint.getFirstVar());

            queue.addLast(arc1);
            queue.addLast(arc2);

        }

    }

    /**
     * A method for checking whether an arc is already in the Queue or not.
     * 
     */
    private boolean checkContains(Arc arc) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getFirst() == arc.getFirst() && queue.get(i).getSecond() == arc.getSecond()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A method for revising arc.
     * 
     * @param head Head arc in the queue.
     * 
     */
    private boolean revise(Arc head) {
        boolean changed = false;
        int varId1 = head.getFirst();
        int varId2 = head.getSecond();
        revisionNum++;
        for (int i = 0; i < varListDomain.get(varId1).getDomainNum(); i++) {
            boolean supported = false;
            int di = varListDomain.get(varId1).getDomain().get(i);

            for (int j = 0; j < varListDomain.get(varId2).getDomainNum(); j++) {

                int dj = varListDomain.get(varId2).getDomain().get(j);
                for (BinaryConstraint constraint : constraints) {
                    if (constraint.getFirstVar() == varId1 && constraint.getSecondVar() == varId2) {
                        for (BinaryTuple tuple : constraint.getTuples()) {
                            if (tuple.getFirstVal() == di && tuple.getSecondVal() == dj) {
                                supported = true;
                                break;
                            }
                        }
                    } else if (constraint.getFirstVar() == varId2 && constraint.getSecondVar() == varId1) {
                        for (BinaryTuple tuple : constraint.getTuples()) {
                            if (tuple.getFirstVal() == dj && tuple.getSecondVal() == di) {
                                supported = true;
                                break;
                            }
                        }
                    }
                }

                if (supported)
                    break;
            }
            if (!supported) { // remove value
                int index = varListDomain.get(varId1).getDomain().indexOf(di);
                varListDomain.get(varId1).getDomain().remove(index);
                i--;
                changed = true;
            }
        }
        if (varListDomain.get(varId1).getDomainNum() == 0) {// fail
            fail();
            return false;
        }
        return changed;
    }

    /**
     * A method to call if a variable domain is empty.
     */
    private void fail() {
        emptyDomain = true;
        queue.clear();
    }

    /**
     * A method to unassign a value of a variable.
     * 
     */
    private void unassign(Variable var, int val) {
        assigned.remove(assigned.indexOf(var.getId()));
        unassigned.add(var.getId());
        // System.out.println("Unassigned Var : " + var.getId() + ", Unassigned Value: "
        // + val);
    }

    /**
     * A method to assign a value to a variable.
     * 
     */
    private void assign(Variable var, int val) {
        assigned.add(var.getId());
        unassigned.remove(unassigned.indexOf(var.getId()));
        // System.out.println("Assigned Var : " + var.getId() + ", Assigned Value: " +
        // val);
    }

    /**
     * A method for selecting a variable in ascending or smallest domain order.
     */
    private Variable selectVar(ArrayList<Variable> varList) {

        Variable var = varList.get(0);
        int smallestDomain = var.getDomainNum();
        // System.out.println("First in varList Domain: " + smallestDomain);

        for (int i = 0; i < varList.size(); i++) {
            if (varListDomain.get(varList.get(i).getId()).getDomainNum() < smallestDomain) {
                var = varListDomain.get(varList.get(i).getId());
                smallestDomain = var.getDomainNum();
                // System.out.println("smallestDomain: " + smallestDomain);
            }
        }
        return var;

    }

    /**
     * A method for selecting a value in ascending order.
     *
     */
    private int selectVal(Variable var) {
        this.varListDomain.get(var.getId()).getDomain().sort(null);// assending order
        int val = this.varListDomain.get(var.getId()).getDomain().get(0);
        return val;
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
        if (unassigned.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

}
