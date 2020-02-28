package emma.logic;

import com.emma.Tarea;
import com.sun.mail.util.MailConnectException;
import emma.models.Cuenta;
import emma.models.EmailTreeItem;
import emma.models.Mensaje;
import emma.models.MensajeInforme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public EmailTreeItem cargarCarpetas() {

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

    public List<MensajeInforme> get_mensaje_informe(Mensaje m){

        List<MensajeInforme> mensajeList = new ArrayList<>();
        String plainText = null;

        try {
            if(m.getContent()!=null)
                plainText = Jsoup.parse(m.getContent()).text();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MensajeInforme mi = new MensajeInforme(m.getFrom(),m.getTo(), m.getSubject(), m.getReceivedDate());
            mi.setContent(plainText);
            mensajeList.add(mi);

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mensajeList;
    }

    public List<MensajeInforme> get_informes_carpetas(List<Mensaje> mensajes, String folder){
        String from = null, to = null, subject = null, date = null;
        List<MensajeInforme> lista = new ArrayList<MensajeInforme>();

        for(int i = 0; i<mensajes.size(); i++){
            try {
                from = mensajes.get(i).getFrom();
                to = mensajes.get(i).getTo();
                subject = mensajes.get(i).getSubject();
                date = mensajes.get(i).getReceivedDate();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            MensajeInforme mi =new MensajeInforme(from, to, subject, date);
            mi.setFolder(folder);

            lista.add(mi);
        }
        return lista;
    }

    public List<MensajeInforme> get_informes_correo(String email){

        List<MensajeInforme> lista = new ArrayList<>();

        EmailTreeItem rootItem = new EmailTreeItem("",null,null, null);
        Folder[] folders = null;



        for (int i = 0; i < cuentas.size(); i++) {
            if(cuentas.get(i).getEmail().equals(email)) {
                try {
                    folders = cuentas.get(i).getStore().getDefaultFolder().list();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        for(int i = 0; i<folders.length; i++) {

            try {
                if(folders[i].list().length>0){

                    Folder[] folders_2 = folders[i].list();
                    for(int j = 0; j<folders_2.length; j++){

                        List<Mensaje> mensajes = obtenerCorreos(folders_2[j]);

                        for(int k = 0; k<mensajes.size(); k++){
                            MensajeInforme mi = null;
                            try {
                                mi = new MensajeInforme(mensajes.get(k).getFrom(), mensajes.get(k).getTo(), mensajes.get(k).getSubject(), mensajes.get(k).getReceivedDate());
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                            mi.setFolder(folders_2[j].getName());
                            lista.add(mi);
                        }
                    }
                }else {

                    List<Mensaje> mensajes = obtenerCorreos(folders[i]);

                    for(int j = 0; j<mensajes.size(); j++){
                        MensajeInforme mi = null;
                        mi = new MensajeInforme(mensajes.get(j).getFrom(), mensajes.get(j).getTo(), mensajes.get(j).getSubject(), mensajes.get(j).getReceivedDate());
                        mi.setFolder(folders[i].getName());
                        lista.add(mi);
                    }

                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            }

        return lista;
    }

        public File getFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showSaveDialog(null);
    }

}
