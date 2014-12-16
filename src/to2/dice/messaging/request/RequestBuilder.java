package to2.dice.messaging.request;

import to2.dice.game.GameSettings;

/**
 * @author Fan
 * @version 0.1
 * Builder for requests. Creates instance of LoginRequest, CreateGameRequest, GameActionRequest or GetRoomListRequest.
 */
public class RequestBuilder {
    private Request.Type type;
    private String login;
    private GameSettings settings;
    private to2.dice.messaging.GameAction action;


    /**
     * Creates new RequestBuilder instance without type validation. If the type parameter won't be set later,
     * type of built request will be determined by set parameters.
     */
    public RequestBuilder() { }

    /**
     * Create new builder instance with type validation. Setting any parameter that is not allowed for specified type
     * will raise InvalidArgumentException with appropriate message.
     * @param type Type of the request that is being built.
     */
    public RequestBuilder(Request.Type type) {
        this.type = type;
    }

    /**
     * Adds Login parameter to the Request. May raise IllegalArgumentException if type was previously set
     * and it forbids login from being set.
     * @param login Login parameter for the request
     * @return Same instance of RequestBuilder, thus supporting method chaining
     */
    public RequestBuilder login(String login) {
        this.login = login;
        typeValidation("Adding login is not allowed for GetRoomList request");
        return this;
    }

    /**
     * Adds GameSettings parameter to the Request. May raise IllegalArgumentException if type was previously set
     * and it forbids GameSettings from being set.
     * @param settings Settings parameter for the request
     * @return Same instance of RequestBuilder, thus supporting method chaining
     */
    public RequestBuilder settings(GameSettings settings) {
        this.settings = settings;
        typeValidation("Adding settings is not allowed for Login, GameAction and GetRoomList requests");
        return this;
    }

    /**
     * Adds GameAction parameter to the Request. May raise IllegalArgumentException if type was previously set
     * and it forbids GameAction from being set.
     * @param action GameAction parameter for the request
     * @return Same instance of RequestBuilder, thus supporting method chaining
     */
    public RequestBuilder action(to2.dice.messaging.GameAction action) {
        this.action = action;
        typeValidation("Adding GameAction is not allowed for Login, CreateGame and GetRoomList requests");
        return this;
    }

    /**
     * Sets desired type of the request. Validates all parameters already set and will check parameters added later.
     * Raises IllegalArgumentException if specified type disallows any of the parameters already set.
     * @param type Desired type of the request
     * @return Same instance of RequestBuilder, thus supporting method chaining
     */
    public RequestBuilder type(Request.Type type) {
        this.type = type;
        typeValidation("Setting type for this request is not possible due to invalid arguments added before");
        return this;
    }

    /**
     * Builds new Request. If type was already set, it will create instance of appropriate class. If type was not set,
     * builder will try to determine request type by set parameters. It will raise IllegalArgumentException if it's not
     * possible.
     * @return Request object with appropriate type
     */
    public Request build() {
        if (type == null)
            type = validType();

        if (type == null)
            throw new IllegalArgumentException("Set parameters don't define correct request");

        switch (type) {
            case GET_ROOM_LIST:
                return new GetRoomListRequest();
            case LOGIN:
                return new LoginRequest(login);
            case CREATE_GAME:
                return new CreateGameRequest(login, settings);
            case GAME_ACTION:
                return new GameActionRequest(login, action);
        }

        return null;
    }

    private Request.Type validType() {
        if (login == null)
            if ((settings == null) && (action == null))
                return Request.Type.GET_ROOM_LIST;
            else
                return null;
        else if (settings == null)
            if (action == null)
                return Request.Type.LOGIN;
            else
                return Request.Type.GAME_ACTION;
        else if (action == null)
            return Request.Type.CREATE_GAME;
        else
            return null;
    }

    private void typeValidation(String message) {
        if (type != null && !typeRestrictionFulfilled())
            throw new IllegalArgumentException(message);
    }

    private boolean typeRestrictionFulfilled() {
        switch (type) {
            case GET_ROOM_LIST:
                return (login == null) && (settings == null) && (action == null);
            case LOGIN:
                return (settings == null) && (action == null);
            case CREATE_GAME:
                return action == null;
            case GAME_ACTION:
                return settings == null;
        }

        return false;
    }
}
