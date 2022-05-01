package it.polimi.ingsw.Network.Message.Info;

import it.polimi.ingsw.Utils.Enum.Phase;

public class NextPhaseInfo extends Info{

    private Phase nextPhase;

    public NextPhaseInfo(Phase nextPhase) {
        super(" phase is starting");
        this.nextPhase = nextPhase;
    }

    public Phase getNextPhase() {
        return nextPhase;
    }

    @Override
    public String toString() {
        return nextPhase + super.toString();
    }
}
