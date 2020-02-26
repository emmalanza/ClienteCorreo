package emma.informes;

public class MensajeInforme {

    private String from, to, subject, receivedDate, content, email, folder;

    public MensajeInforme(String from, String to, String subject, String receivedDate){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.receivedDate = receivedDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getFolder() {return folder;}

    public void setFolder(String folder) {this.folder = folder;}



}
