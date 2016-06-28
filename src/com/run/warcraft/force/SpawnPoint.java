package com.run.warcraft.force;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.run.warcraft.Game;

public class SpawnPoint {
	
	private static final List<SpawnPoint> SPAWN_POINTS = new ArrayList<>();
	
	/**
	 * Spawn coordinates.
	 */
	private static final int[][] COORDS = new int[4][2];
	
	private static final int OFFSET = 150;
	
	/**
	 * This algorithm produces a 2D array specifying the 
	 * vertices of a rectangle 2D shape.
	 */
	static {
		for (int i = 0; i < COORDS[0].length; i++) {
			for (int j = 0; j < COORDS.length / 2; j++) {
				int transX = Game.WIDTH * j + (j > 0 ? -OFFSET : OFFSET / 2);
				int transY = Game.HEIGHT * i + (i > 0 ? -OFFSET : OFFSET / 2);
				SpawnPoint sp = new SpawnPoint(transX, transY);
				SPAWN_POINTS.add(sp);
			}
		}
	}
	
	int x, y;
	
	public SpawnPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns a random spawn point.
	 * @return
	 */
	public static SpawnPoint random() {
		Random random = new Random();
		int i = random.nextInt(SPAWN_POINTS.size() - 1);
		return SPAWN_POINTS.remove(i);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " +  y + "]";
	}
}