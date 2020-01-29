package emma.logic;

import com.emma.Tarea;
import com.sun.mail.util.MailConnectException;
import emma.models.Cuenta;
import emma.models.EmailTreeItem;
import emma.models.Mensaje;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.web.HTMLEditor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Logica {

    private static Logica INSTANCE = null;

    private Logica() {}

    public static Logica getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Logica();
        return INSTANCE;
    }

    private ObservableList<Cuenta> cuentas = FXCollections.observableArrayList();
    private ObservableList<Tarea> tareas = FXCollections.observableArrayList();

    public void anadir_tareas(Tarea t) {tareas.add(t);}

    public void eliminar_tareas(Tarea t){tareas.remove(t);}

    public ObservableList<Tarea> getTareas() {return tareas;}

    public void anadir_cuentas(Cuenta c) {cuentas.add(c);}

    public ObservableList getCuentas() {
        return cuentas;
    }

    public void borrarCuentas(String email){
        Cuenta c = null;
        for(int i = 0; i<cuentas.size(); i++){
            if(email.equals(cuentas.get(i).getEmail())){
                c = cuentas.get(i);
            }
        }
        cuentas.remove(c);
    }

    public ConnectException conectar(String email, String password) {

        Properties prop = new Properties();
        prop.setProperty("mail.store.protocol", "imaps");
        prop.setProperty("mail.smtp.host", "smtp.gmail.com");
        prop.setProperty("mail.smtp.port", "587");
        prop.setProperty("mail.smtp.starttls.enable", "true");

        Session sesion = Session.getInstance(prop);
        Store store = null;

        try {
            store = sesion.getStore("imaps");
            store.connect("imap.googlemail.com", email, password);
        } catch (AuthenticationFailedException e) {
            e.printStackTrace();
            return ConnectException.FALLO_AUTENTICACION;
        }catch (IllegalStateException e){
            e.printStackTrace();
            return ConnectException.FALLO_ESTADO;
        }catch (MailConnectException e){
            e.printStackTrace();
            return ConnectException.FALLO_CONEXION;
        }catch (MessagingException e){
            e.printStackTrace();
        }

        for(int i = 0; i<cuentas.size(); i++){
            if(cuentas.get(i).getEmail().equals(email)){
                cuentas.get(i).setStore(store);
                cuentas.get(i).setSession(sesion);
            }
        }
        return ConnectException.CONEXION_CORRECTA;
    }

    public ObservableList<Mensaje> obtenerCorreos(Folder folder)  {

        ObservableList<Mensaje> mensajes = FXCollections.observableArrayList();

        try {
            if(folder!=null && folder.getType()==3){
            if (!folder.isOpen())
                folder.open(Folder.READ_WRITE);

            Message mens[];
            mens = folder.getMessages();
            Mensaje m;

            for (int i = 0; i < mens.length; i++) {
                m = new Mensaje(mens[i]);
                mensajes.add(m);
                }
            }

        } catch (MessagingException e) {
            e.printStackTrace();

        }
        return mensajes;
    }

    public EmailTreeItem cargaCarpetas() {

        EmailTreeItem rootItem = new EmailTreeItem("",null,null, null);

        for (int i = 0; i < cuentas.size(); i++) {

            EmailTreeItem rootItemCuenta = new EmailTreeItem(cuentas.get(i).getEmail(), cuentas.get(i), null, cuentas.get(i).getStore());
            rootItem.getChildren().add(rootItemCuenta);

            try {
                Folder[] folders = cuentas.get(i).getStore().getDefaultFolder().list();
                rootItemCuenta.setExpanded(true);
                cargarCarpetas(rootItemCuenta, folders, cuentas.get(i));
            } catch (MessagingException e) {
                e.printStackTrace();
                return null;
            }
        }

        return rootItem;
    }

    public void cargarCarpetas(EmailTreeItem rootItem, Folder[] folders, Cuenta cuenta) throws MessagingException {

        for (Folder folder : folders) {
            {
                EmailTreeItem item = new EmailTreeItem(folder.getName(), cuenta, folder, cuenta.getStore());
                rootItem.getChildren().add(item);
                if (folder.list().length > 0)
                    cargarCarpetas(item, folder.list(), cuenta);
            }
        }

    }

    public void borrar_email(Mensaje m_borrar, EmailTreeItem email_tree) {

        if(email_tree.getFolder().toString().equals("[Gmail]/Papelera")){

            try {
                m_borrar.getM().setFlag(Flags.Flag.DELETED, true);
                email_tree.getFolder().close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }else {
            Message[] m = new Message[]{m_borrar.getM()};
            Folder trash = null;
            try {
                trash = email_tree.getStore().getFolder("[Gmail]/Papelera");
                email_tree.getFolder().copyMessages(m, trash);
                email_tree.getFolder().close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean redactar_email(String from, String to, String asunto, HTMLEditor mensaje)  { //MODIFICAR

        String password = null; Cuenta c = null;

        for(int i = 0; i<cuentas.size(); i++){
            if(cuentas.get(i).getEmail().equals(from)) {
                password = cuentas.get(i).getPassword();
                c = cuentas.get(i);
            }
        }

        ConnectException estado = conectar(from,password);
        if(estado.equals(ConnectException.CONEXION_CORRECTA)){
            MimeMessage message = new MimeMessage(c.getSession());

            try {
                message.setFrom(new InternetAddress(from));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject(asunto);
                message.setContent(mensaje.getHtmlText(), "text/html");
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            try {
                Transport t = c.getSession().getTransport("smtp");
                t.connect(c.getEmail(),c.getPassword());
                t.sendMessage(message, message.getAllRecipients());
                t.close();
            }catch(MessagingException e) {
                e.printStackTrace();
            }
            return true;
        } else{
            return false;
        }

    }

}
