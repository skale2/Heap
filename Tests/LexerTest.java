import org.codehaus.groovy.util.CharSequenceReader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private void test(String fileName) {
        try {

            BufferedReader file = new BufferedReader(new FileReader(fileName));
            StringBuilder program = new StringBuilder();
            StringBuilder results = new StringBuilder();

            String line = file.readLine();
            boolean isProgram, isResults;
            isProgram = isResults = false;

            while (line != null) {
                if (line.equals("// Script")) {
                    isProgram = true;
                    isResults = false;
                } else if (line.equals("// Tokens")) {
                    isResults = true;
                    isProgram = false;
                } else if (isProgram && !line.isEmpty())
                    program.append(line);
                else if (isResults && !line.isEmpty())
                    results.append(line);
                line = file.readLine();
            }

            Lexer lexer = new Lexer(new BufferedReader(new CharSequenceReader(program)));

            String[] expectedTokens = results.toString().split(",\\s*");

            for (String token : expectedTokens) {
                assertEquals(token, lexer.next().toString());
            }

        } catch (FileNotFoundException fnfe) {
            fail("File not found error");
        } catch (IOException io) {
            fail("Error parsing file");
        }
    }

    @Test
    void testAdd() {
        test("/Users/sohamkale/Documents/Heap/Tests/scripts/add.heap");
    }

    @Test
    void testBinaryOps() {
        test("/Users/sohamkale/Documents/Heap/Tests/scripts/binaryops.heap");
    }

    @Test
    void testIfStatements() {
        test("/Users/sohamkale/Documents/Heap/Tests/scripts/ifstatements.heap");
    }

    @Test
    void testContainers() {
        test("/Users/sohamkale/Documents/Heap/Tests/scripts/containers.heap");
    }

    @Test
    void testClasses() {
        test("/Users/sohamkale/Documents/Heap/Tests/scripts/classes.heap");
    }
}