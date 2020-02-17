package emma.informes;

import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;


public class Mensaje {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private Message m = null;

    public Mensaje(Message m) {
        this.m = m;
    }

    public Message getM(){
        return m;
    }

    public String getFrom() throws javax.mail.MessagingException {
        return m.getFrom()[0].toString();
    }

    public String getSubject() throws javax.mail.MessagingException {
        return m.getSubject();
    }

    public String getReceivedDate() throws  javax.mail.MessagingException {
        String fecha = sdf.format(m.getReceivedDate());
        return fecha;
    }

    public String getTo() throws javax.mail.MessagingException {
        Address[] to = m.getReplyTo();
        String s_to = to[0].toString();
        return s_to;
    }

    public String getContent() throws Exception {
        String result = "";
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) m);
        parser.parse();

        if (m.isMimeType("text/plain")) {
            result = parser.getPlainContent();
        } else if (m.isMimeType("multipart/*")) {
            result = parser.getHtmlContent();
        }
        else if(m.isMimeType("text/Html")){
            result = parser.getHtmlContent();
        }
        return result;
    }



}

