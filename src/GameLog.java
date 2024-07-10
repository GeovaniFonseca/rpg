import java.util.ArrayList;
import java.util.List;

public class GameLog {
    private static List<String> logs = new ArrayList<>();

    public static synchronized void addLog(String log) {
        logs.add(log);
        System.out.println(log);  // Também exibe o log no console do servidor
    }

    public static synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}
