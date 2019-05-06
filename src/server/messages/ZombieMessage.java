package server.messages;

import game.objects.Zombie;
import server.messages.types.MessageType;

public class ZombieMessage extends GameMessage {
    private Zombie zombie;

    public ZombieMessage() {
        this(null, null);
    }

    public ZombieMessage(Zombie zombie, MessageType type) {
        this.zombie = zombie;
        this.type = type;
    }

    public Zombie getZombie() {
        return this.zombie;
    }
}
