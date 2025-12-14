import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // command-line overrides
        if (args.length > 0) {
            if ("console".equalsIgnoreCase(args[0])) {
                ConsoleLauncher.run();
                return;
            }
            if ("gui".equalsIgnoreCase(args[0])) {
                SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
                return;
            }
        }

        // If no interactive console is available, open GUI by default
        if (System.console() == null) {
            SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
            return;
        }

        // Ask the user whether to open GUI or console
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.println("Choose interface: (1) GUI  (2) Console");
        String choice = sc.nextLine().trim();
        if (choice.equals("1") || choice.equalsIgnoreCase("gui")) {
            SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
        } else {
            ConsoleLauncher.run();
        }
    }
}

