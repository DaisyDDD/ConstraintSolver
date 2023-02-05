Author: Yujie Dai
This archive contains source code, and instances for Constraint Programming practical 2.


<1>Source code structure
============================
4 packages are included in the /src directory.

1.  solver package
     This package includes two algorithms, FC and MAC, and a Solver class with main method.

2. input package 
     This package includes 4 classes provided by lectures for supporting CSP file read.

3. help package
    This package includes some auxiliary classes and methods. Variable class refers to the variables in
    problem solving. Arc class represents the arc between two variables. Help class includes some auxiliary
    methods to support deep copy and write instances into txt file.

4. generators package 
    This package includes three problem generators to create various instances.


<2>Instances provided
=================================
All instances can be found in /instances directory. 
14 new generated instances can be found inside instances/newGenerators directory.


<3>Instructions of running this project in windows
==================================
1. Enter the terminal.

2. Change to the CS4402-P2 directory.

3. Change to src directory.

4. Compile all source code.
    javac help/*.java input/*.java solver/*.java

5. Run this project.
   java solver/Solver <file.csp>

6. Input the algorithm you want to use.
   1 for FC and 2 for MAC.


For example, when using MAC to search a solution for 4Queens problem.
You should use:  java solver/Solver ../instances/4Queens.csp
Then input 2 in cmd.
