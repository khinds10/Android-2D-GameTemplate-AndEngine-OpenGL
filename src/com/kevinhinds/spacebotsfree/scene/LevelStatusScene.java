package com.kevinhinds.spacebotsfree.scene;

import java.util.ArrayList;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.objects.Piece;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * credits scene for the game
 * 
 * @author khinds
 */
public class LevelStatusScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_BACK = 1;
	private final int MENU_NEXT = 2;
	private final int MENU_AGAIN = 3;
	private final int MENU_UPGRADE = 1000;
	public ArrayList<Piece> foundPieces = new ArrayList<Piece>();
	private int mostRecentLevelPlayed = 0;

	@Override
	public void createScene() {
		final Sprite spriteBG = new Sprite(0, 0, ResourceManager.getIntance().illuminateBackgroundRegion, ResourceManager.getIntance().vbom);
		attachChild(spriteBG);
		mostRecentLevelPlayed = GameStatus.getMostRecentLevel();
		createMenu();

		// show the ship to rebuild!
		Sprite ship = new Sprite(420, 50, ResourceManager.getIntance().completeShipRegion, this.vbom);
		attachChild(ship);
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().returnToMenuScene();
	}

	/**
	 * create menu elements from local sprites to display
	 */
	private void createMenu() {
		menu = new MenuScene(camera);
		menu.setPosition(0, 0);

		// based on level status show success or fail title
		IMenuItem mainTitle = null;
		if (levelStatus.equals("dead")) {
			mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameRedFont, "AREA INCOMPLETE", 0, false);
		} else {
			mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameGreenFont, "AREA SUCCESS!", 0, false);
		}
		menu.addMenuItem(mainTitle);

		// create statistics menu items
		final IMenuItem timeToComplete = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Time to finish", 0, false);
		menu.addMenuItem(timeToComplete);

		// show how long the level was played for in seconds
		long levelPlayedSecondsCount = GameStatus.getCurrentLevelPlayedTime();
		final IMenuItem timeToCompleteTime = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Long.toString(levelPlayedSecondsCount) + " seconds", 0, false);
		menu.addMenuItem(timeToCompleteTime);

		// show/persist personal best message for time
		IMenuItem timeRecord = null;
		if (!levelStatus.equals("dead")) {
			boolean personalBestTime = false;
			int previousRecordTime = GameStatus.levelRecordByLevelNumber(mostRecentLevelPlayed, "time");
			if (levelPlayedSecondsCount <= previousRecordTime) {
				personalBestTime = true;
				GameStatus.setRecordForMetricByLevelNumber(mostRecentLevelPlayed, "time", Long.toString(levelPlayedSecondsCount));
			}
			if (personalBestTime) {
				timeRecord = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontTinyBlue, "PERSONAL BEST!", 0, false);
				menu.addMenuItem(timeRecord);
			} else {
				timeRecord = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontTinyGreen, "BEST: " + Integer.toString(previousRecordTime) + " seconds", 0, false);
				menu.addMenuItem(timeRecord);
			}
		}
		final IMenuItem marksmanship = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Marksmanship", 0, false);
		menu.addMenuItem(marksmanship);

		// calculate marksmanship
		int levelHits = GameStatus.getGameLevelStats(GameStatus.levelStatsType.HITS);
		int levelShots = GameStatus.getGameLevelStats(GameStatus.levelStatsType.SHOTS);
		float marksmanshipRating = 0;
		if (levelShots > 0) {
			marksmanshipRating = (levelHits * 100 / levelShots);
		}

		// you can kill actors by bombs / flares so this can cause marksmanship to go above 100
		if (marksmanshipRating > 100) {
			marksmanshipRating = 100;
		}

		final IMenuItem marksmanshipPercent = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Float.toString(marksmanshipRating) + " % (" + Integer.toString(GameStatus.getGameLevelStats(GameStatus.levelStatsType.HITS)) + " Hits)", 0, false);
		menu.addMenuItem(marksmanshipPercent);

		// show/persist personal best message for marksmanship
		IMenuItem marksmanshipRecord = null;
		if (!levelStatus.equals("dead")) {
			boolean personalBestMarksmanship = false;
			int previousRecordMarksmanship = GameStatus.levelRecordByLevelNumber(mostRecentLevelPlayed, "marksmanship");
			if (previousRecordMarksmanship <= marksmanshipRating) {
				personalBestMarksmanship = true;
				GameStatus.setRecordForMetricByLevelNumber(mostRecentLevelPlayed, "marksmanship", Float.toString(marksmanshipRating));
			}
			if (personalBestMarksmanship) {
				marksmanshipRecord = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontTinyBlue, "PERSONAL BEST!", 0, false);
				menu.addMenuItem(marksmanshipRecord);
			} else {
				marksmanshipRecord = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontTinyGreen, "BEST: " + Integer.toString(previousRecordMarksmanship) + " % ", 0, false);
				menu.addMenuItem(marksmanshipRecord);
			}
		}

		final IMenuItem shotsFired = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Shots Fired", 0, false);
		menu.addMenuItem(shotsFired);

		final IMenuItem shotsFiredCount = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Integer.toString(GameStatus.getGameLevelStats(GameStatus.levelStatsType.SHOTS)), 0, false);
		menu.addMenuItem(shotsFiredCount);

		final IMenuItem kills = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Aliens Killed", 0, false);
		menu.addMenuItem(kills);

		final IMenuItem killsCount = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Integer.toString(GameStatus.getGameLevelStats(GameStatus.levelStatsType.KILLS)), 0, false);
		menu.addMenuItem(killsCount);

		// create control game menu items
		final IMenuItem rebuildYourShip = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "REBUILD YOUR SHIP", 0, false);
		menu.addMenuItem(rebuildYourShip);

		final IMenuItem menuButtonItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameBlueFont, "MENU", MENU_BACK, true);
		menu.addMenuItem(menuButtonItem);

		// show next button unless we're in the last free level, show the "continue full version message"
		IMenuItem nextAreaItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameGreenFont, "NEXT", MENU_NEXT, true);
		if (mostRecentLevelPlayed == 5) {
			nextAreaItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameGreenFont, "CONTINUE \n  FULL VERSION", MENU_UPGRADE, true);
		}
		menu.addMenuItem(nextAreaItem);

		final IMenuItem againAreaItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameRedFont, "AGAIN", MENU_AGAIN, true);
		menu.addMenuItem(againAreaItem);

		final IMenuItem metalsAchivedItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "METALS", 0, false);
		menu.addMenuItem(metalsAchivedItem);

		// build menu with animations
		menu.buildAnimations();
		menu.setBackgroundEnabled(false);
		mainTitle.setPosition(10, 20);

		// control game menu positions
		rebuildYourShip.setPosition(500, 20);
		menuButtonItem.setPosition(650, 100);
		againAreaItem.setPosition(650, 210);
		nextAreaItem.setPosition(650, 320);

		// position for the continue full version message
		if (mostRecentLevelPlayed == 5) {
			nextAreaItem.setPosition(500, 320);
		}

		// level stats menu positions
		marksmanship.setPosition(100, 60);
		marksmanshipPercent.setPosition(200, 100);
		shotsFired.setPosition(100, 160);
		shotsFiredCount.setPosition(250, 200);
		kills.setPosition(100, 260);
		killsCount.setPosition(250, 300);
		timeToComplete.setPosition(100, 360);
		timeToCompleteTime.setPosition(250, 400);
		metalsAchivedItem.setPosition(this.camera.getWidth() - 330, this.camera.getHeight() - 70);

		// only show the time to complete if they've finished the level
		if (levelStatus.equals("dead")) {
			timeToComplete.setVisible(false);
			timeToCompleteTime.setVisible(false);
			metalsAchivedItem.setVisible(false);
		}

		// only show the next level button if the level after the one most recently played is available to play
		if (!SceneManager.getInstance().hasNextLevelAvailable()) {
			nextAreaItem.setVisible(false);
		}

		// for the level just completed calculate a new status as far as number of stars based on marksmanship and time to complete
		if (!levelStatus.equals("dead")) {

			// show the number of metals achieved in the level
			Sprite playerMetal1 = new Sprite(this.camera.getWidth() - 175, this.camera.getHeight() - 80, ResourceManager.getIntance().playerLevelMetalRegion, this.vbom);
			Sprite playerMetal2 = new Sprite(this.camera.getWidth() - 140, this.camera.getHeight() - 80, ResourceManager.getIntance().playerLevelMetalRegion, this.vbom);
			Sprite playerMetal3 = new Sprite(this.camera.getWidth() - 105, this.camera.getHeight() - 80, ResourceManager.getIntance().playerLevelMetalRegion, this.vbom);
			this.attachChild(playerMetal1);

			// achive a 2 star rating
			if ((marksmanshipRating >= 90 && levelPlayedSecondsCount < 60) || (levelPlayedSecondsCount <= 45)) {
				GameStatus.setLevelStatusByLevelNumber(mostRecentLevelPlayed, "3");
				this.attachChild(playerMetal2);
			}

			// achieve a 3 star rating
			if (marksmanshipRating >= 85 && levelPlayedSecondsCount <= 45) {
				GameStatus.setLevelStatusByLevelNumber(mostRecentLevelPlayed, "4");
				this.attachChild(playerMetal3);
			}

			// show marksmanship / time records status we beat the level
			marksmanshipRecord.setPosition(280, 130);
			timeRecord.setPosition(275, 435);
		}
		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item, float localX, float localY) {
		switch (item.getID()) {

		case MENU_BACK:
			SceneManager.getInstance().returnToMenuScene();
			return true;

		case MENU_AGAIN:
			SceneManager.getInstance().playLevelAgain();
			return true;

		case MENU_UPGRADE:
			viewPremiumApp();
			return true;

		case MENU_NEXT:
			if (SceneManager.getInstance().hasNextLevelAvailable()) {
				SceneManager.getInstance().playNextLevel();
			}
			return true;
		}
		return false;
	}
}