package com.run.warcraft;

import java.util.ArrayList;
import java.util.List;

import com.run.warcraft.engine.GameWorld;
import com.run.warcraft.engine.Tile;
import com.run.warcraft.force.Force;
import com.run.warcraft.force.Player;
import com.run.warcraft.force.Race;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Warcraft extends GameWorld {
	
	private List<Force> forceList = new ArrayList<>();

	public Warcraft(int width, int height) {
		super(width, height, "Warcraft");
	}

	@Override
	public void initialize() {

		Light.Distant light = new Light.Distant();
		light.setAzimuth(-135.0);

		Lighting lighting = new Lighting();
		lighting.setLight(light);
		lighting.setSurfaceScale(5.0);
		
		Text title = new Text("Warcraft");
		title.setFill(Color.GOLD);
		title.setFont(new Font(72));
		title.setTranslateX(375);
		title.setTranslateY(350);
		title.setEffect(lighting);
		title.setOpacity(0);
		
		Text studio = new Text("- Bluray Studio");
		studio.setStroke(Color.WHITE);
		studio.setFill(Color.WHITE);
		studio.setFont(new Font(24));
		studio.setTranslateX(600);
		studio.setTranslateY(450);
		studio.setOpacity(0);

		setScene(new Scene(new Group(title, studio), 1024, 768, Color.BLACK));
		
		SequentialTransition sequence = new SequentialTransition();
		
		FadeTransition titleFade = new FadeTransition();
		titleFade.setNode(title);
		titleFade.setFromValue(title.getOpacity());
		titleFade.setToValue(1);
		titleFade.setDelay(Duration.seconds(1));
		titleFade.setDuration(Duration.seconds(2));
		
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		
		FadeTransition studioFade = new FadeTransition();
		studioFade.setNode(studio);
		studioFade.setFromValue(studio.getOpacity());
		studioFade.setToValue(1);
		studioFade.setDelay(Duration.seconds(0.5));
		studioFade.setDuration(Duration.seconds(2));
		
		PauseTransition pause2 = new PauseTransition(Duration.seconds(2));
		
		sequence.getChildren().addAll(titleFade, pause, studioFade, pause2);
		
		//TODO rewrite this section
		sequence.setOnFinished(e -> {
	        gameScene.setFill(Tile.TYPE_DIRT_GRASS.getPattern());
			setScene(gameScene);
	        beginGameLoop();
	        Animation p = new PauseTransition(Duration.seconds(1.5));
	        p.setOnFinished(a -> eventHandler.handle(new ActionEvent()));
	        p.play();
		});
		
		sequence.play();
	}
	
	private EventHandler<ActionEvent> eventHandler;
	
    public void setOnLoad(EventHandler<ActionEvent> value) {
        this.eventHandler = value;
    }
	
	/**
	 * Adds a force to the game and spawns its initial units and buildings.
	 */
	public void addForce(String name, Race race, boolean player) {
		Force force = player? new Player(name) : new Force(name);
		force.setRace(race);
		force.spawn();
		forceList.add(force);
	}
}