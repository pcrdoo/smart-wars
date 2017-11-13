package controller;

import java.util.ArrayList;

import main.Constants;
import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.Model;
import model.Player;
import model.PlayerSide;
import model.Wormhole;
import model.entitites.Entity;

public class CollisionController {

	private ServerEventBroadcaster broadcaster;
	private Model model;

	public CollisionController(ServerEventBroadcaster broadcaster, Model model) {
		this.broadcaster = broadcaster;
		this.model = model;
	}

	public void checkAsteroidPlayerCollisions() {
		ArrayList<Asteroid> toDisintegrate = new ArrayList<>();
		for (Asteroid asteroid : model.getAsteroids()) {
			if (asteroid.hitTest(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER))) {
				model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).receiveDamage(Constants.ASTEROID_PLAYER_DAMAGE);
				broadcaster.broadcastUpdateEntity(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER));
				broadcaster.broadcastPlayerHit(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER));
				toDisintegrate.add(asteroid);
			} else if (asteroid.hitTest(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER))) {
				model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).receiveDamage(Constants.ASTEROID_PLAYER_DAMAGE);
				broadcaster.broadcastUpdateEntity(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER));
				broadcaster.broadcastPlayerHit(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER));
				toDisintegrate.add(asteroid);
			}
		}
		for (Asteroid asteroid : toDisintegrate) {
			model.removeEntity(asteroid);
			broadcaster.broadcastDisintegrateAsteroid(asteroid);
			broadcaster.broadcastRemoveEntity(asteroid);
		}
	}

	public ArrayList<Entity> checkBulletCollisions() {
		ArrayList<Entity> impactedBullets = new ArrayList<>();
		for (Bullet bullet : model.getBullets()) {
			if ((bullet.getBounces() > 0 || bullet.getOwner() == model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER))
					&& model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).hitTest(bullet.getPosition())) {
				handlePlayerHit(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER), bullet);
				impactedBullets.add(bullet);
			} else if ((bullet.getBounces() > 0 || bullet.getOwner() == model.getPlayerOnSide(PlayerSide.LEFT_PLAYER))
					&& model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).hitTest(bullet.getPosition())) {
				handlePlayerHit(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER), bullet);
				impactedBullets.add(bullet);
			} else {
				// Asteroid collision
				for (Asteroid asteroid : model.getAsteroids()) {
					if (asteroid.hitTest(bullet.getPosition())) {
						handleAsteroidHit(asteroid, bullet);
						impactedBullets.add(bullet);
					}
				}
				if (impactedBullets.contains(bullet)) {
					continue;
				}
				// Mirror collision
				for (Mirror mirror : model.getMirrors()) {
					if (mirror.hitTest(bullet.getPosition())) {
						handleMirrorHit(mirror, bullet);
					}
				}
				// Black hole collision
				for (Wormhole wormhole : model.getWormholes()) {
					if (wormhole.hitTest(bullet.getPosition())) {
						handleWormholeHit(wormhole, bullet);
					}
				}
			}

			// Affect the bullet views by near wormholes
			Wormhole nearestWormhole = null;
			double nearestWormholeDistance = 0.0;
			for (Wormhole w : model.getWormholes()) {
				double dist = w.getPosition().sub(bullet.getPosition()).length();
				if (nearestWormhole == null || dist < nearestWormholeDistance) {
					nearestWormhole = w;
					nearestWormholeDistance = dist;
				}
			}

			if (nearestWormhole != null) {
				broadcaster.broadcastWormholeAffect(bullet, nearestWormhole);
			}
		}
		return impactedBullets;
	}

	private void handleWormholeHit(Wormhole wormhole, Bullet bullet) {
		// Find other black hole.
		assert (model.getWormholes().size() == 2);
		int otherWormholeIndex = wormhole == model.getWormholes().get(1) ? 0 : 1;
		Wormhole otherWormhole = model.getWormholes().get(otherWormholeIndex);
		bullet.teleport(wormhole, otherWormhole);
		broadcaster.broadcastUpdateEntity(bullet);
	}

	private void handleMirrorHit(Mirror mirror, Bullet bullet) {
		if (bullet.bounce(mirror)) {
			broadcaster.broadcastMirrorBounce(mirror, bullet);
		}
		broadcaster.broadcastUpdateEntity(bullet);
	}

	private void handleAsteroidHit(Asteroid asteroid, Bullet bullet) {
		asteroid.getPushed(bullet.getPosition());
		broadcaster.broadcastBulletAsteroidHit(asteroid, bullet);
		broadcaster.broadcastUpdateEntity(asteroid);
	}

	private void handlePlayerHit(Player player, Bullet bullet) {
		player.receiveDamage(bullet.getDamage());
		broadcaster.broadcastPlayerHit(player);
		broadcaster.broadcastUpdateEntity(player);
	}

}
