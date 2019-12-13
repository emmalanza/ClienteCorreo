package emma.views;

import emma.logic.Logica;
import emma.models.Cuenta;
import emma.models.Mensaje;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class PantallaComboController extends BaseController implements Initializable {

    @FXML
    private ComboBox<String> cb_correos;

    private ObservableList<Mensaje> mensajes = FXCollections.observableArrayList();
    private ObservableList<String> mensajes_cb = FXCollections.observableArrayList();
    private Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {

            mensajes = logica.obtenerCorreos(((Cuenta)logica.getCuentas().get(0)).getStore().getFolder("INBOX"));

            for(int i = 0; i<mensajes.size(); i++){
                String from = mensajes.get(i).getFrom();
                String fecha = mensajes.get(i).getReceivedDate();
                String mensaje =  from + ", " + fecha;
                mensajes_cb.add(mensaje);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        cb_correos.setItems(mensajes_cb);

    }

    public void bt_ok(ActionEvent actionEvent){

        String asunto = "";

        try {
             asunto = mensajes.get(cb_correos.getSelectionModel().getSelectedIndex()).getSubject();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFORMACION");
        alert.setHeaderText("Asunto:");
        alert.setContentText(asunto);
        alert.show();

    }
}

