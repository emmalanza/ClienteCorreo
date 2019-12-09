package emma;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/PantallaPrincipal.fxml"));
        Scene scene = new Scene(root, 700, 700);
        scene.getStylesheets().add(getClass().getResource("views/css/tema.css").toExternalForm());
        stage.setTitle("Pantalla principal");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String [] args){
        launch(args);
    }
}
