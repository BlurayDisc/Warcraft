package com.run.warcraft.unit;

import static com.run.warcraft.engine.GameWorld.SPRITE_MANAGER;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Building extends Unit {
	
	private Queue<Unit> productionQueue = new LinkedList<>();
	
	/**
	 * Creates a Building
	 * @param hp The hit points of this unit.
	 * @param armour The defense of this unit.
	 */
	public Building(int hp, int armour, int bt) {
		this(hp, 0, 0, 5, armour, bt);
	}
	
	/**
	 * Creates a ranged unit.
	 * @param hp The hit points of this unit.
	 * @param dmg The Damage for this unit.
	 * @param as The attack speed for this unit, in terms of cool downs between each attack.
	 * @param range the attack range for this unit.
	 * @param armour The defense of this unit.
	 * @param bt the build time in seconds for this unit.
	 */
	public Building(int hp, int dmg, double as, int range, int armour, int bt) {
		super(hp, 0, dmg, as, range, armour, bt);
	}
	
    /***************************************************************************
     *                                                                         *
     * Initialization                                                          *
     *                                                                         *
     **************************************************************************/
	
	@Override
	protected void init() {
		super.init();
		
		nameText.setTranslateX(25);
		hpBar.setPrefWidth(100);
	}
	
    /***************************************************************************
     *                                                                         *
     * Unit Functions                                                          *
     *                                                                         *
     **************************************************************************/
	
	/**
	 * Creates the unit to be produced by this building.
	 * @return
	 */
	public abstract Unit create();
    
    /**
     * Adds an unit to this building's the production queue.
     * @param clazz
     */
    public void queueProduction() {
    	Unit unit = create();
    	unit.setForce(force);
        unit.setX(x);
        unit.setY(y);
    	productionQueue.add(unit);
    }

	/**
	 * Updates production rate.
	 */
    @Override
    public void update() {
    	//Checks for whether the build time has been reached.
    	if (!productionQueue.isEmpty()) {
	    	Unit unit = productionQueue.peek();
			unit.buildTicks++;
			if (unit.buildTicks >= unit.buildTicksTol) {
				unit.buildTicks = 0;
				//Adds units to SpriteManager.
				SPRITE_MANAGER.addSprites(productionQueue.poll());
			}
    	}
    }
}