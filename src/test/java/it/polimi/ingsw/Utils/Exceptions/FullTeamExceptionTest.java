package it.polimi.ingsw.Utils.Exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that it throws correctly
 */
class FullTeamExceptionTest {
    @Test
    public void throwFullTeamException(){
        assertThrows(FullTeamException.class, () -> {throw new FullTeamException();});
    }
}