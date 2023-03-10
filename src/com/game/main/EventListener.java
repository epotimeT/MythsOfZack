package com.game.main;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.game.logic.events.DeathEvent;
import com.game.logic.events.EntitySpecifier;
import com.game.logic.events.HitEvent;
import com.game.logic.events.NewEntityEvent;
import com.game.logic.events.PlayerAssignmentEvent;
import com.game.logic.events.PositionUpdateEvent;
import com.game.logic.events.RotationUpdateEvent;
import com.game.logic.events.RotationalVelocityUpdateEvent;
import com.game.logic.events.SoundEvent;
import com.game.logic.events.StartEvent;
import com.game.logic.events.VelocityUpdateEvent;
import com.game.logic.events.WinLoseEvent;
import com.game.screens.GUI;
import com.game.utilities.Semaphore;
import com.game.utilities.Vector;

//This will decipher what the received data means (e.g. ID -> action)

/**
 * A class to decipher received packets from both UDP and TCP, and to send them to be translated into gameplay.
 * @author sjo948, kxg911, mxb1143
 *
 */
public class EventListener {

	/**
	 * A constant containing the Client object for this player.
	 */
	private Client client;
	
	/**
	 * A semaphore used during setup.
	 */
	private Semaphore semSetName;
	
	/**
	 * An integer to contain the number of erroneous packets received in a row.
	 */
	private int errorsInARow = 0;

	/**
	 * A variable containing the NetworkEventCallback object for this player.
	 */
	private NetworkEventCallback eventCallback;
	
	/**
	 * A constant containing the GUI for this player.
	 */
	private GUI gui;
	
	/**
	 * A variable to contain the number of erroneous packets received in a row when setting up a UDP port.
	 */
	private int badPackets = 0;
	
	/**
	 * The constructor for this class, given references to a Client, GUI and Semaphore
	 * @param myclient A reference to this player's client.
	 * @param sem1 A reference to the necessary semaphore.
	 * @param gui A reference to the player's GUI.
	 */
	public EventListener(Client myclient, Semaphore sem1, GUI gui) {
		this.client = myclient;
		this.semSetName = sem1;
		this.gui = gui;
	}

	
	/**
	 * A function to set the NetworkEventCallback variable for this class.
	 * @param callback The object reference needed.
	 */
	public void setCallback(NetworkEventCallback callback) {
		this.eventCallback = callback;
	}
	
	
	/**
	 * A function to unset the NetworkEventCallback variable for this class.
	 */
	public void unsetCallback() {
		this.eventCallback = null;
	}
	
	
	/**
	 * A function used to Decipher TCP packets.  An actionID is used to tell the function which action
	 * is to be performed.
	 * @param actionID The actionID of the packet, determining what action must be carried out.
	 * @param packet The rest of the packet, as a byte array.
	 */
	public void received(int actionID, byte[] packet) {

		if (packet instanceof byte[]) {

			// data is sent via TCP in the form
			String packetStr = new String(packet, StandardCharsets.UTF_8);
			System.out.println("Received Packet via TCP: '" + packetStr + "'");
			System.out.println("ActionID: " + String.valueOf(actionID));
			
			if (actionID == 0) { //CMD RELATED TO SETTING UP UDP CONNECTION
				
				System.out.println("Setting up UDP Connection");

				try {
					
					client.myUDP.setID(Integer.parseInt(packetStr));
					String newPacket = String.valueOf(client.myUDP.getID()) + " UDP";//Add UDP ActionID later
					client.myUDP.sendPacket(0, newPacket);
					System.out.println("Sent UDP Packet: '" + newPacket + "'");

				} catch (NumberFormatException e) {
					System.out.println("bad packet");
					badPackets++;
					if (badPackets > 4) {
						client.disconnectClient();
					}
				}

			} else if (actionID == 1) { // CMDS 1-4 RELATED TO LOBBY SCREEN

				// set this client's id locally
				client.setClientName(client.myUDP.getID());
				semSetName.take();
				client.sendByteArray(1, client.getName());

			} else if (actionID == 2) {//all other clients' names/ids
				
				String[] packetSplit = packetStr.split("#");
				System.out.println("length: "+packetSplit.length);
				
				for (int i=0; i<packetSplit.length; i++) {
					
					int id = Character.getNumericValue(packetSplit[i].charAt(0)); //ids of other clients
					String name = packetSplit[i].substring(1); //names of other clients
					//update client
					client.setClientName(id, name);
				
				}

			} else if (actionID == 3) {// add client

				int id = Character.getNumericValue(packetStr.charAt(0)); // id of new client
				String name = packetStr.substring(1); // name of new client
				// update client
				client.setClientName(id, name);

			} else if (actionID == 4) {// remove client

				int id = Character.getNumericValue(packetStr.charAt(0));// Maybe send & receive as int - don't convert
																		// to string by default at top
				client.setClientName(id, null);

			} else if (actionID == 5) { // Message received

				// String[] splitString = packetStr.split(",");

				client.setMessages(packetStr);

				/*
				 * if(splitString.length!=1) {
				 * 
				 * badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);
				 * 
				 * } else {
				 * 
				 * badPackets = 0;
				 * 
				 * String message = splitString[0];
				 * 
				 * //Send stuff to gui here
				 * 
				 * 
				 * }
				 */

			} else if (actionID == 6) { // initialisation handshake
				System.out.println("actionid6");
				eventCallback.onEvent(new StartEvent());
			} else if (actionID == 7) { // new entity

				String[] splitString = packetStr.split(",");

				badPackets = 0;

				int screenId = Integer.parseInt(splitString[0]);
				EntitySpecifier which = EntitySpecifier.values()[Integer.parseInt(splitString[1])];
				Object extraData = null;
				if (which == EntitySpecifier.ROCK || which == EntitySpecifier.SPIKYROCK) {
					extraData = new Vector(Double.parseDouble(splitString[2]), Double.parseDouble(splitString[3]));
				}

				//eventCallback.onEvent(
				//		new NewEntityEvent(screenId, which, extraData, client.getName(), client.getClientIndex()));

			} else if (actionID == 8) { // assign player to entity

				String[] splitString = packetStr.split(",");

				if (splitString.length != 2) {

					badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);

				} else {

					badPackets = 0;

					int screenId = Integer.parseInt(splitString[0]);
					int entityId = Integer.parseInt(splitString[1]);

					eventCallback.onEvent(new PlayerAssignmentEvent(screenId, entityId));
				}

			} else if (actionID == 9) {// Being hit - sent by server
				String[] splitString = packetStr.split(",");

				if (splitString.length != 2) {

					badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);

				} else {

					badPackets = 0;

					int collision = Integer.parseInt(splitString[0]);
					int id = Integer.parseInt(splitString[1]);

					eventCallback.onEvent(new HitEvent(id, collision));

				}

			} else if (actionID == 10) {// Dying - sent by server

				String[] splitString = packetStr.split(",");

				if (splitString.length != 1) {

					badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);

				} else {

					badPackets = 0;

					int entityId = Integer.parseInt(splitString[0]);

					eventCallback.onEvent(new DeathEvent(entityId));

				}

			} else if (actionID == 11) {// Sound

				String[] splitString = packetStr.split(",");

				if (splitString.length != 2) {

					badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);

				} else {

					badPackets = 0;

					int entityId = Integer.parseInt(splitString[0]);
					int soundId = Integer.parseInt(splitString[1]);
					eventCallback.onEvent(new SoundEvent(entityId, soundId));

				}

			} else if (actionID == 12) { // Game starting
				// Tell gui to create + show the game
				gui.setGameStarted(true);

				// Load all players

				// Tell server it is ready (?)

			} else if (actionID == 13) {
				String[] splitString = packetStr.split(",");

				if (splitString.length != 1) {

					badPacket("\tWrong length\n\tTCP\n\tactionID = " + actionID);

				} else {

					badPackets = 0;

					int didWin = Integer.parseInt(splitString[0]);

					eventCallback.onEvent(new WinLoseEvent(didWin == 1));

				}

			} else {

				badPacket("\tBad actionID\n\tTCP\n\tactionID = " + Integer.toString(actionID));

			}

		} else if (actionID == 12) {// Game start
			// Tell gui to create & show game
			gui.setGameStarted(true);
			// Load all players
			// Tell server it's ready
		} else {

			badPacket("\tBad format - not byte[]\n\tTCP\n\tactionID = " + Integer.toString(actionID));

		}

	}
	
	
	/**
	 * A function to be called when a UDP packet has been received.  The only thing UDP packets are used for is
	 * general player movement, as it is the one change that isn't as crucial to other players' experiences.
	 * @param packet - the byte array containing the movement details needed.
	 */
	public void receivedUDP(byte[] packet) {
		
		System.out.println("Received Packet via UDP");
		
		byte[] byteScreen = new byte[4];
		
		for(int x = 0; x < 4; x++) {
		
			byteScreen[x] = packet[x];
		
		}
        
		int screenId = ByteBuffer.wrap(byteScreen).getInt();
        		
        
		byte[] bytePlayer = new byte[4];
		
        for(int x = 4; x < 8; x++) {
		
        	bytePlayer[x-4] = packet[x];
		
        }
        
        int playerId = ByteBuffer.wrap(bytePlayer).getInt();
		
        
        byte[] byteXpos = new byte[8];
		
        for(int x = 8; x < 16; x++) {
		
        	byteXpos[x-8] = packet[x];
		
        }
		
        double xpos = ByteBuffer.wrap(byteXpos).getDouble();
		
		
        byte[] byteYpos = new byte[8];
		
        for(int x = 16; x < 24; x++) {
		
        	byteYpos[x-16] = packet[x];
		
        }
		
        double ypos = ByteBuffer.wrap(byteYpos).getDouble();
		Vector position = new Vector(xpos, ypos);
		
		
		byte[] byteXVector = new byte[8];
		
		for(int x = 24; x < 32; x++) {
		
			byteXVector[x-24] = packet[x];
		
		}
		
		Double xVector = ByteBuffer.wrap(byteXVector).getDouble();
		
		
		byte[] byteYVector = new byte[8];
		
		for(int x = 32; x < 40; x++) {
		
			byteYVector[x-32] = packet[x];
		
		}
		
		Double yVector = ByteBuffer.wrap(byteYVector).getDouble();
		Vector velocity = new Vector(xVector, yVector);
        
		
		byte[] byteRotation = new byte[8];
		
		for(int x = 40; x < 48; x++) {
		
			byteRotation[x-40] = packet[x];
		
		}
		
		double rotation = ByteBuffer.wrap(byteRotation).getDouble();
        
		
		byte[] byteRotationalVelocity = new byte[8];
		
		for(int x = 48; x < 56; x++) {
		
			byteRotationalVelocity[x-48] = packet[x];
		
		}
		
		double rotationalVelocity = ByteBuffer.wrap(byteRotationalVelocity).getDouble();
		
		
		if(eventCallback != null) {
		
			eventCallback.onEvent(new PositionUpdateEvent(screenId, playerId, position));
			eventCallback.onEvent(new VelocityUpdateEvent(screenId, playerId, velocity));
            eventCallback.onEvent(new RotationUpdateEvent(screenId, playerId, rotation));
            eventCallback.onEvent(new RotationalVelocityUpdateEvent(screenId, playerId, rotationalVelocity));
		
		}
		
	}
	
	
	/**
	 * A function to be called when a bad packet is received.  If too many are received the client will disconnect.
	 * @param problem The issue that has presented itself.
	 */
	public void badPacket(String problem) {

		System.out.println("BAD PACKET RECEIVED.  problem:\n" + problem);
		badPackets++;

		if (badPackets >= 5) {

			client.disconnectClient();

		}

	}

}
