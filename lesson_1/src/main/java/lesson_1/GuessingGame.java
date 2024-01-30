package max.vanach.lesson_1;

import java.io.*;
import java.util.*;


public class GuessingGame {

    private static GamePrompt gamePrompt;
    private boolean exit = false;
    private Random random;
    private Scanner scanner;
    private String gameDifficulty;
    private GameMode gameMode;
    private int upperGuessBound;
    private int lowerGuessBound;
    private PlayerFileOperation fileOperation;
    private List<Gamer> players;
    private Gamer computer;
    private Gamer player1;
    private List<String> playersNicks = new ArrayList<>();
    private int fistPlayerIndex;
    private int maxNumberOfPlayers = 5;
    private boolean isTournament;

    /**
     * Create new GuessingGame object that represent game
     * in witch player can guess number against computer
     */
    public GuessingGame(){
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        gamePrompt = new GamePrompt();
        this.player1 = null;
        this.isTournament = false;

        this.gameDifficulty = "None";
        this.fileOperation = new PlayerFileOperation();
        this.players = new ArrayList<>();
        this.players.add(this.createComputer());
    }

    /**
     * Start whole game.
     */
    public void run(){
        try{
            //this.choseGameplayType();

            this.mainMenu();
        }
        catch (IOException e){
            System.err.format("createFile error: %s%n", e);
            // for debug proposes
            e.printStackTrace();
        }
        finally {
            this.closeNecessaryResources();
        }
    }

    /**
     * close all resources needed to be close
     * if something go wrong and program close because of unexpected error.
     */
    private void closeNecessaryResources(){
        this.scanner.close();
    }

    /**
     * Create Gamer object representing players playing in single player mode
     */
    private void createPlayersForSinglePlayer() {
        if(this.players.size() < 2){
            this.players.add(this.createPlayer());
            this.setImportantPlayers();
        }
        System.out.printf("Player1 is %s%n", this.player1.getNick());
    }

    /**
     * Create number of players chosen by user.
     */
    private void createMultiplePlayers() {
        String userNumber;
        int numberOfPlayers = 0;
        boolean isNumberDecide = false;
        int maxNumberOfPlayersLeftToCreate = this.maxNumberOfPlayers - this.players.size();// max players is 4 with computer 5
        while (!isNumberDecide && maxNumberOfPlayersLeftToCreate != 0) {
            System.out.printf("Enter number of players to create (maximum = %d).%n", maxNumberOfPlayersLeftToCreate);
            System.out.print("Type number: " );
            userNumber = this.getUserInput();
            System.out.println();

            try {
                numberOfPlayers = Integer.parseInt(userNumber);
            }
            catch (NumberFormatException e) {
                System.err.println("Error occur please enter correct number of players.");
                e.printStackTrace();
            }

            if (numberOfPlayers < 2 || numberOfPlayers > 4) {
                System.out.println("Incorrect number of players.");
            }
            else {
                isNumberDecide = true;
            }
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            this.players.add(this.createPlayer());
        }

        this.setImportantPlayers();
    }

    /**
     * Create Gamer object representing computer.
     * @return Gamer object representing computer
     */
    private Gamer createComputer(){
        Gamer computer = new Gamer("computer");
        this.playersNicks.add(computer.getNick());

        try {
            this.fileOperation.createPlayerFileIfDoesNotExist(computer);
        }
        catch (IOException e){
            System.err.format("createFile error: %s%n", e);
            // for debug proposes
            e.printStackTrace();
        }

        return computer;
    }

    /**
     * Create single Gamer object with chosen nickname.
     * @return Gamer object representing newly created player.
     */
    private Gamer createPlayer(){
        boolean isCreated = false;
        Gamer player = new Gamer("computer");

        while (!isCreated) {
            String nick;
            System.out.print("Enter your Nickname: ");
            nick = this.getUserInput();
            System.out.println();

            if(!this.playersNicks.contains(nick)){
                playersNicks.add(nick);
                player = new Gamer(nick);
                isCreated = true;
            }
            else {
                System.out.println("This name is not available, please chose another name.");
            }
        }

        this.validateIfNewPlayer(player);

        return player;
    }

    /**
     * Methode simulate coin toss
     * @return boolean value represent head or tail
     */
    private boolean coinToss(){
        return random.nextInt(3) == 1 ? true : false;
    }

    /**
     * Set GameMode enum to ether multiplayer or singleplayer
     * that decide in witch format player data will be saved.
     */
    private void choseGameplayType(){
        System.out.println("Press 1 to play single player mode or play 2 for multiplayer.");
        String playerChose = this.getUserInput();

        switch (playerChose) {
            case "1": {
                this.singlePlayerMode();
                break;
            }
            case "2":{
                this.multiPlayerMode();
                break;
            }
            default:{
                System.out.println("Well it seams like you couldn't decide witch mode to chose so we chose it for you by coin toss.");

                if (this.coinToss()) {
                    this.multiPlayerMode();
                    break;
                }
                else {
                    this.singlePlayerMode();
                    break;
                }
            }
        }
    }

    private void validateIfNewPlayer( Gamer player) {

        if(new File(player.getPath()).exists()){
            System.out.printf("Welcome again Gamer %s%n",
                    player.getNick());
        }
        else {
            System.out.printf("Welcome new Gamer %s%n",
                    player.getNick());

            try {
                this.fileOperation.createPlayerFileIfDoesNotExist(player);
            }
            catch (IOException e){
                System.err.format("createFile error: %s%n", e);
                // for debug proposes
                e.printStackTrace();
            }
        }
    }

    /**
     * set deciding player1(player that decide game setings)
     * and computer
     */
    private void setImportantPlayers(){
        this.computer = this.players.get(0);
        this.player1 = this.players.get(1);
    }

    /**
     * Exit game action
     */
    private void ExitGame() throws IOException {
        this.updateAllPlayersFile();

        this.exit = true;
    }

    private void updateAllPlayersFile() throws IOException{
        for (Gamer player : this.players) {
            this.fileOperation.updatePlayerFile(
                    player,
                    player.gamerScores.getCurrentScoresValues());
        }
    }

    /**
     * Steps require to prepare singlePlayerMode
     */
    private void singlePlayerMode(){
        System.out.println("In single-player mode we keep track of only your victory's");

        this.fistPlayerIndex = 0; //Include computer in game
        this.createPlayersForSinglePlayer();
        this.gameMode = GameMode.Singleplayer;
        this.choseDifficultyLevel();
    }

    /**
     * Steps require to prepare multiPlayerMode
     */
    private void multiPlayerMode() {
        System.out.println("In multiplayer player mode we keep track of both your victory's and defeats.");

        this.createMultiplePlayers();

        this.fistPlayerIndex = 1; //exclude computer from game start from player1
        this.gameMode = GameMode.Multiplayer;
        this.choseDifficultyLevel();
    }

    /**
     * Update player statistics in multiplayer.
     */
    private void updateScores(){

        for (int i = this.fistPlayerIndex; i < this.players.size(); i++) {
            var player = this.players.get(i);

            player.gamerScores.updateScores(this.gameDifficulty, this.gameMode, player.getLastGameOutcome());
        }

        this.resetPlayersLastRoundOutcomes();
    }

    /**
     * Set players won parameter back to default state
     */
    private void resetPlayersLastRoundOutcomes() {
        for(Gamer player : this.players) {
            player.resetWonLastGame();
        }
    }

    private void choseDifficultyLevel() {
        do {
            System.out.printf("%s chose difficulty (type one of the following case sensitive)%n" +
                            "Easy%n" +
                            "Medium%n" +
                            "Hard%n" +
                            "Chaos%n",
                    this.players.get(1).getNick());
            System.out.print("Your choice: ");
            this.gameDifficulty = this.getUserInput();
            System.out.println();

            switch (this.gameDifficulty){
                case "Easy": {
                    System.out.println("You chose easy mode.\n" +
                            "In this difficulty level you guess a number between 0-100.");
                    this.gameDifficulty = DifficultyLevel.Easy.name();
                    this.lowerGuessBound = 0;
                    this.upperGuessBound = 100;
                    break;
                }
                case "Medium": {
                    System.out.println("You chose medium mode.\n" +
                            "In this difficulty level you guess a number between 0-10000.");
                    this.gameDifficulty = DifficultyLevel.Medium.name();
                    this.lowerGuessBound = 0;
                    this.upperGuessBound = 10000;
                    break;
                }
                case "Hard": {
                    System.out.println("You chose hard mode.\n" +
                            "In this difficulty level you guess a number between 0-1000000.");
                    this.gameDifficulty = DifficultyLevel.Hard.name();
                    this.lowerGuessBound = 0;
                    this.upperGuessBound = 1000000;
                    break;
                }
                case "Chaos": {
                    System.out.println("You chose hard mode.\n" +
                            "In this difficulty level you guess a number between 0-1000000.");
                    this.gameDifficulty = DifficultyLevel.Chaos.name();
                    this.setCustomGuessBounds();
                    break;
                }
                default: {
                    System.out.println("Please chose correct difficulty mode (case sensitive).\n" +
                            "Type Easy, Medium or Hard.");
                    this.gameDifficulty = DifficultyLevel.None.name();
                }
            }
        } while (this.gameDifficulty.equals(DifficultyLevel.None.name()));
    }

    private void setCustomGuessBounds(){
        System.out.println("Chose lower bound (lowes possible number is 0): ");
        int bound = this.getUserInputAsInteger();
        this.lowerGuessBound = bound < 0 ? 0 : bound;

        System.out.printf("Chose upper bound (lowes possible number is %d): %n", this.lowerGuessBound);
        bound = this.getUserInputAsInteger();
        this.upperGuessBound = bound < lowerGuessBound ? lowerGuessBound+1 : bound;
    }

    //TODO:Create GameActions class and move all game actions.
    /**
     * Convert user input into corresponding game actions in singleplayer.
     * @param option string value representing chosen by user option.
     */
    private boolean gameActionsSinglePlayer(String option) throws IOException{
        boolean goBack = false;

        switch(option){
            case "1":
            {
                this.StartStandardGame(false);
                break;
            }
            case "2":
            {
                this.StartStandardGame(true);
                break;
            }
            case "3":
            {
                this.StartMixedGame();
                break;
            }
            case "4":
            {
                this.choseDifficultyLevel();
                break;
            }
            case "5":
            {
                this.DisplayPlayerInformation();
                break;
            }
            case "6":
            {
                goBack = true;
                break;
            }
            default:
            {
                System.out.println("Inalide option! - Please try to choose correct option again.");
            }
        }

        return goBack;
    }

    /**
     * Convert user input into corresponding game actions in multiplayer.
     * @param option string value representing chosen by user option.
     */
    private boolean gameActionsMultiPlayer(String option) throws IOException{
        boolean goBack = false;

        switch(option){
            case "1":
            {
                this.startMultiPlayerGame();
                break;
            }
            case "2":
            {
                this.startTournament();
                break;
            }
            case "3":
            {
                this.choseDifficultyLevel();
                break;
            }
            case "4":
            {
                this.DisplayPlayerInformation();
                break;
            }
            case "5":
            {
                goBack = true;
                break;
            }
            default:
            {
                System.out.println("Inalide option! - Please try to choose correct option again.");
            }
        }

        return goBack;
    }

    private void DisplayPlayerInformation() throws IOException{
        System.out.println("Witch player information you want to display?");
        for (int i = 0; i < this.playersNicks.size(); i++) {
            System.out.printf("%d - %s%n", i, this.playersNicks.get(i));
        }

        System.out.print("Enter correct number representing available players: ");
        int playerId = this.getUserInputAsInteger();

        this.fileOperation.updatePlayerFile(
                this.players.get(playerId),
                this.players.get(playerId).gamerScores.getCurrentScoresValues());

        this.fileOperation.displayFileContent(this.players.get(playerId).getPath());
    }

    private void mainMenuActions(String option) throws IOException{
        switch(option){
            case "1":
            {
                this.choseGameplayType();

                if(this.gameMode.equals(GameMode.Singleplayer)) {
                    this.singlePlayerMenu();
                }
                else {
                    this.multiPlayerMenu();
                }
                break;
            }
            case "2":
            {
                this.ExitGame();
                break;
            }
            default:
            {
                System.out.println("Inalide option! - Please try to choose correct option again.");
            }
        }
    }

    private void singlePlayerMenu() throws IOException {
        boolean goBackToMainMenu = false;

        while (!goBackToMainMenu) {
            gamePrompt.singlePlayerModePrompt();
            goBackToMainMenu = this.gameActionsSinglePlayer(this.getUserInput());
        }
    }

    private void multiPlayerMenu() throws IOException {
        boolean goBackToMainMenu = false;

        while (!goBackToMainMenu) {
            gamePrompt.multiPlayerModePrompt();
            goBackToMainMenu = this.gameActionsMultiPlayer(this.getUserInput());
        }
    }

    /**
     * Gameplay in witch both players chose number for opponent to guess
     * then players guess each others numbers in turn.
     */
    private void StartMixedGame() {
        System.out.println(String.format("Hello %s lets start!",
                this.player1.getNick()));
        System.out.printf("Enter number between %d - %d for computer to guess: ",
                this.lowerGuessBound,
                this.upperGuessBound);
        int playerGuess = this.playerGuess(this.getUserInput());
        System.out.println();

        int computerGuess = random.nextInt(this.upperGuessBound - this.lowerGuessBound + 1) + this.lowerGuessBound;
        System.out.println("Your opponent provide as with his guess");
        List<Integer> alreadyGuessedNumbers = new ArrayList<>();

        boolean playerStart = this.coinToss();
        boolean playerWin = false;

        if (playerStart){
            System.out.println("Coin decide you guess first!");
            while (true){
                if (playerWin = this.standardRound(computerGuess)) break;
                if (playerWin = this.reverseRound(playerGuess, alreadyGuessedNumbers)) break;
            }
        }
        else {
            System.out.println("Coin decide you guess second!");
            while (true){
                if (playerWin = this.reverseRound(playerGuess, alreadyGuessedNumbers)) break;
                if (playerWin = this.standardRound(computerGuess)) break;
            }
        }

        this.updateScores();
    }

    /**
     * Starting standard version of guessing game.
     * @param reverse bool option to play game in reverse (if true player choose number computer guess)
     */
    private void StartStandardGame(boolean reverse) {
        int opponentGuess;
        List<Integer> alreadyGuessedNumbers = new ArrayList<>();

        if (!reverse) {
             opponentGuess = random.nextInt(this.upperGuessBound - this.lowerGuessBound + 1) + this.lowerGuessBound;
        }
        else {
            System.out.printf("Player %s enter your guess for computer to guess " +
                            "(Your difficulty mode is %s soo chose a number between %d-%d): ",
                    this.player1.getNick(),
                    this.gameDifficulty,
                    this.lowerGuessBound,
                    this.upperGuessBound);

            opponentGuess = this.playerGuess(this.getUserInput());
            System.out.println();
        }

        boolean finished = false;
        while (!finished) {
            if(!reverse) {
                finished = this.standardRound(opponentGuess);
            }
            else {
                finished = this.reverseRound(opponentGuess, alreadyGuessedNumbers);
            }
        }

        if (reverse) {
            System.out.println("Machine always win hahaha!");
        }

        this.updateScores();
    }

    /**
     * Represent single iteration of player guessing.
     * @param opponentGuess number between 0-this.upperGuessBound for player to guess
     */
    private boolean standardRound(int opponentGuess){

        System.out.printf("%s enter your guess (Your difficulty mode is %s soo chose a number between %d-%d): ",
                this.player1.getNick(),
                this.gameDifficulty,
                this.lowerGuessBound,
                this.upperGuessBound);

        int playerGuess = this.playerGuess(this.getUserInput());
        System.out.println();

        //System.out.println(opponentGuess);

        if (playerGuess == -1) {
            System.out.printf("%s Haha Seems like you give up guessing better luck next time (You lost this round!). :P",
                    this.player1.getNick());
            this.computer.setWonLastGame(true);
            return true;
        }

        if (opponentGuess == playerGuess){
            System.out.println("You win this round!");
            this.player1.setWonLastGame(true);
            return true;
        }
        else{
            System.out.println("Try again next time!");
            System.out.printf("Chose an number outside of %d-%d to surrender.%n",
                    this.lowerGuessBound,
                    this.upperGuessBound);
        }

        return false;
    }

    /**
     * Tournament in which scores are not updated you play for Master Title and glory
     */
    private void startTournament() {
        int tournamentFormat = this.setupTournament();
        System.out.println("Welcome Gamers!");
        System.out.println("In Tournament mode all Gamers duels to decide ultimate winner!");
        System.out.println("Scores are not updated you play for Master Title and glory");

        var tournamentCompetitorsLeft = new ArrayList<Integer>();


        for (int i = this.fistPlayerIndex; i < this.players.size() ; i++) {
            tournamentCompetitorsLeft.add(i);
        }


        //static tournament setup
        while (tournamentCompetitorsLeft.size() != 1) {
            var duelists = this.prepareTournamentPlayers(tournamentCompetitorsLeft);
            var losers = new ArrayList<Gamer>();

            if (duelists.length == 2) {
                System.out.printf("Championship %s VS %s%n",duelists[0].getNick(), duelists[1].getNick());
                for (int i = 0; i < tournamentFormat; i++) {
                    if (this.isTournamentRoundWinner(duelists, tournamentFormat, i)) break;
                    System.out.println("Start of round " + (i+1));

                    System.out.printf("%s VS %s%n",duelists[0].getNick(), duelists[1].getNick());
                    this.duelRound(duelists);
                }

                if (duelists[0].getCurrentTournamentScore() > duelists[1].getCurrentTournamentScore()) {
                    duelists[0].setTournamentTitle(GamerTournamentTitles.Master);
                    duelists[1].setTournamentTitle(GamerTournamentTitles.Loser);
                    System.out.printf("Welcome Master %s%n", duelists[0].getNick());
                }
                else {
                    duelists[1].setTournamentTitle(GamerTournamentTitles.Master);
                    duelists[0].setTournamentTitle(GamerTournamentTitles.Loser);
                    System.out.printf("Welcome Master %s%n", duelists[1].getNick());
                }

                losers.add(
                        (duelists[0].isMaster()) ?
                                duelists[1] : duelists[0]);

            }
            else if (duelists.length == 4) {

                System.out.printf("%s VS %s%n",duelists[0].getNick(), duelists[1].getNick());
                for (int i = 0; i < tournamentFormat; i++) {
                    if (this.isTournamentRoundWinner(duelists, tournamentFormat, i)) break;
                    System.out.println("Start of round " + (i+1));

                    this.duelRound(new Gamer[] {duelists[0], duelists[1]});
                }

                losers.add(
                        (duelists[0].getCurrentTournamentScore() > duelists[1].getCurrentTournamentScore()) ?
                        duelists[1] : duelists[0]);

                System.out.printf("%s VS %s%n",duelists[2].getNick(), duelists[3].getNick());
                for (int i = 0; i < tournamentFormat; i++) {
                    if (this.isTournamentRoundWinner(duelists, tournamentFormat, i)) break;
                    System.out.println("Start of round " + (i+1));

                    this.duelRound(new Gamer[] {duelists[2], duelists[3]});
                }

                losers.add(
                        (duelists[2].getCurrentTournamentScore() > duelists[3].getCurrentTournamentScore()) ?
                                duelists[3] : duelists[2]);
            }
            else if (duelists.length == 3) {
                System.out.printf("%s VS %s%n",duelists[0].getNick(), duelists[1].getNick());
                for (int i = 0; i < tournamentFormat; i++) {
                    if (this.isTournamentRoundWinner(duelists, tournamentFormat, i)) break;
                    this.duelRound(new Gamer[] {duelists[0], duelists[1]});
                    System.out.println("Start of round " + (i+1));

                }

                losers.add(
                        (duelists[0].getCurrentTournamentScore() > duelists[1].getCurrentTournamentScore()) ?
                                duelists[1] : duelists[0]);
            }
            
            for (Gamer player : losers) {
                Integer tmp = this.players.indexOf(player);
                tournamentCompetitorsLeft.remove(tmp);
            }
        }
        this.endTournament();
    }

    private boolean isTournamentRoundWinner(Gamer[] duelists, int tournamentFormat, int currentRound) {
        int roundsLeft = tournamentFormat - currentRound;

        for (Gamer player : duelists) {
            if (player.getCurrentTournamentScore() > roundsLeft) return true;
        }
        return false;
    }

    private void endTournament(){
        this.resetTournamentScores();
        this.isTournament = false;
        this.updateScores();
    }
    private void resetTournamentScores() {
        for (Gamer player : this.players) {
            player.resetCurrentTournamentScore();
        }
    }

    private Gamer[] prepareTournamentPlayers(List<Integer> participants){
        var tournamentPlayers = new Gamer[participants.size()];

        int i = 0;
        for (int index : participants) {

            tournamentPlayers[i] = this.players.get(index);

            i++;
        }

        return tournamentPlayers;
    }

    private int setupTournament(){
        this.isTournament = true;
        System.out.println("Chose Tournament format\n" +
                "1 - BO1 format\n" +
                "2 - BO3 format\n" +
                "3 - BO5 format\n");
        int choice = this.getUserInputAsInteger();

        switch (choice) {
            case 1: {
                System.out.println("You chose BO1 format.");
                break;
            }
            case 2: {
                System.out.println("You chose BO3 format.");
                return 3;
            }
            case 3: {
                System.out.println("You chose BO5 format.");
                return 5;
            }
            default: {
                System.out.println("Well if you can't decide you get BO1 format.");
            }
        }

        return 1;
    }

    private void startMultiPlayerGame() {
        System.out.println("Welcome Gamers!");
        System.out.println("In Multiplayer mode you can challenge each other to duels!");
        System.out.println("Type player number to chose players participating in duel: ");

        for (int i = this.fistPlayerIndex; i < this.players.size(); i++) {
            System.out.printf("%d %s%n", i, this.players.get(i).getNick());
        }

        var duelingPlayers = new Gamer[2];

        for (int i = 0; i < duelingPlayers.length; i++) {
            System.out.println("Chose player" + (i+1));
            duelingPlayers[i] = this.chosePlayer();
        }

        this.duelRound(duelingPlayers);

        this.updateScores();
    }

    private void duelRound(Gamer[] duelists) {
        var duelistsNumber = new int[2];

        for (int i = 0; i < duelists.length; i++) {
            System.out.printf("%s enter your number for opponent to guess%n (Your difficulty mode is %s soo chose a number between %d-%d): ",
                    duelists[i].getNick(),
                    this.gameDifficulty,
                    this.lowerGuessBound,
                    this.upperGuessBound);

            duelistsNumber[i] = this.playerGuess(this.getUserInput());

            if (duelistsNumber[i] == -1) {
                System.out.printf("Ups it's seems like %s give up before duel begin!%n", duelists[i].getNick());
                int j = i == 0 ? 1 : 0;

                duelists[j].setWonLastGame(true);
                duelists[i].setWonLastGame(false);

                if(isTournament) duelists[j].incrementCurrentTournamentScore();

                System.out.printf("%s win%n", duelists[j].getNick());

                return;
            }
        }

        boolean isDuelFinished = false;
        while (!isDuelFinished) {
            isDuelFinished = this.duel(duelists, duelistsNumber);
        }
    }

    private boolean duel(Gamer[] duelists, int[] duelistsNumber) {
        for (int i = 0; i < duelists.length; i++) {
            int j = i == 0 ? 1 : 0;
            int opponentGuess = duelistsNumber[j];

            if (duelists[i].isLeader()){
                System.out.printf("Congrats %s on being the Leader!%n", duelists[i].getNick());
                System.out.print("As recognition you get a additional guess: ");
                int playerAdditionalGuess = this.playerGuess(this.getUserInput());
                System.out.println();

                if (playerAdditionalGuess == -1) {
                    System.out.printf("%s Haha Seems like you give up guessing better luck next time (You lost this round!). :P",
                            duelists[i].getNick());

                    duelists[j].setWonLastGame(true);
                    if(isTournament) duelists[j].incrementCurrentTournamentScore();

                    duelists[i].setWonLastGame(false);
                    System.out.printf("%s win%n", duelists[j].getNick());

                    return true;
                }

                if (opponentGuess == playerAdditionalGuess){
                    duelists[i].setWonLastGame(true);
                    if(isTournament) duelists[i].incrementCurrentTournamentScore();

                    duelists[j].setWonLastGame(false);
                    System.out.printf("%s win%n", duelists[i].getNick());

                    return true;
                }
                else{
                    System.out.println("Try again next time!");
                    System.out.printf("Chose an number outside of %d-%d to surrender.%n",
                            this.lowerGuessBound,
                            this.upperGuessBound);
                }
            }

            if (duelists[i].isMaster()) {
                int bound = this.isTournament ? 51 : 11;

                System.out.printf("Congrats %s on being the Master!%n", duelists[i].getNick());
                System.out.println("As recognition you get chance to pick into your opponent number.");
                System.out.printf("Chose number from 1-%d: ", bound-1);

                if (this.getUserInputAsInteger() == random.nextInt(bound)) {
                    System.out.printf("%n%s chosen number is %d good luck.%n", duelists[i].getNick(), duelistsNumber[j]);
                }
                else {
                    System.out.printf("%n%s Uuu you missed your shot. Good luck next time.%n", duelists[i].getNick());
                }
            }

            System.out.printf("%s enter your guess (Your difficulty mode is %s soo chose a number between %d-%d): ",
                    duelists[i].getNick(),
                    this.gameDifficulty,
                    this.lowerGuessBound,
                    this.upperGuessBound);

            int playerGuess = this.playerGuess(this.getUserInput());
            System.out.println();

            if (playerGuess == -1) {
                System.out.printf("%s Haha Seems like you give up guessing better luck next time (You lost this round!). :P",
                        duelists[i].getNick());

                duelists[j].setWonLastGame(true);
                if(isTournament) duelists[j].incrementCurrentTournamentScore();

                duelists[i].setWonLastGame(false);
                System.out.printf("%s win%n", duelists[j].getNick());

                return true;
            }

            if (opponentGuess == playerGuess){
                duelists[i].setWonLastGame(true);
                if(isTournament) duelists[i].incrementCurrentTournamentScore();

                duelists[j].setWonLastGame(false);
                System.out.printf("%s win%n", duelists[i].getNick());

                return true;
            }
            else{
                System.out.println("Try again next time!");
                System.out.printf("Chose an number outside of %d-%d to surrender.%n",
                        this.lowerGuessBound,
                        this.upperGuessBound);
            }
        }

        return false;
    }

    private Gamer chosePlayer() {
        boolean isChosen = false;

        Gamer player = null;

        while (!isChosen) {
            int id = this.getUserInputAsInteger();

            if (id >= 0 && id < this.players.size()) {
                player = this.players.get(id);
                isChosen = true;
            }
        }

        return player;
    }

    /**
     * Represent single iteration of computer guessing.
     * Rigged version of the guessing game in witch computer always win (not a bug it's a feature)
     * @param playerGuess number between 0-100 for computer to guess
     * @return true if computer guess correctly and false if not
     */
    private boolean reverseRound(int playerGuess, List<Integer> alreadyGuessedNumbers){
        int computerGuess;
        boolean newGuess = false;
        do {
            computerGuess = random.nextInt(this.upperGuessBound - this.lowerGuessBound + 1) + this.lowerGuessBound;

            if (!alreadyGuessedNumbers.contains(computerGuess)){
                newGuess = true;
                alreadyGuessedNumbers.add(computerGuess);
            }
        }while (!newGuess);

        System.out.println("Computer's guess: " + computerGuess);

        if (computerGuess == playerGuess){
            System.out.println("Computer win this round! haha machine always win!!");
            return true;
        }
        else{
            System.out.println("You got lucky this time!");
            return false;
        }
    }

    /**
     * Perform validation on user input in order to check if it's a number
     * @param userInput String value representing user's input from console
     * @return Integer number that is users guess.
     */
    private int playerGuess(String userInput) {
        int guess = -1;

        try{
            guess = Integer.parseInt(userInput);
            if (guess < this.lowerGuessBound || guess > this.upperGuessBound) {
                guess = -1;
            }
        }
        catch (NumberFormatException e){
            System.out.println("Please chose number.");
        }

        return guess;
    }

    /**
     * Get trimmed user input without any validation.
     * @return String containing user input.
     */
    private String getUserInput() {
        return scanner.nextLine().trim();
    }

    /**
     * Get user input as a integer number.
     * @return Int containing chosen number.
     */
    private int getUserInputAsInteger() {
        try {
            return Integer.parseInt(this.getUserInput());
        }
        catch (NumberFormatException e) {
            System.err.printf("Can't convert user input into integer error occur - %s%n", e);
        }

        return -1;
    }

    private void mainMenu() throws IOException{
        while(!this.exit)
        {
            gamePrompt.StartingPrompt();
            this.mainMenuActions(this.getUserInput());
        }
    }
}