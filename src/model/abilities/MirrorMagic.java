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
	private Mirror mirror;

	public MirrorMagic(Player player, double mirrorLength) {
		super(Constants.MIRROR_MAGIC_COOLDOWN);
		this.player = player;
		this.mirrorLength = mirrorLength;
		mirror = null;
	}

	public boolean launchMirror() {
		if (execute()) {
			Vector2D mirrorPosition = null;
			Vector2D mirrorVelocity = null;
			if(player.getPlayerSide() == PlayerSide.LEFT_PLAYER) {
				mirrorPosition = new Vector2D(player.getPosition().getdX() + 20, player.getPosition().getdY());
				mirrorVelocity = new Vector2D(30, 0);
			} else {
				mirrorPosition = new Vector2D(player.getPosition().getdX() - 20, player.getPosition().getdY());
				mirrorVelocity = new Vector2D(-30, 0);
			}
			mirror = new Mirror(mirrorPosition, mirrorVelocity, mirrorLength);
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
