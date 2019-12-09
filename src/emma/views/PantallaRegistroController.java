package emma.views;

import emma.logic.ConnectException;
import emma.logic.Logica;
import emma.models.Cuenta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import java.net.URL;
import java.util.ResourceBundle;

public class PantallaRegistroController extends BaseController implements Initializable {

    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_email;

    @FXML
    private Button bt_aceptar;

    private Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

       //tf_email.setText("pruebapruebadi@gmail.com"); pf_password.setText("PruebaDi2019");

        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(tf_email, Validator.createEmptyValidator("Este campo no puede estar vacío"));
        validationSupport.registerValidator(pf_password, Validator.createEmptyValidator("Este campo no puede estar vacío"));
        bt_aceptar.disableProperty().bind(validationSupport.invalidProperty());

    }

    @FXML
    void aceptar(ActionEvent event) {

        Cuenta c = new Cuenta(tf_email.getText(), pf_password.getText());
        logica.anadir_cuentas(c);
        String estado = logica.conectar(tf_email.getText(), pf_password.getText()).toString();

        if(estado.equals(ConnectException.FALLO_AUTENTICACION.toString())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Imposible conectar.");
            alert.setContentText("Email o contraseña incorrectas.");
            alert.show();
            logica.getCuentas().remove(c);
        }else if(estado.equals(ConnectException.FALLO_CONEXION.toString())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Imposible conectar.");
            alert.setContentText("Fallo en la conexión.");
            alert.show();
            logica.getCuentas().remove(c);
        }else if(estado.equals(ConnectException.FALLO_ESTADO.toString())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Imposible conectar.");
            alert.setContentText("No se puede conectar.");
            alert.show();
            logica.getCuentas().remove(c);
        }else if(estado.equals(ConnectException.CONEXION_CORRECTA.toString())) {
            getStage().close();
        }
    }
}
