package controller;

import java.nio.ByteBuffer;
import java.util.Collection;

import main.GameMode;
import main.GameState;
import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.Player;
import model.Wormhole;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.OpenPipes;
import multiplayer.messages.*;

public class ServerEventBroadcaster {
	private GameMode mode;
	
	public ServerEventBroadcaster(GameMode mode) {
		this.mode = mode;
	}

	public void broadcastAddEntity(Entity entity) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new AddEntityMessage(entity));
	}
	
	public void broadcastRemoveEntity(Entity entity) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new RemoveEntityMessage(entity));
	}
	
	public void broadcastUpdateEntity(Entity entity) {
		if (mode == GameMode.LOCAL) {
			return;
		}
		
		OpenPipes.getInstance().scheduleMessageWriteToAll(new UpdateEntityMessage(entity.getUuid(), entity));
	}

	public void broadcastLocations(Collection<Entity> entities) {
		if (mode == GameMode.LOCAL) {
			return;
		}
		
		OpenPipes.getInstance().scheduleMessageWriteToAll(new PositionSyncMessage(entities));
	}

	public void broadcastWormholeAffect(Bullet bullet, Wormhole nearestWormhole) {
		/*Message message = new Message(MessageType.VIEW_WORMHOLE_AFFECT);
		message.addToPayload(bullet.getUuid());
		message.addToPayload(nearestWormhole.getPosition());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);*/
		
		// TODO: Remove completely
	}

	public void broadcastDisintegrateAsteroid(Asteroid asteroid) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new DisintegrateAsteroidMessage(asteroid));
	}

	public void broadcastPlayerHit(Player player) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new PlayerHitMessage(player));
	}

	public void broadcastMirrorBounce(Mirror mirror, Bullet bullet) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new MirrorBounceMessage(mirror, bullet.getPosition()));
	}
	
	public void broadcastBulletAsteroidHit(Asteroid asteroid, Bullet bullet) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new AsteroidHitMessage(asteroid, bullet.getPosition()));
	}

	public void broadcastGameOver(GameState gameState) {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new GameOverMessage(gameState));
	}
	
	public void broadcastNewGameStarting() {
		OpenPipes.getInstance().scheduleMessageWriteToAll(new NewGameStartingMessage());
	}

}
