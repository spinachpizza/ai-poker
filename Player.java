import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Player {
    
    public JLabel mDisplay, betAmountLabel, handDisplay;
    public JButton callButton;
    public RoundedButton betButton, foldButton, upButton, downButton;

    public ImagePanel card1, card2;

    public int balance = 1000;

    public int currentBet; //Holds current bet amount of current stage
    public int myPot;   //Holds amount bet the whole round 
    public int index;

    public int betAmount;

    public String[] cards = new String[2];


    public Player(int index)
    {
        this.index = index;
    }

    public void GameSetup()
    {
        theGUI();
    }



    public void RoundSetup()
    {
        GetStarterCards();
        unHideCards();
        UpdateHandDisplay();

        DisableButtons();

        currentBet = 0;
        myPot = 0;

        UpdateMoney();
    }   


    public void GetStarterCards()
    {
        cards[0] = Main.getCard();
        cards[1] = Main.getCard();
    }




    public void DisplayCards()
    {
        ImageIcon card = new ImageIcon(new ImageIcon("cards/" + cards[0] + ".png").getImage().getScaledInstance(120,180, Image.SCALE_DEFAULT));
        card1 = new ImagePanel(card.getImage());
        card1.setOpaque(false);
        card1.setBounds(325, 490, 120, 180);
        GUI.panel.add(card1);

        card = new ImageIcon(new ImageIcon("cards/" + cards[1] + ".png").getImage().getScaledInstance(120,180, Image.SCALE_DEFAULT));
        card2 = new ImagePanel(card.getImage());
        card2.setOpaque(false);
        card2.setBounds(455, 490, 120, 180);
        GUI.panel.add(card2);
    }


    public void SmallBlind()
    {
        Main.GlobalPot += 2;
        balance -= 2;

        currentBet = 2;
        myPot = 2;

        UpdateMoney();
    }


    public void BigBlind()
    {
        Main.GlobalPot += 4;
        balance -= 4;

        currentBet = 4;
        myPot = 4;

        UpdateMoney();
    }


    public void Call()
    {  
        int amount = Main.CurrentBet - currentBet; //Take off already betted amount
        currentBet += amount; //Increase current bet by call amount

        //If they dont have enough money to call just go all in (simpler than doing a 2nd pot)
        if(balance < amount)
        {
            Main.GlobalPot += balance;
            balance = 0;

        } else {
            Main.GlobalPot += amount; //Update global money pot
            balance -= amount;  //Update current balance
        }

        UpdateMoney();
    }


    public void Fold()
    {
        HideCards();
        Main.players[index] = -1;

        //If this player was the raised variable (meaning they were the last one to riase and they are to play last in that round)
        //Then this variable must be assigned to the next available person
        if(Main.raised == index)
        {
            Main.NewRaised(index);
        }
    }


    public void Raise(int amount)
    {
        balance -= amount;

        Main.raised = index;
        Main.GlobalPot += amount;
        Main.CurrentBet = amount;

        currentBet = Main.CurrentBet;

        UpdateMoney();
    }


    public void HideCards()
    {
        card1.setImage(null);
        card2.setImage(null);
    }



    public void unHideCards()
    {
        ImageIcon card = new ImageIcon(new ImageIcon("cards/" + cards[0] + ".png").getImage().getScaledInstance(120,180, Image.SCALE_DEFAULT));
        card1.setImage(card.getImage());
        card = new ImageIcon(new ImageIcon("cards/" + cards[1] + ".png").getImage().getScaledInstance(120,180, Image.SCALE_DEFAULT));
        card2.setImage(card.getImage());
    }




    public void ResetCurrBet()
    {
        currentBet = 0;
    }


    public void DisableButtons()
    {
        foldButton.setEnabled(false);
        betButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }


    public void EnableButtons()
    {
        foldButton.setEnabled(true);
        betButton.setEnabled(true);
        upButton.setEnabled(true);
        downButton.setEnabled(true);
    }


    public void UpdateMoney()
    {
        mDisplay.setText("$" + Integer.toString(balance));
        
        GUI.UpdateMoneyPotDisplay();
    }



    public void startTurn()
    {
        betAmount = Main.CurrentBet;
        EnableButtons();
        UpdateBetAmount();
        UpdateHandDisplay();
    }


    public void endTurn()
    {
        DisableButtons();
        Main.NextTurn(false);
    }


    public void UpdateBetAmount()
    {
        betAmountLabel.setText("$" + String.valueOf(betAmount));
    }


    public void UpdateHandDisplay()
    {
        String hand = "";
        if(Main.currentStage > 0)
        {
            String[] cardSet = new String[Main.currentStage+4];
            //Assign player cards to the set
            cardSet[0] = cards[0];
            cardSet[1] = cards[1];
            //Assign the table cards to the set
            for(int i=0; i<Main.currentStage+2; i++)
            {
                cardSet[i+2] = Main.tableCards[i];
            }
            hand = PokerHands.valueToName(PokerHands.getCardValue(cardSet));

        } else {
            hand = PokerHands.valueToName(PokerHands.getCardValue(cards));
        }

        handDisplay.setText(hand);
    }


    private void Bet()
    {
        if(betAmount == Main.CurrentBet)
        {
            Call();

        } else {
            Raise(betAmount);
        }
    }


    private void IncreaseBet()
    {
        if(betAmount <= 100)
        {
            betAmount ++;
            UpdateBetAmount(); 
        }

        if(betAmount == Main.CurrentBet)
        {
            betButton.setText("CALL");
        } else {
            betButton.setText("BET");
        }
    }

    private void DecreaseBet()
    {
        if(betAmount >= 1 && betAmount > Main.CurrentBet)
        {
            betAmount --;
            UpdateBetAmount();
        }

        if(betAmount == Main.CurrentBet)
        {
            betButton.setText("CALL");
        } else {
            betButton.setText("BET");
        }
    }




    public void theGUI()
    {
        betButton = new RoundedButton("CALL",7,new Color(95,115,100));
        betButton.setBackground(Color.WHITE);
        betButton.setBorderPainted(false);
        betButton.setBounds(195,630,100,30);
        betButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Bet();
                endTurn();
            }
         });
        GUI.panel.add(betButton);


        JLabel upButtonText = new JLabel("+", SwingConstants.CENTER);
        upButtonText.setBounds(296,630,15,12);
        GUI.panel.add(upButtonText);

        JLabel downButtonText = new JLabel("-", SwingConstants.CENTER);
        downButtonText.setBounds(297,645,15,12);
        GUI.panel.add(downButtonText);

    
        upButton = new RoundedButton("", 3, new Color(95,115,100));
        upButton.setBackground(Color.WHITE);
        upButton.setBorderPainted(false);
        upButton.setBounds(297,630,15,14);
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IncreaseBet();
            }
         });
        GUI.panel.add(upButton);


        downButton = new RoundedButton("", 3, new Color(95,115,100));
        downButton.setBackground(Color.WHITE);
        downButton.setBorderPainted(false);
        downButton.setBounds(297,646,15,14);
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DecreaseBet();
            }
         });
        GUI.panel.add(downButton);


        foldButton = new RoundedButton("FOLD",7,new Color(95,115,100));
        foldButton.setBackground(Color.WHITE);
        foldButton.setBorderPainted(false);
        foldButton.setBounds(605,630, 100,30);
        foldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Fold();
                endTurn();
            }
         });
        GUI.panel.add(foldButton);


        betAmountLabel = new JLabel("$0", SwingConstants.LEFT);
        betAmountLabel .setFont(new Font("Arial", Font.BOLD, 15));
        betAmountLabel .setForeground(new Color(190,190,190));
        betAmountLabel .setBounds(230,600,50,20);
        GUI.panel.add(betAmountLabel);


        DisplayCards();


        //lines
        RoundedLabel line1 = new RoundedLabel(5,new Color(210,210,210));
        line1.setBounds(30,620,275,5);
        GUI.panel.add(line1);

        RoundedLabel line2 = new RoundedLabel(5,new Color(210,210,210));
        line2.setBounds(595,620,275,5);
        GUI.panel.add(line2);


        JLabel cashTitle = new JLabel("MONEY", SwingConstants.LEFT);
        cashTitle.setFont(new Font("Arial", Font.BOLD, 13));
        cashTitle.setForeground(new Color(190,190,190));
        cashTitle.setBounds(30,600,50,20);
        GUI.panel.add(cashTitle);


        mDisplay = new JLabel("", SwingConstants.LEFT);
        mDisplay.setFont(new Font("Arial", Font.BOLD, 15));
        mDisplay.setForeground(new Color(190,190,190));
        mDisplay.setBounds(30,625,50,20);
        GUI.panel.add(mDisplay);


        handDisplay = new JLabel("", SwingConstants.CENTER);
        handDisplay.setFont(new Font("Arial", Font.BOLD, 22));
        handDisplay.setForeground(new Color(220,220,220));
        handDisplay.setBounds(300,672,300,40);
        GUI.panel.add(handDisplay);

    }
}
