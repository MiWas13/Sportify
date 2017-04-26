package com.michael.sportify.CreateGame;

/**
 * Created by Michael on 12.05.16.
 */
public class CreateGameItem {
    private String sportKind;
    private String description;
    private String quantity;
    private String comment;
    private String locationLatitude;
    private String locationLongitude;
    private String equipmentNeed;
    private String time;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSportKind() {
        return sportKind;
    }

    public void setSportKind(String sportKind) {
        this.sportKind = sportKind;
    }

    public String getEquipmentNeed() {
        return equipmentNeed;
    }

    public void setEquipmentNeed(String equipmentNeed) {
        this.equipmentNeed = equipmentNeed;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
