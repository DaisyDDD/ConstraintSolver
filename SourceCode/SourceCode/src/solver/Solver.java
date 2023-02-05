package solver;

import java.util.ArrayList;
import java.util.Scanner;

import help.Variable;
import input.BinaryCSP;
import input.BinaryCSPReader;
import input.BinaryConstraint;

/**
 * A solver class contains main method.
 * 
 * @author 200010781
 *
 */
public class Solver {

    private static int[][] domain;
    private static ArrayList<BinaryConstraint> constraints;
    private static ArrayList<Integer> unassigned = new ArrayList();
    private static ArrayList<Variable> varListDomain = new ArrayList<Variable>();

    public static void main(String[] args) {

        BinaryCSPReader reader = new BinaryCSPReader();
        BinaryCSP binaryCsp = reader.readBinaryCSP(args[0]);
        constraints = binaryCsp.getConstraints();
        domain = reader.getDomainBounds();// int [][]

        for (int i = 0; i < domain.length; i++) {
            Variable variable = new Variable(domain[i]);
            variable.setId(i);
            varListDomain.add(variable);
            unassigned.add(i);
        }

        System.out.println("Choose method you wish to search.....");
        System.out.println("1. Forward Checking");
        System.out.println("2. Maintaining Arc Consistency \n");

        System.out.println("Please input:");
        Scanner input = new Scanner(System.in);// get user input for choosing searching method
        int method = input.nextInt();

        switch (method) {
        case 1: // Forward checking
            System.out.println("Forward Checking Searching......");
            FC fc = new FC(varListDomain, unassigned, constraints);
            break;
        case 2: // Maintaining Arc consistency
            System.out.println("Maintaining Arc Consistency Searching......");
            MAC mac = new MAC(varListDomain, unassigned, constraints);
            break;
        default:
            System.out.println("Method not supported. Please input \"1\" or \"2\".");
            break;
        }
    }

    public int[][] getDomain() {
        return domain;
    }

    public void setDomain(int[][] domain) {
        this.domain = domain;
    }
}
