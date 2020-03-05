package emma.views;

import com.emma.Reloj;
import com.emma.Tarea;
import emma.logic.Logica;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class PantallaTareasController extends BaseController implements Initializable {

    @FXML
    private TableView<Tarea> tv_tareas;

    @FXML
    private TextField tf_desc;

    @FXML
    private TextField tf_h;

    @FXML
    private TextField tf_m;

    @FXML
    private TextField tf_s;

    @FXML
    private DatePicker dp_fecha;

    private Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tv_tareas.setItems(logica.getTareas());

    }


    public void aniadir_tarea(ActionEvent actionEvent) {

        int h,m,s; Date fecha; LocalDate localDate;
        h = Integer.parseInt(tf_h.getText());
        m = Integer.parseInt(tf_m.getText());
        s = Integer.parseInt(tf_s.getText());
        localDate = dp_fecha.getValue();
        fecha = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Tarea t = new Tarea(fecha,h,m,s, tf_desc.getText());
        logica.anadir_tareas(t);
        tv_tareas.setItems(logica.getTareas());

        tf_h.setText("");
        tf_m.setText("");
        tf_s.setText("");
        tf_desc.setText("");

    }

    public void eliminar_tarea(ActionEvent actionEvent) {

        logica.eliminar_tareas(tv_tareas.getSelectionModel().getSelectedItem());
        tv_tareas.setItems(logica.getTareas());
    }

}
