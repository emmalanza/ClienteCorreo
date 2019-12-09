package emma.views;

import emma.logic.Logica;
import emma.logic.services.ObtenerCarpetasService;
import emma.logic.services.ObtenerCorreosService;
import emma.models.EmailTreeItem;
import emma.models.Mensaje;
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
import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class PantallaPrincipalController extends BaseController implements Initializable {

    @FXML
    private TreeView treev_carpetas;

    @FXML
    private TableView<Mensaje> tv_mensajes;

    @FXML
    private WebView wv_html;

    private WebEngine webEn;
    private Logica logica = Logica.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        PantallaRegistroController registro_controller =
                (PantallaRegistroController) cargarDialogo("PantallaRegistro.fxml", 600, 400);
        registro_controller.getStage().setResizable(false);
        registro_controller.abrirDialogo(true);

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

        //poner en negrita mensajes no leídos
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

    public void nueva_cuenta (ActionEvent actionEvent){

        PantallaRegistroController registro_controller =
                (PantallaRegistroController) cargarDialogo("PantallaRegistro.fxml", 600, 400);
        registro_controller.getStage().setResizable(false);
        registro_controller.abrirDialogo(true);
        treev_carpetas.setRoot(logica.cargaCarpetas());
    }

    public void borrar_cuenta(ActionEvent actionEvent){

        PantallaConfigController config_controller =
                (PantallaConfigController) cargarDialogo("PantallaConfig.fxml", 500, 400);
        config_controller.getStage().setResizable(false);
        config_controller.abrirDialogo(true);
        treev_carpetas.setRoot(logica.cargaCarpetas());

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
                correo_controller.setTo(tv_mensajes.getSelectionModel().getSelectedItem().getTo());
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
                correo_controller.setTo(tv_mensajes.getSelectionModel().getSelectedItem().getTo());
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

    public void mover_email (ActionEvent actionEvent){

        }

    }







