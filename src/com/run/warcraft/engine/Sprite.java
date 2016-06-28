package com.run.warcraft.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * The Sprite class represents a image or node to be displayed.
 * In a 2D game a sprite will contain a velocity for the image to
 * move across the scene area. The game loop will call the update()
 * and collide() method at every interval of a key frame. A list of
 * animations can be used during different situations in the game
 * such as rocket thrusters, walking, jumping, etc.
 * @author cdea
 */
public abstract class Sprite {
	
	/** position vector x, y coordinates */
	public double x, y = 0;
	
    /** velocity vector x, y direction */
	public double vX, vY = 0;
	
    /** dead? */
    public boolean isDead = false;
    
	public ImageView model;
	
	public Circle collisionBounds;
	
	private List<Node> attachmentList;
    
    public Sprite() {
    	
		attachmentList = new ArrayList<>();
    	
		model = getModel();
		//Not visible before playing spawn animation.
		model.setOpacity(0);
		
		collisionBounds = new Circle();
		
		collisionBounds.setRadius(model.getImage().getWidth() / 2);
		collisionBounds.setTranslateX(model.getTranslateX() + model.getImage().getWidth() / 2);
		collisionBounds.setTranslateY(model.getTranslateY() + model.getImage().getHeight() / 2);
		collisionBounds.setStroke(Color.WHITE);
		collisionBounds.setFill(Color.TRANSPARENT);
		collisionBounds.setVisible(true);

		addAll(model, collisionBounds);
    }

	public void kill() {
        vX = vY = 0;
        isDead = true;
    }

    public void addAll(Node... nodes) {
    	this.attachmentList.addAll(Arrays.asList(nodes));
    }
    
    public List<Node> getAttachmentList() {
    	return attachmentList;
    }

    /**
     * Updates this sprite object's velocity, or animations.
     * Updates the sprite object's information to position on the game surface.
     */
    public void update() {
    	for (Node n: attachmentList) {
            n.setTranslateX(n.getTranslateX() + vX);
            n.setTranslateY(n.getTranslateY() + vY);
    	}
    }
    
    public void setX(double x) {
    	this.x = x;
    	for (Node n: attachmentList) {
    		n.setTranslateX(n.getTranslateX() + x);
    	}
    }
    
    public void setY(double y) {
    	this.y =  y;
    	for (Node n: attachmentList) {
    		n.setTranslateY(n.getTranslateY() + y);
    	}
    }
    
    protected abstract ImageView getModel();
    
    /**
     * Did this sprite collide into the other sprite?
     *
     * @param other - The other sprite.
     * @return
     */
    public abstract boolean collide(Sprite sprite);
    
    /**
     * Handles the collision once it occurs
     * @param other
     */
    public abstract void handleCollision(Sprite sprite);
    
    /**
     * Did this sprite contain the other sprite within it's attack range?
     * @param other
     * @return
     */
    public abstract boolean attackRangeContains(Sprite sprite);
    

    /**
     * Handles the attack order once it occurs.
     * @param sprite
     */
    public abstract void handleAttack(Sprite sprite);
    
    public abstract boolean isIdle();
    
    public abstract void handleIdle();
}