import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ThreadLocalRandom;

public class AI extends Player{

    public int[] GUIcoords;
    public String name;
    public int x,y;
    public float bluffValue;

    public ImageIcon cardBack = new ImageIcon(new ImageIcon("cards/back.jpg").getImage().getScaledInstance(60,90, Image.SCALE_DEFAULT));

    public JLabel moveMessage;
    public RoundedLabel msgBorder;


    //Initialisation Method
    public AI(int index, String name, int[] coords)
    {
        super(index);

        this.name = name;
        this.GUIcoords = coords;

        x = GUIcoords[0];
        y = GUIcoords[1];

        GameSetup();
    }

    //First time game setup
    public @Override void GameSetup()
    {
        theGUI();
    }


    //Setup for each round of the game
    public @Override void RoundSetup()
    {
        GetStarterCards();
        UpdateMoney();

        card1.setImage(cardBack.getImage());
        card2.setImage(cardBack.getImage());

        //unHideCards(); // For testing

        bluffValue = ThreadLocalRandom.current().nextInt(3,16) / 10;

        currentBet = 0;
        myPot = 0;
    }



    public void unHideCards()
    {
        ImageIcon card = new ImageIcon(new ImageIcon("cards/" + cards[0] + ".png").getImage().getScaledInstance(60,90, Image.SCALE_DEFAULT));
        card1.setImage(card.getImage());
        card = new ImageIcon(new ImageIcon("cards/" + cards[1] + ".png").getImage().getScaledInstance(60,90, Image.SCALE_DEFAULT));
        card2.setImage(card.getImage());
    }



    public void makeMove()
    {
        // Create a timer that waits for 1000 milliseconds (1 second)
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeDecision();
                Main.NextTurn(false);
            }
        });

        // Start the timer
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
    }



    public void makeDecision()
    {

        //Different decisions based on each stage of game

        //Stage 0 (no cards on table revealed)
        if(Main.currentStage == 0)
        {
            
            FirstRoundDecision();
        

        //Other Stages
        } else {

            MidGameDecision();
        }

    }



    public void FirstRoundDecision()
    {
        int cardValue = getStarterHandValue();

        //System.out.println(name + " has card value " + cardValue);


        //If others have folded then less likely to fold (multiplier)
        int multi = 1;
        if(Main.PlayersRemaining() < 4)
        {
            multi = 2;

        } else if(Main.PlayersRemaining() < 3)
        {
            multi = 5;
        }



        //Chances of folding
        int random = 1;
        //100% chance of folding
        if(cardValue < 5)
        {
            random = 0;

        //Else chance is based on card value (over 20 wont fold)
        } else if(cardValue < 21)
        {
            random = ThreadLocalRandom.current().nextInt(0, ((int) Math.ceil((cardValue * cardValue) / 30) * multi) + 1);
        }


        //TEMPORARY CODE IF RUN OUT OF MONEY JUST FOLD
        //
        if(balance < Main.CurrentBet)
        {
            random = 0;
        }
        //
        ////////////////////////////////////////////////


        //Fold if random variable is 0
        if(random == 0)
        {
            Fold();
            DisplayMessage("Fold");

        //Else try to raise instead 
        } else {

            random = 1;
            
            int raiseAmount = 0;

            //Chances of raising and bet amount based on card value
            if(cardValue >= 24)
            {
                //31 - cardValue is upper bound of random
                random = ThreadLocalRandom.current().nextInt(0,(32 - cardValue));
                raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+((cardValue-10)*3));

            // Else 2% chance if lower card value
            } else {
                random = ThreadLocalRandom.current().nextInt(0,50);
                raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+15);
            }


            if(random == 0)
            {
                //Raise
                //If balance is low raise less
                if(balance < 50 && balance > Main.CurrentBet)
                {
                    raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+balance);
                } 

                Raise(raiseAmount);
                DisplayMessage("Raise " + raiseAmount);


            } else {

                Call();
                DisplayMessage("Call");
            }

        }

    }


    public void MidGameDecision()
    {
        int cardValue = getRelativeCardValue() - 10; //Returns card value

        //Fold chance equations (the higher the number the less likely to fold)
        float BetValue = (float) 20 / Main.CurrentBet;
        int FoldChance = (int) Math.ceil((cardValue*cardValue * BetValue) / 16);
        if(Main.PlayersRemaining() == 3) {FoldChance *= 3;} //Chance of folding decreases with less players
        else if(Main.PlayersRemaining() == 2) {FoldChance *= 5;} //Chance of folding decreases with less players



        //System.out.println(name + " has card value " + cardValue);
 
        //Run chance of folding
        int random = ThreadLocalRandom.current().nextInt(0,FoldChance + 5);
        if(random >= FoldChance)
        {
            Fold();
            DisplayMessage("Fold");


        //Else try to raise
        } else {

            random = ThreadLocalRandom.current().nextInt(0,(int)Math.ceil((FoldChance+20)*2/bluffValue));
            if(random <= FoldChance)
            {
                int raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+cardValue);
                Raise(raiseAmount);
                DisplayMessage("Raise " + raiseAmount);
            } else {
                Call();
                DisplayMessage("Call");
            }
        }



    }


    
    public void MidGameDecision2()
    {
        int cardValue = getRelativeCardValue();

        //System.out.println(name + " has card value " + cardValue);

        //TEMPORARY CODE
        //
        if(Main.CurrentBet > balance)
        {
            Fold();
            DisplayMessage("Fold");
        }
        //
        ////////////////////



        //Folding chances run first

        //If others have folded then less likely to fold (multiplier)
        int multi = 1;
        if(Main.PlayersRemaining() < 4) {multi = 2;} else if(Main.PlayersRemaining() < 3) {multi = 5;}
        //If bet is low multi also goes up
        if(Main.CurrentBet <= 5) {multi *= 2;}



        //Chances of folding
        //If current bet is 0 theres no point in folding
        int random = 1;
        //100% chance of folding
        if(cardValue < 5 && Main.CurrentBet != 0)
        {
            random = 0;

        //Else chance is based on card value (over 20 wont fold)
        } else if(cardValue < 21 && Main.CurrentBet != 0)
        {
            random = ThreadLocalRandom.current().nextInt(0, ((int) Math.ceil((cardValue * cardValue) / 40) * multi) + 1);
        }


        if(random == 0)
        {
            Fold();
            DisplayMessage("Fold");

        //Else try to raise
        } else {

            random = 1;
            int raiseAmount = 0;

            //Multiplier based on current bet amount to reduce constant raises
            float rmulti = 1;
            if(Main.CurrentBet >= 100) {rmulti = bluffValue * (int) Math.ceil(Main.CurrentBet/100);}
            //If nobody has bet yet the chance increases else bluffing value is applied
            else if(Main.CurrentBet <= 5) {rmulti = 0.2f;} else {rmulti=bluffValue;}
            //If rmulti somehow reaches 0 it is set to 0.1
            if(rmulti == 0) {rmulti = 0.1f;}


            //Chances of raising and bet amount based on card value
            if(cardValue >= 50)
            {
                //For really good cards 50/50 chance of raising
                random = ThreadLocalRandom.current().nextInt(0,(int)Math.ceil(2*rmulti)+1);
                raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+(int)Math.ceil(cardValue*1.5));

            } else if(cardValue >= 30)
            {
                //For mid cards based on card value
                int upperBound = (int) Math.ceil((((int) Math.ceil((50 - cardValue)/5)) * rmulti) + 3);
                random = ThreadLocalRandom.current().nextInt(0, upperBound);
                raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+cardValue);

            // Else low chance if lower card value
            } else {
                random = ThreadLocalRandom.current().nextInt(0,(int)Math.ceil(30*rmulti)+1);
                raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+15);
            }


            if(random == 0)
            {
                //Raise
                //If balance is low raise less
                if(balance < 50)
                {
                    raiseAmount = ThreadLocalRandom.current().nextInt(Main.CurrentBet+1,Main.CurrentBet+5);
                } 

                Raise(raiseAmount);
                DisplayMessage("Raise " + raiseAmount);


            } else {

                Call();
                DisplayMessage("Call");
            }
        }
    }




    //Methods for AI decision for each stage of game

    //Returns the algorithmic value for the starter hand
    public int getStarterHandValue()
    {
        //Card value is assigned a value out of 30 (30 is the best)
        int cardValue;
        int card1Num = PokerHands.convert(cards[0].charAt(0));
        int card2Num = PokerHands.convert(cards[1].charAt(0));
        
        //Check for pair
        if(PokerHands.checkPair(cards))
        {
            //Card1Num used as both cards are the same so it doesnt matter
            //Best cards are pairs of 10 or higher
            if(card1Num >= 10)
            {
                cardValue = 16 + card1Num;

            //Other pairs are good but are ranked less
            } else {

                cardValue = card1Num * 2;
            }

        //If both cards are greater than or equal to 10 more value
        } else if(card1Num >= 10 && card2Num >= 10)
        {
            //If both cards dont have the same suite score is reduced
            if(cards[0].charAt(1) == cards[1].charAt(1))
            {
                cardValue = card1Num + card2Num;
            } else {
                cardValue = card1Num + card2Num - 3;
            }

        //Else cards are just "normal"
        } else {

            //Find highest card number of the 2
            int highestValue = PokerHands.getMaxValue(cards);

            //If both cards have the same suite card value is increased slightly
            if(cards[0].charAt(1) == cards[1].charAt(1))
            {
                cardValue = highestValue + 2;
            } else {
                cardValue = highestValue;
            }
        }

        return cardValue;
    }




    public int getRelativeCardValue()
    {
        int cardValue = 0;


        //If all cards are revealed the value of those is returned
        if(Main.currentStage == 3)
        {
            String[] allCards = {cards[0], cards[1], Main.tableCards[0],
                Main.tableCards[1], Main.tableCards[2], Main.tableCards[3], Main.tableCards[4]};

            cardValue = PokerHands.getCardValue(allCards);

            //If all value is coming from the table cards then only look at highest value of cards in hand
            if(PokerHands.getCardValue(Main.tableCards) >= cardValue)
            {
                cardValue = PokerHands.getCardValue(cards);
            }



        //If 1 card left hidden algorithm tests for adding 1 additional card
        } else if(Main.currentStage == 2)
        {
            //Array containing only the table cards with an empty slot
            String[] tableCards = {Main.tableCards[0], Main.tableCards[1],
                                Main.tableCards[2], Main.tableCards[3], null};

            //Array containing cards on table and deck
            String[] currCards = {cards[0], cards[1], Main.tableCards[0],
                Main.tableCards[1], Main.tableCards[2], Main.tableCards[3]};

            cardValue = PokerHands.getCardValue(currCards); //Value if no cards are added

            //Array containing table and deck cards with an empty slot
            String[] allCards = {cards[0], cards[1], Main.tableCards[0],
                Main.tableCards[1], Main.tableCards[2], Main.tableCards[3], null};
            

            int totalValue = 0;

            //Iterates through every possible card adding it to the current 
            for(int i=0; i<Main.cards.length; i++)
            {
                allCards[6] = Main.cards[i];
                tableCards[4] = Main.cards[i];

                int val = PokerHands.getCardValue(allCards);

                //If the value of the cards is only from table cards it is worthless
                //E.G. If theres 2 pairs on the table and thats all it considers everyone has that same value
                if(PokerHands.getCardValue(tableCards) >= val)
                {
                    val = 0;
                } 


                totalValue += val;
            }

            //If estimated value exceeds current value take that as value
            totalValue = Math.round(totalValue / Main.cards.length);
            if(totalValue > cardValue)
            {
                cardValue = totalValue;
            }






        //If 2 cards left hidden algorithm test for adding 2 additional cards (every combination 2704 iterations)
        } else {

            //Array containing cards on table and deck
            String[] currCards = {cards[0], cards[1], Main.tableCards[0],
                Main.tableCards[1], Main.tableCards[2]};

            cardValue = PokerHands.getCardValue(currCards); //Value if no cards are added



            //Array containing only the table cards with 2 empty slots
            String[] tableCards = {Main.tableCards[0], Main.tableCards[1],
                Main.tableCards[2], null, null};

            String[] allCards = {cards[0], cards[1], Main.tableCards[0],
                Main.tableCards[1], Main.tableCards[2], null, null};
            
            int totalValue = 0;

            for(int j = 0; j<Main.cards.length; j++)
            {
                allCards[5] = Main.cards[j];
                tableCards[3] = Main.cards[j];


                for(int i=0; i<Main.cards.length; i++)
                {
                    allCards[6] = Main.cards[i];
                    tableCards[4] = Main.cards[i];

                    int val = PokerHands.getCardValue(allCards);

                    //If the value of the cards is only from table cards it is worthless
                    //E.G. If theres 2 pairs on the table and thats all it considers everyone has that same value
                    if(PokerHands.getCardValue(tableCards) >= val)
                    {
                        val = 0;

                    //If theres a high scoring hand it has much higher value
                    } else if(val > 300)
                    {
                        val *= 20;
                    }


                    totalValue += val;
                }
            }

            //If estimated value exceeds current value take that as value
            totalValue = Math.round(totalValue / 2704);
            if(totalValue > cardValue)
            {
                cardValue = totalValue;
            }
        }


        int firstDigit = Integer.parseInt(Integer.toString(cardValue).substring(0, 1));
        //System.out.println(Math.round((cardValue - (firstDigit * 100)) / 3) + (firstDigit * 3));
        cardValue = (firstDigit * 10) + Math.round((cardValue - (firstDigit * 100)) / 2); //Changes the 3 digit number to a 2 digit score

        return cardValue;

    }









    public void DisplayMessage(String msg)
    {
        moveMessage.setText(msg);
        msgBorder.setVisible(true);

        // Create a timer that waits for 1000 milliseconds (1 second)
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                msgBorder.setVisible(false);
                moveMessage.setText("");
            }
        });

        // Start the timer
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
    }






    public @Override void theGUI()
    {


        //AI message
        moveMessage = new JLabel("", SwingConstants.CENTER);
        moveMessage.setOpaque(false);
        moveMessage.setFont(new Font("Arial", Font.BOLD, 16));
        moveMessage.setForeground(new Color(40, 50, 40));
        moveMessage.setBounds(x+25,y+40,75,30);
        GUI.panel.add(moveMessage);

        msgBorder = new RoundedLabel(20, new Color(210,210,210));
        msgBorder.setBounds(x+25,y+40,75,30);
        GUI.panel.add(msgBorder);
        msgBorder.setVisible(false);


        //AI Cards
        card1 = new ImagePanel(cardBack.getImage());
        card1.setOpaque(false);
        card1.setBounds(x-2, y+100, 60, 90);
        GUI.panel.add(card1);
        
        card2 = new ImagePanel(cardBack.getImage());
        card2.setOpaque(false);
        card2.setBounds(x+67, y+100, 60, 90);
        GUI.panel.add(card2);

        //AI Money
        mDisplay = new JLabel("", SwingConstants.CENTER);
        mDisplay.setFont(new Font("Arial", Font.BOLD, 14));
        mDisplay.setForeground(new Color(40,50,40));
        mDisplay.setBounds(x+37,y-25,50,20);
        GUI.panel.add(mDisplay);

        RoundedLabel border = new RoundedLabel(20, new Color(210,210,210));
        border.setBounds(x+22, y-27, 80, 25);
        GUI.panel.add(border);


        //AI Icon
        String filename = "icons/character" + Integer.toString(index + 1) + ".jpg";
        ImageIcon pic = new ImageIcon(new ImageIcon(filename).getImage().getScaledInstance(125,125,Image.SCALE_DEFAULT));
        ImagePanel icon = new ImagePanel(pic.getImage());
        icon.setOpaque(false);
        icon.setBounds(x,y,125,125);
        GUI.panel.add(icon);


    }
    
}
