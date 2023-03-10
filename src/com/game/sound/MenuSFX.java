package com.game.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.game.sound.SfxController;


/**
 * A class which allows for the playing of sound effects in the game. An object
 * of this class will be assigned to each entity which requires sound effects.
 * 
 * @author sjo948
 *
 */
public class MenuSFX {

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
	private String[] nameArray = {
			
			//Moving Menu Selection
			//Credits: "Sound effect: Short - Swish by 99Sounds"
			"moveSelection.wav", 
			
			//Making Menu Selection
			//Credits: "Sound effect: Retro - Chip Power by 99Sounds"
			"makeSelection.wav", 
			
			//Swing weapon
			//Credits: "Sound effect: "Metal hit woosh" - mixkit.co/free-sound-effects/whoosh
			//Licensed under Mixkit License.
			"swingSword.wav", 
			
			//Get hit
			"getHit.wav",

			// Die
			"playerDie.wav",

			// See player
			"enemySee.wav",

			// Enemy attack
			"enemyAttack.wav",

			// Shoot arrow
			// Credits: "Sound effect: "Arrow Whoosh" - mixkit.co/free-sound-effects/whoosh
			// Licensed under Mixkit Licence
			"shootArrow.wav",

			// Enemy dies
			"enemyDie.wav" };

	/**
	 * Empty constructor for a MenuSFX object
	 */
	public MenuSFX() {

	}

	/**
	 * Loads and plays a specific sound effect.  
	 * @param trackNo The number of the track to be played.  If out of
	 * bounds, it will simply play track 0.
	 */
	public void loadTrack(int trackNo) {
				
		if (trackNo>(nameArray.length-1)||trackNo<0) {
			
			this.fileString = "moveSelection.wav";;
		
		} else {

			this.fileString = nameArray[trackNo];

		}
		
		if (clip == null || !clip.isActive()) {
					
		} else {
			
			clip.setFramePosition(0);
			clip.setMicrosecondPosition(0);
			this.stopSound();
			
		}
		
		try {
			audioStream = AudioSystem.getAudioInputStream(MenuSFX.class.getResource(fileString));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
			volumeCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

		} catch (UnsupportedAudioFileException e) {
			
			System.out.println("Error - unsupported audio file");

		} catch (IOException e) {
			
			System.out.println("Error - IO Exception");

		} catch (LineUnavailableException e) {
		
			System.out.println("Error - line unavailable");

		}

		volumeCtrl.setValue(SfxController.volume);
		playSound();

	}

	/**
	 * Plays a sound effect if one isn't already playing, and as long as SfxController's soundOn is true.
	 */
	public void playSound() {

		System.out.println("playSound has started");
		// get position on screen
		if (SfxController.soundOn == false) {

			return;

		}

		clip.setFramePosition(0);
		clip.setMicrosecondPosition(0);
		clip.loop(0);

	}

	/**
	 * Closes the current clip, makes the object unusable until you load another
	 * track.
	 */
	public void stopSound() {

		System.out.println("stopSound has started");// ONLY WORKS IF THIS PRINT STATEMENT IS HERE

		if (clip == null || !clip.isActive()) {

		} else {

			clip.stop();
			clip.close();

		}

	}
}
