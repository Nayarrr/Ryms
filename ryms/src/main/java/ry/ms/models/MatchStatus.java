package ry.ms.models;

/**
 * Statut d'un match dans le système
 */
public enum MatchStatus {

    SCHEDULED("À VENIR"),

    IN_PROGRESS("EN COURS"),

    FINISHED("TERMINÉ");

    private final String displayName;

    MatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convertit une chaîne de caractères en MatchStatus
     */
    public static MatchStatus fromString(String status) {
        if (status == null) {
            return SCHEDULED;
        }
        
        try {
            return MatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SCHEDULED;
        }
    }
}