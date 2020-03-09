package emma.views;

import com.emma.Evento;
import com.emma.Reloj;
import com.emma.Tarea;
import emma.logic.Logica;
import emma.logic.services.ObtenerCarpetasService;
import emma.logic.services.ObtenerCorreosService;
import emma.models.EmailTreeItem;
import emma.models.Mensaje;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.docgene.help.JavaHelpFactory;
import org.docgene.help.gui.jfx.JFXHelpContentViewer;

import javax.mail.MessagingException;
import java.io.File;
import java.net.URL;
import java.util.*;

public class PantallaPrincipalController extends BaseController implements Initializable {

    @FXML
    private TreeView treev_carpetas;

    @FXML
    private Button bt_responder;

    @FXML
    private TableView<Mensaje> tv_mensajes;

    @FXML
    private WebView wv_html;

    @FXML
    private Reloj reloj;

    private WebEngine webEn;
    private Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        PantallaRegistroController registro_controller =
                (PantallaRegistroController) cargarDialogo("PantallaRegistro.fxml", 600, 400);
        registro_controller.getStage().setResizable(false);
        registro_controller.abrirDialogo(true);

        reloj.start();
        reloj.addEvento(new Evento() {
            @Override
            public void ejecuta(Tarea tarea) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("AVISO");
                String hora = tarea.getSfecha();
                alert.setHeaderText(hora);
                alert.setContentText(tarea.getDescripcion());
                alert.show();

            }
        });

        ObtenerCarpetasService obtenerCarpetasService = new ObtenerCarpetasService();
        obtenerCarpetasService.start();
        obtenerCarpetasService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                treev_carpetas.setRoot(obtenerCarpetasService.getValue());
                treev_carpetas.setShowRoot(false);
            }
        });

        treev_carpetas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                ObtenerCorreosService obtenerCorreosService =
                        new ObtenerCorreosService(((EmailTreeItem) treev_carpetas.getSelectionModel().getSelectedItem()).getFolder());
                obtenerCorreosService.start();

                obtenerCorreosService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        tv_mensajes.setItems(obtenerCorreosService.getValue());
                    }
                });
            }
        });

        tv_mensajes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mensaje>() {
            @Override
            public void changed(ObservableValue observableValue, Mensaje oldValue, Mensaje newValue) {
                webEn = wv_html.getEngine();
                try {
                    if (newValue != null)
                        webEn.loadContent(newValue.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //poner en negrita mensajes no leídos que por cierto no funciona
        tv_mensajes.setRowFactory(new Callback<TableView<Mensaje>, TableRow<Mensaje>>() {
            @Override
            public TableRow<Mensaje> call(TableView<Mensaje> tvMensaje) {
                return new TableRow<>() {
                    @Override
                    protected void updateItem(Mensaje m, boolean b) {
                        super.updateItem(m, b);
                        if(m!=null){
                            if(!m.isRead()){
                                setStyle("-fx-font-weight:bold");
                            }else
                                setStyle("");
                        }
                    }
                };
            }
        });

    }

    public void stopReloj(){
        reloj.finalizar();
    }

    public void nueva_cuenta (ActionEvent actionEvent){

        PantallaRegistroController registro_controller =
                (PantallaRegistroController) cargarDialogo("PantallaRegistro.fxml", 600, 400);
        registro_controller.getStage().setResizable(false);
        registro_controller.abrirDialogo(true);
        treev_carpetas.setRoot(logica.cargarCarpetas());
    }

    public void borrar_cuenta(ActionEvent actionEvent){

        PantallaConfigController config_controller =
                (PantallaConfigController) cargarDialogo("PantallaConfig.fxml", 500, 400);
        config_controller.getStage().setResizable(false);
        config_controller.abrirDialogo(true);
        treev_carpetas.setRoot(logica.cargarCarpetas());

    }

    public void borrar_email (ActionEvent actionEvent){

        Mensaje m = tv_mensajes.getSelectionModel().getSelectedItem();
        EmailTreeItem email_tree = (EmailTreeItem) treev_carpetas.getSelectionModel().getSelectedItem();

        if (m == null) {
            Alert alert_null = new Alert(Alert.AlertType.WARNING);
            alert_null.setTitle("ERROR");
            alert_null.setContentText("No hay mensajes seleccionados");
            alert_null.showAndWait();
        } else {
            logica.borrar_email(m, email_tree);
            tv_mensajes.setItems(logica.obtenerCorreos(((EmailTreeItem) treev_carpetas.getSelectionModel().getSelectedItem()).getFolder()));
        }
    }

    public void redactar_email (ActionEvent actionEvent){

        PantallaCorreoController correo_controller =
                (PantallaCorreoController) cargarDialogo("PantallaCorreo.fxml", 600, 400);
        correo_controller.getStage().setResizable(false);
        correo_controller.abrirDialogo(true);

    }

    public void responder_email (ActionEvent actionEvent){

        PantallaCorreoController correo_controller =
                (PantallaCorreoController) cargarDialogo("PantallaCorreo.fxml", 600, 400);
        correo_controller.getStage().setResizable(false);
        if(tv_mensajes.getSelectionModel().getSelectedItem()!=null) { //lanzar mensaje
            try {
                correo_controller.setAsunto("RE: " + tv_mensajes.getSelectionModel().getSelectedItem().getSubject());
                correo_controller.setTo(tv_mensajes.getSelectionModel().getSelectedItem().getFrom());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        correo_controller.abrirDialogo(true);
    }

    public void reenviar_email (ActionEvent actionEvent){

        PantallaCorreoController correo_controller =
                (PantallaCorreoController) cargarDialogo("PantallaCorreo.fxml", 600, 400);
        correo_controller.getStage().setResizable(false);

        if(tv_mensajes.getSelectionModel().getSelectedItem()!=null){ //lanza mensaje
            try {
                correo_controller.setAsunto("RE: " + tv_mensajes.getSelectionModel().getSelectedItem().getSubject());
                correo_controller.setContent("\n" +
                        "................................................................................................................................................."
                        + tv_mensajes.getSelectionModel().getSelectedItem().getContent());
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        correo_controller.abrirDialogo(true);
    }

    public void tema_claro(ActionEvent event){
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        bt_responder.setStyle("-fx-text-fill: black"); //probando
    }
    public void tema_oscuro(ActionEvent event){
        Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
        bt_responder.setStyle("-fx-text-fill: white"); //probando
    }

    public void combo (ActionEvent actionEvent){

        PantallaComboController combo_controller =
                (PantallaComboController) cargarDialogo("PantallaCombo.fxml", 600, 200);
        combo_controller.getStage().setResizable(false);
        combo_controller.abrirDialogo(true);

        }

    public void config_tareas(ActionEvent actionEvent) {

        PantallaTareasController tareas_controller =
                (PantallaTareasController) cargarDialogo("PantallaTareas.fxml", 600,400);
        tareas_controller.getStage().setResizable(false);
        tareas_controller.abrirDialogo(true);

        ArrayList<Tarea> tareas = new ArrayList<>();

        for (int i = 0; i<logica.getTareas().size(); i++){
            tareas.add(logica.getTareas().get(i));
        }

        reloj.sincronizar_lista(tareas);

    }

    public void exportar_email(ActionEvent actionEvent) { //exporta el correo seleccionado

        Mensaje m = tv_mensajes.getSelectionModel().getSelectedItem();

        if (m==null)
        {
            Alert alert_null = new Alert(Alert.AlertType.WARNING);
            alert_null.setTitle("ERROR");
            alert_null.setContentText("No hay mensajes seleccionados");
            alert_null.showAndWait();

        }else{
            File file = logica.getFile();

            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(logica.get_mensaje_informe(m));
            Map<String,Object> parametros = new HashMap<>();
            JasperPrint print = null;
            try {
                print = JasperFillManager.fillReport(getClass().getResourceAsStream("/emma/informesjasper/InformeCorreo.jasper"), parametros, jr);
                JasperExportManager.exportReportToPdfFile(print, file.toPath().toString());

            } catch (JRException e) {
                e.printStackTrace();
            }

        }
    }

    public void exportar_listaCorreos(ActionEvent actionEvent){ //imprime la lista de correos de una carpeta seleccionada

        if (treev_carpetas.getSelectionModel().getSelectedItem()==null)
        {
            Alert alert_null = new Alert(Alert.AlertType.WARNING);
            alert_null.setTitle("ERROR");
            alert_null.setContentText("No hay carpetas seleccionados");
            alert_null.showAndWait();
        }else{

            File file = logica.getFile();

            String folder = ((EmailTreeItem) treev_carpetas.getSelectionModel().getSelectedItem()).getFolder().toString();
            List<Mensaje> lista = new ArrayList<Mensaje>();
            lista = tv_mensajes.getItems();
            JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(logica.get_informes_carpetas(lista, folder));
            Map<String,Object> parametros = new HashMap<>();
            JasperPrint print = null;
            try {
                print = JasperFillManager.fillReport(getClass().getResourceAsStream("/emma/informesjasper/ListaCorreos.jasper"), parametros, jr);
                JasperExportManager.exportReportToPdfFile(print, file.toPath().toString());
            } catch (JRException e) {
                e.printStackTrace();
            }

        }

        Alert alert_null = new Alert(Alert.AlertType.INFORMATION);
        alert_null.setTitle("Éxito");
        alert_null.setContentText("Informe creado ;)");
        alert_null.showAndWait();

    }

    public void exportar_correos_email(ActionEvent actionEvent){ //exporta todos los correos de una cuenta

        PantallaExportarController imprimir_controller =
                (PantallaExportarController) cargarDialogo("PantallaExportar.fxml", 400, 200);
        imprimir_controller.getStage().setResizable(false);
        imprimir_controller.abrirDialogo(true);

        Alert alert_null = new Alert(Alert.AlertType.INFORMATION);
        alert_null.setTitle("Éxito");
        alert_null.setContentText("Informe creado ;)");
        alert_null.showAndWait();
    }

    public void ayuda(ActionEvent actionEvent){
        try {
            URL url = new File("help/articles.zip").toURI().toURL();
            JavaHelpFactory factory = new JavaHelpFactory(url);
            factory.create();
            JFXHelpContentViewer viewer = new JFXHelpContentViewer();
            factory.install(viewer);
            viewer.getHelpWindow(getStage(), "Ayuda Correo", 900, 600);
            viewer.showHelpDialog(350, 50);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}







