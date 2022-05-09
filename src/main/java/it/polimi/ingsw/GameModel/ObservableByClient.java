package it.polimi.ingsw.GameModel;

import it.polimi.ingsw.Utils.Enum.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface ObservableByClient {

    //region State observer methods

        //region Game

        /**
         * Getter for the number of players selected for this game
         * @return the number of players
         */
        int getNumOfPlayers();

        /**
         * Getter for the game mode selected for this game
         * @return the game mode selected for this game
         */
        GameMode getGameMode();

        /**
         * Getter for the current player in the game.
         * @return the player currently executing their planning/action turn, null if firstRoundOrder
         * hasn't been determined yet
         */
        String getCurrentPlayer();

        /**
         * Returns current player order
         * @return a list of nicknames ordered
         */
        List<String> getPlayerOrder();

        /**
         * Method used to observe cards played this round. Returned according to current order (planning or action).
         * To return them ordered, it uses the currentOrder given by TurnManager.
         * If no order has been established yet, it will return an empty LinkedHashMap.
         * @return A LinkedHashMap containing the nickname of the Player and the ID of the card played.
         */
        LinkedHashMap<String, Integer> getCardsPlayedThisRound();

        /**
         * Method used to observe cards played last round. Returned according to current order (planning or action).
         * To return them ordered, it uses the currentOrder given by TurnManager.
         * If no order has been established yet, it will return an empty LinkedHashMap.
         * @return A LinkedHashMap containing the nickname of the Player and the ID of the card played
         */
         LinkedHashMap<String, Integer> getCardsPlayedLastRound();

        /**
         * Method to observe the current phase of the game. Needed to let know the controller which order to calculate.
         * @return the current phase.
         */
        Phase getCurrentPhase();
        //endregion

        //region Player

        /**
         * Returns a list of cards that weren't yet played (thus to be shown to the player)
         * @return a list of cards IDs
         */
        List<Integer> getCardsLeft(String nickname);

        /**
         * Method to observe all the students in the entrance and their color
         * @return HashMap with the student ID as key and its color as object
         */
        HashMap<Integer, Color> getEntranceStudentsIDs(String nickname);

        /**
         * Method to get all the table IDs and their color
         * @return an HashMap with the table color as key and the Table ID as object
         */
        HashMap<Color, Integer> getTableIDs(String nickname);

        /**
         * Method to observe all the students in a table
         * @param color The color of the table
         * @return List with the student IDs in the requested table
         */
        List<Integer> getTableStudentsIDs(String nickname, Color color);

        /**
         * Returns the amount of towers contained in the TowerSpace of a given team
         * @param towerColor the nickname of the player to check
         * @return the amount of towers contained in the TowerSpace
         */
        int getTowersLeft(TowerColor towerColor);

        /**
         * Method used to observe which player chose which wizard
         * @return An HashMap containing the nickname of the Player and the Wizard chosen
         */
        HashMap<String, WizardType> getPlayersWizardType();
        //endregion

        //region ProfessorSet
        /**
         * Method to observe which Professor is owned by who
         * @return An HashMap with the color of the professor as Key and its owner as Object (null if no player owns it)
         */
        HashMap<Color, String> getProfessorsOwner();
        //endregion

        //region Clouds
        /**
         * Returns a list containing all the IDs of CloudTiles
         * @return a list containing all the IDs of CloudTiles
         */
        List<Integer> getCloudIDs();

        /**
         * Return all the IDs of students contained in a given cloud along with their color
         * @param cloudTileID the ID of the CloudTile
         * @return an HashMap with the Student IDs as Key and their color as Object
         * @throws IllegalArgumentException thrown when the CloudTileID doesn't match any existing cloud
         */
        HashMap<Integer, Color> getCloudStudentsIDs(int cloudTileID) throws IllegalArgumentException;
        //endregion

        //region Bag
        /**
         * Method to observe all the students in the bag and their color
         * @return HashMap with the student ID as key and its color as object
         */
        HashMap<Integer, Color> getBagStudentsIDs();
        //endregion

        //region Archipelago
        /**
         * Returns all students contained in all islands with their color
         * @return An HashMap with StudentID as key and Color as value
         */
        HashMap<Integer, Color> getArchipelagoStudentIDs();

        /**
         * Searches all IslandTiles to find which students each contains
         * @return A HashMap containing as Key the idx of the IslandTile, as object a list of StudentIDs
         */
        HashMap<Integer, List<Integer>> getIslandTilesStudentsIDs();

        /**
         * For each IslandGroup finds the IDs of its IslandTiles
         * @return An HashMap with key the (current) index of the IslandGroup and a list of its IslandTiles IDs
         */
        HashMap<Integer, List<Integer>> getIslandTilesIDs();

        /**
         * Returns the IslandTile ID of the IslandTile which contains MotherNature
         * @return the IslandTile ID of the IslandTile which contains MotherNature
         */
        int getMotherNatureIslandTileID();

        /**
         * Returns the IslandGroups indexes along with the TowerColor of the Team who has towers.
         * The color is null when no Team holds the IslandGroup
         * @return an HashMap containing the indexes of the IslandGroup as key and the TowerColor as Key
         */
        HashMap<Integer, TowerColor> getIslandGroupsOwners();

        //endregion

    //endregion
}
