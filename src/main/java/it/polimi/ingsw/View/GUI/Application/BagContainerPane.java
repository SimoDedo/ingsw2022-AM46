package it.polimi.ingsw.View.GUI.Application;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * This class contains the students bag and the coin heap.
 */
public class BagContainerPane extends HBox {

    /**
     * The height of the bag container. It is set to be equal to the height of each bag.
     */
    static double bagContainerHeight = BagPane.bagSize;

    /**
     * The width of the bag container. It is set to be greater than the width of two bags.
     */
    static double bagContainerWidth = BagPane.bagSize*2.5;

    /**
     * Constructor for the class. It sets the right size and alignment and creates the students bag.
     */
    public BagContainerPane() {
        super(5.0);
        this.setId("bagContainerPane");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(bagContainerWidth, bagContainerHeight);
        this.setMaxSize(bagContainerWidth, bagContainerHeight);

        createBag();
    }

    /**
     * Method for creating the students bag.
     */
    public void createBag() {
        this.getChildren().add(new BagPane("students"));
    }

    /**
     * Method for creating the coin heap.
     */
    public void createCoinsHeap() {
        this.getChildren().add(new BagPane("coins"));
    }

    /**
     * Method for updating the students counter on the students bag.
     * @param studentsLeft the updated count
     */
    public void updateBag(int studentsLeft) {
        ((BagPane)this.lookup("#studentsBagPane")).updateCount(studentsLeft);
    }

    /**
     * Method for updating the coin counter on the coin heap.
     * @param coinsLeft the updated count
     */
    public void updateCoinHeap(int coinsLeft) {
        if(this.lookup("#coinsBagPane") != null)
            ((BagPane)this.lookup("#coinsBagPane")).updateCount(coinsLeft);
    }

}
