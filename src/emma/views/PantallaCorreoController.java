package emma.views;
import emma.logic.Logica;
import emma.models.Cuenta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import java.net.URL;
import java.util.ResourceBundle;

public class PantallaCorreoController extends BaseController implements Initializable {

    @FXML
    private ComboBox<String> cb_from;

    @FXML
    private TextField tf_to;

    @FXML
    private TextField tf_asunto;

    @FXML
    private HTMLEditor html_editor;

    Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for(int i = 0; i<logica.getCuentas().size(); i++){
           cb_from.getItems().add(((Cuenta)logica.getCuentas().get(i)).getEmail());
        }

       // cb_from.setSelectionModel();
    }

    public void enviar(ActionEvent actionEvent){
        String email = cb_from.getSelectionModel().getSelectedItem();
        if(logica.redactar_email(email, tf_to.getText(), tf_asunto.getText(), html_editor)){
            getStage().close();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("No se puede enviar el email.");
            alert.setContentText("Compruebe su conexión a Internet.");
            alert.show();
        }

    }

    public void setTo(String to){
        tf_to.setText(to);
    }
    public void setAsunto (String asunto){
        tf_asunto.setText(asunto);
    }
    public void setContent(String content) { html_editor.setHtmlText(content);}
}
