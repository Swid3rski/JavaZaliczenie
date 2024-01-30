package max.vanach.lesson_1;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PlayerFileOperation {
    private final String[][] mapOfScoreLines= {
            {"Best score (Easy): ", "easyWins"},
            {"Best score (Medium): ", "mediumWins"},
            {"Best score (Hard): ", "hardWins"},
            {"Best score (Chaos): ", "chaosWins"},
            {"Overall wins: ", "multiWins"},
            {"Overall losses: ", "multiLosses"},
    };


    public void updatePlayerFile(Gamer player, Map<String, Integer> currentGameScore) throws IOException {
        Map<String, Integer> bestGameScores = this.calculateBestScores(player.getPath(), currentGameScore);
        try (PrintWriter printWriter = new PrintWriter(player.getPath())) {

            printWriter.println("Nick: " + player.getNick());
            printWriter.println("Title: " + player.getTitle());
            printWriter.println("Tournament Title: " + player.getTournamentTitle());
            printWriter.println("Statistics from single-player mode:");
            printWriter.printf("Best score (Easy): %d%n", bestGameScores.get("easyWins"));
            printWriter.printf("Best score (Medium): %d%n", bestGameScores.get("mediumWins"));
            printWriter.printf("Best score (Hard): %d%n", bestGameScores.get("hardWins"));
            printWriter.printf("Best score (Chaos): %d%n", bestGameScores.get("chaosWins"));
            printWriter.println("Statistics from multiplayer mode:");
            printWriter.printf("Overall wins: %d%n", bestGameScores.get("multiWins"));
            printWriter.printf("Overall losses: %d%n", bestGameScores.get("multiLosses"));

        }
    }

    public void displayFileContent(String path) throws IOException{
        try (Scanner printReader = new Scanner(new File(path))) {
            while (printReader.hasNextLine()) {
                System.out.println(printReader.nextLine());
            }
        }
    }

    public Map<String, Integer> calculateBestScores(String path, Map<String, Integer> currentGameScores) throws IOException{
        Map<String, Integer> bestGameScores = new HashMap<>();
        Map<String, Integer> currentFileScores = this.getPlayerScoresFromFile(path);

        for (String key : currentGameScores.keySet()) {
            if (key.contains("multi")) {
                bestGameScores.put(key, currentFileScores.get(key) + currentGameScores.get(key));
            }
            else {
                bestGameScores.put(key, Math.max(currentFileScores.get(key), currentGameScores.get(key)));
            }
        }

        return  bestGameScores;
    }

    /**
     * Create file for new player with initial data
     * @throws IOException
     */
    public void createPlayerFileIfDoesNotExist(Gamer player) throws IOException {
        var file = new File(player.getPath());
        if (!file.exists()){
            file.createNewFile();
            this.playerFileInitialData(player);
        }
    }

    public String getPlayerTitle(String path) {
        String title = GamerTitles.Loser.name();

        if (!new File(path).exists()){
            title =  GamerTitles.Loser.name();
        }
        else {
            try (Scanner fileReader = new Scanner(new File(path))) {

                while (fileReader.hasNextLine()) {
                    String line = fileReader.nextLine();

                    if (line.startsWith("Title: ")) {
                        title = line.split("Title: " )[1].trim();
                    }
                }
            }
            catch (IOException e) {
                System.err.println(e.getStackTrace());
            }
        }

        return title;
    }

    public String getPlayerTournamentTitle(String path) {
        String title = GamerTournamentTitles.Loser.name();

        if (!new File(path).exists()){
            title =  GamerTournamentTitles.Loser.name();
        }
        else {
            try (Scanner fileReader = new Scanner(new File(path))) {

                while (fileReader.hasNextLine()) {
                    String line = fileReader.nextLine();

                    if (line.startsWith("Tournament Title: ")) {
                        title = line.split("Tournament Title: ")[1].trim();
                    }
                }
            }
            catch (IOException e) {
                System.err.println(e.getStackTrace());
            }
        }

        return title;
    }

    /**
     * Read file to find player best score
     * @return HashMap<String, Integer> playerScoresFromFile containing scores values currently inside file
     * @throws IOException
     */
    public Map<String, Integer> getPlayerScoresFromFile(String path) throws IOException{
        var playerScoresFromFile = new  HashMap<String, Integer>();


        try (Scanner fileReader = new Scanner(new File(path))) {

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                String key = this.mapLineToScore(line);

                if (!key.isEmpty()){
                    playerScoresFromFile.put(key, Integer.parseInt(line.split(": ")[1]));
                }
            }
        }

        return playerScoresFromFile;
    }

    /**
     * Map file lines with score value to a key inside score map object
     * @param line represent line inside a file
     * @return key for correct score inside score hashMap object
     */
    private String mapLineToScore(String line) {
        String key = "";
        int index = 0;
        while (index < this.mapOfScoreLines.length) {
            if (line.startsWith(this.mapOfScoreLines[index][0])){
                key = this.mapOfScoreLines[index][1];
                break;
            }

            index++;
        }
        return key;
    }

    /**
     * Write initial data to the file
     * @throws IOException
     */
    private void playerFileInitialData(Gamer player) throws IOException {
        int initialScore = 0;
        try (PrintWriter printWriter = new PrintWriter(player.getPath())){

            printWriter.println("Nick: " + player.getNick());
            printWriter.println("Title: " + player.getTitle());
            printWriter.println("Tournament Title: " + player.getTournamentTitle());
            printWriter.println("Statistics from single-player mode:");
            printWriter.printf("Best score (Easy): %d%n", initialScore);
            printWriter.printf("Best score (Medium): %d%n", initialScore);
            printWriter.printf("Best score (Hard): %d%n", initialScore);
            printWriter.printf("Best score (Chaos): %d%n", initialScore);
            printWriter.println("Statistics from multiplayer mode:");
            printWriter.printf("Overall wins: %d%n", initialScore);
            printWriter.printf("Overall losses: %d%n", initialScore);
        }
    }
}
