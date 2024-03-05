package com.example.friendslocationv1;

public class Position {
    private int idPosition;
    private String longitude, latitude, pseudo;

    public Position(int idPosition, String longitude, String latitude, String pseudo) {
        this.idPosition = idPosition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pseudo = pseudo;
    }

    public Position() {
    }

    @Override
    public String toString() {
        return "Position{" +
                "idPosition=" + idPosition +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }
}
