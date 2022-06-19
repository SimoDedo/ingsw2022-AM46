package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.GUI.ObservableGUI;
import it.polimi.ingsw.View.GUI.ObserverGUI;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains most of the game elements that pertain to a player. The board, discard pane, coin stack and
 * assistants are all contained here.
 */
public class PlayerPane extends GridPane implements ObservableGUI {

    /**
     * The observer of this GUI element.
     */
    private ObserverGUI observer;

    /**
     * The nickname of the player owning this board
     */
    private final String nickname;
    /**
     * The ID connected to the nickname of the player owning this board.
     */
    private final int nickID;

    /**
     * The size of the board
     */
    public final static double sizeBoardV = 170.0;
    /**
     * Specifies how much smaller should other player's boards be when compared to the main board.
     */
    private final double resizeFactor = 0.85;
    /**
     * Size of the discard pane.
     */
    private double sizeDiscard = 80.0;
    /**
     * Size of the coin stack.
     */
    private double sizeCoin = 30.0;

    /**
     * The board pane of this player
     */
    private final BoardPane boardPane;
    /**
     * A container for the discard pane and the coin stack.
     */
    private final VBox discardCoinPane;
    /**
     * The image of the wizard chosen by this player.
     */
    private ImageView wizardView;
    /**
     * The container of all assistants cards
     */
    private final AssistantContainerPane assistantContainerPane;


    /**
     * Constructor of the player pane.
     * @param nickname the nickname of this player.
     * @param nickID the ID associated to this nickname.
     * @param isMainPlayer true if this player pane should be bigger (since it's the main player), false otherwise.
     */
    public PlayerPane(String nickname, int nickID, boolean isMainPlayer) {
        this.nickname = nickname;
        this.nickID = nickID;
        this.setId("playerPane" + nickID);
        this.setHgap(5.0);
        this.setVgap(5.0);
        this.setAlignment(Pos.CENTER);

        boardPane = new BoardPane(nickID, isMainPlayer ? sizeBoardV : sizeBoardV*resizeFactor);
        discardCoinPane = new VBox();
        assistantContainerPane = new AssistantContainerPane(nickID,sizeBoardV / 2);
    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
        boardPane.setObserver(observer);
    }

    /**
     * Sets the nickname of the player to be visible.
     * @param nickname the nickname to display.
     */
    public void setNickname(String nickname){
        Text nickPane = new Text("   " + nickname);
        nickPane.setId("nickPane" + nickname);
        nickPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 15.0));
        this.getChildren().add(0, nickPane);
    }

    /**
     * Creates the board associated to this player using the data sent by the server.
     * @param entranceID The ID of this player's entrance.
     * @param tableIDs The IDs of this player's tables.
     * @param towerColor The tower color chosen by this player.
     */
    public void createBoard(int entranceID, HashMap<Color, Integer> tableIDs, TowerColor towerColor){
        boardPane.createEntrance(entranceID);
        boardPane.createDiningRoom(tableIDs);
        boardPane.createProfessors();
        boardPane.createTowerSpace(towerColor);
        this.add(boardPane, 0, 1);
    }

    /**
     * Creates the discard pile and the coin stack.
     * @param withCoins True if the coin stack should be created, false otherwise.
     * @param isMainPlayer True if this is the main player, false otherwise.
     * @param wizardType The wizard type chosen by this player.
     */
    public void createDiscardCoin(boolean withCoins, boolean isMainPlayer, WizardType wizardType){
        //Create discard and coin pane
        sizeDiscard = isMainPlayer ? sizeDiscard : sizeDiscard * resizeFactor;
        Image discard = new Image("/deck/" + wizardType.toString().toLowerCase() + ".png", 200, 200, true, true);
        wizardView = new ImageView(discard);
        wizardView.setId("wizard" + nickID);
        wizardView.setPreserveRatio(true);
        wizardView.setFitWidth(sizeDiscard);
        wizardView.setEffect(new DropShadow());

        StackPane discardPane = new StackPane();
        discardPane.setId("discardPane" + nickID);
        discardPane.getChildren().add(wizardView);

        discardCoinPane.getChildren().add(discardPane);
        if(withCoins){
            sizeCoin = isMainPlayer ? sizeCoin : sizeCoin * resizeFactor;
            Image coin = new Image("/world/coin.png", 100, 100, true, true);
            ImageView imageViewCoin = new ImageView(coin);
            imageViewCoin.setPreserveRatio(true);
            imageViewCoin.setFitWidth(sizeCoin);
            imageViewCoin.setEffect(new DropShadow());

            Text coinsPane = new Text(String.valueOf(3));
            coinsPane.setId("coinsPane" + nickID);
            coinsPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, sizeCoin * 0.75));
            coinsPane.setFill(javafx.scene.paint.Color.WHITE);
            coinsPane.setEffect(new DropShadow(20, javafx.scene.paint.Color.BLACK));

            StackPane coinStack = new StackPane();
            coinStack.getChildren().addAll(imageViewCoin, coinsPane);

            discardCoinPane.getChildren().add(coinStack);
        }

        discardCoinPane.setAlignment(Pos.CENTER);
        discardCoinPane.setSpacing(10);

        this.add(discardCoinPane, 1, 1);
    }

    /**
     * Creates the container with the assistants.
     * @param isMainPlayer True if this is the main player, false otherwise.
     */
    public void createAssistantContainerPane(boolean isMainPlayer){
        if(! isMainPlayer){
            assistantContainerPane.resizeAssistant(resizeFactor);
        }
        this.add(assistantContainerPane, 2, 1);
    }

    public void updateAssistants(Integer assistantUsed, List<Integer> assistantsLeft){
        ImageView assistant = (ImageView) this.lookup("#assistant"+ nickID + assistantUsed);
        assistantContainerPane.updateAssistant(assistantsLeft);
        StackPane discardPane = (StackPane) this.lookup("#discardPane" + nickID);
        if(assistantUsed == null){
            discardPane.getChildren().clear();
            discardPane.getChildren().add(wizardView);
        }
        else {
            discardPane.getChildren().clear();
            ImageView discarded = new ImageView(assistant.getImage());
            discarded.setFitWidth(sizeDiscard);
            discarded.setPreserveRatio(true);
            discarded.setSmooth(true);
            discarded.setEffect(new DropShadow());
            discardPane.getChildren().add(discarded);
        }
    }

    /**
     * Updates the entrance adding and removing students according to the new contents given.
     * @param students The students that are now contained in this player's entrance.
     */
    public void updateEntrance(HashMap<Integer, Color> students){
        boardPane.updateEntrance(students);
    }

    /**
     * Updates the dining room adding and removing students according to the new contents given.
     * @param color The color of the table to update.
     * @param studs The students that are now contained in this player's table of the given color.
     */
    public void updateDiningRoom(Color color, List<Integer> studs){
        boardPane.updateTable(color, studs);
    }

    /**
     * Updates the professors owned by this player.
     * @param professors The professor colors and their owners.
     */
    public void updateProfessors(HashMap<Color, String> professors){
        List<Color> profColor = professors.entrySet().stream().filter(e -> nickname.equals(e.getValue())).map(Map.Entry::getKey).toList();
        boardPane.updateProfessors(profColor);
    }

    /**
     * Updates the towers remaining in this player's tower space.
     * @param newNumOfTowers The number of towers remaining in the tower space.
     */
    public void updateTowers(int newNumOfTowers){
        boardPane.updateTowers(newNumOfTowers);
    }

    /**
     * Updates the number of coins that this player owns.
     * @param coins The number of coins that this player owns.
     */
    public void updateCoins(int coins){
        if(this.lookup("#coinsPane" + nickID) != null)
            ((Text)this.lookup("#coinsPane" + nickID)).setText(String.valueOf(coins));
    }

    /**
     * Enables the selection of students in the entrance.
     */
    public void enableSelectStudentsEntrance() {
        boardPane.enableSelectStudentsEntrance();
    }

    /**
     * Disables the selection of students in the entrance.
     */
    public void disableSelectStudentsEntrance() {
        boardPane.disableSelectStudentsEntrance();
    }

    /**
     * Enables the selection of students in the dining room.
     */
    public void enableSelectStudentsDR() {
        boardPane.enableSelectStudentsDR();
    }

    /**
     * Disables the selection of students in the dining room.
     */
    public void disableSelectStudentsDR() {
        boardPane.disableSelectStudentsDR();
    }

    /**
     * Enables the selection of all tables.
     */
    public void enableSelectTables() {
        boardPane.enableSelectTables();
    }

    /**
     * Enables the selection of a single table.
     * @param color The color of the table to activate.
     */
    public void enableSelectTables(Color color) {
        boardPane.enableSelectTables(color);
    }

    /**
     * Disable the selection of all tables.
     */
    public void disableSelectTables() {
        boardPane.disableSelectTables();
    }

    /**
     * Enables the selection of assistants.
     */
    public void enableSelectAssistant(){
        Pane discard = (Pane) this.lookup("#discardPane"+nickID);
        List<String> ids = discard.getChildren().stream().map(Node::getId).toList();

        AssistantContainerPane assistantContainerPane = (AssistantContainerPane) this.lookup("#assistantContainerPane" + nickID);
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickID+i);
            if(assistant != null && !ids.contains("assistant"+nickID+i)) {
                assistant.setEffect(Effects.enabledAssistantEffect);
                assistantContainerPane.setZoomOnAssistant(assistant, Effects.hoveringAssistantEffect, Effects.enabledAssistantEffect);
                int assistantID = i;
                assistant.setOnMouseClicked(event -> {
                    assistantContainerPane.setAssistantChosen(assistantID);
                    observer.notifyAssistantCard();
                });
            }
        }
    }

    /**
     * Disables the selection of assistants.
     */
    public void disableSelectAssistant(){
        AssistantContainerPane assistantContainerPane = (AssistantContainerPane) this.lookup("#assistantContainerPane" + nickID);
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickID+i);
            if(assistant != null) {
                assistant.setEffect(Effects.disabledAssistantEffect);
                assistantContainerPane.setZoomOnAssistant(assistant, Effects.disabledAssistantEffect, Effects.disabledAssistantEffect);
                assistant.setOnMouseClicked(event -> {});
            }
        }
    }

}
