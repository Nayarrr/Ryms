package ry.ms.businessLogic.games.models;

import java.util.Date;

public class Game {
    private int gameId;
    private String name;
    private String editor;
    private Date releaseDate;
    private byte[] logo; // BYTEA in Postgres maps to byte[] in Java

    // Constructeur pour créer un nouveau jeu (sans ID)
    public Game(String name, String editor, Date date, byte[] logo) {
        this.name = name;
        this.editor = editor;
        this.releaseDate = date;
        this.logo = logo;
    }

    // Constructeur pour charger depuis la DB (avec ID)
    public Game(int gameId, String name, String editor, Date date, byte[] logo) {
        this.gameId = gameId;
        this.name = name;
        this.editor = editor;
        this.releaseDate = date;
        this.logo = logo;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}