package model.abilities;

import main.Constants;
import model.Bullet;
import model.Mirror;
import model.Player;
import util.Vector2D;

public class MirrorMagic extends Ability {
	private Player player;
	private double mirrorLength;

	public MirrorMagic(Player player, double shortMirrorLength) {
		super(Constants.MIRROR_MAGIC_COOLDOWN);
		this.player = player;
		this.mirrorLength = shortMirrorLength;
	}

	public Mirror createMirror(Vector2D position, Vector2D velocity) {
		if (!execute()) {
			return null;
		}
		return new Mirror();
	}
}
