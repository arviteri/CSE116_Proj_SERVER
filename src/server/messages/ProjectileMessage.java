package server.messages;

import game.objects.Projectile;
import server.messages.types.MessageType;

public class ProjectileMessage extends GameMessage {
    private Projectile projectile;

    public ProjectileMessage() {
        this(null, null);
    }

    public ProjectileMessage(Projectile projectile, MessageType type) {
        this.projectile = projectile;
        this.type = type;
    }

    public Projectile getProjectile() {
        return this.projectile;
    }
}
