package com.kevinhinds.spacebotsfree.scene;

import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.objects.Actor;
import com.kevinhinds.spacebotsfree.player.Controls;
import com.kevinhinds.spacebotsfree.player.Player;

/**
 * main arcade scene which contains multiple levels
 * 
 * @author khinds
 */
public class ArcadeScene extends GameScene {

	public int secondsPassed = 0;

	@Override
	public void createScene() {

		setBackground();
		createHUD();
		createPhysics();
		addPlayer();
		createControls();
		createLevel(this.levelNumber);
		level = levelXMLBuilder.level;
		currentPiecesObtained = "";
		
		// create 5 initial actors with high IDs to not conflict with ones generated each second
		for (int i = 1000; i <= 1005; i++) {
			createNewArcadeActor(i);
		}

		// every 1 seconds update scene timer
		this.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {

				// create new adversary for each second passed
				secondsPassed++;
				createNewArcadeActor(secondsPassed);

				// update arcade stats and check if we're passed 45 seconds yet
				controls.updateLevelStats();
				player.energyAmount = 100;
				checkLevelComplete();
			}
		}));
	}

	/**
	 * create random actor
	 * 
	 * @param id
	 */
	protected void createNewArcadeActor(int id) {

		// villain random facing direction
		Random random = new Random();
		String facing = "left";
		int facingRandom = random.nextInt(2) + 1;
		if (facingRandom == 1) {
			facing = "right";
		}

		// random villain appears in random x position
		int villainsRandom = random.nextInt(GameConfiguration.arcadeVillains.length);
		int villain = GameConfiguration.arcadeVillains[villainsRandom];
		int appearsRandom = random.nextInt((int) ResourceManager.getIntance().camera.getWidth());

		// create random villain
		Actor actor = new Actor("Actor " + Integer.toString(id), id, villain, "walking", 3, "", facing, false, 100, 2, 0, appearsRandom, 10, 1, 1, 1, ResourceManager.getIntance().actorsRegion, vbom);
		level.actors.add(actor);
		actor.createBodyAndAttach(this, physicsWorld);
	}

	/**
	 * add new player to this scene
	 */
	protected void addPlayer() {
		player = new Player(this, physicsWorld, true);
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().returnToMenuScene();
	}

	/**
	 * from the current list of pieces needed to complete a level, if the player has all the pieces then the level is complete!
	 */
	public void checkLevelComplete() {
		if (secondsPassed > GameConfiguration.arcadeNumberSeconds) {
			SceneManager.getInstance().loadArcadeStatusScene("success");
		}
	}

	/**
	 * create controls for the game for the player's character
	 */
	protected void createControls() {
		controls = new Controls(this, player, true);
	}
}