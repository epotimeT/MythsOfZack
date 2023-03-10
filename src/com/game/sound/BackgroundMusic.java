package com.game.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class that allows for music to be played in the background, with varying
 * volume, and that can be stopped and started at any point.
 * 
 * @author sjo948
 *
 */
public class BackgroundMusic {
	
	/**
	 * The music file that is to be played.
	 */
	private static File musicFile;
	
	/**
	 * Tells us whether or not music is to be played.
	 */
	private static boolean playMusic = true;

	/**
	 * The stream from which audio is taken.
	 */
	private static AudioInputStream audioStream;

	/**
	 * The clip object, from which music is played.
	 */
	private static Clip clip;

	/**
	 * An integer representation of the volume at present.
	 */
	private static int volumeInt = 10;

	/**
	 * The float value of the volume at present.
	 */
	private static float volume = -20.0f;

	/**
	 * The previous integer volume at which music played.
	 */
	private static int oldVolumeInt = 10;

	/**
	 * The previous float volume at which music played.
	 */
	private static float oldVolume = -20.0f;

	/**
	 * A controller for the volume of the clip object.
	 */
	private static FloatControl volumectrl;
	
	/**
	 * An array of song names from which to choose.
	 */
	private static String[] songList = {
			// To be played on the main menu
			// Credits: "Music: "Gold Gryphons", from PlayOnLoop.com.
			// Licensed under Creative Commons by Attribution 4.0"
			"src/com/game/sound/mainMenu.wav",
			// To be played
			// Credits: "Music: "Misty Dungeon", from PlayOnLoop.com.
			// Licensed under Creative Commons by Attribution 4.0"
			"src/com/game/sound/mainGame.wav",
			// Credits: "Music: "Race Duel", from PlayOnLoop.com
			// Licensed under Creative Commons by Attribution 4.0
			"src/com/game/sound/winGame.wav",
			// Credits: "Music: "Henchman", from PlayOnLoop.com
			// Licensed under Creative Commons by Attribution 4.0
			"src/com/game/sound/loseGame.wav",
			// Credits: "Music: "Staff Roll", from PlayOnLoop.com
			// Licensed under Creative Commons by Attribution 4.0
			"src/com/game/sound/staffRoll.wav" };

	/**
	 * The individual song name which is being played at the moment.
	 */
	private static String fileString = "src/com/game/sound/mainMenu.wav";

	/**
	 * A constructor used to pick a song and start playing it.
	 * 
	 * @param soundNo Lets you pick the track to be played. 0-4 pick the first 5
	 *                tracks, any other number picks the first.
	 */
	public BackgroundMusic(int soundNo) {

		changeTrack(soundNo);

	}

	/**
	 * Allows you to change the track of the background music.
	 * 
	 * @param trackno Is the number of the track to be played.
	 */
	public static void changeTrack(int trackNo) {

		if (trackNo > (songList.length - 1) || trackNo < 0) {

			fileString = "src/com/game/sound/mainMenu.wav";

		} else {

			fileString = songList[trackNo];

		}

		musicFile = new File(fileString);

		Mixer.Info[] MixerInfo = AudioSystem.getMixerInfo();

		if (clip == null || !clip.isActive()) {

		} else {

			clip.setFramePosition(0);
			clip.setMicrosecondPosition(0);
			stop();
			clip.close();

		}

		try {

			System.out.println(musicFile.exists());
			audioStream = AudioSystem.getAudioInputStream(musicFile);
			AudioFormat audioFormat = audioStream.getFormat();
			DataLine.Info data = new DataLine.Info(Clip.class, audioFormat);
			clip = (Clip) AudioSystem.getLine(data);
			clip.open(audioStream);
			volumectrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			setMusicVolume(volumeInt);

		} catch (UnsupportedAudioFileException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (LineUnavailableException e) {

			e.printStackTrace();

		}
		play();

	}

	/**
	 * Starts music playing. 
	 */
	private static void play() {

		System.out.println("play has started");

		if (playMusic) {

			clip.loop(Clip.LOOP_CONTINUOUSLY);

		}
	}

	/**
	 * Stops music playing. 
	 */
	public static void stop() {

		System.out.println("stop has started");
		clip.stop();

	}

	/**
	 * Closes the music clip.
	 */
	public static void close() {

		System.out.println("close has started");

		clip.close();

	}

	/**
	 * Stops playing and closes the clip. Clip.play will no longer work after this.
	 */
	public static void kill() {
		playMusic = false;
		clip.close();

	}

	/**
	 * Allows the user to change the music volume. If the number entered is above
	 * 100, it will default to 100. if below 0, it will default to 0. Otherwise, the
	 * number entered is used.
	 * 
	 * @param newVolume The new volume at which music will be played.
	 */
	public static void setMusicVolume(int newVolume) {

		if (newVolume > 100) {

			System.out.println("VOLUME TOP");

			// Turns on sound if it isn't on already
			if (playMusic == false) {
				toggleSound();

			}

			// Sets volumeInt to 100, then sets the actual volume to 0f.
			volumeInt = 100;
			volume = 0f;
			System.out.println(volume);
			volumectrl.setValue(volume);

		} else if (newVolume <= 0) {

			System.out.println("VOLUME BOTTOM");
			System.out.println(volumectrl.getMinimum());
			// Turns sound off if volume is set to 0
			volumeInt = 0;
			volume = -80.0f;
			System.out.println(volume);
			volumectrl.setValue(volume);
			if (playMusic == true) {
				toggleSound();
			}

		} else {

			System.out.println("VOLUME CHANGE");
			// Turns on sound if it isn't on already
			if (playMusic == false) {
				toggleSound();

			}

			// Set volumeInt to the volume input, then converts the value out of 100 to an
			// appropriate volume
			volumeInt = newVolume;
			float newVolumeFloat = (float) newVolume / 100;
			volume = (((float) Math.log10(newVolumeFloat)) * 20f);
			System.out.println(volume);
			volumectrl.setValue(volume);
		}
	}

	/**
	 * Stops or starts playing the music, dependent on whether it's playing or not.
	 */
	public static void toggleSound() {
		if (playMusic == true) {

			System.out.println("Music toggled off");
			playMusic = false;
			oldVolumeInt = volumeInt;
			oldVolume = volume;
			volumeInt = 0;
			volume = -80.0f;

			clip.stop();

		} else {

			System.out.println("Music toggled on");
			volumeInt = oldVolumeInt;
			volume = oldVolume;
			playMusic = true;
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}

	}

}
