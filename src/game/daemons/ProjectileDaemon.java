package game.daemons;

import game.Game;
import game.objects.Player;
import game.objects.Projectile;
import game.objects.Zombie;
import server.messages.ProjectileMessage;
import server.messages.types.MessageType;

import java.util.Iterator;
import java.util.Map;

public class ProjectileDaemon extends Thread {

    private Game game;

    public ProjectileDaemon(Game game) {
        this.game = game;
    }

    /**
     * ProjectileDaemon
     */
    @Override
    public synchronized void run() {
        while (true) {
            removeOldProjectiles();
            updateProjectiles();
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Projectile daemon error.");
            }
        }
    }

    /**
     * Checks time projectiles have been in air,
     * sends message to client to remove them if they've
     * been alive for their max time.
     */
    public synchronized void removeOldProjectiles() {
        Map<String, Projectile> projectiles = game.getProjectiles();
        Iterator<Map.Entry<String, Projectile>> iter = projectiles.entrySet().iterator();
        while (iter.hasNext()) {
            Projectile projectile = iter.next().getValue();
            if (game.getPlayers().get(projectile.ownerId) == null) {
                ProjectileMessage removeMessage = new ProjectileMessage(projectile, MessageType.REMOVE);
                game.sendUpdateToClients("/game/projectiles", removeMessage);
                iter.remove();
            }
        }
    }


    /**
     * Updates projectiles location and sends UPDATE message to clients.
     * Checks if projectile has hit a zombie, if so sends score update message to client who owns that projectile.
     * Checks if projectile hit another player, if so, sends message to REMOVE it.
     */
    public synchronized void updateProjectiles() {
        Map<String, Projectile> projectiles = game.getProjectiles();
        Iterator<Map.Entry<String, Projectile>> iter = projectiles.entrySet().iterator();
        while (iter.hasNext()) {
            Projectile projectile = iter.next().getValue();
            if (projectile.timeAlive >= Projectile.MAX_TIME_ALIVE) {
                ProjectileMessage removeMessage = new ProjectileMessage(projectile, MessageType.REMOVE);
                game.sendUpdateToClients("/game/projectiles", removeMessage);
                iter.remove();
            } else {
                double dx = projectile.location.x + (projectile.velocity.x);
                double dy = projectile.location.y + (projectile.velocity.y);
                projectile.setLocation(dx, dy, 0);
                projectile.timeAlive += 100;
                ProjectileMessage updateMessage = new ProjectileMessage(projectile, MessageType.UPDATE);

                Map<String, Player> players = game.getPlayers();
                Map<String, Zombie> zombies = game.getZombies();

                /* Check if hit a zombie */
                Iterator<Map.Entry<String, Zombie>> zIter = zombies.entrySet().iterator();
                while (zIter.hasNext()) {
                    Zombie zombie = zIter.next().getValue();
                    if (projectile.location.x >= zombie.location.x && projectile.location.x <= zombie.location.x+Zombie.WIDTH) {
                        if (projectile.location.y >= zombie.location.y && projectile.location.y <= zombie.location.y+Zombie.HEIGHT) {
                            zombie.health -= Projectile.HIT_DAMAGE;
                            if (players.get(projectile.ownerId) != null) {
                                players.get(projectile.ownerId).score += Projectile.HIT_DAMAGE;
                                game.sendUpdateToUser(projectile.ownerId,"/queue/score", new Double(players.get(projectile.ownerId).score));
                            }
                            updateMessage.setType(MessageType.REMOVE);
                        }
                   }
                }

                /* Check if hit a another player */
                Iterator<Map.Entry<String, Player>> pIter = players.entrySet().iterator();
                while (pIter.hasNext()) {
                    Player player = pIter.next().getValue();
                    if (!projectile.ownerId.equals(player.id)) {
                        if (projectile.location.x >= player.location.x && projectile.location.x <= player.location.x+Zombie.WIDTH) {
                            if (projectile.location.y >= player.location.y && projectile.location.y <= player.location.y+Zombie.HEIGHT) {
                                updateMessage.setType(MessageType.REMOVE);
                            }
                        }
                    }
                }

                game.sendUpdateToClients("/game/projectiles", updateMessage);
                if (updateMessage.getType() == MessageType.REMOVE) {
                    iter.remove();
                }
            }
        }
    }
}
