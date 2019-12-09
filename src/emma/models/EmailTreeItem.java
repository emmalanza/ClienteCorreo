package emma.models;

import javafx.scene.control.TreeItem;
import javax.mail.Folder;
import javax.mail.Store;

public class EmailTreeItem extends TreeItem<String> {

    private String nombre;
    private Cuenta cuenta;
    private Folder folder;
    private Store store;

    public Folder getFolder(){return folder;}
    public Store getStore(){return store;}

    public EmailTreeItem(String nombre, Cuenta cuenta,Folder folder, Store store) {
        super(nombre);
        this.nombre = nombre;
        this.cuenta = cuenta;
        this.folder = folder;
        this.store = store;

    }




}
