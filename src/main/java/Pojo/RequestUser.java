package Pojo;

public class RequestUser {

    public RequestUser(String sender_code) {
        this.sender_code = sender_code;
    }

    public String getSender_code() {
        return sender_code;
    }

    public void setSender_code(String sender_code) {
        this.sender_code = sender_code;
    }

    private  String sender_code;
}
