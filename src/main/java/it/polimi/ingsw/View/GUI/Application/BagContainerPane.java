package it.polimi.ingsw.View.GUI.Application;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class BagContainerPane extends HBox {

    static double bagContainerHeight = BagPane.bagSize, bagContainerWidth = BagPane.bagSize*2.5;

    public BagContainerPane() {
        super(5.0);
        this.setId("bagContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(bagContainerWidth, bagContainerHeight);
        this.setMaxSize(bagContainerWidth, bagContainerHeight);

        createBag();
    }

    public void createBag() {
        this.getChildren().add(new BagPane("students"));
    }

    public void createCoinsHeap() {
        this.getChildren().add(new BagPane("coins"));
    }

    public void updateBag(int studentsLeft) {
        ((BagPane)this.lookup("#studentsBagPane")).updateCount(studentsLeft);
    }

    public void updateCoinHeap(int coinsLeft) {
        if(this.lookup("#coinsBagPane") != null)
            ((BagPane)this.lookup("#coinsBagPane")).updateCount(coinsLeft);
    }

}
