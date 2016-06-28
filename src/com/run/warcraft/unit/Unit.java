package com.run.warcraft.unit;

import java.util.Random;

import com.run.warcraft.engine.Sprite;
import com.run.warcraft.force.Force;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Unit extends Sprite {
	
	protected Force force;
	private String name;
	
	private IntegerProperty maxHp;
	private IntegerProperty hp;
	
	private IntegerProperty maxMana;
	private IntegerProperty mp;
	
	private IntegerProperty damage;
	
	/**
	 * The cool down between each attack. i.e. 1.5 attack/second.
	 */
	private DoubleProperty attackSpeed;
	
	private IntegerProperty attackRange;
	
	private IntegerProperty armour;
	
	private IntegerProperty movementSpeed;
	
	private int buildTime = 0;
	
	private int attackTicks = 0;
	private int attackTicksTol = 0;
	
	protected int buildTicks = 0;
	protected int buildTicksTol = 0;
	
	private int idleTicks = 0;
	private int idleTicksTol = 0;
	
	private Circle attackRangeBounds;
	
	/**
	 * Creates a melee unit.
	 * @param hp The hit points of this unit.
	 * @param mp The mana points of this unit.
	 * @param dmg The Damage for this unit.
	 * @param as The attack speed for this unit, in terms of cool downs between each attack.
	 * @param armour The defense of this unit.
	 * @param bt the build time in seconds for this unit.
	 */
	public Unit(int hp, int mp, int dmg, double as, int armour, int bt) {
		this(hp, mp, dmg, as, 5, armour, bt);
	}
	
	/**
	 * Creates a ranged unit.
	 * @param hp The hit points of this unit.
	 * @param mp The mana points of this unit.
	 * @param dmg The Damage for this unit.
	 * @param as The attack speed for this unit, in terms of cool downs between each attack.
	 * @param range the attack range for this unit.
	 * @param armour The defense of this unit.
	 * @param bt the build time in seconds for this unit.
	 */
	public Unit(int hp, int mp, int dmg, double as, int range, int armour, int bt) {
		
		this.name = getClass().getSimpleName();
		this.maxHp = new SimpleIntegerProperty(hp);
		this.maxMana = new SimpleIntegerProperty(mp);
		this.hp = new SimpleIntegerProperty(hp);
		this.mp = new SimpleIntegerProperty(mp);
		this.damage = new SimpleIntegerProperty(dmg);
		this.attackSpeed = new SimpleDoubleProperty(as);
		this.attackRange = new SimpleIntegerProperty(range);
		this.attackRangeBounds = new Circle();
		this.armour = new SimpleIntegerProperty(armour);
		this.movementSpeed = new SimpleIntegerProperty(100);
		this.buildTime = bt;
		this.attackTicksTol = getTicksLimit(attackSpeed.get());
		this.buildTicksTol = getTicksLimit(buildTime);
		this.idleTicksTol = getTicksLimit(2);
				
		init();
	}

    /***************************************************************************
     *                                                                         *
     * Initialization                                                          *
     *                                                                         *
     **************************************************************************/

	protected ProgressBar hpBar;
	protected Text nameText;
	private Text menuTitle;
	
	/**
	 * Initialize the Unit View.
	 */
	protected void init() {
		
		nameText = new Text(name);
		nameText.setTranslateX(15);
		
		hpBar = new ProgressBar();
		hpBar.progressProperty().bind(Bindings.createDoubleBinding(() -> (double) hp.get(), hp).divide(maxHp));
		hpBar.setPrefWidth(75);
		hpBar.setTranslateX(0);
		hpBar.setTranslateY(15);
		
		attackRangeBounds.setRadius(model.getImage().getWidth() / 2 + attackRange.get());
		attackRangeBounds.setTranslateX(model.getTranslateX() + model.getImage().getWidth() / 2);
		attackRangeBounds.setTranslateY(model.getTranslateY() + model.getImage().getHeight() / 2);
		attackRangeBounds.setStroke(Color.GREEN);
		attackRangeBounds.setFill(Color.TRANSPARENT);
		attackRangeBounds.setVisible(true);
		
		
		//Unit description and states menu
		Rectangle background = new Rectangle(125, 165);
		background.setFill(Color.WHITE);

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(10));
		menuTitle = new Text(name);
		layout.getChildren().addAll(
				menuTitle, 
				new Text("Max HP: " + maxHp.get()),
				new Text("Damage: " + damage.get()),
				new Text("Attack Speed: " + attackSpeed.get()),
				new Text("Attack Range: " + attackRange.get()),
				new Text("Armour: " + armour.get()));
		
		Pane states = new Pane();
		states.setOpacity(0.8);
		states.setTranslateX(model.getTranslateX() + model.getImage().getWidth() + 10);
		states.setTranslateY(model.getTranslateY());
		states.setVisible(false);
		states.getChildren().addAll(background, layout);
		
		attackRangeBounds.setOnMouseClicked(e -> states.setVisible(!states.isVisible()));
		
		addAll(nameText, hpBar, attackRangeBounds, states);
	}
	
    /***************************************************************************
     *                                                                         *
     * Unit Functions                                                          *
     *                                                                         *
     **************************************************************************/
	
	public void attack(Unit other) {
		if (damage.get() <= 0) return;		
		//Checks for whether the attack cool down has been reached.
		if (attackTicks == 0) {
			//Checks for unit death condition.
			if (other.hp.get() < damage.get()) {
				//Perform hp reduction and set its minimum to 0
				other.hp.set(0);
				other.kill();
			} else {
				other.hp.set(other.hp.get() - this.damage.get());
			}
			debugAttack(other);
		}
		
		//Process attack cool down tick.
		attackTicks++;
		if (attackTicks >= attackTicksTol) {
			attackTicks = 0;
		}
	}
	
	private long before = 0;
	
	/**
	 * Debugs the attack processs.
	 */
	protected void debugAttack(Unit other) {
		long now = System.currentTimeMillis();
		if (before > 0) {
			log.debug("{} attacking {} after: {} s", this, other, (now - before) / 1000.0);
		}
		before = System.currentTimeMillis();
	}
	
    /***************************************************************************
     *                                                                         *
     * Cartesian Movement Calculations                                         *
     *                                                                         *
     *            |                                                            *
     *            |                                                            *
     *   -------------------                                                   *                                         
     *            |                                                            *
     *            |                                                            *
     *                                                                         *
     **************************************************************************/
	
	public void move(Unit other) {
		move(other.collisionBounds.getTranslateX(), other.collisionBounds.getTranslateY());
	}
	
	public void move(double x, double y) {

	}
	
	public void moveAtRandom() {
		Random rnd = new Random();
		this.vX = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
		this.vY = (rnd.nextInt(2) + rnd.nextDouble()) * (rnd.nextBoolean() ? 1 : -1);
	}
	
	public void stop() {
		this.vX = 0;
		this.vY = 0;
	}
	
	public boolean isStopped() {
		if (vX == 0 && vY == 0) {
			idleTicks++;
			if (idleTicks > idleTicksTol) {
				idleTicks = 0;
				return true;
			}
		}
		return false;
	}
	
    /***************************************************************************
     *                                                                         *
     * Unit Helper Methods                                                     *
     *                                                                         *
     **************************************************************************/
	
	public void setForce(Force owner) {
		this.force = owner;
		this.menuTitle.setText(name + "   " + force.getName());
	}
	
	public boolean isFriendlyUnitOf(Unit unit) {
		return this.force.equals(unit.force);
	}
	
	public String getName() {
		return name;
	}

	public IntegerProperty getHp() {
		return hp;
	}

	public IntegerProperty getMp() {
		return mp;
	}

	public IntegerProperty getDamage() {
		return damage;
	}

	public DoubleProperty getAttack_speed() {
		return attackSpeed;
	}

	public IntegerProperty getArmour() {
		return armour;
	}

	public IntegerProperty getMax_hp() {
		return maxHp;
	}

	public IntegerProperty getMax_mana() {
		return maxMana;
	}
	
	public IntegerProperty getAttackRange() {
		return attackRange;
	}
	
	public IntegerProperty getMovementSpeed() {
		return movementSpeed;
	}
	
	public int getBuildTime() {
		return buildTime;
	}
	
	/**
	 * Returns the number of ticks required to satisfy the attack cool down.
	 * @param property The property in seconds.
	 * @return
	 */
	private int getTicksLimit(double property) {
		double milliPerTick = 1000.0 / 60.0;
		int ticks_required = (int) (property * 1000.0 / milliPerTick);
		// seems like 2 ticks happen for each frame.
		return ticks_required;
	}
	
    /***************************************************************************
     *                                                                         *
     * Unit Animations                                                         *
     *                                                                         *
     **************************************************************************/
	
	public Animation getAttackAnimation() {
		return new PauseTransition(Duration.seconds(0));
	}
	
	public Animation getOnDamageAnimation() {
		return new PauseTransition(Duration.seconds(0));
	}
	
	public Animation getSpawnAnimation() {
        FadeTransition fade = new FadeTransition();
        fade.setNode(model);
        fade.setDuration(Duration.seconds(2));
        fade.setFromValue(model.getOpacity());
        fade.setToValue(1);
        return fade;
	}
    
    /**
     * Animate an implosion. Once done remove from the game world
     */
    public Animation getDeathAnimation() {
        FadeTransition fade = new FadeTransition();
        fade.setNode(model);
        fade.setDuration(Duration.seconds(2));
        fade.setFromValue(model.getOpacity());
        fade.setToValue(0);
        return fade;
    }
    
    /***************************************************************************
     *                                                                         *
     * Game Engine Implementation                                              *
     *                                                                         *
     **************************************************************************/
	
	private static final Image DEFAULT_IMAGE = new Image("images/human_peasant.png");
	

    @Override
	protected ImageView getModel() {
		String path = this.getClass().getName()
				.toLowerCase()
				.substring("com.run.warcraft.unit.".length())
				.replace(".", "_");
		Image image = new Image("images/" + path + ".png");
		if (image.isError()) {
			image = DEFAULT_IMAGE;
		}
		ImageView iView = new ImageView(image);
		iView.setTranslateX(25);
		iView.setTranslateY(35);
		return iView;
	}
	
    @Override
    public boolean collide(Sprite that) {
        if (!this.model.isVisible() || !that.model.isVisible() || this == that) {
            return false;
        }
    	Unit other = (Unit) that;
		if (this.isFriendlyUnitOf(other) || this.isDead) {
			return false;
		}
        return collisionBounds.getBoundsInParent()
        		.intersects(that.collisionBounds.getBoundsInParent());
    }
    
	@Override
	public boolean attackRangeContains(Sprite that) {
        if (!this.model.isVisible() || !that.model.isVisible() || this == that) {
            return false;
        }
    	Unit other = (Unit) that;
		if (this.isFriendlyUnitOf(other) || this.isDead) {
			return false;
		}
		return attackRangeBounds.getBoundsInParent()
				.intersects(that.collisionBounds.getBoundsInParent());
	}
    
    @Override
    public void handleCollision(Sprite that) {
    	Unit other = (Unit) that;
    	this.stop();
    	other.stop();
    }
    
	@Override
	public void handleAttack(Sprite sprite) {
    	if (!(sprite instanceof Unit)) {
    		return;
    	}
    	Unit other = (Unit) sprite;
		this.stop();
		this.attack(other);
	}
	
	@Override
	public boolean isIdle() {
		if (vX == 0 && vY == 0) {
			idleTicks++;
			if (idleTicks > idleTicksTol) {
				idleTicks = 0;
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleIdle() {
		moveAtRandom();
	}
    
    @Override
    public String toString() {
    	return name + " hp: " + hp.get();
    }
}