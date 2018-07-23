import Main.Lexer;
import Main.Parser;
import org.json.simple.*;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    void test(String fileName) {
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
                    isResults = false;
                    isProgram = false;
                } else if (line.equals(("// AST"))) {
                    isResults = true;
                    isProgram = false;
                } else if (isProgram && !line.isEmpty())
                    program.append(line);
                else if (isResults && !line.isEmpty())
                    results.append(line);
                line = file.readLine();
            }

            Parser parser = new Parser(
                    new Lexer(
                            new BufferedReader(
                                    new StringReader(
                                            program.toString()
                                    )
                            )
                    )
            );

            String expectedJSONString = results.toString().replaceAll("\\s","");
            JSONObject expectedJSON = (JSONObject) JSONValue.parse(expectedJSONString);

            JSONObject actualJSON = parser.parse().toJSON();

            assertEquals(expectedJSON, actualJSON);

        } catch (FileNotFoundException fnfe) {
            fail("File not found error");
        } catch (IOException io) {
            fail("Error parsing file");
        }
    }

    @Test
    void testAdd() {
        test("/Users/sohamkale/Documents/Heap/Heap/Tests/scripts/add.heap");
    }
}