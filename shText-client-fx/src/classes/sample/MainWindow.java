package classes.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/fxml/main.fxml"));
        stage.setTitle("小说管理");
        stage.setScene(new Scene(root, 900,600));
        stage.show();
    }

}
