package game.objects.types;

public enum Direction {
    UP(-1), DOWN(1), LEFT(-2), RIGHT(2);

    private int value;

    private Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}