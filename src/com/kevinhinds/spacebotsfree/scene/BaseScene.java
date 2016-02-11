package com.kevinhinds.spacebotsfree.scene;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.kevinhinds.spacebotsfree.ResourceManager;
import com.kevinhinds.spacebotsfree.MainGameActivity;

/**
 * basic scene for all other game scenes
 * 
 * @author khinds
 */
public abstract class BaseScene extends Scene {

	public HUD gameHUD;
	protected Engine engine;
	public VertexBufferObjectManager vbom;
	public PhysicsWorld physicsWorld;
	protected MainGameActivity activity;
	public Camera camera;
	public int levelNumber;
	public String levelStatus;

	public BaseScene() {
		engine = ResourceManager.getIntance().engine;
		vbom = ResourceManager.getIntance().vbom;
		activity = ResourceManager.getIntance().activity;
		camera = ResourceManager.getIntance().camera;
	}

	/**
	 * create a basic sprite on the scene in question
	 * 
	 * @param x
	 *            location x coordinates
	 * @param y
	 *            location y coordinates
	 * @param region
	 * @param vbom
	 * @return
	 */
	public Sprite createSprite(float x, float y, ITextureRegion region, VertexBufferObjectManager vbom) {
		Sprite sprite = new Sprite(x, y, region, vbom) {
			protected void preDraw(GLState glState, Camera camera) {
				super.preDraw(glState, camera);
				glState.enableDither();
			}
		};
		return sprite;
	}

	/**
	 * create an animated sprite on the scene in question
	 * 
	 * @param x
	 *            location x coordinates
	 * @param y
	 *            location y coordinates
	 * @param region
	 * @param vbom
	 * @return
	 */
	public AnimatedSprite createAnimatedSprite(float x, float y, ITiledTextureRegion region, VertexBufferObjectManager vbom) {
		AnimatedSprite sprite = new AnimatedSprite(x, y, region, vbom) {
			protected void preDraw(GLState glState, Camera camera) {
				super.preDraw(glState, camera);
				glState.enableDither();
			}
		};
		return sprite;
	}

	public abstract void createScene();

	public abstract void onBackPressed();

	/**
	 * remove any game HUD present
	 */
	public void disposeScene() {
		gameHUD = null;
		camera.setHUD(gameHUD);
	}

	/**
	 * set the level the game is currently playing
	 * 
	 * @param levelNumber
	 */
	public void setGameLevel(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	/**
	 * set the current status the scene level is in as an additional option to render different elements
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.levelStatus = status;
	}
	/**
	 * show the premium app in the market place!
	 */
	public void viewPremiumApp() {
		this.activity.viewPremiumApp();
}
}
