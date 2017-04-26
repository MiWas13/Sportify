package com.michael.sportify.ConcreteGameChat;

/**
 * Created by Michael on 04.06.16.
 */
public class ConcreteGameChatItem {
    private String userName;
    private String msgText;
    private int state;
    private String date;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
