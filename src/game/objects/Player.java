package game.objects;

import game.objects.types.Direction;

public class Player {
    public static final double MAX_HEALTH = 100;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    public String id;
    public String name;
    public PhysicsVector location;
    public double health;
    public double score;
    public Direction lastMove;

    public Player() {
        this(null, null);
    }

    public Player(String id) {
        this(id, null);
    }

    public Player(String id, PhysicsVector location) {
        this.id = id;
        this.location = (location != null) ? location : new PhysicsVector();
        this.health = MAX_HEALTH;
        this.score = 0;
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
