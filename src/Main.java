package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        BufferedReader _program;
        try {
            _program = new BufferedReader(new FileReader(args[0]));
        } catch(FileNotFoundException fnfe) {
            // Throw File Not Found exception
            return;
        }
    }
}
