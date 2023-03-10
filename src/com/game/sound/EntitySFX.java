package com.game.sound;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.game.utilities.Vector;
import com.game.entities.GenericEntity;
import com.game.sound.SfxController;

/**
 * A class which allows for the playing of sound effects in the game. An object
 * of this class will be assigned to each entity which requires sound effects.
 * 
 * @author sjo948
 *
 */
public class EntitySFX {

	/**
	 * A stream from which we get the audio to play.
	 */
	private AudioInputStream audioStream;

	/**
	 * A clip used to play sound effects.
	 */
	private Clip clip;

	/**
	 * A string containing the current sound's file name.
	 */
	private String fileString;

	/**
	 * A volume controller for the sound effects.
	 */
	private FloatControl volumeCtrl;

	/**
	 * A balance controller for sound effects. Lets us create a pseudo-3d sound
	 * effect.
	 */
	private FloatControl balanceCtrl;

	/**
	 * An array containing the file names of all sounds.
	 */
	private String[][] nameArrays = {

			// Player
			{

					// Finding a collectable
					// Credits: "Sound effect: Retro - Chip Power by 99Sounds"
					"makeSelection.wav",

					// Swing weapon
					// Credits: "Sound effect: "Metal hit woosh" -
					// mixkit.co/free-sound-effects/whoosh
					// Licensed under Mixkit License.
					"swingSword.wav",

					// Get hit
					"getHit.wav",

					// Die
					"playerDie.wav",

					// Shoot arrow
					// Credits: "Sound effect: "Arrow Whoosh" - mixkit.co/free-sound-effects/whoosh
					// Licensed under Mixkit Licence
					"shootArrow.wav",

					// Fire magic
					"fireMagic.wav",

					// Silent Sound
					"silentSound.wav"

			},

			// Monster
			{

					// See player
					"enemySee.wav",

					// Enemy gets hit
					"enemyHit.wav",

					// Shoot arrow
					// Credits: "Sound effect: "Arrow Whoosh" - mixkit.co/free-sound-effects/whoosh
					// Licensed under Mixkit Licence
					"shootArrow.wav",

					// Enemy dies
					"enemyDie.wav"

			},

			// Magic
			{

					// Magic exploding
					"magicExplode.wav"

			}

	};

	/**
	 * The name array for the specific object creating this EntitySFX object.
	 */
	private String[] nameArray;

	/**
	 * An array to contain the clips which are required for the object creating this one.
	 */
	private Clip[] trackArray;

	/**
	 * A variable to contain the entity to which this object is assigned.
	 */
	private GenericEntity entity;

	/**
	 * A float to contain the width of the screen, for the purpose of changing the balance of the sound
	 * effects.
	 */
	private float screenWidth;

	/**
	 * A float to contain the current left/right balance of the audio clip to be played.
	 */
	private float leftRightBalance;

	/**
	 * The constructor for an EntitySFX object.  It allows for the use of pseudo surround sound within
	 * the game.
	 * 
	 * @param thisEntity The entity for which the object will be playing sound.
	 * @param thisWidth The width of the screen.
	 * @param type The type of SFX object; either for a player, a monster or a
	 * magic projectile.
	 */
	public EntitySFX(GenericEntity thisEntity, float thisWidth, int type) {

		entity = thisEntity;
		screenWidth = thisWidth;

		if (type > nameArrays.length - 1 || type < 0) {

			nameArray = nameArrays[0];
			trackArray = new Clip[nameArray.length];

		} else {

			nameArray = nameArrays[type];
			trackArray = new Clip[nameArray.length];

		}

		// Load tracks here, put in array
		for (int x = 0; x < nameArray.length; x++) {

			String fileName = nameArray[x];

			try {
				
				audioStream = AudioSystem.getAudioInputStream(EntitySFX.class.getResource(fileName));
				trackArray[x] = AudioSystem.getClip();
				trackArray[x].open(audioStream);

			} catch (UnsupportedAudioFileException e) {
				
				System.out.println("Error - unsupported audio file");

			} catch (IOException e) {
				
				System.out.println("Error - IO Exception");

			} catch (LineUnavailableException e) {
			
				System.out.println("Error - line unavailable");

			}
		}

		if (type == 0) {

			PlayTrack(trackArray.length - 1);
		}
	}

	/**
	 * Loads and plays a specific sound effect.  
	 * @param trackNo The number of the track to be played.  If out of
	 * bounds, it will simply play track 0.
	 */
	public void PlayTrack(int trackNo) {

		if (clip == null || !clip.isActive()) {

		} else {
			StopSound();
		}
		
		if (trackNo>(trackArray.length-1)||trackNo<0) {
			
			clip = trackArray[0];
		
		} else {

			clip = trackArray[trackNo];

		}
		if (clip == null)
			System.out.println("CLIP == NULL");
		volumeCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeCtrl.setValue(SfxController.volume);
		PlaySound();

	}

	/**
	 * Plays a sound effect as long as SfxController's soundOn is true.
	 */
	public void PlaySound() {
				
		balanceCtrl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);

		// Left-right balance: ((position*2)-canvas)/canvas
		float onScreenPos = (float) (entity.getPosition().x());
		float distanceFromCentre = ((onScreenPos * 2) - screenWidth) / 2;
		leftRightBalance = distanceFromCentre / screenWidth;

		balanceCtrl.setValue(leftRightBalance);

		if (SfxController.soundOn == false) {

			return;

		}

		clip.setFramePosition(0);
		clip.setMicrosecondPosition(0);
		clip.loop(0);

	}

	/**
	 * Stops the current clip from playing.
	 */
	public void StopSound() {

		if (clip == null || !clip.isActive()) {

		} else {

			clip.stop();

		}

	}

	/**
	 * Closes all clips. Must be called upon termination of the program, or the
	 * object.
	 */
	public void KillSound() {

		for (int x = 0; x < trackArray.length; x++) {

			if (trackArray[x] == null || !trackArray[x].isActive()) {

			} else if (trackArray[x].isActive()) {

				trackArray[x].stop();
				trackArray[x].close();

			} else if (trackArray[x].isOpen()) {

				trackArray[x].close();

			}

		}

	}

}
