package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.adapters.CustomAdapterImpl;
import com.vungle.publisher.AdConfig;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VungleAdEventListener;
import com.vungle.publisher.VungleInitListener;
import com.vungle.publisher.VunglePub;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CustomAdaptervungle extends CustomAdapterImpl {
	private String APP_ID = "";
	private String PLACEMENT_ID = "";
	private String PLACEMENT_INIT_IDS = "";
	Activity mAct = null;
	private VunglePub vunglePub = VunglePub.getInstance();
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;

	private boolean isActive = true;
	private Timer timer = null;

	public CustomAdaptervungle(Context context) {
		super(context);
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.vungle.publisher.VunglePub");
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		APP_ID = mAdShown.getAdapterKey(0);
		PLACEMENT_ID = mAdShown.getAdapterKey(1);
		PLACEMENT_INIT_IDS = mAdShown.getAdapterKey(2);
		super.setSupported(true);
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = 60;//mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitialV(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}
	
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle LoadBanner");
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitialV(Context context, AdmofiAd mAdCurrent) {

		try{
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle LoadInter");
			mAct = (Activity) context;
			//initializing vungle SDK
			ArrayList<String> pids = new ArrayList<String>();
			if(PLACEMENT_INIT_IDS != null && PLACEMENT_INIT_IDS.length()>0){
				String strPids[] = PLACEMENT_INIT_IDS.split(",");
				for(int i=0;i<strPids.length;i++){
					pids.add(strPids[i]);
				}
			}
			String[] placement_list = new String[pids.size()];
			for(int k=0;k<pids.size();k++) {
				placement_list[k] = pids.get(k);

			}


			vunglePub.init(mAct, APP_ID, placement_list, new VungleInitListener() {
				@Override
				public void onSuccess() {
					try {
						final AdConfig globalAdConfig = vunglePub.getGlobalAdConfig();
						globalAdConfig.setSoundEnabled(true);
						globalAdConfig.setOrientation(Orientation.autoRotate);
						vunglePub.clearAndSetEventListeners(vungleListener);

						if (vunglePub != null && vunglePub.isInitialized()) {
							// Load an ad using a Placement ID
							vunglePub.loadAd(PLACEMENT_ID);
						} else {
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
						}
					}catch (Exception e){
						e.printStackTrace();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
					}
				}

				@Override
				public void onFailure(Throwable throwable) {
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
				}
			});

			try {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						stopTimer();
						if (isActive) {

							isActive = false;
							if ((vunglePub != null) && (vunglePub.isAdPlayable(PLACEMENT_ID))) {
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle TimerTask Adready");
								adEventReady(null);
							} else {
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle TimerTask Ad Failed");
								adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
							}
						}
					}
				}, (long) (iAdTimeout * 1000), (long) (iAdTimeout * 1000));
			} catch (Exception e) {
				e.printStackTrace();
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			}


		}catch (Exception e){
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}


	public void stopTimer() {
		try{
			if (null != timer) {
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Stop Timer");
					timer.cancel();
					timer.purge();
				} catch (Exception e) {
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private final VungleAdEventListener vungleListener = new VungleAdEventListener() {
		@Override
		public void onAdEnd(String s, boolean b, boolean b1) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Event Completed:::"+b+":::"+b1);
			stopTimer();
			isActive = false;
			AdmofiView.percentVideoWatched = 100;
			adEventCompleted();
		}

		@Override
		public void onAdStart(String s) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Event ad started");
		}

		@Override
		public void onUnableToPlayAd(String s, String s1) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Event Failed");
			if (isActive) {
				isActive = false;
				stopTimer();
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				return;
			}
		}

		@Override
		public void onAdAvailabilityUpdate(String s, boolean b) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Event AdReady");
			if (isActive) {
				isActive = false;
				stopTimer();
				adEventReady(null);
			}
		}
	};
	

	@Override
	public boolean showinterstitial() {

		try{
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Vungle Show Inter");
			AdmofiView.percentVideoWatched = 0;
			isActive = false;

			if ( PLACEMENT_ID != null && !PLACEMENT_ID.equals("") && getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
				try {
					if ((vunglePub != null) && (vunglePub.isAdPlayable(PLACEMENT_ID))) {
						final AdConfig overrideConfig = new AdConfig();
						overrideConfig.setSoundEnabled(true);
						vunglePub.playAd(PLACEMENT_ID,overrideConfig);
						adEventImpression();
						return true;
					}
				} catch (Exception e) {
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					return false;
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public void onAdmPause() {
		super.onAdmPause();
		if (null != vunglePub) {
			vunglePub.onPause();
		}
	}
	@Override
	public void onAdmResume() {
		super.onAdmResume();
		if (null != vunglePub) {
			vunglePub.onResume();
		}
	}

	@Override
	public void vAdmofiCleanup() {
		try {
			if(vunglePub!=null){
				vunglePub = null;
				if(vungleListener != null)
					vunglePub.removeEventListeners(vungleListener);
			}
		} catch (Exception e) {
		}
		super.vAdmofiCleanup();
	}
}
