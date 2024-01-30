package max.vanach.lesson_1;

import java.util.HashMap;
import java.util.Map;

public class GameScore {
    private Map<String, Integer> currentScoresValues;

    public GameScore(){
        this.currentScoresValues = new HashMap<String, Integer>();

        this.currentScoresValues.put("multiWins", 0);
        this.currentScoresValues.put("multiLosses", 0);
        this.currentScoresValues.put("easyWins", 0);
        this.currentScoresValues.put("mediumWins", 0);
        this.currentScoresValues.put("hardWins", 0);
        this.currentScoresValues.put("chaosWins", 0);
    }

    public Map<String, Integer> getCurrentScoresValues() {return this.currentScoresValues;}

    public void updateScores(String gameDifficulty, GameMode gameMode,boolean win) {
        String keyName = null;

        if(gameMode.equals(GameMode.Multiplayer)) {
            if(win) {
                keyName = "multiWins";
            }
            else {
                keyName = "multiLosses";
            }
            this.currentScoresValues.compute(keyName, (key, value) -> value + 1);
        }

        this.updateScoresBaseOnDifficulty(gameDifficulty, win);
    }

    private void updateScoresBaseOnDifficulty(String gameDifficulty, boolean win){
        String keyName = null;

        if (win) {
            if (gameDifficulty.equals(DifficultyLevel.Easy.name())) {
                keyName = "easyWins";
            }
            else if (gameDifficulty.equals(DifficultyLevel.Medium.name())) {
                keyName = "mediumWins";
            }
            else if (gameDifficulty.equals(DifficultyLevel.Hard.name())) {
                keyName = "hardWins";
            }
            else if (gameDifficulty.equals(DifficultyLevel.Chaos.name())) {
                keyName = "chaosWins";
            }

            this.currentScoresValues.compute(keyName, (key, value) -> value + 1);
        }
    }
}
