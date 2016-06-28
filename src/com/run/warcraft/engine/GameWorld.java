package com.run.warcraft.engine;

import com.sun.javafx.perf.PerformanceTracker;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
/**
 * This application demonstrates a JavaFX 2.x Game Loop.
 * Shown below are the methods which comprise of the fundamentals to a
 * simple game loop in JavaFX:
*
 *  <strong>initialize()</strong> - Initialize the game world.
 *  <strong>beginGameLoop()</strong> - Creates a JavaFX Timeline object containing the game life cycle.
 *  <strong>updateSprites()</strong> - Updates the sprite objects each period (per frame)
 *  <strong>checkCollisions()</strong> - Method will determine objects that collide with each other.
 *  <strong>cleanupSprites()</strong> - Any sprite objects needing to be removed from play.
 *
 * @author cdea
 */
public abstract class GameWorld extends Stage {
	
    /**
     * All JavaFX nodes which are rendered onto the game surface(Scene) is
     * a JavaFX Group object.
     * <p> The JavaFX Group that will hold all JavaFX nodes which are rendered
     * onto the game surface(Scene) is a JavaFX Group object.
     * @return Group The root containing many child nodes to be displayed into
     * the Scene area.
     */
    private static final Group ROOT = new Group();
	
    public static final ObservableList<Node> NODE_LIST = ROOT.getChildren();
    
    /**
     * Returns the sprite manager containing the sprite objects to
     * manipulate in the game.
     * @return SpriteManager The sprite manager.
     */
    public static final SpriteManager SPRITE_MANAGER = new SpriteManager(NODE_LIST);
    
    /** The game loop using JavaFX's <code>AnimationTimer</code> API.*/
    public static AnimationTimer gameLoop;
    
    protected Scene gameScene;
    
    /*
     * Performance and FPS
     */
	
	private PerformanceTracker tracker;
	
    protected final static Label FPS_FIELD = new Label();
    
    protected final static Label PULSES_FIELD = new Label();
    
    /** Read only field to show the number of sprite objects are on the field*/
    protected final static Label PARTICLES_FIELD = new Label();
    
    private IntegerProperty fps = new SimpleIntegerProperty(0);
 
    /**
     * Constructor that is called by the derived class. This will
     * set the frames per second, title, and setup the game loop.
     * @param fps - Frames per second.
     * @param title - Title of the application window.
     */
    public GameWorld(int width, int height, String title) {
        // create and set timeline for the game loop
        
        // Create the scene
    	// Title in the application window.
        setTitle(title);
        gameScene = new Scene(ROOT, width, height);
        
        // Create GUI		
        VBox stats = new VBox(10);
        stats.relocate(10, 10);
     
        // lay down the controls
        stats.getChildren().addAll(PARTICLES_FIELD, FPS_FIELD, PULSES_FIELD);
        ROOT.getChildren().add(stats);
        
        init();
    }
    
    /**
     * Builds and sets the game loop ready to be started.
     */
    final private void init() {
    	
    	// JavaFX Bindings    	
    	IntegerBinding size = Bindings.size(ROOT.getChildren());
    	PARTICLES_FIELD.textProperty().bind(Bindings.format("Particles: %d", size));
 
        // sets the game world's game loop (Timeline)
        GameWorld.gameLoop = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				
		        // update actors
		        updateSprites();
		        
	            // check for collision
	            checkCollisions();
	            
	            // removed dead things
	            cleanupSprites();
	            
	            updateFPS3();
			}
		};        
        /*
         * Sample code that creates a dynamic game loop.
         * 
	     *   double lastTime = System.currentTimeMillis();
	     *   while (true)
	     *   {
	     *     double current = System.currentTimeMillis();
	     *     double elapsed = current - lastTime;
	     *     processInput();
	     *     update(elapsed);
	     *     render();
	     *     lastTime = current;
	     *   }
        */
    }

	private long before = 0;
    private int counter;
    
    /**
     * Debugging method showing the fps.
     */
    protected void updateFPS() {
    	counter++;
        if (counter == 60) {
        	counter = 0;
        	long now = System.currentTimeMillis();
        	int fps = (int) (60 / ((now - before) / 1000.0));
        	this.fps.set(fps);
        	before = System.currentTimeMillis();
        }
    }
    
    /**
     * 2nd algorithm for fps.
     */
    protected void updateFPS2() {
    	counter++;
    	long now = System.currentTimeMillis();
    	if (now - before >= 1000) {
    		this.fps.set(counter);
    		before = now;
    		counter = 0;
    	}
    }
    
    protected void updateFPS3() {
    	FPS_FIELD.setText("fps: " +  tracker.getAverageFPS());
    	PULSES_FIELD.setText("Pulses: " +  tracker.getAveragePulses());
    }
 
    /**
     * Initialize the game world by update the JavaFX Stage.
     * @param primaryStage
     */
    public abstract void initialize();
    
 
    /**Kicks off (plays) the Timeline objects containing one key frame
     * that simply runs indefinitely with each frame invoking a method
     * to update sprite objects, check for collisions, and cleanup sprite
     * objects.
     *
     */
    public void beginGameLoop() {
    	tracker = PerformanceTracker.getSceneTracker(getScene());
        gameLoop.start();
    }
 
    /**
     * Updates each game sprite in the game world. This method will
     * loop through each sprite and passing it to the handleUpdate()
     * method. The derived class should override handleUpdate() method.
     *
     */
    private void updateSprites() {
        for (int i = 0; i < SpriteManager.GAME_ACTORS.size(); i++) {
        	Sprite sprite = SpriteManager.GAME_ACTORS.get(i);
    		// Update unit velocity x and y.
    		sprite.update();
    		
            // bounce off the walls when outside of boundaries
            handleBoundaries(sprite);
    		
    		// Check idle units();
    		if (sprite.isIdle()) {
    			sprite.handleIdle();
    		}
        }
    }
    
    /**
     * Bounce off the walls when outside of boundaries
     * @param sprite
     */
    private void handleBoundaries(Sprite sprite) {
        double width = sprite.collisionBounds.getRadius() * 2;
        double xBound = gameScene.getWidth() - width;
        if (sprite.collisionBounds.getTranslateX() > xBound || 
        	sprite.collisionBounds.getTranslateX() < 0) {                 
        	sprite.vX = sprite.vX * -1;
        }
        
        double height = sprite.collisionBounds.getRadius() * 2;	
        double yBound = gameScene.getHeight() - height;
        if (sprite.collisionBounds.getTranslateY() > yBound || 
        	sprite.collisionBounds.getTranslateY() < 0) {
        	sprite.vY = sprite.vY * -1;
        }
    }
 
    /**
     * Checks each game sprite in the game world to determine a collision
     * occurred. The method will loop through each sprite and
     * passing it to the handleCollision()
     * method. The derived class should override handleCollision() method.
     */
    private void checkCollisions() {
        // check each sprite against other sprite objects.
        for (int i = 0; i < SpriteManager.GAME_ACTORS.size(); i++) {
        	Sprite spriteA = SpriteManager.GAME_ACTORS.get(i);
        	
            for (int j = 0; j < SpriteManager.GAME_ACTORS.size(); j++) {
            	Sprite spriteB = SpriteManager.GAME_ACTORS.get(j);

        		// Check units within attack range
        		if (spriteA.attackRangeContains(spriteB)) {
        			spriteA.handleAttack(spriteB);
        			// Check collisions
            		if (spriteA.collide(spriteB)) {
            			spriteA.handleCollision(spriteB);
            		}
                    // The break helps optimize the collisions
                    //  The break statement means one object only hits another
                    // object as opposed to one hitting many objects.
                    // To be more accurate comment out the break statement.
        			break;
        		}
            }
        }
    }
    
    /**
     * Sprites to be cleaned up.
     */
    private void cleanupSprites() {
        SPRITE_MANAGER.cleanupSprites();
    }
}