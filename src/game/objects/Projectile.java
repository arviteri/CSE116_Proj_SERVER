package game.objects;

public class Projectile {
    public static final int MAX_TIME_ALIVE = 2000;
    public static final int HIT_DAMAGE = 25;

    public String id;
    public String ownerId;
    public PhysicsVector location;
    public PhysicsVector velocity;
    public int timeAlive;

    public Projectile() {
        this(null, null);
    }

    public Projectile(String id, String ownerId) {
        this(id, ownerId, null);
    }

    public Projectile(String id, String ownerId, PhysicsVector location) {
        this.id = id;
        this.ownerId = ownerId;
        this.location = (location != null) ? location : new PhysicsVector();
        this.velocity = new PhysicsVector();
        this.timeAlive = 0;
    }

    public void setLocation(PhysicsVector location) {
        this.setLocation(location.x, location.y, location.z);
    }

    public void setLocation(double x, double y, double z) {
        this.location.x = x;
        this.location.y = y;
        this.location.z = z;
    }

    public void setVelocity(PhysicsVector velocity) {
        this.setVelocity(velocity.x, velocity.y, velocity.z);
    }

    public void setVelocity(double x, double y, double z) {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }
}
