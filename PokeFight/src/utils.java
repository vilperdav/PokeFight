import java.io.IOException;

public class utils {

    public static void clean(String so) {

        // Clean for windows
        if (so.equals("W")) {

            // Clean the Shell
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {

            // TODO - CLEAN IN LINUX IF NECESSARY

        }
    }
}
