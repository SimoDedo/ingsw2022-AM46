package it.polimi.ingsw.Network.Server;

import it.polimi.ingsw.Network.Message.UserAction.LoginUserAction;
import it.polimi.ingsw.Network.Message.UserAction.UserAction;

import java.net.InetAddress;

/**
 * Interface for all the server classes in the project. Servers have common methods based on having
 * similar functions, for example handling client login, registering and unregistering clients, and
 * more.
 */
public interface Server {

    void parseAction(ConnectionThread connectionThread, UserAction clientAction);

    void handleLogin(ConnectionThread connectionThread, LoginUserAction loginAction);

    void close();
}
