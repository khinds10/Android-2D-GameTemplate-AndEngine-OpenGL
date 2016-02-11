package com.kevinhinds.spacebotsfree.player;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.scene.GameScene;

/**
 * special ability buttons added from acquired tokens in the game
 * 
 * @author khinds
 */
public class TokenButton extends TiledSprite {

	public final int playerAbilityID;
	public final String playerAbilityName;
	public int countAvailable;
	private final int tileIndex;
	public Scene scene;

	/**
	 * create the token button to attach to the game scene HUD
	 * 
	 * @param playerAbilityID
	 * @param isAvailable
	 * @param countAvailable
	 * @param tileIndex
	 * @param x
	 * @param y
	 * @param texture
	 * @param vbom
	 */
	public TokenButton(int playerAbilityID, int countAvailable, int tileIndex, float x, float y, ITiledTextureRegion texture, VertexBufferObjectManager vbom) {
		super(x, y, texture, vbom);
		this.tileIndex = tileIndex;
		this.playerAbilityID = playerAbilityID;
		this.playerAbilityName = GameConfiguration.playerAbilitiesTokensMapping.get(playerAbilityID);
		this.countAvailable = countAvailable;
	}

	/**
	 * acquire a new token button from an item acquired in the game
	 */
	public void aquire() {
		countAvailable++;
		setVisible(true);
	}

	/**
	 * use this token, decrease the value on hand by one and set the token button to invisible if there are no left to use
	 */
	public void use() {
		countAvailable--;
		if (countAvailable <= 0) {
			countAvailable = 0;
			setVisible(false);
		}
	}

	/**
	 * attach the button to the game scene HUD
	 * 
	 * @param scene
	 */
	public void attachToHUD(GameScene scene) {
		this.scene = scene;
		setCurrentTileIndex(tileIndex);
		scene.gameHUD.registerTouchArea(this);
		scene.gameHUD.attachChild(this);
		if (countAvailable <= 0) {
			setVisible(false);
		}
	}
}