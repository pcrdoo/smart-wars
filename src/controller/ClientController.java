package controller;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import main.Constants;
import main.GameMode;
import main.GameStarter;
import main.GameState;
import memory.Pools;
import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.Model;
import model.Player;
import model.PlayerSide;
import model.Wormhole;
import model.entitites.Entity;
import multiplayer.Message;
import multiplayer.MessageType;
import multiplayer.NetworkPipe;
import multiplayer.OpenPipes;
import multiplayer.Side;
import util.Vector2D;
import view.AsteroidView;
import view.BulletView;
import view.ClientView;
import view.EntityView;
import view.MirrorView;
import view.PlayerView;
import view.WormholeView;

public class ClientController extends GameStateController {

	private Model model;
	private ClientView view;
	private GameStarter gameStarter;
	private HashMap<Control, Integer> leftPlayerControls;
	private HashMap<Control, Integer> rightPlayerControls;
	private HashMap<Integer, Boolean> keyboardState;

	// View-related dependencies for each entity.
	private HashMap<Entity, EntityView> viewMap;
	private ArrayList<AsteroidView> disintegratingAsteroids;

	private InetSocketAddress serverAddress;
	private NetworkPipe serverPipe;
	private ArrayList<PlayerSide> sidesToControl; // 1 on network, 2 on local
	private GameMode gameMode;

	public ClientController(GameStarter gameStarter, ClientView view, Model model, InetSocketAddress serverAddress) {
		super();
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;
		sidesToControl = new ArrayList<>();
		if (serverAddress != null) {
			gameMode = GameMode.NETWORK;
			this.serverAddress = serverAddress;
		} else {
			// LOCAL
			gameMode = GameMode.LOCAL;
		}

		viewMap = new HashMap<>();
		disintegratingAsteroids = new ArrayList<>();

		PlayerView leftPlayerView = new PlayerView(model.getLeftPlayer());
		view.addDrawable(leftPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(leftPlayerView);
		leftPlayerControls = new HashMap<Control, Integer>();
		leftPlayerControls.put(Control.MOVE_UP, KeyEvent.VK_W);
		leftPlayerControls.put(Control.MOVE_DOWN, KeyEvent.VK_S);
		leftPlayerControls.put(Control.FIRE_GUN, KeyEvent.VK_D);
		leftPlayerControls.put(Control.SHORT_MIRROR_MAGIC, KeyEvent.VK_Q);
		leftPlayerControls.put(Control.LONG_MIRROR_MAGIC, KeyEvent.VK_A);

		PlayerView rightPlayerView = new PlayerView(model.getRightPlayer());
		view.addDrawable(rightPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(rightPlayerView);
		rightPlayerControls = new HashMap<Control, Integer>();
		rightPlayerControls.put(Control.MOVE_UP, KeyEvent.VK_I);
		rightPlayerControls.put(Control.MOVE_DOWN, KeyEvent.VK_K);
		rightPlayerControls.put(Control.FIRE_GUN, KeyEvent.VK_J);
		rightPlayerControls.put(Control.SHORT_MIRROR_MAGIC, KeyEvent.VK_O);
		rightPlayerControls.put(Control.LONG_MIRROR_MAGIC, KeyEvent.VK_L);

		initKeyboardState();

		viewMap.put(model.getLeftPlayer(), leftPlayerView);
		viewMap.put(model.getRightPlayer(), rightPlayerView);
	}

	@Override
	public void setUpConnections() throws IOException {
		if (gameMode == GameMode.NETWORK) {
			Socket socket = new Socket(serverAddress.getHostName(), serverAddress.getPort());
			OpenPipes.getInstance().setSide(Side.CLIENT);
			serverPipe = new NetworkPipe(socket);
			OpenPipes.getInstance().addPipe(serverPipe);
			Message message = serverPipe.readMessage();
			if (message.getType() != MessageType.SIDE_ASSIGNMENT) {
				throw new RuntimeException("First message from server is not SIDE_ASSIGNMENT");
			}
			PlayerSide side = (PlayerSide) message.getPayload().get(0);
			sidesToControl.add(side);
		} else {
			// TODO: enable single player again
			sidesToControl.add(PlayerSide.LEFT_PLAYER);
			sidesToControl.add(PlayerSide.RIGHT_PLAYER);
		}
	}

	private void initKeyboardState() {
		keyboardState = new HashMap<Integer, Boolean>();
		for (HashMap.Entry<Control, Integer> entry : leftPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		for (HashMap.Entry<Control, Integer> entry : rightPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		keyboardState.put(KeyEvent.VK_ENTER, false);
	}

	public void sendActionToServer(PlayerSide playerSide, Control control) {
		Message message = new Message(MessageType.PLAYER_CONTROL);
		message.addToPayload(playerSide);
		message.addToPayload(control);
		serverPipe.scheduleMessageWrite(message);
	}

	@Override
	public void update(double dt) {
		receiveUpdates();
		if (model.getGameState() != GameState.RUNNING) {
			if (keyboardState.get(KeyEvent.VK_ENTER)) {
				sendActionToServer(sidesToControl.get(0), Control.NEW_GAME);
			}
			return;
		}
		if (sidesToControl.contains(PlayerSide.LEFT_PLAYER)) {
			checkPlayerControls(model.getLeftPlayer(), leftPlayerControls);
		}
		if (sidesToControl.contains(PlayerSide.RIGHT_PLAYER)) {
			checkPlayerControls(model.getRightPlayer(), rightPlayerControls);
		}
		checkDisintegratingAsteroids();
		serverPipe.writeScheduledMessages();
	}

	private void receiveUpdates() {
		while (serverPipe.hasMessages()) {
			Message message = serverPipe.readMessage();
			switch (message.getType()) {
			case ENTITY_ADDED:
				doAddEntity((Entity) message.getPayload().get(0));
				break;
			case ENTITY_REMOVED:
				doRemoveEntity((UUID) message.getPayload().get(0));
				break;
			case ENTITY_UPDATED:
				doUpdateEntity((Entity) message.getPayload().get(0));
				break;
			case LOCATION_UPDATE:
				for (int i = 0; i < message.getPayload().size(); i += 2) {
					// TODO: Bad design, don't rely on indices
					UUID entityId = (UUID) (message.getPayload().get(i));
					Vector2D newPosition = (Vector2D) (message.getPayload().get(i + 1));
					doLocationUpdate(entityId, newPosition);
				}
				break;
			case VIEW_WORMHOLE_AFFECT:
				UUID bulletId = (UUID) (message.getPayload().get(0));
				Vector2D wormholePosition = (Vector2D) (message.getPayload().get(1));
				doWormholeAffect(bulletId, wormholePosition);
				break;
			case VIEW_DISINTEGRATE_ASTEROID:
				UUID asteroidId = (UUID) (message.getPayload().get(0));
				doDisintegrateAsteroid(asteroidId);
				break;
			case VIEW_PLAYER_ASTEROID_HIT:
				UUID playerId = (UUID) (message.getPayload().get(0));
				doPlayerAsteroidHit(playerId);
				break;
			case VIEW_MIRROR_BOUNCE:
				UUID mirrorId = (UUID) (message.getPayload().get(0));
				Vector2D bulletPosition = (Vector2D) (message.getPayload().get(1));
				doMirrorBounce(mirrorId, bulletPosition);
				break;
			case VIEW_BULLET_ASTEROID_HIT:
				UUID asteroidUid = (UUID) (message.getPayload().get(0));
				Vector2D bulletPos = (Vector2D) (message.getPayload().get(1));
				doBulletAsteroidHit(asteroidUid, bulletPos);
				break;
			case GAME_OVER:
				GameState gameState = (GameState) (message.getPayload().get(0));
				doGameOver(gameState);
				break;
			case NEW_GAME_STARTING:
				doNewGameStarting();
				break;
			default:
				throw new RuntimeException("Unexpected message type: " + message.getType());
			}
		}
	}

	// TODO This is why passing an object was a bad idea, definitely serialize on
	// your own
	private void doAddEntity(Entity entity) {
		if (entity instanceof Asteroid) {
			Asteroid temp_asteroid = (Asteroid) entity;
			Asteroid asteroid = Pools.ASTEROID.create(temp_asteroid.getPosition(), temp_asteroid.getVelocity(),
					temp_asteroid.getType(), temp_asteroid.getFrame());
			model.addEntity(asteroid);
			AsteroidView asteroidView = Pools.ASTEROID_VIEW.create(asteroid);
			viewMap.put(asteroid, asteroidView);
			view.addDrawable(asteroidView, Constants.Z_ASTEROID);
			view.addUpdatable(asteroidView);
		} else if (entity instanceof Bullet) {
			Bullet temp_bullet = (Bullet) entity;
			Bullet bullet = Pools.BULLET.create(temp_bullet.getPosition(), temp_bullet.getVelocity(),
					temp_bullet.getOwner());
			model.addEntity(bullet);
			((PlayerView) viewMap.get(bullet.getOwner())).onBulletFired(); // Action
			BulletView bulletView = Pools.BULLET_VIEW.create(bullet);
			viewMap.put(bullet, bulletView);
			view.addDrawable(bulletView, Constants.Z_BULLETS);
			view.addUpdatable(bulletView);
		} else if (entity instanceof Mirror) {
			Mirror mirror = (Mirror) entity;
			model.addEntity(mirror);
			MirrorView mirrorView = new MirrorView(mirror);
			viewMap.put(mirror, mirrorView);
			view.addDrawable(mirrorView, Constants.Z_MIRROR);
			view.addUpdatable(mirrorView);
		} else if (entity instanceof Wormhole) {
			Wormhole wormhole = (Wormhole) entity;
			model.addEntity(wormhole);
			WormholeView wormholeView = new WormholeView(wormhole);
			viewMap.put(wormhole, wormholeView);
			view.addDrawable(wormholeView, Constants.Z_WORMHOLE);
			view.addUpdatable(wormholeView);
		} else {
			throw new RuntimeException("Invalid entity type: " + entity);
		}
	}

	private void doRemoveEntity(UUID uuid) {
		Entity entity = model.getEntityById(uuid);
		deleteView(entity);
		model.removeEntity(entity);
	}

	private void deleteView(Entity entity) {
		EntityView entityView = viewMap.get(entity);
		if (entityView != null) {
			view.removeUpdatable(entityView);
			view.removeDrawable(entityView);
			entityView.onRemoved();
			viewMap.remove(entity);
		}
	}

	private void doUpdateEntity(Entity updatedEntity) {
		Entity entity = model.getEntityById(updatedEntity.getUuid());
		// TODO: entity.mergeFrom(updatedEntity); and byte buffer
	}

	private void doLocationUpdate(UUID entityId, Vector2D newPosition) {
		Entity entity = model.getEntityById(entityId);
		entity.setPosition(newPosition);
	}

	private void doWormholeAffect(UUID bulletId, Vector2D wormholePosition) {
		Bullet bullet = (Bullet) model.getEntityById(bulletId);
		((BulletView) viewMap.get(bullet)).wormholeAffect(wormholePosition);
	}

	private void doDisintegrateAsteroid(UUID asteroidId) {
		// Delete asteroid, keep just the view for the animation.
		Asteroid asteroid = (Asteroid) model.getEntityById(asteroidId);
		AsteroidView asteroidView = (AsteroidView) viewMap.get(asteroid);
		viewMap.remove(asteroid);
		disintegratingAsteroids.add(asteroidView);
		asteroidView.onAsteroidDisintegrated();
	}

	private void doPlayerAsteroidHit(UUID playerId) {
		Player player = (Player) model.getEntityById(playerId);
		((PlayerView) viewMap.get(player)).onPlayerHit();
	}

	private void doMirrorBounce(UUID mirrorId, Vector2D bulletPosition) {
		Mirror mirror = (Mirror) model.getEntityById(mirrorId);
		((MirrorView) viewMap.get(mirror)).onMirrorHit(bulletPosition);
	}

	private void doBulletAsteroidHit(UUID asteroidId, Vector2D bulletPosition) {
		Asteroid asteroid = (Asteroid) model.getEntityById(asteroidId);
		((AsteroidView) viewMap.get(asteroid)).onAsteroidHit(bulletPosition);
	}

	private void doGameOver(GameState gameState) {
		model.setGameState(gameState);
		for (HashMap.Entry<Entity, EntityView> ev : viewMap.entrySet()) {
			ev.getValue().onRemoved();
		}
		viewMap.clear();
	}

	private void doNewGameStarting() {
		gameStarter.startGame();
	}

	// Controls

	public void handleKeyDown(int keyCode) {
		keyboardState.put(keyCode, true);
		if (sidesToControl.contains(PlayerSide.LEFT_PLAYER)) {
			if (keyCode == leftPlayerControls.get(Control.SHORT_MIRROR_MAGIC)) {
				sendActionToServer(PlayerSide.LEFT_PLAYER, Control.SHORT_MIRROR_MAGIC);
			}
			if (keyCode == leftPlayerControls.get(Control.LONG_MIRROR_MAGIC)) {
				sendActionToServer(PlayerSide.LEFT_PLAYER, Control.LONG_MIRROR_MAGIC);
			}
		}
		if (sidesToControl.contains(PlayerSide.RIGHT_PLAYER)) {
			if (keyCode == rightPlayerControls.get(Control.SHORT_MIRROR_MAGIC)) {
				sendActionToServer(PlayerSide.RIGHT_PLAYER, Control.SHORT_MIRROR_MAGIC);
			}
			if (keyCode == rightPlayerControls.get(Control.LONG_MIRROR_MAGIC)) {
				sendActionToServer(PlayerSide.RIGHT_PLAYER, Control.LONG_MIRROR_MAGIC);
			}
		}
	}

	public void handleKeyUp(int keyCode) {
		keyboardState.put(keyCode, false);
	}

	private void checkPlayerControls(Player player, HashMap<Control, Integer> controls) {
		if (keyboardState.get(controls.get(Control.FIRE_GUN))) {
			sendActionToServer(player.getPlayerSide(), Control.FIRE_GUN);
		}
		if (keyboardState.get(controls.get(Control.MOVE_UP))) {
			sendActionToServer(player.getPlayerSide(), Control.MOVE_UP);
		}
		if (keyboardState.get(controls.get(Control.MOVE_DOWN))) {
			sendActionToServer(player.getPlayerSide(), Control.MOVE_DOWN);
		}
		if (keyboardState.get(controls.get(Control.MOVE_UP)) && keyboardState.get(controls.get(Control.MOVE_DOWN))) {
			sendActionToServer(player.getPlayerSide(), Control.STOP);
		}
		if (!keyboardState.get(controls.get(Control.MOVE_UP)) && !keyboardState.get(controls.get(Control.MOVE_DOWN))) {
			sendActionToServer(player.getPlayerSide(), Control.STOP);
		}
	}

	private void checkDisintegratingAsteroids() {
		ArrayList<AsteroidView> finished = new ArrayList<>();
		for (AsteroidView asteroidView : disintegratingAsteroids) {
			if (asteroidView.isDisintegrated()) {
				finished.add(asteroidView);
				view.removeUpdatable(asteroidView);
				view.removeDrawable(asteroidView);
				asteroidView.onRemoved();
			}
		}
		for (AsteroidView asteroidView : finished) {
			disintegratingAsteroids.remove(asteroidView);
		}
	}
}