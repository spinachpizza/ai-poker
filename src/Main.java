import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GUI gui = new GUI(primaryStage);
        Game game = new Game(gui);
        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}