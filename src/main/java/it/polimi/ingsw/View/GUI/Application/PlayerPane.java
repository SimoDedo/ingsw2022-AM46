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

public class PlayerPane extends GridPane implements ObservableGUI {

    private ObserverGUI observer;

    private final String nickname;
    private final int nickID;

    public final static double sizeBoardV = 170.0;
    private final double resizeFactor = 0.85;
    private double sizeDiscard = 80.0;
    private double sizeCoin = 30.0;

    private final BoardPane boardPane;
    private final VBox discardCoinPane;
    private ImageView wizardView;
    private final AssistantContainerPane assistantContainerPane;


    public PlayerPane(String nickname, int nickID, boolean isMainPlayer) {
        this.nickname = nickname;
        this.nickID = nickID;
        this.setId("playerPane" + nickID);
        this.setHgap(5.0);
        this.setVgap(5.0);
        this.setAlignment(Pos.CENTER);

        boardPane = new BoardPane(nickID, isMainPlayer ? sizeBoardV : sizeBoardV*resizeFactor);
        discardCoinPane = new VBox();
        assistantContainerPane = new AssistantContainerPane(nickname, nickID,sizeBoardV / 2);
        // if (isMainPlayer) enableSelectAssistant();
        // else disableSelectAssistant();
    }

    @Override
    public void setObserver(ObserverGUI observer) {
        this.observer = observer;
        boardPane.setObserver(observer);
    }

    public void setNickname(String nickname){
        Text nickPane = new Text("   " + nickname);
        nickPane.setId("nickPane" + nickname);
        nickPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 15.0));
        this.getChildren().add(0, nickPane);
    }

    public void createBoard(int entranceID, HashMap<Color, Integer> tableIDs, TowerColor towerColor, int numOfTowers){
        boardPane.createEntrance(entranceID);
        boardPane.createDiningRoom(tableIDs);
        boardPane.createProfessors();
        boardPane.createTowerSpace(towerColor, numOfTowers);
        this.add(boardPane, 0, 1);
    }

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

    public void updateEntrance(HashMap<Integer, Color> students){
        boardPane.updateEntrance(students);
    }

    public void updateDiningRoom(Color color, List<Integer> studs){
        boardPane.updateTable(color, studs);
    }

    public void updateProfessors(HashMap<Color, String> professors){
        List<Color> profColor = professors.entrySet().stream().filter(e -> nickname.equals(e.getValue())).map(Map.Entry::getKey).toList();
        boardPane.updateProfessors(profColor);
    }

    public void updateTowers(int newNumOfTowers){
        boardPane.updateTowers(newNumOfTowers);
    }

    public void updateCoins(int coins){
        if(this.lookup("#coinsPane" + nickID) != null)
            ((Text)this.lookup("#coinsPane" + nickID)).setText(String.valueOf(coins));
    }

    public void enableSelectStudentsEntrance() {
        boardPane.enableSelectStudentsEntrance();
    }

    public void disableSelectStudentsEntrance() {
        boardPane.disableSelectStudentsEntrance();
    }

    public void enableSelectStudentsDR() {
        boardPane.enableSelectStudentsDR();
    }

    public void disableSelectStudentsDR() {
        boardPane.disableSelectStudentsDR();
    }

    public void enableSelectTables() {
        boardPane.enableSelectTables();
    }

    public void enableSelectTables(Color color) {
        boardPane.enableSelectTables(color);
    }

    public void disableSelectTables() {
        boardPane.disableSelectTables();
    }

    public void enableSelectAssistant(){
        Pane discard = (Pane) this.lookup("#discardPane"+nickID);
        List<String> ids = discard.getChildren().stream().map(Node::getId).toList();

        AssistantContainerPane assistantContainerPane = (AssistantContainerPane) this.lookup("#assistantContainerPane" + nickID);
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickID+i);
            if(assistant != null && !ids.contains("assistant"+nickID+i)) {
                assistant.setEffect(Effects.enabledAssistantShadow);
                assistantContainerPane.setZoomOnAssistant(assistant, Effects.hoveringAssistantShadow, Effects.enabledAssistantShadow);
                int assistantID = i;
                assistant.setOnMouseClicked(event -> {
                    assistantContainerPane.setAssistantChosen(assistantID);
                    observer.notifyAssistantCard();
                });
            }
        }
    }

    public void disableSelectAssistant(){
        AssistantContainerPane assistantContainerPane = (AssistantContainerPane) this.lookup("#assistantContainerPane" + nickID);
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickID+i);
            if(assistant != null) {
                assistant.setEffect(Effects.disabledAssistantShadow);
                assistantContainerPane.setZoomOnAssistant(assistant, Effects.disabledAssistantShadow, Effects.disabledAssistantShadow);
                assistant.setOnMouseClicked(event -> {});
            }
        }
    }

}
