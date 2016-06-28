package com.run.warcraft.unit.human;

import com.run.warcraft.unit.Building;
import com.run.warcraft.unit.Unit;

public class TownHall extends Building {

	public TownHall() {
		super(1800, 5, 60);
	}

	@Override
	public Unit create() {
		return new Peasant();
	}

}
