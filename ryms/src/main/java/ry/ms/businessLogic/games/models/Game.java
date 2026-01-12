package ry.ms.businessLogic.games.models;

import java.util.Date;

/**
 * Model class representing a Game in the catalog.
 */
public class Game {
    private int gameId;
    private String name;
    private String editor;
    private Date releaseDate;
    private byte[] logo; // BYTEA in Postgres maps to byte[] in Java

    /**
     * Constructor for creating a new game (without ID).
     * 
     * @param name   The name of the game.
     * @param editor The editor/publisher of the game.
     * @param date   The release date.
     * @param logo   The logo image data.
     */
    public Game(String name, String editor, Date date, byte[] logo) {
        this.name = name;
        this.editor = editor;
        this.releaseDate = date;
        this.logo = logo;
    }

    /**
     * Constructor for loading a game from the database (with ID).
     * 
     * @param gameId The unique ID of the game.
     * @param name   The name of the game.
     * @param editor The editor/publisher of the game.
     * @param date   The release date.
     * @param logo   The logo image data.
     */
    public Game(int gameId, String name, String editor, Date date, byte[] logo) {
        this.gameId = gameId;
        this.name = name;
        this.editor = editor;
        this.releaseDate = date;
        this.logo = logo;
    }

    /**
     * Gets the game ID.
     * 
     * @return The game ID.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Sets the game ID.
     * 
     * @param gameId The game ID.
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Gets the game name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the game name.
     * 
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the editor.
     * 
     * @return The editor.
     */
    public String getEditor() {
        return editor;
    }

    /**
     * Sets the editor.
     * 
     * @param editor The editor.
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * Gets the release date.
     * 
     * @return The release date.
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Sets the release date.
     * 
     * @param releaseDate The release date.
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Gets the logo data.
     * 
     * @return The logo as a byte array.
     */
    public byte[] getLogo() {
        return logo;
    }

    /**
     * Sets the logo data.
     * 
     * @param logo The logo as a byte array.
     */
    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}