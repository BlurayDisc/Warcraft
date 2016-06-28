package com.run.warcraft.unit.orc;

import com.run.warcraft.unit.Building;
import com.run.warcraft.unit.Unit;

public class Barracks extends Building {

	public Barracks() {
		super(150, 1, 1, 75, 5, 45);
	}

	@Override
	public Unit create() {
		return new Worker();
	}
}
