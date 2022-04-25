package it.polimi.ingsw.Network.Message.Request;

import it.polimi.ingsw.Network.Message.Message;
import it.polimi.ingsw.Utils.Enum.RequestParameter;
import it.polimi.ingsw.Utils.Enum.UserActionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that models a request sent by the server to a client.
 * It contains a string describing the request, and a list of RequestParameters.
 * the client who receives this list will proceed with a selection to create a UserAction in response to the request.
 */
public class Request extends Message {

    private final String recipient;

    /**
     * Used in toString method
     */
    private final String request;

    private final List<RequestParameter> requestParameters;

    private final UserActionType expectedUserAction;

    public Request(String recipient,String request, UserActionType expectedUserAction) {
        super("Server");
        this.recipient = recipient;
        this.request = request;
        this.expectedUserAction = expectedUserAction;
        requestParameters = new ArrayList<>();
    }

    public String getRecipient() {
        return recipient;
    }

    public List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public void addRequestParameter(RequestParameter requestParameter){
        requestParameters.add(requestParameter);
    }

    public void addAllRequestParameters(List<RequestParameter> requestParameters) {
        this.requestParameters.addAll(requestParameters);
    }

    public UserActionType getExpectedUserAction() {
        return expectedUserAction;
    }


    @Override
    public String toString() {
        return request;
    }
}
