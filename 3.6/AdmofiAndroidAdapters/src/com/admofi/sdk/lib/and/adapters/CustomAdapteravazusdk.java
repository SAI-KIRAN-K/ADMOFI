package com.admofi.sdk.lib.and.adapters;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import nativesdk.ad.common.AdSdk;
import nativesdk.ad.common.IAdSdkListener;
import nativesdk.ad.common.IAppwallListener;

/**
 * Created by apple on 12/09/17.
 */

public class CustomAdapteravazusdk extends CustomAdapterImpl {

	private String APP_ID = "";
	private static boolean isinitialised = false;
	private static  IAppwallListener appwallListener;

	public CustomAdapteravazusdk(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu checking classes");
			Class.forName("nativesdk.ad.common.AdSdk");
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		APP_ID = mAdShown.getAdapterKey(0);

		super.setSupported(true);

		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu LoadBanner");
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);

	}

	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {

		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu LoadInterstitial");
			if (!CustomAdapteravazusdk.isinitialised) {
				AdSdk.initialize(mContext, APP_ID, new IAdSdkListener() {
					@Override
					public void onInitSuccess() {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu onInitSuccess and Ad ready");
						// set market style
						AdSdk.preloadMarketData(mContext);
						CustomAdapteravazusdk.isinitialised = true;
						setcallback();
						adEventReady(null);
					}

					@Override
					public void onInitFailed(String msg) {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu onInitFailed :: " + msg);
						CustomAdapteravazusdk.isinitialised = false;
						adEventLoadFailed();
					}
				});
			} else {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu Ad ready");
				setcallback();
				adEventReady(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}


	private void setcallback() {
		try {
			if (appwallListener == null)
			{
				appwallListener = new IAppwallListener() {
					@Override
					public void onAppwallOpened() {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu onAppwallOpened");
						adEventImpression();
					}

					@Override
					public void onAppwallClosed() {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu onAppwallClosed");
						adEventCompleted();
					}
				};
				AdSdk.registerAppwallListener(appwallListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean showinterstitial() {

		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazu Show Interstitial");
			if (CustomAdapteravazusdk.isinitialised && mContext != null) {
				AdSdk.showAppMarket(mContext);
				return true;
			} else {
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			}

		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			e.printStackTrace();
		}
		return false;
	}

}
