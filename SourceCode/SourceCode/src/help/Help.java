package help;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * A Help class for supporting some auxiliary methods.
 * 
 * @author 200010781
 *
 */
public class Help {
    private static String filePath = "C:\\Users\\DaisyDai\\Desktop\\JAVA\\java-practice\\CS4402-p2\\instances\\newGenerators";

    public static Variable deepclone(Variable var)
            throws CloneNotSupportedException, ClassNotFoundException, IOException {
        // A method for deep clone variable object
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(var);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        Variable varClone = (Variable) in.readObject();
        return varClone;

    }

    public static ArrayList<Variable> copy(ArrayList<Variable> varList) {
        // A method for copy the ArrayList with type of Variable
        ArrayList<Variable> newVarList = new ArrayList<>();
        for (Variable v : varList) {
            try {
                newVarList.add(Help.deepclone(v));

            } catch (ClassNotFoundException | CloneNotSupportedException | IOException e) {
                e.printStackTrace();
            }
        }
        return newVarList;
    }

    public static ArrayList<Variable> remove(ArrayList<Variable> varList, Variable var) {
        // A method for copy the ArrayList with type of Variable without a Variable var
        ArrayList<Variable> newVarList = new ArrayList<>();
        for (Variable v : varList) {
            try {
                if (v.getId() != var.getId()) { // varList-var
                    newVarList.add(Help.deepclone(v));
                }
            } catch (ClassNotFoundException | CloneNotSupportedException | IOException e) {
                e.printStackTrace();
            }
        }
        return newVarList;
    }

    public static void saveAsFileWriter(String content) {
        // A method for write instances into txt file.
        FileWriter fwriter = null;
        try {

            fwriter = new FileWriter(filePath + "\\11Queens.txt", true);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
