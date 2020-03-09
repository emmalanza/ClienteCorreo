package emma;

import emma.views.PantallaPrincipalController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/PantallaPrincipal.fxml"));
        Parent root = fxmlLoader.load();
        PantallaPrincipalController pantallaPrincipalController = (PantallaPrincipalController) fxmlLoader.getController();
        Scene scene = new Scene(root, 700, 800);
        scene.getStylesheets().add(getClass().getResource("views/css/tema.css").toExternalForm());
        stage.setTitle("Pantalla principal");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                pantallaPrincipalController.stopReloj();

            }
        });
    }

    public static void main(String [] args){
        launch(args);
    }
}
