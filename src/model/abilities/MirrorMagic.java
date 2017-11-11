package model.abilities;

import main.Constants;
import model.Bullet;
import model.Mirror;
import model.MirrorState;
import model.Player;
import model.PlayerSide;
import util.Vector2D;

public class MirrorMagic extends Ability {
	private Player player;
	private double mirrorLength;
	private boolean isLong;
	private Mirror mirror;

	public MirrorMagic(Player player, double mirrorLength, boolean isLong) {
		super(Constants.MIRROR_MAGIC_COOLDOWN);
		this.player = player;
		this.mirrorLength = mirrorLength;
		this.isLong = isLong;
		mirror = null;
	}

	public boolean launchMirror() {
		if (execute()) {
			Vector2D mirrorPosition = null;
			Vector2D mirrorVelocity = null;
			if(player.getPlayerSide() == PlayerSide.LEFT_PLAYER) {
				mirrorPosition = new Vector2D(Constants.MIRROR_X_LAUNCH_LEFT, player.getPosition().getY());
				mirrorVelocity = new Vector2D(Constants.MIRROR_VELOCITY, 0);
			} else {
				mirrorPosition = new Vector2D(Constants.MIRROR_X_LAUNCH_RIGHT, player.getPosition().getY());
				mirrorVelocity = new Vector2D(-Constants.MIRROR_VELOCITY, 0);
			}
			mirror = new Mirror(mirrorPosition, mirrorVelocity, player, mirrorLength, isLong);
			return true;
		}
		return false;
	}
	
	public void removeMirror() {
		mirror = null;
	}

	public Mirror getMirror() {
		return mirror;
	}
}
