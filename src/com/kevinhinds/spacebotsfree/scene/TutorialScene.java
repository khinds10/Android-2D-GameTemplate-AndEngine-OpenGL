package com.kevinhinds.spacebotsfree.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.ResourceManager;

/**
 * main menu scene for the game
 * 
 * @author khinds
 */
public class TutorialScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_EXIT = 1;

	@Override
	public void createScene() {
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

		// create menu items
		final IMenuItem mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().levelSelectFontPlay, "How to Play", 0, false);
		menu.addMenuItem(mainTitle);

		final IMenuItem objectiveItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Collect all the pieces in each area to rebuild your ship.", 0, false);
		menu.addMenuItem(objectiveItem);

		final IMenuItem goalsItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Complete areas quickly with accurate shooting skills\n against enemies to win metals!", 0, false);
		menu.addMenuItem(goalsItem);

		final IMenuItem backItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().levelSelectFontOne, "BACK", MENU_EXIT, true);
		menu.addMenuItem(backItem);

		// player sprite appears
		AnimatedSprite playerSprite = this.createAnimatedSprite(100, 80, ResourceManager.getIntance().playerRegion, this.vbom);
		playerSprite.animate(new long[] { GameConfiguration.playerAnimationSpeed }, new int[] { GameConfiguration.playerStartLevelFrame });
		this.attachChild(playerSprite);

		// ship piece appears
		TiledSprite pieceSprite = this.createAnimatedSprite(150, 100, ResourceManager.getIntance().pieceLevelRegion, this.vbom);
		pieceSprite.setCurrentTileIndex(60);
		this.attachChild(pieceSprite);

		// metal displayed to show awards you can win
		Sprite playerMetal = new Sprite(125, 155, ResourceManager.getIntance().playerLevelMetalSmallRegion, this.vbom);
		this.attachChild(playerMetal);

		// reload ability description
		TiledSprite reloadAbility = this.createAnimatedSprite(20, 220, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		reloadAbility.setCurrentTileIndex(0);
		this.attachChild(reloadAbility);

		final IMenuItem reloadItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Reload Blaster", 0, false);
		menu.addMenuItem(reloadItem);

		// bomb ability description
		TiledSprite bombAbility = this.createAnimatedSprite(20, 300, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		bombAbility.setCurrentTileIndex(1);
		this.attachChild(bombAbility);

		final IMenuItem bombItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Bomb Floors and Walls \nto get through", 0, false);
		menu.addMenuItem(bombItem);

		// jump ability
		TiledSprite jumpAbility = this.createAnimatedSprite(255, 220, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		jumpAbility.setCurrentTileIndex(2);
		this.attachChild(jumpAbility);

		final IMenuItem jumpItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Extra High Jump", 0, false);
		menu.addMenuItem(jumpItem);

		TiledSprite lifeAbility = this.createAnimatedSprite(350, 300, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		lifeAbility.setCurrentTileIndex(3);
		this.attachChild(lifeAbility);

		final IMenuItem lifeItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Life", 0, false);
		menu.addMenuItem(lifeItem);

		TiledSprite bridgeAbility = this.createAnimatedSprite(500, 220, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		bridgeAbility.setCurrentTileIndex(4);
		this.attachChild(bridgeAbility);

		final IMenuItem bridgeItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Build Extra Platform", 0, false);
		menu.addMenuItem(bridgeItem);

		TiledSprite flareAbility = this.createAnimatedSprite(500, 300, ResourceManager.getIntance().itemButtonRegion, this.vbom);
		flareAbility.setCurrentTileIndex(5);
		this.attachChild(flareAbility);

		final IMenuItem flareItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Burn through Walls", 0, false);
		menu.addMenuItem(flareItem);

		// down button displayed to show the special ability for it
		Sprite downButton = new Sprite(20, 375, ResourceManager.getIntance().controlDownRegion, this.vbom);
		this.attachChild(downButton);

		final IMenuItem downButtonItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontMedium, "Press down to \nload more powerful shot", 0, false);
		menu.addMenuItem(downButtonItem);

		menu.buildAnimations();
		menu.setBackgroundEnabled(false);

		// position the menu items
		mainTitle.setPosition(50, 20);
		objectiveItem.setPosition(180, 100);
		goalsItem.setPosition(180, 160);
		backItem.setPosition(backItem.getX() + 200, backItem.getY() + 220);

		// position ability text descriptions
		reloadItem.setPosition(reloadAbility.getX() + 50, reloadAbility.getY() + 25);
		bombItem.setPosition(bombAbility.getX() + 50, bombAbility.getY() + 25);
		jumpItem.setPosition(jumpAbility.getX() + 50, jumpAbility.getY() + 25);
		bridgeItem.setPosition(bridgeAbility.getX() + 50, bridgeAbility.getY() + 25);
		flareItem.setPosition(flareAbility.getX() + 50, flareAbility.getY() + 25);
		lifeItem.setPosition(lifeAbility.getX() + 50, lifeAbility.getY() + 25);
		downButtonItem.setPosition(120, 400);

		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item, float localX, float localY) {
		switch (item.getID()) {
		case MENU_EXIT:
			SceneManager.getInstance().returnToMenuScene();
			return true;
		}
		return false;
	}
}
