import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Runner {

	public static void main(String[] args) {
		// String fileName = args[0];
		String fileName = "factorial.tgr";
		String fullFileText = "";
        String line = null;

	    try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                fullFileText += line + "\n";
            }   
            bufferedReader.close();         
        } catch(FileNotFoundException ex) {
        	System.out.println("Unable to open file '" + fileName + "'");                
        } catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
            // ex.printStackTrace();
        }
	    
	    System.out.println(fullFileText);
	}

}
