package it.polimi.ingsw.GameModel.Characters;

import it.polimi.ingsw.GameModel.BoardElements.PlaceAndRemoveStudent;
import it.polimi.ingsw.GameModel.BoardElements.Student;
import it.polimi.ingsw.Utils.Enum.RequestParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class StudentMoverCharacter extends AbstractCharacter implements PlaceAndRemoveStudent {

    private int usesLeft, maxUses, maxPawns;

    private List<Student> students;

    public StudentMoverCharacter(int ID, int cost, int maxUses, int maxPawns, List<RequestParameters> requestParameters) {
        super(ID, cost, requestParameters);
        this.maxUses = maxUses;
        this.maxPawns = maxPawns;
        this.usesLeft = maxUses;
        students = new ArrayList<>();
    }

    @Override
    public void useAbility(Consumer<List<Integer>> consumer, List<Integer> parameterList) throws IllegalStateException {
        if (usesLeft > 0) {
            super.useAbility(consumer, parameterList);
            usesLeft--;
        } else throw new IllegalStateException("Character uses depleted");
    }

    @Override
    public void resetUseState() {
        super.resetUseState();
        usesLeft = maxUses;
    }

    @Override
    public void placePawn(Student student) {
        if(students.size() == maxPawns) throw new IllegalArgumentException("The container is full");
        if (students.contains(student)) throw new IllegalArgumentException("The student is already on this card");
        students.add(student);
    }

    @Override
    public Student removePawn(Student studentToRemove) {
        int index = students.indexOf(studentToRemove);
        return removePawnByIndex(index);
    }

    @Override
    public Student removePawnByIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= maxPawns)
            throw new IndexOutOfBoundsException("Index not inside the container's range");
        return students.remove(index);
    }

    @Override
    public Student getPawnByID(int ID) {
        return students.stream()
                .filter(student -> student.getID() == ID)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No such element in container"));
    }

    public Object getUsesLeft() {
        return usesLeft;
    }

    public int getSize() {
        return students.size();
    }
}
