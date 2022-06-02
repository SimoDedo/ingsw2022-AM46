package it.polimi.ingsw.View.GUI.Application;

import it.polimi.ingsw.Utils.Enum.Color;
import it.polimi.ingsw.Utils.Enum.TowerColor;
import it.polimi.ingsw.Utils.Enum.WizardType;
import it.polimi.ingsw.View.GUI.GUIController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerPane extends GridPane {

    private GUIController controller;

    private final String nickname;

    private double sizeBoardV = 170.0;
    private final double resizeFactor = 0.85;
    private double sizeDiscard = 80.0;
    private double sizeCoin = 30.0;

    private final BoardPane boardPane;
    private final VBox discardCoinPane;
    private final AssistantContainerPane assistantContainerPane;


    public PlayerPane(GUIController controller, String nickname, boolean isMainPlayer) {
        this.controller = controller;
        this.nickname = nickname;
        this.setId("playerPane" + nickname);
        this.setHgap(1);
        this.setAlignment(Pos.CENTER);
        this.setHgap(5);

        boardPane = new BoardPane(nickname, isMainPlayer ? sizeBoardV : sizeBoardV * resizeFactor);
        discardCoinPane = new VBox();
        assistantContainerPane = new AssistantContainerPane(nickname, sizeBoardV / 2);
        // if (isMainPlayer) enableSelectAssistant();
        // else disableSelectAssistant();
    }

    public void setNickname(String nickname){
        Text nickPane = new Text(nickname);
        nickPane.setId("nickPane" + nickname);
        nickPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 10));
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
        ImageView imageViewDiscard = new ImageView(discard);
        imageViewDiscard.setId("wizard" + nickname);
        imageViewDiscard.setPreserveRatio(true);
        imageViewDiscard.setFitWidth(sizeDiscard);
        imageViewDiscard.setEffect(new DropShadow());

        StackPane discardPane = new StackPane();
        discardPane.setId("discardPane" + nickname);
        discardPane.getChildren().add(imageViewDiscard);

        discardCoinPane.getChildren().add(discardPane);
        if(withCoins){
            sizeCoin = isMainPlayer ? sizeCoin : sizeCoin * resizeFactor;
            Image coin = new Image("/world/coin.png", 100, 100, true, true);
            ImageView imageViewCoin = new ImageView(coin);
            imageViewCoin.setPreserveRatio(true);
            imageViewCoin.setFitWidth(sizeCoin);
            imageViewCoin.setEffect(new DropShadow());

            Text coinsPane = new Text(String.valueOf(3));
            coinsPane.setId("coinsPane" + nickname);
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

    public void updateEntrance(HashMap<Integer, Color> students){
        boardPane.updateEntrance(students);
    }

    public void updateDiningRoom(){//todo
    }

    public void updateProfessors(HashMap<Color, String> professors){
        List<Color> profColor = professors.entrySet().stream().filter(e -> nickname.equals(e.getValue())).map(Map.Entry::getKey).toList();
        boardPane.updateProfessors(profColor);
    }

    public void updateTowers(int newNumOfTowers){
        boardPane.updateTowers(newNumOfTowers);
    }

    public void updateAssistants(List<Integer> assistantsLeft){
        assistantContainerPane.updateAssistant(assistantsLeft);
    }

    public void updateCoins(int coins){
        if(this.lookup("#coinsPane" + nickname) != null)
            ((Text)this.lookup("#coinsPane" + nickname)).setText(String.valueOf(coins));
    }

    public void enableSelectAssistant(){
        Pane discard = (Pane) this.lookup("#discardPane"+nickname);
        List<String> ids = discard.getChildren().stream().map(Node::getId).toList();

        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickname+i);
            if(assistant != null && !ids.contains("assistant"+nickname+i)) {
                int assistantID = i;
                assistant.setOnMouseClicked(event -> {
                    System.out.println("Someone clicked on me! " + assistant.getId());
                    assistantContainerPane.setAssistantChosen(assistantID);
                    controller.notifyAssistantCard();
                });
            }
        }
    }

    public void disableSelectAssistant(){
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+nickname+i);
            if(assistant != null)
                assistant.setOnMouseClicked(event -> System.out.println("Im disabled"));
        }
    }

    public void moveAssistant(int ID){
        ImageView assistant = (ImageView) this.lookup("#assistant"+ nickname + ID);
        assistant.setOnMouseClicked(event -> System.out.println("Im disabled")); //Now can't be clicked (I'm a fkn genius)

        assistant.setVisible(false);

        Pane discard = (Pane) this.lookup("#discardPane"+nickname);
        discard.getChildren().clear();

        ImageView discarded = new ImageView(assistant.getImage());
        discarded.setFitWidth(sizeDiscard);
        discarded.setPreserveRatio(true);
        discarded.setSmooth(true);
        discarded.setEffect(new DropShadow());
        discard.getChildren().add(discarded);
        assistantContainerPane.setAssistantChosen(-1);
        enableSelectAssistant();
    }
}
