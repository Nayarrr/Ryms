package ry.ms.businessLogic.games.models;

import java.awt.*;
import java.util.Date;

public class Game {
    private String name;
    private String editor;
    private Date releaseDate;
    private byte[] logo; // BYTEA in Postgres maps to byte[] in Java

    public Game(String name, String editor, Date date, byte[] logo) {
        this.name = name;
        this.editor = editor;
        this.releaseDate = date;
        this.logo = logo;
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
