package com.run.warcraft;

import com.run.warcraft.force.Race;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the game.
 * @author RuN
 */
public class Game extends Application {
	
	public final static int WIDTH = 1024;
	public final static int HEIGHT = 768;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
    	
    	Warcraft warcraft = new Warcraft(WIDTH, HEIGHT);
    	 
        // setup title, scene, stats, controls, and actors.
        warcraft.initialize();
        
        warcraft.setOnLoad(e -> {
            // Add Players and Bots
            warcraft.addForce("Run", Race.HUMAN, true);
            warcraft.addForce("Computer", Race.ORC, false);
        });
        
        // display window
        warcraft.show();
    }
}