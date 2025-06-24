import java.util.HashMap;
import java.util.Map;

import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class GUI {

    private final int cardWidth = 60;

    private Stage stage;
    private Pane root;

    private ImageView[] tableCards = new ImageView[5];

    private Label handDisplay;

    private final Map<String, Image> imageCache = new HashMap<>();

    private Button callButton;
    private Button raiseButton;
    private Button foldButton;
    private Slider betSlider;

    private Player player;
    
    public GUI(Stage stage) {
        this.stage = stage;
        setup();
    }

    public Pane getRoot() {
        return root;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void reset() {
        for(int i=0; i<5; i++) {
            tableCards[i].setImage(getImage("resources/cards/back.png"));
        }
    }

    public void updateHandDisplay(String text) {
        handDisplay.setText(text);
    }

    public void showTableCards(String[] cards) {

        for(int i=0; i<5; i++) {
            if(cards[i] != null) {
                tableCards[i].setImage(getImage("resources/cards/"+cards[i]+".png"));
            }
        }
    }

    public void disableButtons() {
        callButton.setDisable(true);
        raiseButton.setDisable(true);
        foldButton.setDisable(true);
        betSlider.setDisable(true);
    }

    public void enableButtons() {
        callButton.setDisable(false);
        raiseButton.setDisable(false);
        foldButton.setDisable(false);
        betSlider.setDisable(false);
    }

    public void updateSlider(int balance, int callAmount) {
        betSlider.setMin(callAmount);
        betSlider.setMax(balance);
        betSlider.setValue(callAmount);
    }

    private void setup() {
        root = new Pane();
        stage.setResizable(false);
        root.setStyle(
            "-fx-background-color: #EEEEEE;" // Background color
        );

        ImageView bg = createImage("resources/icons/image.png", 1371, 980, -25, -25);
        root.getChildren().add(bg);

        Pane bar = createBar();
        root.getChildren().add(bar);

        
        ImageView table = createImage("resources/icons/table.png", 900, 564, 50, 25);
        root.getChildren().add(table);

        int x = 330;
        int y = 250;
        for(int i=0; i<5; i++) {
            tableCards[i] = createImage("resources/cards/back.png", cardWidth, cardWidth*1.42, x, y);
            x += cardWidth + 10;
            root.getChildren().add(tableCards[i]);
        }


        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Poker Game");
        stage.show();
    }

    private final Pane createBar() {
        Pane bar = new Pane();
        bar.setPrefSize(500, 150);
        bar.setStyle(
            "-fx-background-radius: 30;" +
            "-fx-background-color: #CCCCCC;" // Background color
        );
        bar.setLayoutX(250);
        bar.setLayoutY(580);

        int width = 140;
        int height = 60;


        Pane bar2 = new Pane();
        bar2.setPrefSize(440, 30);
        bar2.setStyle(
            "-fx-background-radius: 10;" +
            "-fx-background-color: #BBBBBB;" // Background color
        );
        bar2.setLayoutX(30);
        bar2.setLayoutY(12);

        handDisplay = createLabel("THREE OF A KIND", 120, 20, 5, 5);
        handDisplay.setStyle(
            handDisplay.getStyle() + "-fx-text-fill: #A57B01;" // Background color
        );

        Label betDisplay = createLabel("$5", 50, 20, 160, 5);
        betDisplay.setStyle(
            betDisplay.getStyle() + "-fx-background-color: #EEEEEE;" // Background color
        );

        betSlider = new Slider(0, 100, 5);
        betSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            betDisplay.setText("$" + Math.round(newVal.doubleValue()));
        });
        betSlider.setPrefSize(200,10);
        betSlider.setLayoutX(220);
        betSlider.setLayoutY(7);

        bar2.getChildren().addAll(handDisplay, betDisplay, betSlider);


        foldButton = createButton("Fold", width, height, 30, 50);
        foldButton.setOnAction(e -> { player.fold(); });

        callButton = createButton("Call", width, height, 180, 50);
        callButton.setOnAction(e -> { player.call(); });

        raiseButton = createButton("Raise", width, height, 330, 50);
        raiseButton.setOnAction(e -> { player.raise((int) Math.round(betSlider.getValue())); });

        bar.getChildren().addAll(foldButton, callButton, raiseButton, bar2);


        return bar;
    }


    public final Image getImage(String resourcePath) {
        return imageCache.computeIfAbsent(resourcePath, path ->
            new Image(getClass().getResourceAsStream(path))
        );
    }

    public final ImageView createImage(String path, double width, double height, int x, int y) {
        ImageView cardView = new ImageView(getImage(path));
        cardView.setFitWidth(width);  
        cardView.setFitHeight(height);
        cardView.setPreserveRatio(true);
        cardView.setLayoutX(x);
        cardView.setLayoutY(y);
        cardView.setSmooth(true);  // Enables anti-aliasing
        cardView.setCache(true);   // Uses bitmap cache
        cardView.setCacheHint(CacheHint.QUALITY);

        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.7));  // semi-transparent black
        cardView.setEffect(dropShadow);
        
        return cardView;
    }

    public final ImageView createImage(String path, double width, double height) {
        Image cardImage = new Image(getClass().getResourceAsStream(path));
        ImageView cardView = new ImageView(cardImage);
        cardView.setFitWidth(width);  
        cardView.setFitHeight(height);
        cardView.setPreserveRatio(true);

        return cardView;
    }

    private final Button createButton(String text, double width, double height, int x, int y) {
        Button button = new Button(text);
        button.setPrefSize(width, height);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-text-fill: black;"
        );
        return button;
    }

    public final Label createLabel(String text, double width, double height, int x, int y) {
        Label label = new Label(text);
        label.setPrefSize(width, height);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-alignment: center;" +
            "-fx-background-radius: 10;" +
            "-fx-text-fill: black;"
        );
        return label;
    }

}
