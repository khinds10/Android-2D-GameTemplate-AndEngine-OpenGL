package com.kevinhinds.spacebotsfree.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;

import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.status.GameStatus;

/**
 * arcade level status scene
 * 
 * @author khinds
 */
public class ArcadeStatusScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_BACK = 1;
	private final int MENU_AGAIN = 2;
	private int mostRecentLevelPlayed = 0;

	@Override
	public void createScene() {
		final Sprite spriteBG = new Sprite(0, 0, ResourceManager.getIntance().illuminateBackgroundRegion, ResourceManager.getIntance().vbom);
		attachChild(spriteBG);
		mostRecentLevelPlayed = GameStatus.getMostRecentLevel();
		createMenu();
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
			mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameRedFont, "ARCADE", 0, false);
		} else {
			mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameGreenFont, "ARCADE WIN! 45 seconds", 0, false);
		}
		menu.addMenuItem(mainTitle);

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
		boolean personalBestMarksmanship = false;
		int previousRecordMarksmanship = GameStatus.levelRecordByLevelNumber(mostRecentLevelPlayed, "marksmanship");
		if ((previousRecordMarksmanship <= marksmanshipRating) && (marksmanshipRating > 0)) {
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

		final IMenuItem shotsFired = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Shots Fired", 0, false);
		menu.addMenuItem(shotsFired);

		final IMenuItem shotsFiredCount = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Integer.toString(GameStatus.getGameLevelStats(GameStatus.levelStatsType.SHOTS)), 0, false);
		menu.addMenuItem(shotsFiredCount);

		final IMenuItem kills = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "Aliens Killed", 0, false);
		menu.addMenuItem(kills);

		final IMenuItem killsCount = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, Integer.toString(GameStatus.getGameLevelStats(GameStatus.levelStatsType.KILLS)), 0, false);
		menu.addMenuItem(killsCount);

		final IMenuItem menuButtonItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameBlueFont, "MENU", MENU_BACK, true);
		menu.addMenuItem(menuButtonItem);

		final IMenuItem againAreaItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameRedFont, "AGAIN", MENU_AGAIN, true);
		menu.addMenuItem(againAreaItem);

		// build menu with animations
		menu.buildAnimations();
		menu.setBackgroundEnabled(false);
		mainTitle.setPosition(10, 20);

		// control game menu positions
		menuButtonItem.setPosition(650, 210);
		againAreaItem.setPosition(650, 310);

		// level stats menu positions
		marksmanship.setPosition(100, 90);
		marksmanshipPercent.setPosition(200, 130);
		shotsFired.setPosition(100, 190);
		shotsFiredCount.setPosition(250, 230);
		kills.setPosition(100, 290);
		killsCount.setPosition(250, 330);
		marksmanshipRecord.setPosition(280, 170);

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
			SceneManager.getInstance().playArcadeScene();
			return true;
		}
		return false;
	}
}