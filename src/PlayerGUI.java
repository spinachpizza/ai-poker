import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Circle;

public class PlayerGUI {

    private final int[][] coords = {
        {427, 385}, {75, 315},
        {75, 95}, {427, 25},
        {780, 95}, {780, 315}
    };

    private final int[][]chipCoords = {
        {580, 375}, {220, 355},
        {210, 165}, {385, 135}, //565
        {735, 180}, {745, 295}
    };

    private Pane root;
    private Pane chipFrame;

    private ImageView card1;
    private ImageView card2;
    private Label balanceLabel;
    private Label myPotDisplay;

    private GUI gui;
    private Player player;
    
    public PlayerGUI(Player player, GUI gui) {
        this.player = player;
        this.gui = gui;
        root = gui.getRoot();
        setup();
    }

    private void setup() {
        String name = player.getName();
        int balance = player.getBalance();
        int id = player.getID();
        int iconNumber = getIcon(name);


        createPlayerFrame(name, balance, coords[id][0], coords[id][1], iconNumber);
        createChipFrame(chipCoords[id][0], chipCoords[id][1]);
    }

    private final void createPlayerFrame(String name, int balance, int x, int y, int iconNumber) {
        Pane frame = new Pane();
        frame.setPrefSize(145, 145);

        Circle border = new Circle(72, 70, 60);  // radius a bit bigger than clip
        border.setStroke(Color.BLACK); 

        ImageView icon = gui.createImage("resources/icons/character"+String.valueOf(iconNumber)+".jpg", 130, 130, 7, 5);
        Circle clip = new Circle(65, 65, 55);
        icon.setClip(clip);


        Label nameLabel = gui.createLabel(name, 100, 30, 22, 110);
        nameLabel.setStyle(
            nameLabel.getStyle() + "-fx-background-color: #CCCCCC;" // Background color
        );

        balanceLabel = gui.createLabel("$1000", 70, 30, 37, 135);
        balanceLabel.setStyle(
            balanceLabel.getStyle() + "-fx-background-color: #BBBBBB;" // Background color
        );

        card1 = gui.createImage("resources/cards/back.png", 60, 85, 10, 35);
        card1.setRotate(-10);
        card1.setRotationAxis(Rotate.Z_AXIS);

        card2 = gui.createImage("resources/cards/back.png", 60, 85, 74, 35);
        card2.setRotate(10);
        card2.setRotationAxis(Rotate.Z_AXIS);

        frame.getChildren().addAll(border, icon, card1, card2, balanceLabel, nameLabel);
        frame.setLayoutX(x);
        frame.setLayoutY(y);
        root.getChildren().add(frame);
    }

    private final void createChipFrame(int x, int y) {
        chipFrame = new Pane();
        chipFrame.setPrefSize(50,50);
        chipFrame.setLayoutX(x);
        chipFrame.setLayoutY(y);

        ImageView chip = gui.createImage("/resources/icons/chip.png", 30, 30, 10, 0);

        myPotDisplay = gui.createLabel("$0", 50, 20, 0, 30);
        myPotDisplay.setStyle(
            myPotDisplay.getStyle() + "-fx-background-color: rgba(204, 204, 204, 0.6);"
        );

        chipFrame.getChildren().addAll(chip, myPotDisplay);
        root.getChildren().add(chipFrame);
    }

    public final void showCards(String[] cards) {
        card1.setImage(gui.getImage("/resources/cards/" + cards[0] + ".png"));
        card2.setImage(gui.getImage("/resources/cards/" + cards[1] + ".png"));
    }

    public final void flipCards() {
        card1.setImage(gui.getImage("/resources/cards/back.png"));
        card2.setImage(gui.getImage("/resources/cards/back.png"));
    }

    public final void hideCards() {
        card1.setImage(null);
        card2.setImage(null);
    }

    public final void showChip() {
        chipFrame.setVisible(true);
    }

    public final void hideChip() {
        chipFrame.setVisible(false);
    }

    public final void updatePotDisplay(int betAmount) {
        myPotDisplay.setText("$"+betAmount);
    }

    public final void updateBalance(int newBalance) {
        balanceLabel.setText("$" + String.valueOf(newBalance));
    }



    private int getIcon(String name) {
        switch(name) {
            case "Toby":
                return 1;
            case "Abigail":
                return 2;
            case "Jess":
                return 3;
            case "Tom":
                return 4;
            case "Katie":
                return 5;
            case "Steve":
                return 6;
            default:
                return 0;
        }
    }
}
