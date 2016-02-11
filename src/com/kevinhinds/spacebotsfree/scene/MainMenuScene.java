package com.kevinhinds.spacebotsfree.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;

import com.kevinhinds.spacebotsfree.ResourceManager;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * main menu scene for the game
 * 
 * @author khinds
 */
public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_PLAY = 1;
	private final int MENU_EXIT = 2;
	private final int MENU_LEVELS = 3;
	private final int MENU_CREDITS = 4;
	private final int MENU_TUTORIAL = 5;
	private final int MENU_ARCADE = 6;
	private final int MENU_PREMIUM = 7;

	@Override
	public void createScene() {
		final Sprite spriteBG = new Sprite(0, 0, ResourceManager.getIntance().planetBackgroundRegion, ResourceManager.getIntance().vbom);
		attachChild(spriteBG);
		createMenu();
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
	}

	/**
	 * create menu elements from local sprites to display
	 */
	private void createMenu() {
		menu = new MenuScene(camera);
		menu.setPosition(0, 0);

		/** create menu items */
		final IMenuItem mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().titleFont, "TITAN ADRIFT FREE", 0, false);
		menu.addMenuItem(mainTitle);

		final IMenuItem playItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().menuBlueFont, "PLAY", MENU_PLAY, true);
		menu.addMenuItem(playItem);
		
		final IMenuItem arcadeItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().menuGrayFont, "ARCADE", MENU_ARCADE, true);
		menu.addMenuItem(arcadeItem);

		final IMenuItem levelSelectItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().menuGreenFont, "AREA SELECT", MENU_LEVELS, true);
		menu.addMenuItem(levelSelectItem);

		final IMenuItem getFullVersion = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameGreenFont, "UPGRADE NEW LEVELS & ARCADE", MENU_PREMIUM, true);
		menu.addMenuItem(getFullVersion);
		final IMenuItem exitItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().menuGrayFont, "EXIT", MENU_EXIT, true);
		menu.addMenuItem(exitItem);

		final IMenuItem creditsItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "CREDITS", MENU_CREDITS, true);
		menu.addMenuItem(creditsItem);

		final IMenuItem tutorialItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameRedFont, "HOW TO PLAY", MENU_TUTORIAL, true);
		menu.addMenuItem(tutorialItem);

		menu.buildAnimations();
		menu.setBackgroundEnabled(false);

		// position the menu items
		mainTitle.setPosition(mainTitle.getX(), mainTitle.getY() - 60);
		playItem.setPosition(playItem.getX(), playItem.getY() - 15);
		arcadeItem.setPosition(arcadeItem.getX(), arcadeItem.getY());
		levelSelectItem.setPosition(levelSelectItem.getX(), levelSelectItem.getY() + 18);
		getFullVersion.setPosition(levelSelectItem.getX() - 50, levelSelectItem.getY() + 70);
		exitItem.setPosition(exitItem.getX(), exitItem.getY() + 60);
		creditsItem.setPosition(creditsItem.getX() + 250, creditsItem.getY() + 70);
		tutorialItem.setPosition(creditsItem.getX() - 550, creditsItem.getY());

		// show lock next to arcade menu
		Sprite lockIcon = new Sprite(arcadeItem.getX() + 295, arcadeItem.getY() + 15, ResourceManager.getIntance().lockRegion, this.vbom);
		this.attachChild(lockIcon);
		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item, float localX, float localY) {
		switch (item.getID()) {
		case MENU_PLAY:
			SceneManager.getInstance().playLevelAgain();
			return true;
		case MENU_LEVELS:
			SceneManager.getInstance().loadLevelSelectScene();
			return true;
		case MENU_CREDITS:
			SceneManager.getInstance().loadCreditsScene();
			return true;
		case MENU_TUTORIAL:
			SceneManager.getInstance().loadTutorialScene();
			return true;
		case MENU_PREMIUM:
			viewPremiumApp();
			return true;
		case MENU_ARCADE:
			// show the purchase dialog on android activity UI update thread
			final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
			builder.setTitle("Get the full version ($0.99 USD)");
			builder.setPositiveButton("Let's go!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					viewPremiumApp();
				}
			});
			builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			});
			return true;
		case MENU_EXIT:
			System.exit(0);
			return true;
		}
		return false;
	}
}