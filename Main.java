import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;
import java.awt.event.*;

public class Main {


    public static final int AIamount = 5;
    public static final int playerAmount = 6;
    public static AI[] AIlist = new AI[AIamount];

    public static Player player;


    public static int GlobalPot;
    public static int CurrentBet;

    //Stores the index of the player that raised the bet(starts off as big blind)
    public static int raised;

    public static int currentStage;

    //Holds the current players (index) in an array format
    public static int[] players = {0,1,2,3,4,5};
    public static int currentPlayer;
    public static int[] outList = {-1,-1,-1,-1,-1,-1};

    public AI AI;

    public static boolean[] usedCards = new boolean[52];
    public static String[] cards = {
        "ac","ah","as","ad","2c","2h","2s","2d","3c","3h","3s","3d",
        "4c","4h","4s","4d","5c","5h","5s","5d","6c","6h","6s","6d","7c","7h","7s","7d",
        "8c","8h","8s","8d","9c","9h","9s","9d","0c","0h","0s","0d","jc","jh","js","jd",
        "qc","qh","qs","qd","kc","kh","ks","kd"
    };


    public static String[] tableCards = new String[5];

    public static String winnerMsg;

    
    public static void main(String[] args)
    {
        GameSetup();
    }



    public static void GameSetup()
    {
        //Setup main GUI elements
        GUI.setupGUI();

        //Initialise the AI
        SetupAI();

        player = new Player(5);
        player.GameSetup();

        StartRound();

        GUI.GUIsettings();
    }

    public static void SetupAI()
    {
        String[] nameArray = {"Max","Lauren","Amanda","Jake","Rose"};
        int[][] coordArray = {{10,265},{110,45},{387,30},{655,45},{755,265}};

        for(int i = 0; i<AIamount; i++)
        {
            AIlist[i] = new AI(i,nameArray[i],coordArray[i]);
        }
    }


    public static void StartRound()
    {
        //Reset money pot
        GlobalPot = 0; 
        GUI.UpdateMoneyPotDisplay();

        GUI.winnerDisplay.setText("");


        //Reset variables
        currentStage = 0;


        //Reset cards used array
        for(int i=0; i<usedCards.length; i++)
        {
            usedCards[i] = false;
        }

        //Reset player array removing out players
        for(int i=0; i<playerAmount; i++)
        {
            if(outList[i] == -1)
            {
                players[i] = i;
            } else {
                players[i] = -1;
            }
        }


        //Reset cards etc and AI cards
        for(int i=0; i<5; i++)
        {
            tableCards[i] = "";

            if(players[i] != -1)
            {
                AIlist[i].RoundSetup();
            }
        }

        GUI.ResetTableCards();

        SetTableCards(); //Assign the table cards

        
        player.RoundSetup();


        //Choose random dealer
        currentPlayer = ThreadLocalRandom.current().nextInt(0,6);
        
        Blinds();


        CurrentBet = 4; //Default starting value of big blind
        NextTurn(false);
    }


    public static void Blinds()
    {
        //Do actions for small blind
        //Loops until a player is found to be small blind
        boolean found = false;
        while (found == false)
        {
            currentPlayer ++;
            if(currentPlayer >= playerAmount)
            {
                currentPlayer = 0;
            }
            

            if(players[currentPlayer] == -1)
            {
                break;
            } else if(players[currentPlayer] == player.index)
            {
                found = true;
                player.SmallBlind();
            } else {
                found = true;
                AIlist[players[currentPlayer]].SmallBlind();
            }
        }


        //Do actions for big blind
        //Loops until a player is found to be big blind
        found = false;
        while(found == false)
        {
            currentPlayer ++;
            if(currentPlayer >= playerAmount)
            {
                currentPlayer = 0;
            }
            

            if(players[currentPlayer] == -1)
            {
                break;

            }else if(players[currentPlayer] == player.index)
            {
                found = true;
                player.BigBlind();
            } else {
                found = true;
                AIlist[players[currentPlayer]].BigBlind();
            }

            raised = currentPlayer; //Assigns this player as the one who went first
        }
    }



    //Returns a random card from the deck
    public static String getCard() {

        int randomIndex = 0;

        boolean newCard = false;
        while(newCard == false) {

            //Choose random card number
            randomIndex = ThreadLocalRandom.current().nextInt(0,cards.length);
            
            //Checks if that card has already been used
            if(usedCards[randomIndex] == false)
            {
                newCard = true;
            }
        }

        //Sets the index of that card to be true as it has been used
        usedCards[randomIndex] = true;
        
        //Returns random card
        return cards[randomIndex];
    }



    public static void SetTableCards()
    {
        for(int i=0; i<5; i++)
        {
            tableCards[i] = getCard();
        }
    }



    public static void NextTurn(boolean firstTurn)
    {
        
        currentPlayer ++;
        //If current player goes too high resets to 0 (circular play)
        if(currentPlayer >= playerAmount)
        {
            currentPlayer = 0;
        }

        player.UpdateHandDisplay();


        if(CheckIfAllFolded() != -1)
        {
            CheckWinner();
        
        }else if(players[currentPlayer] == raised && firstTurn == false)
        {
            NextStage();

        //-1 means player has folded or is out
        } else if(players[currentPlayer] == -1)
        {
            NextTurn(false);

        } else if(players[currentPlayer] == player.index)
        {
            player.startTurn();

        } else {

            AIlist[currentPlayer].makeMove();
        }
        
    }


    public static void NextStage()
    {
        currentStage ++;

        Main.NewRaised(currentPlayer);

        //As the nextturn function increases this it must be decremented first
        currentPlayer --;
        //Reset current bet for next stage
        CurrentBet = 5;

        //Reset current bets
        player.ResetCurrBet();
        for(int i=0; i<AIamount; i++)
        {
            AIlist[i].ResetCurrBet();
        }



        if(currentStage == 1)
        {
            //Turn over first 3 cards
            GUI.RevealFirst3();
            NextTurn(true);

        } else if(currentStage == 2)
        {
            //Turn over next (4th) card
            GUI.Reveal4();
            NextTurn(true);

        } else if(currentStage == 3)
        {
            //Turn over final card
            GUI.Reveal5();
            NextTurn(true);

        } else if(currentStage == 4)
        {
            //End game compare cards etc
            CheckWinner();
        }

    }


    public static int PlayersRemaining()
    {
        int count = 0;
        for(int i=0; i<playerAmount; i++)
        {
            if(players[i] != -1)
            {
                count++;
            }
        }

        return count;
    }



    public static int CheckIfAllFolded()
    {
        int count = 0;
        int winnerIndex = 0;
        for(int i=0; i<playerAmount; i++)
        {
            //System.out.println(players[i]);
            if(players[i] == -1)
            {
                count++;
            } else {
                winnerIndex = i;
            }
        }

        //System.out.println(count);
        //System.out.println("Winner index: "+ winnerIndex);

        if(count>=5)
        {
            return winnerIndex;
        } else {
            return -1;
        }
    }


    //If a player folds then the raised variable needs to be assigned to the next playing person
    public static void NewRaised(int index)
    {
        for(int i=0; i<playerAmount; i++)
        {
            if(players[index] == -1)
            {
                index--;
                if(index < 0)
                {
                    index = 5;
                }
            } else {
                raised = index;
                i = playerAmount; //break out of loop
            }
        }
    }



    public static void CheckWinner()
    {
        int highestScore = 0;
        int winnerIndex = 1;

        //Find AI with highest score
        for(int i=0; i<AIamount; i++)
        {
            //Check they havent folded and its not the player
            if(players[i] != -1 && players[i] != player.index)
            {
                //Unhide that AIs cards
                AIlist[i].unHideCards();

                //Get a numerical score for the card set along with the table cards
                String[] cards = {AIlist[i].cards[0], AIlist[i].cards[1], tableCards[0],
                                tableCards[1], tableCards[2], tableCards[3], tableCards[4]};
                int cardValue = PokerHands.getCardValue(cards);
                if(cardValue > highestScore)
                {

                    winnerIndex = i;
                    highestScore = cardValue;
                    winnerMsg = AIlist[winnerIndex].name + " won with a " + PokerHands.valueToName(cardValue);


                //if scores are equal then kicker is required to determine the winner
                } else if(cardValue >= highestScore)
                {
                    //Compares the highest card in their hand to compare
                    //If new comparison AI has higher cards then they are the new winner
                    if(PokerHands.getMaxValue(AIlist[i].cards) > PokerHands.getMaxValue(AIlist[winnerIndex].cards))
                    {
                        winnerIndex = i;
                    }
                }

            }
        }


        //Compare highest AI score with player score
        //Check player hasnt folded
        if(players[5] != -1)
        {
            String[] cards = {player.cards[0], player.cards[1], tableCards[0],
                        tableCards[1], tableCards[2], tableCards[3], tableCards[4]};
            int cardValue = PokerHands.getCardValue(cards);

            if(cardValue > highestScore)
            {
                //Player Wins
                winnerIndex = player.index;
                winnerMsg = "You won with a " + PokerHands.valueToName(cardValue);;

            //If scores are equal kicker is needed
            } else if(cardValue == highestScore)
            {
                //Compare value of cards in hand
                int playerVal = PokerHands.convert(player.cards[0].charAt(0)) + PokerHands.convert(player.cards[1].charAt(0));
                int AIval = PokerHands.convert(AIlist[winnerIndex].cards[0].charAt(0)) + PokerHands.convert(AIlist[winnerIndex].cards[1].charAt(0));
                if(playerVal > AIval)
                {
                    //Player Wins
                    winnerIndex = player.index;
                    winnerMsg = "You won with a " + PokerHands.valueToName(cardValue);;
                }
            }
        }   
        

        EndRound(winnerIndex);
        
    }






    public static void EndRound(int winnerIndex)
    {
        //Check if its the player or AI
        if(winnerIndex == 5)
        {
            player.balance += GlobalPot;
            player.UpdateMoney();

        } else {

            AIlist[winnerIndex].balance += GlobalPot;
            AIlist[winnerIndex].UpdateMoney();
        }

        GUI.winnerDisplay.setText(winnerMsg.toUpperCase());

        GlobalPot = 0; 
        GUI.UpdateMoneyPotDisplay();


        //Remove all players who are out from game
        for(int i=0; i<AIamount; i++)
        {
            if(AIlist[i].balance < 5)
            {
                outList[i] = 1;
            }

        }



        if(player.balance > 5)
        {
            // Create a timer that waits for 10000 milliseconds (10 seconds)
            Timer timer = new Timer(10000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StartRound();
                }
            });

            // Start the timer
            timer.setRepeats(false); // Ensure the timer only runs once
            timer.start();

        } else {
            System.out.println("You are out of money");
        }
    }

}
