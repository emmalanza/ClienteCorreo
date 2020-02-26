package emma.logic.services;

import emma.logic.Logica;
import emma.models.EmailTreeItem;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ObtenerCarpetasService extends Service<EmailTreeItem> {

    @Override
    protected Task<EmailTreeItem> createTask() {
        return new Task<EmailTreeItem>() {
            @Override
            protected EmailTreeItem call() throws Exception {
                return Logica.getInstance().cargarCarpetas();
            }
        };
    }
}
