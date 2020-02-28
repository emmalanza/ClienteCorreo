package emma.views;

import emma.logic.Logica;
import emma.models.Cuenta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PantallaImprimirController extends BaseController implements Initializable{

    @FXML
    private Label label;

    @FXML
    private ComboBox<String> cb_correos;

    Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        label.setText("Seleccione una cuenta para imprimir todos sus correos.");

        for(int i = 0; i<logica.getCuentas().size(); i++){
            cb_correos.getItems().add(((Cuenta)logica.getCuentas().get(i)).getEmail());
        }

        cb_correos.getSelectionModel().selectFirst();

    }

    public void imprimir(ActionEvent actionEvent){

        File file = logica.getFile();

        JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(logica.get_informes_correo(cb_correos.getSelectionModel().getSelectedItem()));
        Map<String,Object> parametros = new HashMap<>();
        JasperPrint print = null;
        try {
            print = JasperFillManager.fillReport(getClass().getResourceAsStream("/emma/informesjasper/TodosLosCorreos.jasper"), parametros, jr);
            JasperExportManager.exportReportToPdfFile(print, file.toPath().toString());
        } catch (JRException e) {
            e.printStackTrace();
        }

    }
}
