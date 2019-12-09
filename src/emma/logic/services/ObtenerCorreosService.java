package emma.logic.services;

import emma.logic.Logica;
import emma.models.Mensaje;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Folder;

public class ObtenerCorreosService extends Service<ObservableList<Mensaje>> {

    private Folder folder;

    public ObtenerCorreosService(Folder folder){
        this.folder = folder;
    }

    @Override
    protected Task<ObservableList<Mensaje>> createTask() {
        return new Task<>() {
            @Override
            protected ObservableList<Mensaje> call() {
                return Logica.getInstance().obtenerCorreos(folder);
            }
        };
    }
}
