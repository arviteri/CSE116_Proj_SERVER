package server.messages;

import server.messages.types.MessageType;

/* Needed to create a generic message sending function in Game using GameController */
public class GameMessage {
    protected MessageType type;
    public GameMessage() { }

    public MessageType getType() {
        return this.type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
