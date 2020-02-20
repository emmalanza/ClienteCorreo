package emma.models;

public class MensajeInforme {

    private String from, to, subject, receivedDate, content;

    public MensajeInforme(String from, String to, String subject, String receivedDate, String content){
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.receivedDate = receivedDate;
        this.content = content;
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


}
