package pl.lucky;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.lucky.view.GUICreator;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GUICreator creator = new GUICreator();
        creator.createGUI(primaryStage);
    }
}