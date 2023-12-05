import java.io.IOException;

public class utils {

    // Function for clean the Shell in Linux and Windows
    public static void clean() {

        // Obtein SO name
        String so = System.getProperty("os.name");
        // Return: Windows 10

        // Clean for windows
        if (so.contains("Windows")) {

            // Clean the Shell
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {

            // Clean the Shell in Linux SO
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
                ;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        
    }
}
