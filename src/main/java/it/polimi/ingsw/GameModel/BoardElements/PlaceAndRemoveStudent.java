package it.polimi.ingsw.GameModel.BoardElements;

public interface PlaceAndRemoveStudent {

    void placePawn(Student student);

    boolean removePawn(Student studentToRemove);

    Student removePawnByID(int index);

    Student getPawnByID(int ID);

}
