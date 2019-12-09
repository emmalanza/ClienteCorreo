package emma.views;
import emma.logic.Logica;
import emma.models.Cuenta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class PantallaConfigController extends BaseController implements Initializable {

    @FXML
    private ListView<String> lv_correos;

    private ObservableList<String> emails = FXCollections.observableArrayList();
    Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(int i = 0; i< logica.getCuentas().size(); i++){
            emails.add(((Cuenta)logica.getCuentas().get(i)).getEmail());
        }

        lv_correos.setItems(emails);

    }

    public void borrar_cuenta (ActionEvent actionEvent){

        if(lv_correos.getSelectionModel().getSelectedItem()!=null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("BORRADO DE CUENTAS");
            alert.setHeaderText("ATENCIÓN");
            alert.setContentText("¿Está seguro de que desea borrar la cuenta?");
            alert.showAndWait();

            if((alert.getResult() == ButtonType.OK)){
                logica.borrarCuentas(lv_correos.getSelectionModel().getSelectedItem());
                //hacer que desaparez el email de la lista!
                alert.setContentText("Cuenta correctamente.");
                alert.show();
                }else{
                alert.setContentText("Borrado cancelado.");
                alert.show();
            }

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("NO HAY CUENTAS SELECCIONADAS");
            alert.setContentText("Seleccione una cuenta.");
            alert.show();
        }

    }

    public void salir(ActionEvent actionEvent){
        getStage().close();
    }
}
