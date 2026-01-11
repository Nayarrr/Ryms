package ry.ms;

import junit.framework.TestCase;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.games.GameCatalogFacade;
import ry.ms.persistLogic.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Test d'intégration pour le use case de gestion des jeux
 */
public class GamesUseCaseTest extends TestCase {

    // Constantes de test
    private static final String GAME_NAME = "JUnit_Test_Game";
    private static final String GAME_EDITOR = "JUnit_Editor";
    private static final String UPDATED_EDITOR = "JUnit_Updated_Editor";


    private GameCatalogFacade gameCatalogFacade;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gameCatalogFacade = GameCatalogFacade.getGameCatalogFactory();
        cleanupTestData();
    }

    @Override
    protected void tearDown() throws Exception {
        cleanupTestData();
        super.tearDown();
    }

    /**
     * Test 1: Créer et récupérer un jeu
     */
    public void testCreateAndRetrieveGame() throws SQLException {
        // Créer un jeu
        Game game = new Game(GAME_NAME, GAME_EDITOR, new Date(), null);
        gameCatalogFacade.addGame(game);

        // Vérifier que le jeu a été créé avec un ID
        assertTrue("Le jeu devrait avoir un ID après création", game.getGameId() > 0);

        // Récupérer le jeu par nom
        Game retrievedGame = gameCatalogFacade.getGameByName(GAME_NAME);

        // Vérifications
        assertNotNull("Le jeu devrait être récupérable", retrievedGame);
        assertEquals("Le nom du jeu devrait correspondre", GAME_NAME, retrievedGame.getName());
        assertEquals("L'éditeur devrait correspondre", GAME_EDITOR, retrievedGame.getEditor());
        assertTrue("L'ID devrait être positif", retrievedGame.getGameId() > 0);
    }

    /**
     * Test 2: Mettre à jour et supprimer un jeu
     */
    public void testUpdateAndDeleteGame() throws SQLException {
        // Créer un jeu
        Game game = new Game(GAME_NAME, GAME_EDITOR, new Date(), null);
        gameCatalogFacade.addGame(game);

        // Récupérer le jeu créé
        Game createdGame = gameCatalogFacade.getGameByName(GAME_NAME);
        assertNotNull("Le jeu devrait exister", createdGame);

        // Mettre à jour l'éditeur
        createdGame.setEditor(UPDATED_EDITOR);
        gameCatalogFacade.updateGame(createdGame);

        // Vérifier la mise à jour
        Game updatedGame = gameCatalogFacade.getGameByName(GAME_NAME);
        assertNotNull("Le jeu devrait toujours exister", updatedGame);
        assertEquals("L'éditeur devrait être mis à jour", UPDATED_EDITOR, updatedGame.getEditor());

        // Supprimer le jeu
        gameCatalogFacade.deleteGame(updatedGame);

        // Vérifier la suppression
        Game deletedGame = gameCatalogFacade.getGameByName(GAME_NAME);
        assertNull("Le jeu ne devrait plus exister", deletedGame);
    }

    /**
     * Test 3: Récupérer le catalogue de jeux
     */
    public void testGetGameCatalog() throws SQLException {
        // Créer plusieurs jeux
        Game game1 = new Game(GAME_NAME + "_1", GAME_EDITOR, new Date(), null);
        Game game2 = new Game(GAME_NAME + "_2", GAME_EDITOR, new Date(), null);

        gameCatalogFacade.addGame(game1);
        gameCatalogFacade.addGame(game2);

        // Récupérer le catalogue
        List<Game> catalog = gameCatalogFacade.loadGameCatalog();

        // Vérifications
        assertNotNull("Le catalogue ne devrait pas être null", catalog);
        assertTrue("Le catalogue devrait contenir au moins 2 jeux", catalog.size() >= 2);

        // Vérifier que nos jeux sont dans le catalogue
        boolean found1 = catalog.stream().anyMatch(g -> (GAME_NAME + "_1").equals(g.getName()));
        boolean found2 = catalog.stream().anyMatch(g -> (GAME_NAME + "_2").equals(g.getName()));

        assertTrue("Le jeu 1 devrait être dans le catalogue", found1);
        assertTrue("Le jeu 2 devrait être dans le catalogue", found2);

        // Nettoyer les jeux supplémentaires
        gameCatalogFacade.deleteGame(game1);
        gameCatalogFacade.deleteGame(game2);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private void cleanupTestData() throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            // Supprimer tous les jeux de test
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM games WHERE name LIKE ?")) {
                stmt.setString(1, GAME_NAME + "%");
                stmt.executeUpdate();
            }
        }
    }
}
