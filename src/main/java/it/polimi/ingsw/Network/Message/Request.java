package it.polimi.ingsw.Network.Message;

import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.util.ArrayList;

/**
 * Class that models a request sent by the server to a client.
 * It contains a string describing the request, and a list of RequestParameters.
 * the client who receives this list will proceed with a selection to create a UserAction in response to the request.
 */
public class Request extends Message {
    /**
     * Used in toString method
     */
    private String request;

    private ArrayList<RequestParameter> requestParameters;

    private UserActionType expectedUserAction;

    public Request(String request, UserActionType expectedUserAction) {
        super("Server");
        this.request = request;
        this.expectedUserAction = expectedUserAction;
        requestParameters = new ArrayList<>();
    }

    public ArrayList<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public void addRequestParameter(RequestParameter requestParameter){
        requestParameters.add(requestParameter);
    }

    public UserActionType getExpectedUserAction() {
        return expectedUserAction;
    }


    @Override
    public String toString() {
        return request;
    }
}
