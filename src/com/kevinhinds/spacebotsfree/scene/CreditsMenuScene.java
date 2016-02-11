package com.kevinhinds.spacebotsfree.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;

import com.kevinhinds.spacebotsfree.ResourceManager;

/**
 * credits scene for the game
 * 
 * @author khinds
 */
public class CreditsMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menu;
	private final int MENU_BACK = 1;

	@Override
	public void createScene() {
		final Sprite spriteBG = new Sprite(0, 0, ResourceManager.getIntance().colorgalaxyBackgroundRegion, ResourceManager.getIntance().vbom);
		attachChild(spriteBG);
		
		final Sprite andLogoSprite = new Sprite(ResourceManager.getIntance().camera.getWidth() - 200, ResourceManager.getIntance().camera.getHeight() - 200, ResourceManager.getIntance().andengineRegion, ResourceManager.getIntance().vbom);
		attachChild(andLogoSprite);
		
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
		final IMenuItem mainTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontLarge, "TITAN ADRIFT", 0, false);
		menu.addMenuItem(mainTitle);

		final IMenuItem developTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFont, "DEVELOPED BY\n\t\t\t\tKEVIN HINDS at BITSTREET APPS\n", 0, false);
		menu.addMenuItem(developTitle);

		final IMenuItem artTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFont, "ARTWORK\n\t\t\t\tM484GAMES & OPENGAMEART.ORG\n\nSHIP ARTWORK\n\t\t\tSKORPIO\n", 0, false);
		menu.addMenuItem(artTitle);

		final IMenuItem musicTitle = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFont, "MUSIC\n\t\t\t\tWZZARD\n", 0, false);
		menu.addMenuItem(musicTitle);

		final IMenuItem backButtonItem = ResourceManager.getIntance().createTextMenuItem(ResourceManager.getIntance().gameFontGray, "MAIN MENU", MENU_BACK, true);
		menu.addMenuItem(backButtonItem);

		menu.buildAnimations();
		menu.setBackgroundEnabled(false);

		// position the menu items
		mainTitle.setPosition(10, 20);
		developTitle.setPosition(10, 100);
		artTitle.setPosition(10, 180);
		musicTitle.setPosition(10, 330);
		backButtonItem.setPosition(backButtonItem.getX(), backButtonItem.getY() + 35);
		menu.setOnMenuItemClickListener(this);
		setChildScene(menu);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene scene, IMenuItem item, float localX, float localY) {
		switch (item.getID()) {
		case MENU_BACK:
			SceneManager.getInstance().returnToMenuScene();
			return true;
		}
		return false;
	}
}