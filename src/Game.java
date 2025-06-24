import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Game {

    private final int playerAmount = 6;
    private final int startBalance = 1000;
    private final String[] names = {"Toby", "Abigail", "Jess", "Steve", "Katie", "Tom"};
    
    private final int smallBlind = 5;
    private final int bigBlind = 10;

    private int globalPot;
    private int currentBet;
    private int raised;

    private Player[] players = new Player[playerAmount];
    private int currentIndex;
    private int roundNum = 0;
    
    private Deck deck;
    private GUI gui;
    private String[] tableCards = new String[5];

    public Game(GUI gui) {
        this.gui = gui;
        setup();
    }

    public int getGlobalPot() {
        return globalPot;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public String[] getTableCards() {
        return tableCards;
    }

    public int getRaised() {
        return raised;
    }

    public int getPlayersLeft() {
        int count = 0;
        for(int i=0; i<playerAmount; i++) {
            if(!players[i].hasFolded()) {
                count++;
            }
        }
        return count;
    }

    public int getCurrentRound() {
        return roundNum;
    }
 
    public void addToGlobalPot(int amount) {
        globalPot += amount;
    }

    public void changeCurrentBet(int newAmount) {
        currentBet = newAmount;
    }

    public void updateRaised(int id) {
        raised = id;
    }

    public boolean hasRaised() {
        for(int i=0; i<playerAmount; i++) {
            if(players[i].hasRaised()) {
                return true;
            }
        }
        return false;
    }



    private final void setup() {

        deck = new Deck();

        players[0] = new Player(0, "You", startBalance, deck, this, gui);
        gui.setPlayer(players[0]);
        for(int i=1; i<playerAmount; i++) {
            players[i] = new AI(i, names[i-1], startBalance, deck, this, gui);
        }

        reset();
    }

    private final void reset() {
        globalPot = 0;
        currentBet = 0;
        raised = -1;
        roundNum = 0;
        deck.reset();
        gui.reset();

        for(int i=0; i<5; i++) {
            tableCards[i] = null;
        }

        for(int i=0; i<playerAmount; i++) {
            players[i].reset();
        }
    }

    private final void roundReset() {
        currentBet = 0;
        for(int i=0; i<playerAmount; i++) {
            players[i].roundReset();
        }
    }

    public final void start() {
        currentIndex = 0;

        //Do the small and big blinds
        nextTurn();
        players[currentIndex].blind(smallBlind);
        System.out.println("Player " + String.valueOf(currentIndex) + " is small blind.");
        nextTurn();
        players[currentIndex].blind(bigBlind);
        System.out.println("Player " + String.valueOf(currentIndex) + " is big blind.");
        nextTurn();
        raised = currentIndex;
        currentBet = bigBlind;

        getStartingCards();
        players[0].updateHandDisplay();

        nextRound();
        
    }

    private final void nextRound() {
        System.out.println("Round: " + String.valueOf(roundNum));
        getTableCards(roundNum);

        if(roundNum < 4) {
            System.out.println("Player " + String.valueOf(currentIndex) + " turn.");
            players[currentIndex].startTurn();
        } else {
            doScoring();
        }
    }

    public final void doTurn() {
        if(currentIndex != raised) {
            if(allFolded()) {
                showRemainingTableCards();
                doScoring();
            } else {
                System.out.println("Player " + String.valueOf(currentIndex) + " turn.");
                players[currentIndex].startTurn();
            }
        } else {
            roundReset();
            roundNum++;
            nextRound();
        }
    }

    private final void getStartingCards() {
        for(int i=0; i<playerAmount; i++) {
            players[i].drawCards();
        }
    }

    private final boolean allFolded() {
        int count = getPlayersLeft();
        if(count == 1) {
            return true;
        }
        return false;
    }

    private final void showRemainingTableCards() {
        for(int i=0; i<5; i++) {
            if(tableCards[i] == null) {
                tableCards[i] = deck.getCard();
            }
        }
        gui.showTableCards(tableCards);
    }

    private final void doScoring() {
        int highest = 0;
        int highestID = 0;
        for(int i=0; i<playerAmount; i++) {
            if(!players[i].hasFolded()) {
                int score = players[i].getScore();
                players[i].showCards();
                if(score>highest) {
                    highest = score;
                    highestID = i;
                }
            }
        }
        System.out.println(players[highestID].getName() + " wins with a " + players[highestID].getHandName());
        players[highestID].addWinnings(globalPot);
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> nextGame());
        pause.play();
    }

    private final void nextGame() {
        reset();
        start();
    }

    private final void getTableCards(int num) {
        switch(num) {
            case 1:
                tableCards[0] = deck.getCard();
                tableCards[1] = deck.getCard();
                tableCards[2] = deck.getCard();
                break;
            case 2:
                tableCards[3] = deck.getCard();
                break;
            case 3:
                tableCards[4] = deck.getCard();
                break;
        }
        players[0].updateHandDisplay();
        gui.showTableCards(tableCards);
    }


    public final void nextTurn() {
        currentIndex ++;
        //Circular array -> resets to 0 if over
        if(currentIndex >= playerAmount) {
            currentIndex = 0;
        }
        //Continue if player has folded
        if(players[currentIndex].hasFolded()) {
            nextTurn();
        }
    }

    public final void changeRaised(int currentID) {
        if(raised == currentID) {
            while (true) {
                raised --;
                if(raised < 0) {
                    raised = playerAmount-1;
                }
                if(!players[raised].hasFolded()) {
                    break;
                }
            }
        }
    }
}
