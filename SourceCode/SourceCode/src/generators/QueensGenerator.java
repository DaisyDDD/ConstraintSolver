package generators;

import help.Help;

public final class QueensGenerator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java QueensGenerator <n>");
            return;
        }
        int n = Integer.parseInt(args[0]);
        Help.saveAsFileWriter("//" + n + "-Queens.\r\n");
        Help.saveAsFileWriter("\n// Number of variables:\n" + n + "\r\n");
        Help.saveAsFileWriter("\n// Domains of the variables: 0.. (inclusive)\r\n");
        for (int i = 0; i < n; i++)
            Help.saveAsFileWriter("0, " + (n - 1) + "\r\n");
        Help.saveAsFileWriter("\n// constraints (vars indexed from 0, allowed tuples):\r\n");

        for (int row1 = 0; row1 < n - 1; row1++)
            for (int row2 = row1 + 1; row2 < n; row2++) {
                Help.saveAsFileWriter("c(" + row1 + ", " + row2 + ")\r\n");
                for (int col1 = 0; col1 < n; col1++)
                    for (int col2 = 0; col2 < n; col2++) {
                        if ((col1 != col2) && (Math.abs(col1 - col2) != (row2 - row1))) {
                            Help.saveAsFileWriter(col1 + ", " + col2 + "\r\n");
                        }
                    }
                Help.saveAsFileWriter("\r\n");
            }
    }
}