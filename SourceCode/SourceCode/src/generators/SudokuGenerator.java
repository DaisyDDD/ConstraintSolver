package generators;

import help.Help;

public final class SudokuGenerator {

    /**
     * The constraint is always the same != on 1..9 Only the
     */
    private static void diseqTuples() {
        for (int val1 = 1; val1 <= 9; val1++)
            for (int val2 = 1; val2 <= 9; val2++)
                if (val1 != val2)
                    Help.saveAsFileWriter(val1 + ", " + val2 + "\r\n");
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Usage: java SudokuGenerator");
            return;
        }
        String content = "";

        // System.out.println("//Sudoku.");
        content = "//Sudoku.\r\n";
        Help.saveAsFileWriter(content);
        Help.saveAsFileWriter("\r\n// Always 81 variables:\n" + 81 + "\r\n");
        Help.saveAsFileWriter("\r\n// Domains of the variables: 1..9 (inclusive)\r\n");
        Help.saveAsFileWriter("\r\n// Edit the following to provide clues\r\n");
        for (int i = 0; i < 81; i++) {
            if (i == 12 || i == 28 || i == 52 || i == 69)
                Help.saveAsFileWriter("1, 1\r\n");
            else if (i == 5 || i == 42)
                Help.saveAsFileWriter("2, 2\r\n");
            else if (i == 13)
                Help.saveAsFileWriter("3, 3\r\n");
            else if (i == 66 || i == 80)
                Help.saveAsFileWriter("4, 4\r\n");
            else if (i == 38 || i == 1 || i == 67)
                Help.saveAsFileWriter("5, 5\r\n");
            else if (i == 14 || i == 63 || i == 75)
                Help.saveAsFileWriter("6, 6\r\n");
            else if (i == 79 || i == 0 || i == 35)
                Help.saveAsFileWriter("7, 7\r\n");
            else if (i == 17 || i == 21 || i == 45 || i == 68)
                Help.saveAsFileWriter("8, 8\r\n");
            else if (i == 11 || i == 40 || i == 59)
                Help.saveAsFileWriter("9, 9\r\n");
            else
                Help.saveAsFileWriter("1, 9\r\n");
        }

        Help.saveAsFileWriter("\r\n// constraints (vars indexed from 0, allowed tuples):");

        // Rows
        for (int row = 1; row <= 9; row++) {
            Help.saveAsFileWriter("\r\n//Row: " + row);
            for (int col1 = 1; col1 <= 8; col1++)
                for (int col2 = col1 + 1; col2 <= 9; col2++) {
                    Help.saveAsFileWriter(
                            "\r\nc(" + ((row - 1) * 9 + col1 - 1) + ", " + ((row - 1) * 9 + col2 - 1) + ")\r\n");
                    diseqTuples();
                    // System.out.println();
                }
        }

        // Cols
        for (int col = 1; col <= 9; col++) {
            Help.saveAsFileWriter("//Col: " + col + "\r\n");
            for (int row1 = 1; row1 <= 8; row1++)
                for (int row2 = row1 + 1; row2 <= 9; row2++) {
                    Help.saveAsFileWriter(
                            "c(" + ((row1 - 1) * 9 + col - 1) + ", " + ((row2 - 1) * 9 + col - 1) + ")\r\n");
                    diseqTuples();
                    // System.out.println();
                }
        }

        // 3 x 3 subsquares

        for (int subRow = 1; subRow <= 7; subRow += 3)
            for (int subCol = 1; subCol <= 7; subCol += 3) {
                Help.saveAsFileWriter("//Subsquare starting at row: " + subRow + ", col: " + subCol + "\r\n");
                for (int row1 = subRow; row1 <= subRow + 2; row1++)
                    for (int col1 = subCol; col1 <= subCol + 2; col1++)
                        for (int row2 = row1; row2 <= subRow + 2; row2++)
                            for (int col2 = subCol; col2 <= subCol + 2; col2++) {
                                // break symmetry: only allow cell1 != cell2 where cell1 is less than cell2 in
                                // the row-wise ordering of the subsquare
                                if ((row2 > row1) || (col2 > col1)) {
                                    Help.saveAsFileWriter("c(" + ((row1 - 1) * 9 + col1 - 1) + ", "
                                            + ((row2 - 1) * 9 + col2 - 1) + ")\r\n");
                                    diseqTuples();
                                    // System.out.println();
                                }
                            }
            }
    }
}