/**
 * Generic market place specific intent manager for Bitstreet Apps
 */
package com.kevinhinds.spacebotsfree.marketplace;

import java.util.List;
import java.util.Locale;

import com.kevinhinds.spacebotsfree.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * Manage intents to use within a specific device marketplace environment
 * 
 * @author khinds
 */
public class MarketPlace {

	/**
	 * Marketplace enumerated type
	 */
	public enum MarketLocale {
		GOOGLE, AMAZON, NOOK;
	}

	/**
	 * Assign the market where this application will live
	 */
	protected MarketLocale marketLocale;

	/**
	 * the package name of the premium version of the application
	 */
	protected String packageName;

	/**
	 * intent to gather to return for the specific device
	 */
	protected Intent intent;

	/**
	 * construct MarketPlace
	 * 
	 * @param context
	 */
	public MarketPlace(Context context) {
		String deviceMarketPlaceName = getDevice(context);
		packageName = context.getResources().getString(R.string.app_full_version_package);
		if (deviceMarketPlaceName.toUpperCase(Locale.ENGLISH).equals("GOOGLE")) {
			marketLocale = MarketLocale.GOOGLE;
		} else if (deviceMarketPlaceName.toUpperCase(Locale.ENGLISH).equals("AMAZON")) {
			marketLocale = MarketLocale.AMAZON;
		} else if (deviceMarketPlaceName.toUpperCase(Locale.ENGLISH).equals("NOOK")) {
			marketLocale = MarketLocale.NOOK;
		}
	}

	/**
	 * get device manufacturer to provide the correct marketplace intent(s)
	 * 
	 * @param context
	 * @return string Amazon|Nook|Google
	 */
	public String getDevice(Context context) {
		String marketplaceName = context.getResources().getString(R.string.marketplace_name);
		if (!marketplaceName.equals("")) {
			if (marketplaceName.toUpperCase(Locale.ENGLISH).equals("AMAZON")) {
				return "Amazon";
			} else if (marketplaceName.toUpperCase(Locale.ENGLISH).equals("NOOK")) {
				return "Nook";
			}
			return "Google";
		} else {
			String manufacturer = android.os.Build.MANUFACTURER;
			if (manufacturer.toLowerCase(Locale.ENGLISH).contains("amazon")) {
				return "Amazon";
			} else if (manufacturer.toLowerCase(Locale.ENGLISH).contains("nook") || manufacturer.toLowerCase(Locale.ENGLISH).contains("barnes")) {
				return "Nook";
			}
			return "Google";
		}
	}

	/**
	 * user clicks the view the premium app option, gather the device specific intent
	 * 
	 * @param context
	 * @return
	 */
	public Intent getViewPremiumAppIntent(Context context) {
		return getMarketPlaceIntent(context, false, null);
	}

	/**
	 * get the suggested app intent
	 * 
	 * @param context
	 * @return
	 */
	public Intent getViewSuggestedAppIntent(Context context) {
		return getMarketPlaceIntent(context, false, context.getResources().getString(R.string.suggested_app_package));
	}

	/**
	 * user clicks the view all apps option, gather the device specific intent
	 * 
	 * @param context
	 * @return
	 */
	public Intent getViewAllPublisherAppsIntent(Context context) {
		return getMarketPlaceIntent(context, true, null);
	}

	/**
	 * get the intent for the device specific marketplace either for a single app or all apps from
	 * the publisher
	 * 
	 * @param context
	 * @param showAll
	 * @param appPackageName
	 *            , custom override of the package name to be loaded
	 * @return
	 */
	protected Intent getMarketPlaceIntent(Context context, boolean showAll, String appPackageName) {

		String appPublisherName = context.getResources().getString(R.string.app_publisher_name);
		String appPublisherPackage = context.getResources().getString(R.string.app_publisher_package);
		if (!(appPackageName == null)) {
			packageName = appPackageName;
		}

		String deviceMarketPlaceWeburl = null;
		intent = new Intent(Intent.ACTION_VIEW);

		/** switch on marketLocale to get the right intent to open on the device */
		switch (marketLocale) {
		case GOOGLE:
			if (showAll) {
				intent.setData(Uri.parse("market://search?q=pub:\"" + appPublisherName + "\""));
				appPublisherName = appPublisherName.replace(" ", "+");
				deviceMarketPlaceWeburl = "http://play.google.com/store/search?q=pub:" + appPublisherName + "&c=apps";
			} else {
				intent.setData(Uri.parse("market://details?id=" + packageName));
				deviceMarketPlaceWeburl = "http://play.google.com/store/apps/details?id=" + packageName;
			}
			break;
		case AMAZON:
			String showAllParam = "";
			if (showAll) {
				showAllParam = "&showAll=1";
			}
			deviceMarketPlaceWeburl = "http://www.amazon.com/gp/mas/dl/android?p=" + packageName + showAllParam;
			intent = new Intent(Intent.ACTION_VIEW);
			if (showAll) {
				intent.setData(Uri.parse("amzn://apps/android?s=" + appPublisherPackage + showAllParam));
			} else {
				intent.setData(Uri.parse("amzn://apps/android?p=" + packageName));
			}
			break;
		case NOOK:
			if (showAll) {
				intent = null;
				appPublisherName = appPublisherName.replace(" ", "-").toLowerCase();
				deviceMarketPlaceWeburl = "http://www.barnesandnoble.com/c/" + appPublisherName;
			} else {
				String deviceNookEANNumber = context.getResources().getString(R.string.device_nook_ean_number);
				intent = new Intent();
				intent.setAction("com.bn.sdk.shop.details");
				intent.putExtra("product_details_ean", deviceNookEANNumber);
				String appName = context.getResources().getString(R.string.app_name);
				appName = appName.replace(" ", "-").toLowerCase();
				appPublisherName = appPublisherName.replace(" ", "-").toLowerCase();
				deviceMarketPlaceWeburl = "http://search.barnesandnoble.com/" + appName + "/" + appPublisherName + "/e/" + deviceNookEANNumber;
			}
			break;
		}

		/**
		 * if we don't have the marketplace for the device available as an intent, attempt to
		 * produce a standard webpage URL to open
		 */
		if (MarketPlace.isIntentAvailable(context, intent)) {
			return intent;
		} else {
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deviceMarketPlaceWeburl));
			if (MarketPlace.isIntentAvailable(context, intent)) {
				return intent;
			}
			return null;
		}
	}

	/**
	 * determine if the intent is available on the device to continue
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	protected static boolean isIntentAvailable(Context context, Intent intent) {
		if (intent == null) {
			return false;
		}
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * get the suggested app intent
	 */
	public static void viewSuggestedApp(Context context) {
		MarketPlace marketPlace = new MarketPlace(context);
		Intent intent = marketPlace.getViewSuggestedAppIntent(context);
		if (intent != null) {
			context.startActivity(intent);
		}
	}

	/**
	 * view all apps on the device marketplace for current publisher
	 */
	public static void viewAllPublisherApps(Context context) {
		MarketPlace marketPlace = new MarketPlace(context);
		Intent intent = marketPlace.getViewAllPublisherAppsIntent(context);
		if (intent != null) {
			context.startActivity(intent);
		}
	}

	/**
	 * view the premium version of this app
	 */
	public static void viewPremiumApp(Context context) {
		MarketPlace marketPlace = new MarketPlace(context);
		Intent intent = marketPlace.getViewPremiumAppIntent(context);
		if (intent != null) {
			context.startActivity(intent);
		}
	}
}
