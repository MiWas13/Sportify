package com.michael.sportify.ConcreteSportKind;

/**
 * Created by Michael on 09.05.16.
 */
public class ConcreteSportItem {
    private String sportKind;
    private int gameId;
    private String login;
    private String date;
    private String time;
    private String locationLatitude;
    private String locationLongitude;
    private String quantity;
    private int visibleButton;

    public int getVisibleButton() {
        return visibleButton;
    }

    public void setVisibleButton(int visibleButton) {
        this.visibleButton = visibleButton;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getSportKind() {
        return sportKind;
    }

    public void setSportKind(String sportKind) {
        this.sportKind = sportKind;
    }
}
