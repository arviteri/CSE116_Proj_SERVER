package server.messages;

import server.messages.types.MessageType;
import java.util.Map;

public class MapMessage<T, S> extends GameMessage {
    private Map<T, S> map;

    public MapMessage() {
        this(null, null);
    }

    public MapMessage(Map<T, S> map, MessageType type) {
        this.map = map;
        this.type = type;
    }

    public Map<T, S> getMap() {
        return this.map;
    }
}
