package controller;

import java.nio.ByteBuffer;
import java.util.Collection;

import main.GameState;
import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.Player;
import model.Wormhole;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.Message;
import multiplayer.MessageType;
import multiplayer.OpenPipes;

public class ServerEventBroadcaster {
	public ServerEventBroadcaster() {
	}

	public void broadcastAddEntity(Entity entity) {
		Message message = new Message(MessageType.ENTITY_ADDED);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put((byte)EntityType.fromEntity(entity).getNum());
		entity.serializeTo(buf);
		byte[] bytes = new byte[buf.position()];
		buf.position(0);
		buf.get(bytes, 0, bytes.length);
		message.addToPayload(bytes);
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}
	
	public void broadcastRemoveEntity(Entity entity) {
		Message message = new Message(MessageType.ENTITY_REMOVED);
		message.addToPayload(entity.getUuid());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}
	
	public void broadcastUpdateEntity(Entity entity) {
		Message message = new Message(MessageType.ENTITY_UPDATED);
		message.addToPayload(entity.getUuid());
		ByteBuffer buf = ByteBuffer.allocate(1024);
		entity.serializeTo(buf);
		byte[] bytes = new byte[buf.position()];
		buf.position(0);
		buf.get(bytes, 0, bytes.length);
		message.addToPayload(bytes);
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastLocations(Collection<Entity> entities) {
		Message message = new Message(MessageType.LOCATION_UPDATE);
		for(Entity entity: entities) {
			message.addToPayload(entity.getUuid());
			message.addToPayload(entity.getPosition());
		}
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastWormholeAffect(Bullet bullet, Wormhole nearestWormhole) {
		Message message = new Message(MessageType.VIEW_WORMHOLE_AFFECT);
		message.addToPayload(bullet.getUuid());
		message.addToPayload(nearestWormhole.getPosition());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastDisintegrateAsteroid(Asteroid asteroid) {
		Message message = new Message(MessageType.VIEW_DISINTEGRATE_ASTEROID);
		message.addToPayload(asteroid.getUuid());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastPlayerAsteroidHit(Player player, Asteroid asteroid) {
		Message message = new Message(MessageType.VIEW_PLAYER_ASTEROID_HIT);
		message.addToPayload(player.getUuid());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastMirrorBounce(Mirror mirror, Bullet bullet) {
		Message message = new Message(MessageType.VIEW_MIRROR_BOUNCE);
		message.addToPayload(mirror.getUuid());
		message.addToPayload(bullet.getPosition());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}
	
	public void broadcastBulletAsteroidHit(Asteroid asteroid, Bullet bullet) {
		Message message = new Message(MessageType.VIEW_BULLET_ASTEROID_HIT);
		message.addToPayload(asteroid.getUuid());
		message.addToPayload(bullet.getPosition());
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

	public void broadcastGameOver(GameState gameState) {
		Message message = new Message(MessageType.GAME_OVER);
		message.addToPayload(gameState);	
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}
	
	public void broadcastNewGameStarting() {
		Message message = new Message(MessageType.NEW_GAME_STARTING);
		OpenPipes.getInstance().scheduleMessageWriteToAll(message);
	}

}
