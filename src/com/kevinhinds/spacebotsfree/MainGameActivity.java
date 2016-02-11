package com.kevinhinds.spacebotsfree;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.ui.activity.BaseGameActivity;

import com.kevinhinds.spacebotsfree.marketplace.MarketPlace;
import com.kevinhinds.spacebotsfree.scene.SceneManager;
import com.kevinhinds.spacebotsfree.status.GameStatus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * load the AndEngine based game into the main activity for the app and run it
 * 
 * @author khinds
 */
public class MainGameActivity extends BaseGameActivity {

	public Camera camera;
	public SharedPreferences statusAndPreferences;
	public SharedPreferences.Editor statusAndPreferencesEditor;

	@Override
	public EngineOptions onCreateEngineOptions() {

		statusAndPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		statusAndPreferencesEditor = statusAndPreferences.edit();

		camera = new Camera(0, 0, 804, 480);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(800, 480), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		if (!MultiTouch.isSupported(this)) {
			Toast.makeText(this, "Sorry, your device does not support multitouch to play.", Toast.LENGTH_LONG).show();
		}
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback cb) throws Exception {
		ResourceManager.prepareManager(getEngine(), this, camera, getVertexBufferObjectManager());

		// if first install then need some default game player status created
		if (statusAndPreferences.getBoolean("FIRSTRUN", true)) {
			GameStatus.setDefaultGameStatus();
		}
		cb.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback cb) throws Exception {
		SceneManager.getInstance().startGame(cb);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback cb) throws Exception {
		cb.onPopulateSceneFinished();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackPressed();
		}
		return false;
	}
	/**
	 * view the premium version of this app
	 */
	public void viewPremiumApp() {
		MarketPlace marketPlace = new MarketPlace(this);
		Intent intent = marketPlace.getViewPremiumAppIntent(this);
		if (intent != null) {
			startActivity(intent);
}
	}
}
