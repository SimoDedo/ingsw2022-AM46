package it.polimi.ingsw.View.GUI;

import it.polimi.ingsw.Utils.Enum.Color;
import javafx.geometry.HPos;
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

public class PlayerPane extends GridPane {

    private double sizeBoardV = 170.0;
    private final double resizeFactor = 0.85;
    private double sizeDiscard = 80.0;
    private double sizeCoin = 30.0;
    private final int position;

    private final BoardPane boardPane;
    private final VBox discardCoinPane;
    private final AssistantPane assistantPane;


    public PlayerPane(int position, boolean isMainPlayer) {
        this.position = position;
        this.setId("playerPane" + position);
        this.setHgap(1);
        this.setAlignment(Pos.CENTER);
        this.setHgap(5);

        boardPane = new BoardPane(position, isMainPlayer ? sizeBoardV : sizeBoardV * resizeFactor);
        discardCoinPane = new VBox();
        assistantPane = new AssistantPane(position, sizeBoardV / 2);
    }

    public void setNickname(String nickname){
        Text nickPane = new Text();
        nickPane.setId("nickPane" + position);
        nickPane.setFont(Font.font("Eras Demi ITC", FontWeight.EXTRA_LIGHT, 10));
        this.getChildren().add(0, nickPane);
    }

    public void createBoard(int entranceID, HashMap<Color, Integer> tableIDs){
        boardPane.createEntrance(entranceID);
        boardPane.createDiningRoom(tableIDs);
        boardPane.createProfessors();
        boardPane.createTowerSpace();
        this.add(boardPane, 0, 1);
    }

    public void createDiscardCoin(boolean withCoins, boolean isMainPlayer){
        //Create discard and coin pane
        sizeDiscard = isMainPlayer ? sizeDiscard : sizeDiscard * resizeFactor;
        Image discard = new Image("/deck/carteTOT_back_1@3x.png", 200, 200, true, true);
        ImageView imageViewDiscard = new ImageView(discard);
        imageViewDiscard.setId("wizard" + position);
        imageViewDiscard.setPreserveRatio(true);
        imageViewDiscard.setFitWidth(sizeDiscard);
        imageViewDiscard.setEffect(new DropShadow());

        StackPane discardPane = new StackPane();
        discardPane.setId("discardPane" + position);
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
            coinsPane.setId("coinsPane" + position);
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

    public void createAssistantPane(boolean isMainPlayer){
        if(! isMainPlayer){
            assistantPane.resizeAssistant(resizeFactor);
        }
        this.add(assistantPane, 2, 1);
    }

    public void enableSelectAssistant(){
        Pane discard = (Pane) this.lookup("#discardPane"+position);
        List<String> ids = discard.getChildren().stream().map(Node::getId).toList();

        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+position+i);
            int id = i;
            if(assistant != null && !ids.contains("assistant"+position+i))
                assistant.setOnMouseClicked(event -> {
                    moveAssistant(id); //FIXME: debug will call smth else to give selection to controller
                });
        }
    }

    public void disableSelectAssistant(){
        for(int i = 1; i < 11; i++){
            ImageView assistant = (ImageView) this.lookup("#assistant"+position+i);
            if(assistant != null)
                assistant.setOnMouseClicked(event -> System.out.println("Im disabled"));
        }
    }

    public void moveAssistant(int ID){
        ImageView assistant = (ImageView) this.lookup("#assistant"+ position + ID);
        assistant.setOnMouseClicked(event -> System.out.println("Im disabled")); //Now can't be clicked (I'm a fkn genius)

        assistant.setVisible(false);

        Pane discard = (Pane) this.lookup("#discardPane"+position);
        discard.getChildren().removeAll(discard.getChildren());

        ImageView discarded = new ImageView(assistant.getImage());
        discarded.setFitWidth(sizeDiscard);
        discarded.setPreserveRatio(true);
        discarded.setSmooth(true);
        discarded.setEffect(new DropShadow());
        discard.getChildren().add(discarded);
        enableSelectAssistant();
    }
}
