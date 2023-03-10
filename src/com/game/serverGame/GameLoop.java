package com.game.serverGame;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.game.areas.Area0;
import com.game.commands.MeleeCommands;
import com.game.entities.ComplexEntity;
import com.game.entities.GenericEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster;
import com.game.entities.GenericMonster.State;
import com.game.entities.NullEntity;
import com.game.entities.Retriever;
import com.game.entities.Sword;
import com.game.logic.ServerPlayer;
import com.game.server.Server;
import com.game.server.TCPConnection;
import com.game.serverScreen.ServerGUI;
import com.game.utilities.Vector;

public class GameLoop implements Runnable {

	private Server server;
	private ServerGUI gui;

	private GenericEntity retriever = new Retriever();

	private boolean runningGame = false;
	private boolean withGui;
	private int initCount = 0;
	private static final Vector canvasSize = new Vector(1200, 700);

	// Constructor w/ GUI Method
	public GameLoop(int[] idList, Server server, ServerGUI gui) {
		withGui = true;

		this.gui = gui;
		this.server = server;
		TCPConnection[] tcpConnections = server.getTCPConnections();
		for (int i = 0; i < 4; i++) {
			if (server.serverUDPConnection.addresses[i] != null && tcpConnections[i] != null) {
				new ServerPlayer(i, tcpConnections[i], new Vector(), 0, server.getClientNames()[i], (i + 1))
						.setPosition(

								new Vector[] { new Vector(10, 10), new Vector(canvasSize.x() - 10, 10),
										new Vector(10, canvasSize.y() - 10),
										new Vector(canvasSize.x() - 10, canvasSize.y() - 10), }[i]);
			}
		}
	}

	// Constructor w/out GUI Method
	public GameLoop(int[] idList, Server server) {
		withGui = false;
		this.server = server;
		TCPConnection[] tcpConnections = server.getTCPConnections();
		for (int i = 0; i < 4; i++) {
			if (server.serverUDPConnection.addresses[i] != null && tcpConnections[i] != null) {
				new ServerPlayer(i, tcpConnections[i], new Vector(), 0, server.getClientNames()[i], (i + 1))
						.setPosition(new Vector[] { new Vector(20, 20), new Vector(1000, 20), new Vector(20, 500),
								new Vector(1000, 500), }[i]);
			}
		}
	}

	public void startGame() {

		// 1. Assign each player in the game a Player object in the array players
		/*
		 * for (int i = 0; i < Server.MAX_CLIENTS; i++) { if (idList[i] == 1) { //if
		 * player with this id exists then make Player Vector startPos =
		 * startPositions[i]; players[i] = new Player(i,startPos); } }
		 */

		// 2. Start thread (daemon to serverGUI/server thread still)
		runningGame = true;
		Thread game = new Thread(this);
		game.setDaemon(true); // so that when the server closes, so does the thread TODO
		game.start();

		// Tell clients the game can begin
		int clientCount = server.getIdCount();
		server.fwdBroadcastTCPData(12, "");

		System.out.println(clientCount);
		while (initCount < clientCount) {
			// This only works when we print the value out.
			// I'd love to know why.
			System.out.println(initCount);
		}

		/*
		 * Area00 a00 = new Area00(); a00.obstacles(canvasSize);
		 * a00.monsters(canvasSize); a00.items(canvasSize);
		 */
		Area0 a0 = new Area0();
		a0.obstacles(canvasSize);
		a0.monsters(canvasSize);
		// a0.items(canvasSize);

		List<List<GenericEntity>> screens = retriever.getEntities();
		for (int screen = 0; screen < screens.size(); screen++) {
			List<GenericEntity> entities = screens.get(screen);
			for (int entity = 0; entity < entities.size(); entity++) {
				if (entities.get(entity) instanceof ServerPlayer) {
					ServerPlayer player = (ServerPlayer) entities.get(entity);
					/*
					 * for (TCPConnection conn : server.getTCPConnections()) { if (conn != null) {
					 * System.out.println("sending id 8 - player"); conn.sendData(7,
					 * prepareToSendData(screen, EntitySpecifier.PLAYER.ordinal())); } }
					 */
					// server.fwdBroadcastTCPData(7, prepareToSendData(screen,
					// EntitySpecifier.PLAYER.ordinal()));
					player.tcpConnection.sendData(8, prepareToSendData(screen, entity));
				} /*
					 * else if (entities.get(entity) instanceof Slime) {
					 * server.fwdBroadcastTCPData(7, prepareToSendData(screen,
					 * EntitySpecifier.SLIME.ordinal())); } else if (entities.get(entity) instanceof
					 * RockSpitter) { server.fwdBroadcastTCPData(7, prepareToSendData(screen,
					 * EntitySpecifier.ROCKSPITTER.ordinal())); } else if (entities.get(entity)
					 * instanceof Rock) { Vector renderSize =
					 * entities.get(entity).getEntitySprite().getRenderSize();
					 * server.fwdBroadcastTCPData(7, prepareToSendData(screen,
					 * EntitySpecifier.ROCK.ordinal(), renderSize.x(), renderSize.y())); } else if
					 * (entities.get(entity) instanceof SpikyRock) { Vector renderSize =
					 * entities.get(entity).getEntitySprite().getRenderSize();
					 * server.fwdBroadcastTCPData(7, prepareToSendData(screen,
					 * EntitySpecifier.SPIKYROCK.ordinal(), renderSize.x(), renderSize.y())); }
					 */
			}
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		retriever.getEntities().forEach(screen -> {
			for (int i = 0; i < screen.size(); i++) {
				GenericEntity entity = screen.get(i);
				server.serverUDPConnection.broadcastPacket(1, 0, i, entity.getPosition().x(), entity.getPosition().y(),
						entity.getVelocity().x(), entity.getVelocity().y(), entity.getEntitySprite().rotation(),
						entity.getEntitySprite().rotationalVelocity());
			}
		});

		for (TCPConnection conn : server.getTCPConnections()) {
			if (conn != null) {
				conn.sendData(6, "");
			}
		}

		// Tell host the game has begun
		if (withGui) {
			gui.addList("The game has started");
		} else {
			System.out.println("The game has started");
		}
	}

	private String prepareToSendData(Object... values) {
		return Arrays.stream(values).map(x -> x.toString()).collect(Collectors.joining(","));
	}

	public void onMovementVelocityUpdateEvent(int clientId, int screenId, int entityId, Vector position,
			Vector velocity, double rotation, double rotationalVelocity, int invTime) {
		GenericEntity entity = retriever.getEntities().get(screenId).get(entityId);
		entity.setPosition(position);
		entity.setVelocity(position);
		if (entity instanceof ComplexEntity) {
			((ComplexEntity) entity).setInvulnerableTime(invTime);
		}
		server.serverUDPConnection.broadcastPacketExceptClient(1, clientId, screenId, entityId, position.x(),
				position.y(), velocity.x(), velocity.y(), rotation, rotationalVelocity);
	}

	public void updateClientInitialisedCount() {
		this.initCount += 1;
	}

	// Main Thread Method
	@Override
	public void run() {
		while (runningGame) {
			try {
				retriever.getEntities().forEach(screen -> {
					List<GenericEntity> entities = screen;
					for (int i = 0; i < entities.size(); i++) {
						GenericEntity e = entities.get(i);
						if (e.entityType == Type.MONSTER) {
							GenericMonster mon = (GenericMonster) e;
							mon.updateState();
							mon.performAction();
							if (mon.getCurrState() == State.DEAD) {
								entities.set(i, new NullEntity());
								server.fwdBroadcastTCPData(10, Integer.toString(i));
							}
							e.getEntitySprite().updatePositionWithVelocity(66000000);
							e.getEntitySprite().updateRotationWithVelocity(66000000);
							server.serverUDPConnection.broadcastPacket(1, e.getScreenId(), i, e.getPosition().x(),
									e.getPosition().y(), e.getVelocity().x(), e.getVelocity().y(),
									e.getEntitySprite().rotation(), e.getEntitySprite().rotationalVelocity());
						}

						if (e instanceof ServerPlayer) {
							// ((ServerPlayer) e).updateState();
							int clientId = ((ServerPlayer) e).udpid;
							server.serverUDPConnection.broadcastPacketExceptClient(1, clientId, e.getScreenId(), i,
									e.getPosition().x(), e.getPosition().y(), e.getVelocity().x(), e.getVelocity().y(),
									e.getEntitySprite().rotation(), e.getEntitySprite().rotationalVelocity());
						} else if (e instanceof Sword) {
							int swordId = i;
							server.serverUDPConnection.broadcastPacket(1, swordId, i, e.getPosition().x(),
									e.getPosition().y(), e.getVelocity().x(), e.getVelocity().y(),
									e.getEntitySprite().rotation(), e.getEntitySprite().rotationalVelocity());
						} else {
							// server.serverUDPConnection.broadcastPacket(1, e.getScreenId(), i,
							// e.getPosition().x(), e.getPosition().y(), e.getVelocity().x(),
							// e.getVelocity().y(), e.getEntitySprite().rotation(),
							// e.getEntitySprite().rotationalVelocity());
						}

						try {
							entities.forEach(otherEntity -> {
								if (otherEntity != e) {
									e.getEntitySprite().performCollisionCheck(otherEntity.getEntitySprite());
								}
							});
						} catch (Exception ex) {
							System.out.println("Failed to perform a collision check!");
							ex.printStackTrace();
						}
					}
				});

				// check for a winner
				List<ServerPlayer> remainingPlayers = retriever.getEntities().stream().flatMap(List::stream)
						.filter(entity -> entity instanceof ServerPlayer).map(entity -> (ServerPlayer) entity)
						.collect(Collectors.toList());
				if (remainingPlayers.size() == 1) {
					ServerPlayer winningPlayer = remainingPlayers.get(0);
					server.fwdBroadcastExcClientTCPData(winningPlayer.udpid, 13, "0");
					winningPlayer.tcpConnection.sendData(13, "1");
					retriever.getEntities().forEach(screen -> screen.clear());
					break;
				}

				Thread.sleep(66); // ~ 15 ticks/sec
			} catch (InterruptedException e) {
				System.out.println("InterruptedException - GameLoop Thread");
			}
		}
	}

	public void deployClientWeapon(int id, Vector target) {
		MeleeCommands.deployMeleeWeapon((ComplexEntity) retriever.getEntities().get(0).get(id), target);
	}

	public void onClientDeath(int entityId) {
		server.fwdBroadcastExcClientTCPData(((ServerPlayer) retriever.getEntities().get(0).get(entityId)).udpid, 10,
				Integer.toString(entityId));
		retriever.getEntities().get(0).set(entityId, new NullEntity());
	}
}
