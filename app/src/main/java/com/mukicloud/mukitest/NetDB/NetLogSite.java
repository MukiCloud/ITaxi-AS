package com.mukicloud.mukitest.NetDB;

public class NetLogSite {
    private String ID;
    private String Time;
    private String UserID;
    private String Action;
    private String Value;

    public NetLogSite(String ID, String time, String userID, String action, String value) {
        this.ID = ID;
        Time = time;
        UserID = userID;
        Action = action;
        Value = value;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
