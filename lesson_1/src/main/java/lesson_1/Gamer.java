package max.vanach.lesson_1;

public class Gamer {
    private final static String FILE_TYPE = ".txt";

    private String nick;
    private String path;

    public GameScore gamerScores;
    private boolean lastGameWinner;
    //titles restart after leaving the game because you lose when you leave :D
    private String title;
    private String tournamentTitle;
    private int currentTournamentScore;
    private PlayerFileOperation  fileOperation = new PlayerFileOperation();

    public Gamer(String nick){
        this.nick = nick;
        this.gamerScores = new GameScore();
        this.path = this.nick + FILE_TYPE;
        this.lastGameWinner = false;
        this.setTitle(fileOperation.getPlayerTitle(this.getPath()));
        this.setTournamentTitle(fileOperation.getPlayerTournamentTitle(this.getPath()));
        this.setCurrentTournamentScore(0);
    }

    public String getNick(){return this.nick;}

    public String getPath(){return this.path;}

    public String getTitle(){return this.title;}
    public String getTournamentTitle(){return this.tournamentTitle;}

    public boolean getLastGameOutcome(){return this.lastGameWinner;}
    public int getCurrentTournamentScore(){return this.currentTournamentScore;}

    public void resetWonLastGame(){this.lastGameWinner = false;}

    public void resetCurrentTournamentScore(){this.setCurrentTournamentScore(0);}

    public void setWonLastGame(boolean isWinner){
        this.lastGameWinner = isWinner;
        this.setTitle();
    }

    public void setCurrentTournamentScore(int currentTournamentScore) {this.currentTournamentScore = currentTournamentScore;}

    public void incrementCurrentTournamentScore() {
        this.setCurrentTournamentScore(this.getCurrentTournamentScore()+1);
    }

    public void setTournamentTitle(GamerTournamentTitles tournamentTitle) {this.tournamentTitle = tournamentTitle.name();}

    public void setTournamentTitle(String tournamentTitle) {this.tournamentTitle = tournamentTitle;}

    private void setTitle() {
        this.title = this.getLastGameOutcome() ?
                GamerTitles.Leader.name() : GamerTitles.Loser.name();
    }
    private void setTitle(String title) {
        this.title = title;
    }
    public boolean isLeader() {return this.title.equals("Leader");}

    public boolean isMaster() {return this.tournamentTitle.equals("Master");}

}
