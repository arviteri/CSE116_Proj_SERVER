package game;

import game.daemons.ProjectileDaemon;
import game.daemons.ZombieDaemon;
import game.objects.Player;
import game.objects.Projectile;
import game.objects.Zombie;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import server.controllers.GameController;
import server.messages.GameMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    /**
     * The two static ints below
     * are to keep track of zombies
     * and projectiles.
     * They're IDs for each type
     * and only unique to runtime.
     **/
    public static int zID = 0;
    public static int pID = 0;

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    private Map<String, Player> players;
    private Map<String, Zombie> zombies;
    private Map<String, Projectile> projectiles;

    private GameController controller;

    private ZombieDaemon zombieDaemon;
    private ProjectileDaemon projectileDaemon;

    public Game(GameController controller) {
        players = new ConcurrentHashMap<>();
        zombies = new ConcurrentHashMap<>();
        projectiles = new ConcurrentHashMap<>();
        this.controller = controller;

        zombieDaemon = new ZombieDaemon(this);
        projectileDaemon = new ProjectileDaemon(this);
        zombieDaemon.start();
        projectileDaemon.start();
    }

    public void addPlayer(String id, Player player) {
        this.players.put(id, player);
    }

    public Player getPlayer(String id) { return this.players.get(id); }

    public Player removePlayer(String id) {
        return this.players.remove(id);
    }

    public void addZombie(String id, Zombie zombie) {
        this.zombies.put(id, zombie);
    }

    public Zombie removeZombie(String id) {
        return this.zombies.remove(id);
    }

    public void addProjectile(String id, Projectile projectile) {
        this.projectiles.put(id, projectile);
    }

    public Projectile removeProjectile(String id) {
        return this.projectiles.remove(id);
    }

    /**
     * Returns game's players - Used so daemon threads can manipulate it.
     * @return
     */
    public Map<String, Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns game's zombies - Used so daemon threads can manipulate it.
     * @return
     */
    public Map<String, Zombie> getZombies() {
        return this.zombies;
    }

    /**
     * Returns game's projectiles - Used so daemon threads can manipulate it.
     * @return
     */
    public Map<String, Projectile> getProjectiles() {
        return this.projectiles;
    }


    public void sendUpdateToClients(String destination, GameMessage message) {
        controller.messagingTemplate.convertAndSend(destination, message);
    }

    public void sendUpdateToUser(String sessionId, String destination, Object o) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        controller.messagingTemplate.convertAndSendToUser(sessionId, destination, o, headerAccessor.getMessageHeaders());
    }
}
