package com.game.sound;

/**
 * A class that allows for control of sound effects across the game; basically,
 * whether or not they play, and at what volume. All global variables are public
 * static so will be unified across all sound.
 * 
 * @author sjo948
 *
 */
public class SfxController {
	/**
	 * Dictates whether or not sound effects will play.
	 */
	public static boolean soundOn = true;
	/**
	 * The integer representation of the current volume, between 0 and
	 * 100.
	 */
	public static int volumeInt = 100;
	/**
	 * The float value of the current volume, roughly from -80f to 0f.
	 */
	public static float volume = 0;

	/**
	 * Allows you to set the volume of sound effects from 0 to 100. Any newVolume
	 * above 100 will set it to 100; any at 0 or below will set soundOn to false.
	 * 
	 * @param newVolume The new integer volume, to be converted to a float.
	 */
	public static void setVolume(int newVolume) {

		if (newVolume > 100) {

			// Turns on sound if it isn't on already
			if (soundOn == false) {

				toggleSound();

			}

			// Sets volumeInt to 100, then sets the actual volume to 0f.
			volumeInt = 100;
			volume = 0f;

		} else if (newVolume <= 0) {

			// Turns sound off if volume is set to 0
			volumeInt = 0;
			soundOn = false;

		} else {

			// Turns on sound if it isn't on already
			if (soundOn == false) {

				toggleSound();

			}

			// Set volumeInt to the volume input, then converts the value out of 100 to an
			// appropriate volume
			volumeInt = newVolume;
			float newVolumeFloat = (float) newVolume / 100;
			volume = (((float) Math.log10(newVolumeFloat)) * 20f);

		}

	}

	/**
	 * Allows you to toggle the sound on or off.
	 */
	public static void toggleSound() {// Remind Temi to actually implement this

		if (soundOn == true) {

			soundOn = false;

		} else {

			soundOn = true;

		}

	}

}