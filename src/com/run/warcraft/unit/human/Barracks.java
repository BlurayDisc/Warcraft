package com.run.warcraft.unit.human;

import com.run.warcraft.unit.Building;
import com.run.warcraft.unit.Unit;

public class Barracks extends Building {

	public Barracks() {
		super(500, 5, 45);
	}

	@Override
	public Unit create() {
		return new Peasant();
	}
}