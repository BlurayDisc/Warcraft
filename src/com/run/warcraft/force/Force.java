package com.run.warcraft.force;

import static com.run.warcraft.engine.GameWorld.SPRITE_MANAGER;

import java.util.ArrayList;
import java.util.List;

import com.run.warcraft.unit.Building;
import com.run.warcraft.unit.Unit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Force {
	
	private String name;
	private Race race;
	private SpawnPoint spawnPoint;
	private List<Unit> units = new ArrayList<>();
	
	public Force(String name) {
		this.name = name;
		this.spawnPoint = SpawnPoint.random();
	}
	
	public void setRace(Race race) {
		this.race = race;
	}
	
	public void spawn() {
		Building building = null;
		switch (race) {
		case HUMAN:
			building = new com.run.warcraft.unit.human.Barracks();
			break;
		case ORC:
			building = new com.run.warcraft.unit.orc.Barracks();
			break;
		default:
			break;
		}
		building.setForce(this);
		building.setX(spawnPoint.x);
		building.setY(spawnPoint.y);
		
		log.debug("{} spawned at {}", building, spawnPoint);
		
		for (int i = 0; i < 100; i++) {
			building.queueProduction();
		}
		SPRITE_MANAGER.addSprites(building);
	}
	
	public String getName() {
		return name;
	}
	
	public Race getRace() {
		return race;
	}
	
	public List<Unit> getUnits() {
		return units;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other instanceof Force) {
			Force force = (Force) other;
			return this.name.equals(force.name);
		}
		return false;
	}
}