package com.kevinhinds.spacebotsfree.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;

/**
 * daydream scenes for the game
 * 
 * @author khinds
 */
public class DayDreamScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_CONTINUE = 1;
	private int levelToPlay = 1;

	@Override
	public void createScene() {
		createMenu();
	}

	@Override
	public void onBackPressed() {
		SceneManager.getInstance().returnToMenuScene();
	}

	/**
	 * set the level to play after you're done with the daydream
	 * @param levelToPlay
	 */
	public void setLevelToPlayNext(int levelToPlay) {
		this.levelToPlay = levelToPlay;
	}
	
	/**
	 * create menu elements from local sprites to display
	 */
	private void createMenu() {
		menu = new MenuScene(camera);
		menu.setPosition(0, 0);

		// for all daydreams in the mapping, set menu items to read
		if (GameConfiguration.daydreamLevelsMapping.keySet().contains(levelToPlay)) {
			String[] levelDaydreamSentances = GameConfiguration.daydreamLevelsMapping.get(levelToPlay).split("-");
			for (String sentance : levelDaydreamSentances) {
				IMenuItem dayDreamPhrase = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFont, sentance, 0, false);
				menu.addMenuItem(dayDreamPhrase);
			}
		}
		
		// add play button
		final IMenuItem playButtonItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontLarge, "PLAY", MENU_CONTINUE, true);
		menu.addMenuItem(playButtonItem);
		
		// build animations for the menu and click listener
		menu.buildAnimations();
		menu.setBackgroundEnabled(false);	
		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item, float localX, float localY) {
		switch (item.getID()) {
		case MENU_CONTINUE:
			SceneManager.getInstance().playGameScene(levelToPlay);
			return true;
		}
		return false;
	}
}