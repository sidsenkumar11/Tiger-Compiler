/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package tiger.compiler;

import org.junit.jupiter.api.Test;
import tiger.compiler.parser.ParseException;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class AppTest {

    void compareAstsFromFolder(String folderName) {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "bin", "test", folderName);
        File folder = filePath.toFile();
        File[] listOfFiles = folder.listFiles();

        var success = true;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile()) {
                continue;
            }

            var sourceFile = listOfFiles[i].getName();
            if (!sourceFile.endsWith(".tgr")) {
                continue;
            }

            // if (!sourceFile.endsWith("func_ret.tgr")) {
            // continue;
            // }

            var sourceFilePath = listOfFiles[i].getAbsolutePath();
            var astFilePath = sourceFilePath.replace(".tgr", ".ast");
            try {
                System.err.println(sourceFile);
                var astString = App.ParseString(sourceFilePath).strip();
                var solnString = Files.readString(Path.of(astFilePath)).strip();
                // System.err.println(astString);
                if (!astString.equals(solnString)) {
                    System.err.println("--> FAIL - Did not match");
                    success = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } catch (ParseException e) {
                System.err.println(
                        "--> FAIL - Unable to parse");
                success = false;
            }
        }

        assertTrue(success);
    }

    void typeCheckTests(String folderName) {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path filePath = Paths.get(currentPath.toString(), "bin", "test", folderName);
        File folder = filePath.toFile();
        File[] listOfFiles = folder.listFiles();

        var success = true;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile()) {
                continue;
            }

            var sourceFile = listOfFiles[i].getName();
            if (!sourceFile.endsWith(".tgr")) {
                continue;
            }

            // if (!sourceFile.endsWith("func_ret.tgr")) {
            // continue;
            // }

            var sourceFilePath = listOfFiles[i].getAbsolutePath();
            var shouldFail = sourceFile.endsWith("_bad.tgr");
            try {
                System.err.println(sourceFile);
                var astString = App.ParseString(sourceFilePath).strip();
                if (shouldFail) {
                    System.err.println("--> FAIL - Should have failed type checking");
                    success = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } catch (ParseException e) {
                if (!shouldFail) {
                    System.err.println(
                            "--> FAIL - Unable to parse");
                    success = false;
                }
            }
        }

        assertTrue(success);
    }

    @Test
    void p1tests() {
        compareAstsFromFolder("p1tests");
    }

    @Test
    void p1gradedtests() {
        compareAstsFromFolder("p1gradedtests");
    }

    @Test
    void p2tests() {
        typeCheckTests("p2tests");
    }

    @Test
    void p3tests() {
        // runTestsFromFolder("p3tests");
    }
}
