package max.vanach.lesson_1;

public class GamePrompt {
    /**
     * Display starting prompt with listed game options.
     */
    public void StartingPrompt(){
        System.out.println("Main Menu\n"
                + "1-Chose Game mode.\n"
                + "2-exit game.");
    }

    /**
     * Display single player mode prompt with listed game options.
     */
    public void singlePlayerModePrompt(){
        System.out.println("Single player Mode\n"
                + "1-Start standard round of Guessing game with computer.\n"
                + "In this version player try to guess computer's guess.\n"
                + "2-Start reverse round of Guessing game with computer.\n"
                + "In this version you enter your guess and site back and watch computer trying to find your number.\n"
                + "3-Start mixed round.\n"
                + "In this version you and computer try to guess yours numbers in turns. (Winner is whoever guessed first)\n"
                + "4-Change difficulty level.\n"
                + "5-Show statistics.\n"
                + "6-Go back to main menu.");
    }

    /**
     * Display multiplayer mode prompt with listed game options.
     */
    public void multiPlayerModePrompt(){
        System.out.println("Multiplayer mode\n"
                + "1-Start Duel.\n"
                + "In this version you and computer try to guess yours numbers in turns. (Winner is whoever guessed first)\n"
                + "2-Play Tournament\n"
                + "3-Change difficulty level. - not available yet\n"
                + "4-Show statistics.\n"
                + "5-Go back to main menu.");
    }
}
