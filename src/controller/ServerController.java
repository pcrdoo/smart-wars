package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import main.Constants;
import main.GameMode;
import main.GameStarter;
import main.GameState;
import memory.Pools;
import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.MirrorState;
import model.Model;
import model.Player;
import model.PlayerSide;
import model.Wormhole;
import model.abilities.MirrorMagic;
import model.entitites.Entity;
import multiplayer.LocalPipe;
import multiplayer.NetworkPipe;
import multiplayer.OpenPipes;
import multiplayer.Pipe;
import multiplayer.messages.LocalPlayerControlMessage;
import multiplayer.messages.Message;
import multiplayer.messages.PlayerControlMessage;
import multiplayer.messages.SideAssignmentMessage;
import util.Vector2D;
import view.ServerView;

public class ServerController extends GameStateController {

	private Model model;
	private ServerView view;
	private GameStarter gameStarter;
	private double timeToNextAsteroidSpawn;
	private double timeToNextWormholeSpawn;
	private double timeToNextLocationUpdate;
	private Random asteroidRandom;
	private CollisionController collisionController;
	private HashSet<PlayerSide> playersReadyForRestart;
	private ServerEventBroadcaster broadcaster;
	private HashMap<PlayerSide, Pipe> playerPipes;
	private HashMap<PlayerSide, Boolean> playerFiring;
	private Pipe localPlayersPipe;
	private GameMode gameMode;

	public ServerController(GameStarter gameStarter, ServerView view, Model model, GameMode gameMode) {
		super();
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;
		this.gameMode = gameMode;
		broadcaster = new ServerEventBroadcaster(gameMode);
		collisionController = new CollisionController(broadcaster, model);
		timeToNextAsteroidSpawn = Constants.ASTEROID_SPAWN_TIME;
		timeToNextWormholeSpawn = Constants.WORMHOLE_SPAWN_TIME;
		timeToNextLocationUpdate = Constants.LOCATION_UPDATE_TIME;
		asteroidRandom = new Random();
		playerPipes = new HashMap<>();
		playerFiring = new HashMap<>();
		playerFiring.put(PlayerSide.LEFT_PLAYER, false);
		playerFiring.put(PlayerSide.RIGHT_PLAYER, false);
	}

	@Override
	public void setUpConnections() throws IOException {
		ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT);
		// wait for two
		UUID leftUuid = model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).getUuid(),
				rightUuid = model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).getUuid();

		for (int i = 0; i < 2; i++) {
			Socket socket = serverSocket.accept();

			if (playerPipes.containsKey(PlayerSide.LEFT_PLAYER)) {
				Pipe rightPipe = new NetworkPipe(socket);
				playerPipes.put(PlayerSide.RIGHT_PLAYER, rightPipe);
				OpenPipes.getInstance().addPipe(rightPipe);

				rightPipe.scheduleMessageWrite(new SideAssignmentMessage(PlayerSide.RIGHT_PLAYER, leftUuid, rightUuid));
			} else {
				Pipe leftPipe = new NetworkPipe(socket);
				playerPipes.put(PlayerSide.LEFT_PLAYER, leftPipe);
				OpenPipes.getInstance().addPipe(leftPipe);

				leftPipe.scheduleMessageWrite(new SideAssignmentMessage(PlayerSide.LEFT_PLAYER, leftUuid, rightUuid));
			}
		}
		OpenPipes.getInstance().writeScheduledMessagesOnAll();
		serverSocket.close();
		if (view != null) {
			view.addText("Test ServerView: end of setUpConnections");
		}
	}

	@Override
	public void setLocalPipe(LocalPipe pipe) {
		localPlayersPipe = pipe;
		OpenPipes.getInstance().addPipe(pipe);
	}

	private void fireGuns(Player player) {
		if (!playerFiring.get(player.getPlayerSide())) {
			return;
		}

		Bullet bullet = player.fireBullet();
		if (bullet != null) {
			model.addEntity(bullet);
			broadcaster.broadcastAddEntity(bullet);
		}
	}

	@Override
	public void update(double dt) {
		checkForIncomingMessages();
		if (model.getGameState() != GameState.RUNNING) {
			if (playersReadyForRestart.size() == 2) {
				broadcaster.broadcastNewGameStarting();
				gameStarter.startGame(); // start a new game
			}
			return;
		}

		fireGuns(model.getPlayerOnSide(PlayerSide.LEFT_PLAYER));
		fireGuns(model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER));

		cullEntities(collisionController.checkBulletCollisions());
		collisionController.checkAsteroidPlayerCollisions();
		checkDyingWormholes();
		maybeSpawnWormholes(dt);
		maybeSpawnAsteroids(dt);
		cullEntities(getEntitiesToCull());
		maybeSendLocationUpdate(dt);
		checkGameOver();
		OpenPipes.getInstance().writeScheduledMessagesOnAll();
	}

	private void checkForIncomingMessages() {
		if (gameMode == GameMode.NETWORK) {
			checkForPlayerMessages(playerPipes.get(PlayerSide.LEFT_PLAYER));
			checkForPlayerMessages(playerPipes.get(PlayerSide.RIGHT_PLAYER));
		} else {
			checkForPlayerMessages(localPlayersPipe);
		}
	}

	private void checkForPlayerMessages(Pipe pipe) {
		ArrayList<Message> messages = new ArrayList<>();
		while (pipe.hasMessages()) {
			messages.add(pipe.readMessage(model));
		}
		
		for (Message message : messages) {
			if (!(message instanceof PlayerControlMessage)) {
				throw new RuntimeException("Invalid message from the client with type " + message.getType());
			}

			PlayerSide side;
			if (gameMode == GameMode.LOCAL) {
				side = ((LocalPlayerControlMessage) message).getSide();
			} else {
				side = (pipe == playerPipes.get(PlayerSide.LEFT_PLAYER) ? PlayerSide.LEFT_PLAYER
						: PlayerSide.RIGHT_PLAYER);
			}

			Player player = model.getPlayerOnSide(side);

			Control control = ((PlayerControlMessage) message).getControl();
			switch (control) {
			case START_GUN:
				playerFiring.put(side, true);
				break;

			case STOP_GUN:
				playerFiring.put(side, false);
				break;

			case MOVE_UP:
				player.moveUp();
				break;
			case MOVE_DOWN:
				player.moveDown();
				break;
			case STOP:
				player.stopMoving();
				break;
			case SHORT_MIRROR_MAGIC:
				doMirrorMagic(player.getShortMirrorMagic());
				break;
			case LONG_MIRROR_MAGIC:
				doMirrorMagic(player.getLongMirrorMagic());
				break;
			case NEW_GAME:
				if (model.getGameState() != GameState.RUNNING) {
					playersReadyForRestart.add(side);
				}
				break;

			default:
				throw new RuntimeException("Invalid control: " + control);
			}
			broadcaster.broadcastUpdateEntity(player);
		}
	}

	private void doMirrorMagic(MirrorMagic mirrorMagic) {
		if (mirrorMagic.getMirror() == null) {
			if (!mirrorMagic.launchMirror()) {
				return;
			}
			Mirror mirror = mirrorMagic.getMirror();
			model.addEntity(mirror);
			broadcaster.broadcastAddEntity(mirror);
		} else {
			switch (mirrorMagic.getMirror().getMirrorState()) {
			case TRAVELLING:
				mirrorMagic.getMirror().setMirrorState(MirrorState.SPINNING);
				break;
			case SPINNING:
				mirrorMagic.getMirror().setMirrorState(MirrorState.STABLE);
				break;
			case STABLE:
				Mirror mirror = mirrorMagic.getMirror();
				model.removeEntity(mirror);
				mirrorMagic.removeMirror();
				broadcaster.broadcastRemoveEntity(mirror);
				break;
			default:
				break;
			}
		}
		// TODO overkill
		broadcaster.broadcastUpdateEntity(mirrorMagic.getOwner());
	}

	private void cullEntities(ArrayList<Entity> toCull) {
		for (Entity entity : toCull) {
			model.removeEntity(entity);
			broadcaster.broadcastRemoveEntity(entity);
		}
	}

	private void checkDyingWormholes() {
		ArrayList<Entity> deadWormholes = new ArrayList<>();
		for (Wormhole wormhole : model.getWormholes()) {
			if (wormhole.isDead()) {
				deadWormholes.add(wormhole);
			}
		}
		cullEntities(deadWormholes);
	}

	private void maybeSpawnWormholes(double dt) {
		timeToNextWormholeSpawn -= dt;
		if (timeToNextWormholeSpawn <= 0) {
			if (asteroidRandom.nextDouble() <= Constants.WORMHOLE_SPAWN_PROBABILITY && model.getAsteroids().size() >= 2
					&& model.getWormholes().size() == 0) {
				Collections.shuffle(model.getAsteroids());
				ArrayList<Asteroid> asteroidsToTransform = new ArrayList<Asteroid>(model.getAsteroids().subList(0, 2));
				for (Asteroid asteroid : asteroidsToTransform) {
					spawnWormholeFromAsteroid(asteroid);
				}
			}
			timeToNextWormholeSpawn = 1;
		}
	}

	private void spawnWormholeFromAsteroid(Asteroid asteroid) {
		Wormhole wormhole = new Wormhole(asteroid.getPosition());
		model.removeEntity(asteroid);
		broadcaster.broadcastDisintegrateAsteroid(asteroid);
		broadcaster.broadcastRemoveEntity(asteroid);
		model.addEntity(wormhole);
		broadcaster.broadcastAddEntity(wormhole);
	}

	private void maybeSpawnAsteroids(double dt) {
		timeToNextAsteroidSpawn -= dt;
		if (timeToNextAsteroidSpawn <= 0) {
			if (asteroidRandom.nextDouble() <= Constants.ASTEROID_SPAWN_PROBABILITY) {
				int type = asteroidRandom.nextInt(Constants.ASTEROID_TYPE_COUNT);
				int frame = asteroidRandom.nextInt(Constants.ASTEROID_SPRITES_X * Constants.ASTEROID_SPRITES_Y);
				double spawnXRange = Constants.ASTEROID_SPAWN_X_MAX - Constants.ASTEROID_SPAWN_X_MIN;
				double spawnX = asteroidRandom.nextDouble() * spawnXRange + Constants.ASTEROID_SPAWN_X_MIN;
				Vector2D location = new Vector2D(spawnX, Constants.ASTEROID_SPAWN_Y);
				Vector2D velocity = new Vector2D(
						(Math.random() > 0.5 ? -1 : 1) * Constants.ASTEROID_X_VELOCITY
								+ (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_X_VELOCITY_JITTER,
						Constants.ASTEROID_Y_VELOCITY
								+ (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_Y_VELOCITY_JITTER);
				Asteroid asteroid = Pools.ASTEROID.create(location, velocity, type, frame);
				model.addEntity(asteroid);
				broadcaster.broadcastAddEntity(asteroid);
			}
			timeToNextAsteroidSpawn = 1;
		}
	}

	private ArrayList<Entity> getEntitiesToCull() {
		ArrayList<Entity> toCull = new ArrayList<Entity>();
		for (Entity entity : model.getEntities()) {
			if (entity.shouldCull()) {
				toCull.add(entity);
			}
		}
		return toCull;
	}

	private void maybeSendLocationUpdate(double dt) {
		timeToNextLocationUpdate -= dt;
		if (timeToNextLocationUpdate <= 0) {
			broadcaster.broadcastLocations(model.getEntities());
			timeToNextLocationUpdate = Constants.LOCATION_UPDATE_TIME;
		}
	}

	private void checkGameOver() {
		if (!model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).isAlive()
				&& !model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).isAlive()) {
			model.setGameState(GameState.DRAW);
			onGameOver();
		} else if (!model.getPlayerOnSide(PlayerSide.LEFT_PLAYER).isAlive()) {
			model.setGameState(GameState.RIGHT_WIN);
			onGameOver();
		} else if (!model.getPlayerOnSide(PlayerSide.RIGHT_PLAYER).isAlive()) {
			model.setGameState(GameState.LEFT_WIN);
			onGameOver();
		}
	}

	private void onGameOver() {
		broadcaster.broadcastGameOver(model.getGameState());
		playersReadyForRestart = new HashSet<>();
	}
}
