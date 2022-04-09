package it.polimi.ingsw.GameModel.BoardElements;

public interface PlaceAndRemoveStudent {

    void placePawn(Student student);

    Student removePawn(Student studentToRemove);

    Student removePawnByIndex(int index);

    Student getPawnByID(int ID);

}
