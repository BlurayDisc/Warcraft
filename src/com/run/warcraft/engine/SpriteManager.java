package com.run.warcraft.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.run.warcraft.unit.Unit;

import javafx.animation.Animation;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Sprite manager is responsible for holding all sprite objects, and cleaning up
 * sprite objects to be removed. All collections are used by the JavaFX
 * application thread. During each cycle (animation frame) sprite management
 * occurs. This assists the user of the API to not have to create lists to
 * later be garbage collected. Should provide some performance gain.
 * @author cdea
 */
public class SpriteManager {
	
    /** All the sprite objects currently in play */
	public static final List<Sprite> GAME_ACTORS = new ArrayList<>();
	
	private static final List<Sprite> DEAD_ACTORS = new ArrayList<>();
	
	private final ObservableList<Node> nodeList;
    
    public SpriteManager(ObservableList<Node> nodeList) { 
    	this.nodeList = nodeList;
    }
 
    /**
     * VarArgs of sprite objects to be added to the game.
     * @param sprites
     */
    public void addSprites(Sprite... sprites) {
    	for (Sprite sprite: sprites) {
    		if (sprite instanceof Unit) {
    			Unit unit = (Unit) sprite;
    			//Play Spawn animation
		    	Animation animation = unit.getSpawnAnimation();
		    	animation.play();
    		}
    		GAME_ACTORS.add(sprite);
    		nodeList.addAll(sprite.getAttachmentList());
    	}
    }
    
    /**
     * VarArgs of sprite objects to be added to the game.
     * @param sprites
     */
    public void addSprites(List<Sprite> sprites) {
    	GAME_ACTORS.addAll(sprites);
    }
 
    /**
     * VarArgs of sprite objects to be removed from the game.
     * @param sprites
     */
    public void removeSprites(Node... sprites) {
        GAME_ACTORS.removeAll(Arrays.asList(sprites));
    }
 
    /**
     * Removes sprite objects and nodes from all
     * temporary collections such as:
     * CLEAN_UP_SPRITES.
     * The sprite to be removed will also be removed from the
     * list of all sprite objects called (GAME_ACTORS).
     */
    public void cleanupSprites() {
    	for (int i = 0; i < GAME_ACTORS.size(); i++) {
    		Sprite sprite = GAME_ACTORS.get(i);
    		if (sprite instanceof Unit) {
    			Unit unit = (Unit) sprite;
    	    	//Checks for dead Sprites.
        		if (unit.isDead) {
        			DEAD_ACTORS.add(unit);
        			//Play Death Animation
        			//Remove from the game.
    		    	Animation animation = unit.getDeathAnimation();
    		    	animation.setOnFinished(e -> nodeList.removeAll(sprite.getAttachmentList()));
    		    	animation.play();
        		}
    		}
    	}
    	GAME_ACTORS.removeAll(DEAD_ACTORS);
//    	for (Sprite sprite: DEAD_ACTORS) {
//    		nodeList.removeAll(sprite.getAttachmentList());
//    	}
    	DEAD_ACTORS.clear();
    }
}