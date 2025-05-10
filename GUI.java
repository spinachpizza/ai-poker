import javax.swing.*;
import java.awt.*;


public class GUI 
{
    public static JFrame frame;
    public static GradientPanel panel;

    public static JLabel mpotDisplay, winnerDisplay;

    public static ImagePanel[] GUIcards = new ImagePanel[5];

    public static int cardWidth = 100;
    public static int cardHeight = 150;

    public static ImageIcon cardBack;

    public static void setupGUI() 
    {
        //Setup Frame
        frame = new JFrame("Poker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //panel = (JPanel) frame.getContentPane();
        panel = new GradientPanel();
        frame.setContentPane(panel);
        panel.setLayout(null);


        //Money pot
        JLabel mpotTitle = new JLabel("POT", SwingConstants.RIGHT);
        mpotTitle.setFont(new Font("Arial", Font.BOLD, 13));
        mpotTitle.setForeground(new Color(190,190,190));
        mpotTitle.setBounds(820,600,50,20);
        panel.add(mpotTitle);

        mpotDisplay = new JLabel("", SwingConstants.RIGHT);
        mpotDisplay.setFont(new Font("Arial", Font.BOLD, 15));
        mpotDisplay.setForeground(new Color(190,190,190));
        mpotDisplay.setBounds(820,625,50,20);
        panel.add(mpotDisplay);

        //Winner message
        winnerDisplay = new JLabel("", SwingConstants.CENTER);
        winnerDisplay.setFont(new Font("Arial", Font.BOLD, 20));
        winnerDisplay.setForeground(new Color(220,220,220));
        winnerDisplay.setBounds(250,250,400,40);
        GUI.panel.add(winnerDisplay);



        //Setup image icon for back of card
        cardBack = new ImageIcon(new ImageIcon("cards/back.jpg").getImage().getScaledInstance(cardWidth,cardHeight, Image.SCALE_DEFAULT));



        TableCardSetup();
    }


    public static void UpdateMoneyPotDisplay()
    {
        mpotDisplay.setText("$" + String.valueOf(Main.GlobalPot));
    }



    public static void RevealFirst3()
    {
        for(int i=0; i<3; i++)
        {
            String card = Main.tableCards[i];
            ImageIcon cardImg = new ImageIcon(new ImageIcon("cards/" + card + ".png").getImage().getScaledInstance(cardWidth,cardHeight, Image.SCALE_DEFAULT));
            GUIcards[i].setImage(cardImg.getImage());
        } 
    }

    public static void Reveal4()
    {
        String card = Main.tableCards[3];
        ImageIcon cardImg = new ImageIcon(new ImageIcon("cards/" + card + ".png").getImage().getScaledInstance(cardWidth,cardHeight, Image.SCALE_DEFAULT));
        GUIcards[3].setImage(cardImg.getImage());
    }

    public static void Reveal5()
    {
        String card = Main.tableCards[4];
        ImageIcon cardImg = new ImageIcon(new ImageIcon("cards/" + card + ".png").getImage().getScaledInstance(cardWidth,cardHeight, Image.SCALE_DEFAULT));
        GUIcards[4].setImage(cardImg.getImage());
    }

    public static void ResetTableCards()
    {
        for(int i=0; i<5; i++)
        {
            GUIcards[i].setImage(cardBack.getImage());
        }
    }




    public static void TableCardSetup()
    {
        

        for(int i = 0; i<5; i++)
        {
            //Setup cards and orientation leaving a gap of 5 in between
            GUIcards[i] = new ImagePanel(cardBack.getImage());
            GUIcards[i].setOpaque(false);
            int startPos = (900/2) - ((cardWidth+5)*2) - (cardWidth/2);
            GUIcards[i].setBounds(startPos+(i*(cardWidth + 5)), 300, cardWidth, cardHeight);
            panel.add(GUIcards[i]);
        }
        
    }

    public static void GUIsettings()
    {
        //card background
        /*RoundedLabel border = new RoundedLabel(15, new Color(40,40,40));
        border.setBounds(180,290,540,170);
        panel.add(border);*/


        //Background image
        /*ImageIcon cardImg = new ImageIcon(new ImageIcon("icons/background.png").getImage().getScaledInstance(900,750, Image.SCALE_DEFAULT));
        ImagePanel background = new ImagePanel(cardImg.getImage(), 1);
        background.setBounds(0,0,900,750);
        panel.add(background);*/

        //Frame settings

        frame.setSize(900,750);
        frame.setResizable(false);
        //frame.getContentPane().setBackground(new Color(70, 80, 70));
        frame.setVisible(true);
    }
}