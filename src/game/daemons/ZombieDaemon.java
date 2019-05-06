package game.daemons;

import game.Game;
import game.objects.PhysicsVector;
import game.objects.Player;
import game.objects.Zombie;
import server.messages.ZombieMessage;
import server.messages.types.MessageType;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ZombieDaemon extends Thread {

    private static int MOVE_INTERVAL = 2;
    private Random random;
    private Game game;

    public ZombieDaemon(Game game) {
        this.game = game;
        this.random = new Random();
    }

    @Override
    public synchronized void run() {
        while (true) {
            generateZombies();
            updateZombieLocations();
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Zombie daemon error.");
            }
        }
    }

    public void generateZombies() {
        Map<String, Zombie> zombies = game.getZombies();
        while (zombies.size() < maxZombies()) {
            String id = Integer.toString(Game.zID++);
            PhysicsVector randomLocation = generateRandomOutOfBoundsLocation();
            Zombie zombie = new Zombie(id, randomLocation);
            zombies.put(id, zombie);
            ZombieMessage zombieMessage = new ZombieMessage(zombie, MessageType.ADD);
            game.sendUpdateToClients("/game/zombies", zombieMessage);
        }
    }

    /**
     * For each zombie, finds closest player and
     * updates its location to move towards it.
     * Also removes a zombie if it finds it's health is <= 0.
     */
    public synchronized void updateZombieLocations() {
        Map<String, Zombie> zombies = game.getZombies();
        Iterator<Map.Entry<String, Zombie>> iter = zombies.entrySet().iterator();
        while (iter.hasNext()) {
            Zombie zombie = iter.next().getValue();
            if (zombie.health > 0) {
                Player closestPlayer = closestPlayer(zombie.location);
                /* Calculate movement */
                if (zombie.location.y == closestPlayer.location.y) {
                    if (zombie.location.x < closestPlayer.location.x) {
                        zombie.setLocation(zombie.location.x+MOVE_INTERVAL, zombie.location.y, 0);
                    } else {
                        zombie.setLocation(zombie.location.x-MOVE_INTERVAL, zombie.location.y, 0);
                    }
                } else if (zombie.location.x == closestPlayer.location.x) {
                    if (zombie.location.y < closestPlayer.location.y) {
                        zombie.setLocation(zombie.location.x, zombie.location.y+MOVE_INTERVAL, 0);
                    } else {
                        zombie.setLocation(zombie.location.x, zombie.location.y-MOVE_INTERVAL, 0);
                    }
                } else {
                    if (zombie.location.x < closestPlayer.location.x) {
                        zombie.setLocation(zombie.location.x+MOVE_INTERVAL, zombie.location.y, 0);
                    } else {
                        zombie.setLocation(zombie.location.x-MOVE_INTERVAL, zombie.location.y, 0);
                    }
                    if (zombie.location.y < closestPlayer.location.y) {
                        zombie.setLocation(zombie.location.x, zombie.location.y+MOVE_INTERVAL, 0);
                    } else {
                        zombie.setLocation(zombie.location.x, zombie.location.y-MOVE_INTERVAL, 0);
                    }
                }
                ZombieMessage updateMessage = new ZombieMessage(zombie, MessageType.UPDATE);
                game.sendUpdateToClients("/game/zombies", updateMessage);
            } else {
                ZombieMessage removeMessage = new ZombieMessage(zombie, MessageType.REMOVE);
                game.sendUpdateToClients("/game/zombies", removeMessage);
                iter.remove();
            }
        }
    }

    /**
     * Generates random coordinates that are out of bounds but within
     * the zombie's width*2 of the bounds
     * @return
     */
    public PhysicsVector generateRandomOutOfBoundsLocation() {
        int x = random.nextInt((Game.WIDTH+Zombie.WIDTH*2)-(-Zombie.WIDTH*2)) - Zombie.WIDTH*2;
        int y = random.nextInt((Game.HEIGHT+Zombie.HEIGHT*2)-(-Zombie.HEIGHT*2)) - Zombie.HEIGHT*2;
        if (Math.random() < 0.5) {
            y = Math.random() < 0.5 ? random.nextInt((Game.HEIGHT+Zombie.HEIGHT*2)-(Game.HEIGHT+Zombie.HEIGHT)) + (Game.HEIGHT+Zombie.HEIGHT) : random.nextInt(-(Zombie.HEIGHT) - (-(Zombie.HEIGHT)*2)) - (Zombie.HEIGHT*2);
        } else {
            x = Math.random() < 0.5 ? random.nextInt((Game.WIDTH+Zombie.WIDTH*2)-(Game.WIDTH+Zombie.WIDTH)) + (Game.WIDTH+Zombie.WIDTH) : random.nextInt(-(Zombie.WIDTH) - (-(Zombie.WIDTH)*2)) - (Zombie.WIDTH*2);
        }

        return new PhysicsVector(x, y, 0);
    }

    /**
     * Finds the closest player given a location.
     * Used by updateZombieLocations to find a player for a zombie to move towards.
     * @param location
     * @return
     */
    public Player closestPlayer(PhysicsVector location) {
        Player closestPlayer;
        if (game.getPlayers().size() < 1) {
            closestPlayer = new Player("", new PhysicsVector(0,0,0));
        } else {
            Map.Entry<String,Player> entry = game.getPlayers().entrySet().iterator().next();
            closestPlayer = game.getPlayers().get(entry.getKey());
        }
        Map<String, Player> players = game.getPlayers();
        for (Map.Entry<String, Player> i: players.entrySet()) {
            Player playeri = i.getValue();
            double d1 = getDistance(location, playeri.location);
            double d2 = getDistance(location, closestPlayer.location);
            if (d1 < d2) {
                closestPlayer = playeri;
            }
        }
        return closestPlayer;
    }

    /**
     * Calculates distance between two points (locations)
     * @param l1
     * @param l2
     * @return
     */
    public double getDistance(PhysicsVector l1, PhysicsVector l2) {
        double x1 = l1.x;
        double x2 = l2.x;
        double y1 = l1.y;
        double y2 = l2.y;
        return Math.sqrt(Math.pow(Math.abs(x2-x1),2) + Math.pow(Math.abs(y2-y1),2));
    }

    /**
     * Determines max amount of zombies to spawn.
     * @return
     */
    public int maxZombies() {
        return game.getPlayers().size() * 10;
    }
}
