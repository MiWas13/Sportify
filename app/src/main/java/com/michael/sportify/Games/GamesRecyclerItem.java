package com.michael.sportify.Games;

/**
 * Created by Michael on 11.05.16.
 */
public class GamesRecyclerItem {
    private String sportKind;
    private String login;
    private int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getSportKind() {
        return sportKind;
    }

    public void setSportKind(String sportKind) {
        this.sportKind = sportKind;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
