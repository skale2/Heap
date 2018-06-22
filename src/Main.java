import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        Interpreter _interpreter;
        try {
            BufferedReader _programText = new BufferedReader(new FileReader(args[0]));
            _interpreter = new Interpreter(_programText);
            _interpreter.run();
        } catch(FileNotFoundException fnfe) {
            // Throw File Not Found exception
            return;
        }
    }
}
