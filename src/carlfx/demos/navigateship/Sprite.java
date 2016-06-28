package carlfx.demos.navigateship;

import javafx.scene.Node;

public class Sprite {
	
	protected Node node;
	protected Node collisionBounds;
	protected double vX, vY;
	protected boolean isDead;
	
	public boolean collide(Sprite spriteB) { return false;}

	public void handleDeath(TheExpanse theExpanse) { }

	public void update() { }

}
