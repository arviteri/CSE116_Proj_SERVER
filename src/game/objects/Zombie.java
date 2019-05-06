package game.objects;

public class Zombie {
    public static final double MAX_HEALTH = 100;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    public String id;
    public double health;
    public PhysicsVector location;

    public Zombie() {
        this(null);
    }

    public Zombie(String id) {
        this(id, null);
    }

    public Zombie(String id, PhysicsVector location) {
        this.id = id;
        this.health = MAX_HEALTH;
        this.location = (location != null) ? location : new PhysicsVector();
    }

    public void setLocation(PhysicsVector location) {
        this.setLocation(location.x, location.y, location.z);
    }

    public void setLocation(double x, double y, double z) {
        this.location.x = x;
        this.location.y = y;
        this.location.z = z;
    }
}
