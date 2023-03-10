package com.game.areas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.game.graphics.Renderer;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public abstract class Areas {

	/***
	 * Creates rocks and spiky rocks
	 * 
	 * @param canvas - The width and height of the canvas
	 */
	public abstract void obstacles(Vector canvas);

	/***
	 * Creates monsters
	 * 
	 * @param canvas - The width and height of the canvas
	 */
	public abstract void monsters(Vector canvas);

	public abstract void monstersOffline(Vector canvas, Renderer renderer);

	/***
	 * Creates items
	 * 
	 * @param canvas - The width and height of the canvas
	 */
	public abstract void items(Vector canvas);

	public abstract void itemsOffline(Vector canvas, Renderer renderer);

	/***
	 * Loads image
	 * 
	 * @param name - Name of image
	 */
	public static Image loadImage(String name) {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/" + name + ".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Image(inputstream, 70, 70, true, true);
	}
}
