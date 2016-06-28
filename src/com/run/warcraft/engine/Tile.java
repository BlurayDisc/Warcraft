package com.run.warcraft.engine;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public enum Tile {
	
	TYPE_DIRT("Lords_Dirt"),
	TYPE_DIRT_GRASS("Lords_DirtGrass"),
	TYPE_DIRT_ROUGH("Lords_DirtRough"),
	TYPE_GRASS("Lords_Grass"),
	TYPE_GRASS_DARK("Lords_GrassDark"),
	TYPE_ROCK("Lords_Rock");
	
	private ImagePattern pattern;
	
	private Tile(String path) {
		pattern = new ImagePattern(
				  new Image("terrain/" + path + ".JPG"), 0, 0, 32, 32, false);
	}
	
	public ImagePattern getPattern() {
		return pattern;
	}
}