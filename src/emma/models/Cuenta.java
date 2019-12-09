package emma.models;

import javax.mail.Session;
import javax.mail.Store;

public class Cuenta {

    private String email;
    private String password;
    private Store store;
    private Session session;

    public Cuenta(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Cuenta(){};

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setStore(Store store){
        this.store = store;
    }

    public Store getStore(){
        return store;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession(){
        return session;
    }

}
