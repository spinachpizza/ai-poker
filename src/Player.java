public class Player {
    
    private int balance;
    protected int currentBet;
    protected int myPot;
    private int betAmount;
    protected int id;
    private String name;
    protected boolean raised;
    private String[] cards = new String[2];

    private boolean hasFolded = false;

    private Deck deck;
    protected Game game;
    private Scoring scoring;
    private PlayerGUI playerGUI;
    private GUI gui;

    public Player(int id, String name, int balance, Deck deck, Game game, GUI gui) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.deck = deck;
        this.game = game;
        this.gui = gui;
        currentBet = 0;
        gui.disableButtons();
        playerGUI = new PlayerGUI(this, gui);
        scoring = new Scoring();
        reset();
    }

    public final boolean hasFolded() {
        return hasFolded;
    }

    public final String getName() {
        return name;
    }

    public final int getBalance() {
        return balance;
    }

    public final int getCallAmount() {
        return game.getCurrentBet() - currentBet;
    }

    public final int getID() {
        return id;
    }

    public final boolean hasRaised() {
        return raised;
    }

    public void startTurn() {
        //Activate buttons etc
        gui.enableButtons();
        gui.updateSlider(balance, game.getCurrentBet() - currentBet);
    }


    public final void drawCards() {
        //Draw 2 cards at the start of the game
        cards[0] = deck.getCard();
        cards[1] = deck.getCard();
        if(id == 0) {
            showCards(); 
        }
    }

    public final void showCards() {
        playerGUI.showCards(cards);
    }

    public final void flipCards() {
        playerGUI.flipCards();
    }

    public final void hideCards() {
        playerGUI.hideCards();
    }

    public final void addWinnings(int amount) {
        balance += amount;
        updateBalanceDisplay();
    }

    public final void fold() {
        hasFolded = true;
        game.changeRaised(id);
        playerGUI.hideChip();
        hideCards();
        finishTurn();
    }

    public final void blind(int amount) {
        currentBet = amount;
        balance = balance - amount;
        myPot += amount;
        playerGUI.showChip();
        playerGUI.updatePotDisplay(myPot);
        updateBalanceDisplay();
    }

    public final void call() {
        int amount = game.getCurrentBet() - currentBet;
        currentBet = currentBet + amount;
        balance -= amount;
        myPot += amount;
        game.addToGlobalPot(amount);
        updateBalanceDisplay();

        finishTurn();
    }

    public final void raise(int amount) {
        int moneySpent = amount - currentBet;
        balance -= moneySpent;
        myPot += moneySpent;
        game.changeCurrentBet(amount);
        currentBet = amount;
        game.addToGlobalPot(moneySpent);
        game.updateRaised(id);

        raised = true;

        updateBalanceDisplay();
        finishTurn();
    }

    public final void updateHandDisplay() {
        gui.updateHandDisplay(getHandName());
    }

    public final int getTableScore() {
        String[] combined = combineCards();
        if(combined.length <= 2) {
            return 0; //No tablecards present
        }
        String[] tableCards = new String[combined.length - 2];
        for(int i=0; i<tableCards.length; i++) {
            tableCards[i] = combined[i+2];
        }
        int score = scoring.getCardValue(tableCards);
        return score;
    }

    public final int getScore() {
        String[] combined = combineCards();
        int score = scoring.getCardValue(combined);
        return score;
    }

    public final String getHandName() {
        int score = getScore();
        String name = scoring.valueToName(score);
        return name;
    }

    private final void finishTurn() {
        playerGUI.updatePotDisplay(myPot);
        gui.disableButtons();
        game.nextTurn();
        game.doTurn();
    }



    private final String[] combineCards() {
        String[] combined;
        String[] tableCards = game.getTableCards();
        if(tableCards[0] == null) {
            combined = new String[] { cards[0], cards[1] };
        } else if(tableCards[3] == null) {
            combined = new String[] { cards[0], cards[1] , tableCards[0], tableCards[1], tableCards[2]};
        } else if(tableCards[4] == null) {
            combined = new String[] { cards[0], cards[1] , tableCards[0], tableCards[1], tableCards[2], 
                tableCards[3]};
        } else {
            combined = new String[] { cards[0], cards[1] , tableCards[0], tableCards[1], tableCards[2], 
                tableCards[3], tableCards[4]};
        }

        return combined;
    }

    public final void reset() {
        currentBet = 0;
        myPot = 0;
        hasFolded = false;
        playerGUI.showChip();
        playerGUI.updatePotDisplay(0);
        flipCards();
    }

    public final void roundReset() {
        currentBet = 0;
        raised = false;
    }

    private final void updateBalanceDisplay() {
        playerGUI.updateBalance(balance);
    }


    
}
