package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.Timer;
import java.util.TimerTask;


public class CustomAdapterunity extends CustomAdapterImpl /*implements IUnityAdsListener*/ {

	private Context mContext = null;
	private Activity mActivity = null;
	private String ZONE_ID = "";
	private String GAME_ID = "";
	private boolean isActive = true;
	private Timer timer = null;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;

	public CustomAdapterunity(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView,
					   AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.unity3d.ads.UnityAds");

		} catch (Exception  e) {
			super.setSupported(false);
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity exxxxxxxxxxxx  "+e);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}

		super.setSupported(true);
		this.mContext = super.mContext;
		mActivity = (Activity)this.mContext;

		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			isActive = false;
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			isActive = false;
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}

	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {

			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Unity Ad Load Inter");
			GAME_ID = mAdCurrent.getAdapterKey(0);
			ZONE_ID = mAdCurrent.getAdapterKey(1);
			UnityAds.initialize((Activity) context, GAME_ID, new IUnityAdsListener() {

				@Override
				public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {

				}

				@Override
				public void onUnityAdsFinish(String zoneid, UnityAds.FinishState state) {

				}

				@Override
				public void onUnityAdsReady(String zoneId) {

				}

				@Override
				public void onUnityAdsStart(String zoneId) {
				}
			});

			//avatharam commented ,there is no changeactivity in newer version
			//UnityAds.changeActivity(mActivity);			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					stopTimer();
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity Timer");
					if(isActive){
						isActive = false;
						if((UnityAds.isReady()) && (UnityAds.isReady(ZONE_ID))){
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity Timer Ad Ready");
							adEventReady(null);
						} else {
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity Timer Failed Ad");
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
						}
					}
				}
			}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity Load Inter excepton "+e.getMessage());

			isActive = false;
			stopTimer();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);

		}
	}

	public void stopTimer() {
		if (null != timer) {
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unity Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean showinterstitial() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unity Event ShowInter");
		AdmofiView.percentVideoWatched = 0;
		setUnityListener();

		//avatharam commented,
		//UnityAds.setZone(ZONE_ID);
		if(UnityAds.isReady() && UnityAds.isReady(ZONE_ID)) {
			adEventImpression();
			stopTimer();
			isActive = false;
			//UnityAds.setZone(ZONE_ID);
			UnityAds.show(mActivity,ZONE_ID);
			return true;
		}
		return false;
	}

	private void setUnityListener() {
		UnityAds.setListener(new IUnityAdsListener() {

			@Override
			public void onUnityAdsError(UnityAds.UnityAdsError arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnityAdsFinish(String arg0, UnityAds.FinishState arg1) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unity Event onHide(Admofi Ad completed)");
				if(isActive){
					AdmofiView.percentVideoWatched = 100;
					isActive = false;
					stopTimer();
				}
				adEventCompleted();
			}

			@Override
			public void onUnityAdsReady(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnityAdsStart(String arg0) {
				// TODO Auto-generated method stub

			}
			
			/*@Override
			public void onFetchCompleted() {
				
			}

			@Override
			public void onFetchFailed() {
				
			}

			@Override
			public void onHide() {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unity Event onHide(Admofi Ad completed)");
				if(isActive){
					AdmofiView.percentVideoWatched = 100;
					 isActive = false;
					 stopTimer();					 		
				}				
				adEventCompleted();
			}

			@Override
			public void onShow() {
				
			}

			@Override
			public void onVideoCompleted(String arg0, boolean arg1) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unity Event onVideoCompleted");
					 stopTimer();		
			}

			@Override
			public void onVideoStarted() {
						
			}	*/
		});
	}

	@Override
	public void onAdmDestroy() {
		super.onAdmDestroy();
		//avatharam commented
		/*if(mActivity!=null)
			 UnityAds.changeActivity(mActivity);*/
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unity Event destroy");
	}

	@Override
	public void onAdmPause() {
		super.onAdmPause();		
		//avatharam commented
		/*if(mActivity!=null)
			 UnityAds.changeActivity(mActivity);*/
	}

	@Override
	public void onAdmResume() {
		super.onAdmResume();
		/*if(mActivity!=null)
		 UnityAds.changeActivity(mActivity);*/
	}


}