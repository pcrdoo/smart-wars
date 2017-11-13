package controller;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.plaf.synth.SynthSeparatorUI;

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
import model.entitites.EntityType;
import multiplayer.LocalPipe;
import multiplayer.NetworkPipe;
import multiplayer.Pipe;
import multiplayer.messages.AddEntityMessage;
import multiplayer.messages.AsteroidHitMessage;
import multiplayer.messages.DisintegrateAsteroidMessage;
import multiplayer.messages.GameOverMessage;
import multiplayer.messages.LocalPlayerControlMessage;
import multiplayer.messages.Message;
import multiplayer.messages.MirrorBounceMessage;
import multiplayer.messages.PlayerControlMessage;
import multiplayer.messages.PlayerHitMessage;
import multiplayer.messages.PositionSyncMessage;
import multiplayer.messages.RemoveEntityMessage;
import multiplayer.messages.SideAssignmentMessage;
import multiplayer.messages.UpdateEntityMessage;
import multiplayer.messages.UuidPosition;
import util.Vector2D;
import view.AsteroidView;
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
	private Pipe serverPipe;
	private ArrayList<PlayerSide> sidesToControl; // 1 on network, 2 on local
	private GameMode gameMode;

	public ClientController(GameStarter gameStarter, ClientView view, Model model, GameMode gameMode,
			InetSocketAddress serverAddress) {
		super();
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;
		sidesToControl = new ArrayList<>();
		this.gameMode = gameMode;
		this.serverAddress = serverAddress;

		viewMap = new HashMap<>();
		disintegratingAsteroids = new ArrayList<>();

		PlayerView leftPlayerView = new PlayerView(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER));
		view.addDrawable(leftPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(leftPlayerView);
		leftPlayerControls = new HashMap<Control, Integer>();
		leftPlayerControls.put(Control.MOVE_UP, KeyEvent.VK_W);
		leftPlayerControls.put(Control.MOVE_DOWN, KeyEvent.VK_S);
		leftPlayerControls.put(Control.START_GUN, KeyEvent.VK_D);
		leftPlayerControls.put(Control.SHORT_MIRROR_MAGIC, KeyEvent.VK_Q);
		leftPlayerControls.put(Control.LONG_MIRROR_MAGIC, KeyEvent.VK_A);

		PlayerView rightPlayerView = new PlayerView(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER));
		view.addDrawable(rightPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(rightPlayerView);
		rightPlayerControls = new HashMap<Control, Integer>();
		rightPlayerControls.put(Control.MOVE_UP, KeyEvent.VK_I);
		rightPlayerControls.put(Control.MOVE_DOWN, KeyEvent.VK_K);
		rightPlayerControls.put(Control.START_GUN, KeyEvent.VK_J);
		rightPlayerControls.put(Control.SHORT_MIRROR_MAGIC, KeyEvent.VK_O);
		rightPlayerControls.put(Control.LONG_MIRROR_MAGIC, KeyEvent.VK_L);

		initKeyboardState();

		viewMap.put(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER), leftPlayerView);
		viewMap.put(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER), rightPlayerView);
	}

	@Override
	public void setUpConnections() throws IOException {
		Socket socket = new Socket(serverAddress.getHostName(), serverAddress.getPort());
		serverPipe = new NetworkPipe(socket);
		Message message = serverPipe.readMessage(model);
		if (!(message instanceof SideAssignmentMessage)) {
			throw new RuntimeException("First message from server is not SIDE_ASSIGNMENT");
		}

		SideAssignmentMessage sideAssignment = (SideAssignmentMessage) message;

		PlayerSide side = sideAssignment.getClientSide();
		sidesToControl.add(side);

		/*
		 * UUID oldLeftUuid = model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).getUuid(),
		 * oldRightUuid = model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).getUuid();
		 */

		model.updatePlayerId(PlayerSide.LEFT_PLAYER, sideAssignment.getLeftPlayerUuid());
		model.updatePlayerId(PlayerSide.RIGHT_PLAYER, sideAssignment.getRightPlayerUuid());
	}

	@Override
	public void setLocalPipe(LocalPipe pipe) {
		this.serverPipe = pipe;
		sidesToControl.add(PlayerSide.LEFT_PLAYER);
		sidesToControl.add(PlayerSide.RIGHT_PLAYER);
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
		Message message;
		if (gameMode == GameMode.NETWORK) {
			message = new PlayerControlMessage(control);
		} else {
			message = new LocalPlayerControlMessage(control, playerSide);
		}
		serverPipe.scheduleMessageWrite(message);
	}

	@Override
	public void update(double dt) {
		receiveUpdates();
		if (model.getGameState() != GameState.RUNNING) {
			if (keyboardState.get(KeyEvent.VK_ENTER)) {
				for (PlayerSide side : sidesToControl) {
					sendActionToServer(side, Control.NEW_GAME);
				}
			}
		} else {
			checkDisintegratingAsteroids();
		}
		serverPipe.writeScheduledMessages();
	}

	private void receiveUpdates() {
		ArrayList<Message> messages = new ArrayList<>();
		while (serverPipe.hasMessages()) {
			messages.add(serverPipe.readMessage(model));
		}

		for (Message message : messages) {
			switch (message.getType()) {
			case ENTITY_ADDED:
				doAddEntity(((AddEntityMessage) message).getEntity());
				break;

			case ENTITY_REMOVED:
				doRemoveEntity(((RemoveEntityMessage) message).getEntity());
				break;

			case ENTITY_UPDATED:
				((UpdateEntityMessage) message).apply(model);
				break;

			case POSITION_SYNC:
				for (UuidPosition uuidPosition : ((PositionSyncMessage) message).getUuidPositions()) {
					doPositionSync(uuidPosition.getUuid(), uuidPosition.getPosition());
				}
				break;

			case VIEW_DISINTEGRATE_ASTEROID:
				doDisintegrateAsteroid(((DisintegrateAsteroidMessage) message).getAsteroid());
				break;

			case VIEW_PLAYER_HIT:
				doPlayerHit(((PlayerHitMessage) message).getPlayer());
				break;

			case VIEW_MIRROR_BOUNCE:
				MirrorBounceMessage mirrorBounce = (MirrorBounceMessage) message;
				doMirrorBounce(mirrorBounce.getMirror(), mirrorBounce.getPosition());
				break;

			case VIEW_BULLET_ASTEROID_HIT:
				AsteroidHitMessage asteroidHit = (AsteroidHitMessage) message;
				doBulletAsteroidHit(asteroidHit.getAsteroid(), asteroidHit.getPosition());
				break;

			case GAME_OVER:
				doGameOver(((GameOverMessage) message).getState());
				break;

			case NEW_GAME_STARTING:
				doNewGameStarting();
				break;

			default:
				throw new RuntimeException("Unexpected message type: " + message.getType());
			}
		}
	}

	private EntityView createViewForEntity(EntityType t, Entity e) {
		switch (t) {
		case ASTEROID:
			return Pools.ASTEROID_VIEW.create((Asteroid) e);
		case BULLET:
			return Pools.BULLET_VIEW.create((Bullet) e);
		case PLAYER:
			return new PlayerView((Player) e);
		case MIRROR:
			return new MirrorView((Mirror) e);
		case WORMHOLE:
			return new WormholeView((Wormhole) e);
		default:
			System.err.println("Unknown entity type: " + t.getNum());
			return null;
		}
	}

	private int getZIndexForEntityType(EntityType t) {
		switch (t) {
		case ASTEROID:
			return Constants.Z_ASTEROID;
		case BULLET:
			return Constants.Z_BULLETS;
		case PLAYER:
			return Constants.Z_PLAYER;
		case MIRROR:
			return Constants.Z_MIRROR;
		case WORMHOLE:
			return Constants.Z_WORMHOLE;
		default:
			System.err.println("Unknown entity type: " + t.getNum());
			return -1;
		}
	}

	// TODO This is why passing an object was a bad idea, definitely serialize on
	// your own
	private void doAddEntity(Entity entity) {
		EntityType type = EntityType.fromEntity(entity);
		EntityView entityView = createViewForEntity(type, entity);
		view.addDrawable(entityView, getZIndexForEntityType(type));
		view.addUpdatable(entityView);
		viewMap.put(entity, entityView);

		if (gameMode == GameMode.NETWORK) {
			model.addEntity(entity);
		}
	}

	private void doRemoveEntity(Entity entity) {
		deleteView(entity);

		if (gameMode == GameMode.NETWORK) {
			model.removeEntity(entity);
		}
	}

	private void deleteView(Entity entity) {
		EntityView entityView = viewMap.get(entity);
		if (entityView != null) {
			if (!disintegratingAsteroids.contains(entityView)) {
				view.removeUpdatable(entityView);
				view.removeDrawable(entityView);
				entityView.onRemoved();
			}
			viewMap.remove(entity);
		} else {
			throw new RuntimeException(
					"Error: No view found for entity " + entity.getUuid() + " of type " + entity.getClass());
		}
	}

	private void doPositionSync(UUID entityId, Vector2D newPosition) {
		Entity entity = model.getEntityById(entityId);
		entity.setPosition(newPosition);
	}

	// TODO: Reinstate this only on the client side (as it's purely gfx)
	/*
	 * private void doWormholeAffect(UUID bulletId, Vector2D wormholePosition) {
	 * Bullet bullet = (Bullet) model.getEntityById(bulletId); ((BulletView)
	 * viewMap.get(bullet)).wormholeAffect(wormholePosition); }
	 */

	private void doDisintegrateAsteroid(Asteroid asteroid) {
		// Add view to disintegrating asteroids.
		AsteroidView asteroidView = (AsteroidView) viewMap.get(asteroid);
		disintegratingAsteroids.add(asteroidView);
		asteroidView.onAsteroidDisintegrated();
	}

	@SuppressWarnings("unused")
	private void _dumpViewMap() {
		for (HashMap.Entry<Entity, EntityView> e : viewMap.entrySet()) {
			System.out.println(e.getKey().getUuid() + " - " + e.getKey().getClass() + " = " + e.getValue().getClass());
		}
	}

	private void doPlayerHit(Player player) {
		((PlayerView) viewMap.get(player)).onPlayerHit();

	}

	private void doMirrorBounce(Mirror mirror, Vector2D bulletPosition) {
		((MirrorView) viewMap.get(mirror)).onMirrorHit(bulletPosition);
	}

	private void doBulletAsteroidHit(Asteroid asteroid, Vector2D bulletPosition) {
		((AsteroidView) viewMap.get(asteroid)).onAsteroidHit(bulletPosition);
	}

	private void doGameOver(GameState gameState) {
		if (gameMode == GameMode.NETWORK) {
			model.setGameState(gameState);
		}

		for (HashMap.Entry<Entity, EntityView> ev : viewMap.entrySet()) {
			ev.getValue().onRemoved();
		}
		viewMap.clear();
		view.onGameOver(gameState);
	}

	private void doNewGameStarting() {
		gameStarter.startGame();
	}

	// Controls

	public Control keyCodeToControl(int keyCode, HashMap<Control, Integer> controls) {
		for (HashMap.Entry<Control, Integer> e : controls.entrySet()) {
			if (e.getValue() == keyCode) {
				return e.getKey();
			}
		}
		return null;
	}

	public void handlePlayerKeyDown(int keyCode, Player player, HashMap<Control, Integer> controls) {
		Control c = keyCodeToControl(keyCode, controls);
		if (c == null) {
			return;
		}

		Control newControl = null;
		switch (c) {
		case MOVE_UP:
			newControl = Control.MOVE_UP;
			break;
		case MOVE_DOWN:
			newControl = Control.MOVE_DOWN;
			break;
		case START_GUN:
			newControl = Control.START_GUN;
			break;
		default:
			break;
		}

		if (newControl != null) {
			sendActionToServer(player.getPlayerSide(), newControl);
		}
	}

	public void handlePlayerKeyUp(int keyCode, Player player, HashMap<Control, Integer> controls) {
		Control c = keyCodeToControl(keyCode, controls);
		if (c == null) {
			return;
		}

		Control newControl = null;
		switch (c) {
		case MOVE_UP:
			if (keyboardState.get(controls.get(Control.MOVE_DOWN))) {
				newControl = Control.MOVE_DOWN;
			} else {
				newControl = Control.STOP;
			}
			break;

		case MOVE_DOWN:
			if (keyboardState.get(controls.get(Control.MOVE_UP))) {
				newControl = Control.MOVE_UP;
			} else {
				newControl = Control.STOP;
			}
			break;

		case START_GUN:
			newControl = Control.STOP_GUN;
			break;

		default:
			break;
		}

		if (newControl != null) {
			sendActionToServer(player.getPlayerSide(), newControl);
		}
	}

	public void handleKeyDown(int keyCode) {
		if (keyboardState.containsKey(keyCode) && keyboardState.get(keyCode)) {
			return;
		}

		keyboardState.put(keyCode, true);

		if (sidesToControl.contains(PlayerSide.LEFT_PLAYER)) {
			handlePlayerKeyDown(keyCode, model.getPlayerOnSide(PlayerSide.LEFT_PLAYER), leftPlayerControls);

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

			handlePlayerKeyDown(keyCode, model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER), rightPlayerControls);
		}
	}

	public void handleKeyUp(int keyCode) {
		if (!keyboardState.containsKey(keyCode) || !keyboardState.get(keyCode)) {
			return;
		}

		keyboardState.put(keyCode, false);

		if (sidesToControl.contains(PlayerSide.LEFT_PLAYER)) {
			handlePlayerKeyUp(keyCode, model.getPlayerOnSide(PlayerSide.LEFT_PLAYER), leftPlayerControls);
		}
		if (sidesToControl.contains(PlayerSide.RIGHT_PLAYER)) {
			handlePlayerKeyUp(keyCode, model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER), rightPlayerControls);
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