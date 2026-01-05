package ry.ms.view.match;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.match.models.MatchStatus;

/**
 * Cache singleton pour partager les instances de Match entre toutes les vues.
 * Permet aux Observables de fonctionner correctement en garantissant qu'une même
 * instance de Match est utilisée partout dans l'application.
 */
public class MatchCache {
    
    private static MatchCache instance;
    private final Map<Long, Match> matches = new ConcurrentHashMap<>();
    
    private MatchCache() {}
    
    public static MatchCache getInstance() {
        if (instance == null) {
            instance = new MatchCache();
        }
        return instance;
    }
    
    /**
     * Récupère un match du cache ou l'ajoute s'il n'existe pas.
     * Si le match existe déjà, met à jour ses propriétés sans créer un nouvel objet.
     * 
     * @param match Le match à mettre en cache
     * @return L'instance cachée du match
     */
    public Match getOrPut(Match match) {
        if (match == null || match.getMatchId() == null) {
            return match;
        }
        
        // Si le match existe déjà dans le cache, mettre à jour ses propriétés
        if (matches.containsKey(match.getMatchId())) {
            Match cached = matches.get(match.getMatchId());
            
            // Mettre à jour les propriétés sans créer un nouvel objet
            cached.setMatchDate(match.getMatchDate());
            cached.setReferees(match.getReferees());
            cached.setTeams(match.getTeams());
            cached.setTeamResults(match.getTeamResults());
            
            // ✅ IMPORTANT : Mettre à jour le statut (déclenche l'Observable)
            if (match.getStatus() != cached.getStatus()) {
                cached.setStatus(match.getStatus());
                System.out.println("✅ Statut du match " + match.getMatchId() + " mis à jour dans le cache : " + match.getStatus());
            }
            
            return cached;
        }
        
        // Sinon, ajouter le nouveau match au cache
        matches.put(match.getMatchId(), match);
        System.out.println("✅ Match " + match.getMatchId() + " ajouté au cache");
        return match;
    }
    
    /**
     * Met à jour le statut d'un match dans le cache.
     * Déclenche automatiquement les listeners Observable.
     * 
     * @param matchId L'identifiant du match
     * @param newStatus Le nouveau statut
     */
    public void updateStatus(Long matchId, MatchStatus newStatus) {
        Match match = matches.get(matchId);
        if (match != null) {
            match.setStatus(newStatus);
            System.out.println("✅ Statut du match " + matchId + " mis à jour dans le cache : " + newStatus);
        } else {
            System.err.println("⚠️ Match " + matchId + " introuvable dans le cache");
        }
    }
    
    /**
     * Supprime un match du cache
     * 
     * @param matchId L'identifiant du match à supprimer
     */
    public void remove(Long matchId) {
        matches.remove(matchId);
        System.out.println("✅ Match " + matchId + " supprimé du cache");
    }
    
    /**
     * Vide complètement le cache
     */
    public void clear() {
        matches.clear();
        System.out.println("✅ Cache vidé");
    }
}
