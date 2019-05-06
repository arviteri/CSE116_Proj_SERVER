package game.objects;

public class PhysicsVector {
    public double x, y, z;

    public PhysicsVector() {
        this(0,0,0);
    }

    public PhysicsVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PhysicsVector(PhysicsVector location) {
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
    }
}
