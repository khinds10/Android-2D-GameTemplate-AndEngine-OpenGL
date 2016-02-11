package com.kevinhinds.spacebotsfree.status;

import com.kevinhinds.spacebotsfree.GameConfiguration;
import com.kevinhinds.spacebotsfree.MainGameActivity;
import com.kevinhinds.spacebotsfree.ResourceManager;

/**
 * main status of the game for player, persisted to Android SharedPreferences
 * 
 * @author khinds
 */
public class GameStatus {

	// list of keys used for the SharedPreferences references
	public static String HighScore = "HighScore";
	public static String MusicPlay = "MusicPlay";
	public static String SoundFXPlay = "SoundFXPlay";
	public static String LevelStatus = "LevelStatus";
	public static String LevelTimeRecord = "LevelTimeRecord";
	public static String LevelMarksmanShipRecord = "LevelMarksmanShipRecord";
	public static String ShipRepairedStatus = "ShipRepairedStatus";

	// most recent level played temporary stats like shots fired / number of hits, resets for each new play
	public static String MostRecentLevel = "MostRecentLevel";
	public static String MostRecentLevelShots = "MostRecentLevelShots";
	public static String MostRecentLevelKills = "MostRecentLevelKills";
	public static String MostRecentLevelHits = "MostRecentLevelHits";
	public static String MostRecentLevelTime = "MostRecentLevelTime";

	// enumerate the types of temporary level stats that exist
	public static enum levelStatsType {
		SHOTS, KILLS, HITS, TIME
	}

	/**
	 * code that will run if it's the first time we've installed the application
	 */
	public static void setDefaultGameStatus() {

		MainGameActivity activity = ResourceManager.getIntance().activity;
		activity.statusAndPreferencesEditor.putInt(GameStatus.HighScore, 0);
		activity.statusAndPreferencesEditor.putBoolean(GameStatus.MusicPlay, false);
		activity.statusAndPreferencesEditor.putBoolean(GameStatus.SoundFXPlay, false);
		activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevel, 1);

		// create a list of all new level status numbers, marking level 1 as "1" or available to play
		String levelStats = StatusListManager.createDefaultCSVList(GameConfiguration.numberLevels, "0");
		levelStats = StatusListManager.updateIndexByValue(levelStats, 1, "1");
		activity.statusAndPreferencesEditor.putString(GameStatus.LevelStatus, levelStats);

		// create a list of all levels have a time record of "0" seconds for now, until they play a level and do better
		String levelTimeRecords = StatusListManager.createDefaultCSVList(GameConfiguration.numberLevels, "1000");
		activity.statusAndPreferencesEditor.putString(GameStatus.LevelTimeRecord, levelTimeRecords);

		// create a list of all levels have a record of "0" marksmanship for now, until they play a level and do better
		String levelMarksmanshipRecords = StatusListManager.createDefaultCSVList(GameConfiguration.numberLevels, "0");
		activity.statusAndPreferencesEditor.putString(GameStatus.LevelMarksmanShipRecord, levelMarksmanshipRecords);

		// update FirstRun to say we're no longer running game for the first time and commit default values
		activity.statusAndPreferencesEditor.putBoolean("FIRSTRUN", false);
		activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * collect ship piece by name by saving it to the game status list for it
	 * 
	 * @param pieceName
	 */
	public static void collectShipPiece(String pieceName) {
		MainGameActivity activity = ResourceManager.getIntance().activity;
		String shipRepairedStatus = activity.statusAndPreferences.getString(GameStatus.ShipRepairedStatus, "");
		if (!StatusListManager.containsValue(shipRepairedStatus, pieceName)) {
			shipRepairedStatus = shipRepairedStatus + pieceName + ",";
			activity.statusAndPreferencesEditor.putString(GameStatus.ShipRepairedStatus, shipRepairedStatus);
			activity.statusAndPreferencesEditor.commit();
		}
	}

	/**
	 * get the current status of ship in repair to show in UI
	 * 
	 * @return
	 */
	public static String getShipRepairedStatus() {
		return ResourceManager.getIntance().activity.statusAndPreferences.getString(GameStatus.ShipRepairedStatus, "");
	}

	/**
	 * get the highest completed level for player
	 * 
	 * @return
	 */
	public static int getHighestCompletedLevel() {
		int level = GameConfiguration.numberLevels;
		while (level > 1) {
			if (GameStatus.levelStatusByLevelNumber(level) > 0) {
				return level;
			}
			level--;
		}
		return 1;
	}

	// determine if we're supposed to play music in the game
	public static boolean playMusic() {
		return ResourceManager.getIntance().activity.statusAndPreferences.getBoolean(GameStatus.MusicPlay, true);
	}

	// set play music or not in game
	public static void setPlayMusicPreference(boolean playMusic) {
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putBoolean(GameStatus.MusicPlay, playMusic);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	// determine if we're supposed to play sound effects in the game
	public static boolean playSoundFX() {
		return ResourceManager.getIntance().activity.statusAndPreferences.getBoolean(GameStatus.SoundFXPlay, true);
	}

	// set play sound effects or not in game
	public static void setPlaySoundEffectsPreference(boolean playSoundFX) {
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putBoolean(GameStatus.SoundFXPlay, playSoundFX);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * set the current level starting to be played to zero stats
	 */
	public static void resetGameLevelStats() {
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelShots, 0);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelKills, 0);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelHits, 0);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putLong(GameStatus.MostRecentLevelTime, 0);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * flag that the level has began or ended to calculate how long you played
	 */
	public static void flagCurrentLevelTime() {

		long timeStartedLevel = ResourceManager.getIntance().activity.statusAndPreferences.getLong(GameStatus.MostRecentLevelTime, 0);
		long currentTimestamp = System.currentTimeMillis() / 1000;

		// get the time the user started the level
		if (timeStartedLevel == 0) {
			ResourceManager.getIntance().activity.statusAndPreferencesEditor.putLong(GameStatus.MostRecentLevelTime, currentTimestamp);
		} else {
			long timePlayedLevelSeconds = currentTimestamp - timeStartedLevel;
			ResourceManager.getIntance().activity.statusAndPreferencesEditor.putLong(GameStatus.MostRecentLevelTime, timePlayedLevelSeconds);
		}
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * get the current amount of seconds the level was played for
	 * 
	 * @return
	 */
	public static long getCurrentLevelPlayedTime() {
		return ResourceManager.getIntance().activity.statusAndPreferences.getLong(GameStatus.MostRecentLevelTime, 0);
	}

	/**
	 * update game level stats by type
	 * 
	 * @param type
	 * @param value
	 */
	public static void updateGameLevelStats(levelStatsType type, int value) {
		switch (type) {
		case SHOTS:
			ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelShots, value);
			break;
		case KILLS:
			ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelKills, value);
			break;
		case HITS:
			ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevelHits, value);
			break;
		default:
			break;
		}
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * increment a current game statistic
	 * 
	 * @param type
	 */
	public static void incrementGameLevelStat(levelStatsType type) {
		int currentValue = GameStatus.getGameLevelStats(type);
		currentValue = currentValue + 1;
		GameStatus.updateGameLevelStats(type, currentValue);
	}

	/**
	 * get game level stats
	 * 
	 * @param type
	 * @return
	 */
	public static int getGameLevelStats(levelStatsType type) {
		switch (type) {
		case SHOTS:
			return ResourceManager.getIntance().activity.statusAndPreferences.getInt(GameStatus.MostRecentLevelShots, 0);
		case KILLS:
			return ResourceManager.getIntance().activity.statusAndPreferences.getInt(GameStatus.MostRecentLevelKills, 0);
		case HITS:
			return ResourceManager.getIntance().activity.statusAndPreferences.getInt(GameStatus.MostRecentLevelHits, 0);
		default:
			return 0;
		}
	}

	// get the most recently level played
	public static int getMostRecentLevel() {
		return ResourceManager.getIntance().activity.statusAndPreferences.getInt(GameStatus.MostRecentLevel, 1);
	}

	// set the most recently level played
	public static void setMostRecentlLevel(int levelNumber) {
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.putInt(GameStatus.MostRecentLevel, levelNumber);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * set a record for a level recently played by user (personal record for "marksmanship" or "time completed")
	 * 
	 * @param levelNumber
	 * @param recordType
	 *            type of record set for level "marksmanship" | "time"
	 * @param recordValue
	 *            numeric value as a string
	 */
	public static void setRecordForMetricByLevelNumber(int levelNumber, String recordType, String recordValue) {
		MainGameActivity activity = ResourceManager.getIntance().activity;
		if (recordType.equals("time")) {
			String newLevelRecordList = StatusListManager.updateIndexByValue(activity.statusAndPreferences.getString(GameStatus.LevelTimeRecord, ""), levelNumber, recordValue);
			activity.statusAndPreferencesEditor.putString(GameStatus.LevelTimeRecord, newLevelRecordList);
		} else {
			String newLevelRecordList = StatusListManager.updateIndexByValue(activity.statusAndPreferences.getString(GameStatus.LevelMarksmanShipRecord, ""), levelNumber, recordValue);
			activity.statusAndPreferencesEditor.putString(GameStatus.LevelMarksmanShipRecord, newLevelRecordList);
		}
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * get record for level by level number and type
	 * 
	 * @param levelNumber
	 * @param recordType
	 * @return
	 */
	public static int levelRecordByLevelNumber(int levelNumber, String recordType) {
		MainGameActivity activity = ResourceManager.getIntance().activity;
		String levelRecord = "";
		if (recordType.equals("time")) {
			levelRecord = StatusListManager.getValueByIndex(activity.statusAndPreferences.getString(GameStatus.LevelTimeRecord, ""), levelNumber);
		} else {
			levelRecord = StatusListManager.getValueByIndex(activity.statusAndPreferences.getString(GameStatus.LevelMarksmanShipRecord, ""), levelNumber);
		}
		return Integer.parseInt(String.valueOf(Math.round(Float.parseFloat(levelRecord))));
	}

	/**
	 * generate comma separated list of current status code for each level in the game, updating the one specified to a new value
	 * 
	 * @param levelNumber
	 * @param status
	 */
	public static void setLevelStatusByLevelNumber(int levelNumber, String status) {
		MainGameActivity activity = ResourceManager.getIntance().activity;
		String NewLevelStatusList = StatusListManager.updateIndexByValue(activity.statusAndPreferences.getString(GameStatus.LevelStatus, ""), levelNumber, status);
		activity.statusAndPreferencesEditor.putString(GameStatus.LevelStatus, NewLevelStatusList);
		ResourceManager.getIntance().activity.statusAndPreferencesEditor.commit();
	}

	/**
	 * for a given level in the game return the current status as far as an integer
	 * 
	 * @param levelNumber
	 * @return
	 */
	public static int levelStatusByLevelNumber(int levelNumber) {
		MainGameActivity activity = ResourceManager.getIntance().activity;
		String levelStatus = StatusListManager.getValueByIndex(activity.statusAndPreferences.getString(GameStatus.LevelStatus, ""), levelNumber);
		return Integer.parseInt(levelStatus);
	}
}