package com.example.wechat;

public class msgModelclass {
    String Message , senderID;
    long timestamp;

    public msgModelclass(String message,String senderID,long timestamp) {
        Message = message;
        this.senderID=senderID;
        this.timestamp=timestamp;

    }

    public msgModelclass() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
