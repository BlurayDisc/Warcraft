package com.run.warcraft.unit.orc;

import com.run.warcraft.unit.Building;
import com.run.warcraft.unit.Unit;

public class GreatHall extends Building {

	public GreatHall() {
		super(1000, 10, 0.8, 175, 5, 60);
	}

	@Override
	public Unit create() {
		return new Worker();
	}
}