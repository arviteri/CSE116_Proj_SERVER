package server.controllers;

import game.Game;
import game.objects.PhysicsVector;
import game.objects.Player;
import game.objects.Projectile;
import game.objects.Zombie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import server.messages.MapMessage;
import server.messages.PlayerMessage;
import server.messages.ProjectileMessage;
import server.messages.types.MessageType;

@Controller
public class GameController {

    @Autowired
    public SimpMessagingTemplate messagingTemplate;
    private Game game = new Game(this);

    /**
     * Adds player to the game, returns a player message of type JOIN
     * @param sessionId
     * @return
     * @throws Exception
     */
    @MessageMapping("/join")
    @SendTo("/game/players")
    public PlayerMessage joinGame(@Header("simpSessionId") String sessionId, String playerName) throws Exception {
        PhysicsVector location = new PhysicsVector((double)(Game.WIDTH/2 - Player.WIDTH/2), (double)(Game.HEIGHT/2 - Player.HEIGHT/2), 0);
        Player newPlayer = new Player(sessionId, location);
        String playerStr = playerName.replaceAll("\"", "");
        newPlayer.name = playerStr;
        game.addPlayer(sessionId, newPlayer);

        PlayerMessage joinMessage = new PlayerMessage(newPlayer, MessageType.ADD);
        MapMessage<String, Player> allPlayersMessage = new MapMessage<>(game.getPlayers(), MessageType.ADD);
        MapMessage<String, Zombie> allZombiesMessage = new MapMessage<>(game.getZombies(), MessageType.ADD);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        System.out.println("Player has joined: " + playerStr); // DEBUG
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/join", joinMessage, headerAccessor.getMessageHeaders());
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/all_players", allPlayersMessage, headerAccessor.getMessageHeaders());
        return joinMessage;
    }


    /**
     * Returns the player that has been removed.
     * @param sessionId
     * @return
     * @throws Exception
     */
    @MessageMapping("/leave")
    @SendTo("/game/players")
    public PlayerMessage leaveGame(@Header("simpSessionId") String sessionId) throws Exception {
        //Player removedPlayer = game.removePlayer(sessionId);
        //System.out.println("Player has left: " + removedPlayer.name); // DEBUG
        //return new PlayerMessage(removedPlayer, MessageType.REMOVE);
        if (game.getPlayer(sessionId) != null) {
            Player removedPlayer = game.removePlayer(sessionId);
            System.out.println("Player has left: " + removedPlayer.name); // DEBUG
            return new PlayerMessage(removedPlayer, MessageType.REMOVE);
        } else {
            return null;
        }
    }


    /**
     * Updates a player's location
     * @param sessionId
     * @param location
     * @return
     * @throws Exception
     */
    @MessageMapping("/move")
    @SendTo("/game/players")
    public PlayerMessage movePlayer(@Header("simpSessionId") String sessionId, PhysicsVector location) throws Exception {
        Player player = game.getPlayers().get(sessionId);
        player.setLocation(location);
        return new PlayerMessage(player, MessageType.UPDATE);
    }

    /**
     * Updates a player's health
     * @param sessionId
     * @param health
     * @return
     * @throws Exception
     */
    @MessageMapping("/health")
    @SendTo("/game/players")
    public PlayerMessage updateHealth(@Header("simpSessionId") String sessionId, Double health) throws Exception {
        Player player = game.getPlayers().get(sessionId);
        if (player != null) {
            player.health = health;
        }
        return new PlayerMessage(player, MessageType.UPDATE);
    }

    /**
     * Adds active projectile from player.
     * @param sessionId
     * @param projectile
     * @return
     * @throws Exception
     */
    @MessageMapping("/shoot")
    @SendTo("/game/projectiles")
    public ProjectileMessage shootProjectile(@Header("simpSessionId") String sessionId, Projectile projectile) throws Exception {
        String id = Integer.toString(Game.pID++);
        projectile.id = id;
        game.addProjectile(id, projectile);
        return new ProjectileMessage(projectile, MessageType.ADD);
    }


    /**
     * Remove player from game on disconnect.
     * @param event
     */
    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        if (game.getPlayer(sessionId) != null) {
            Player removedPlayer = game.removePlayer(sessionId);
            System.out.println("Player has left: " + removedPlayer.name); // DEBUG
            messagingTemplate.convertAndSend("/game/players", new PlayerMessage(removedPlayer, MessageType.REMOVE));
        }
    }
}
