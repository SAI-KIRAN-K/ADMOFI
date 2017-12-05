package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;

import java.util.Timer;
import java.util.TimerTask;


public class CustomAdapterunityi extends CustomAdapterImpl /*implements IUnityAdsListener*/ {

	private Context mContext = null;
	private Activity mActivity = null;
	private String ZONE_ID = "";
	private String GAME_ID = "";
	private boolean isActive = true;
	private Timer timer = null;
	String appid = "";
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;
	public int UNITY_REWARD_POINTS = 1;


	public CustomAdapterunityi(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView,
					   AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.unity3d.ads.UnityAds");

		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		appid = madView.getAppID();
		this.mContext = super.mContext;
		UNITY_REWARD_POINTS = mAdShown.getCPVValue();
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
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Load Inter");
			GAME_ID = mAdCurrent.getAdapterKey(0);
			ZONE_ID = mAdCurrent.getAdapterKey(1);
			UnityAds.initialize((Activity)context, GAME_ID,new IUnityAdsListener() {

				@Override
				public void onUnityAdsError(UnityAdsError error, String message) {

				}

				@Override
				public void onUnityAdsFinish(String zoneid, FinishState state) {

				}

				@Override
				public void onUnityAdsReady(String zoneId) {
				}

				@Override
				public void onUnityAdsStart(String zoneId) {
				}
			});
			//UnityAds.changeActivity(mActivity);			

			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					
					try {
						stopTimer();
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Timer");
						if(isActive){
							isActive = false;
							if(UnityAds.isReady()){
								AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Timer Ad Ready");

								adEventReady(null);
							} else {
								AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Timer Failed Ad");
								adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
							}
						}
					}catch(Exception e) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Timer Failed with exception :: "+e);
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					}
					
					
				}
			}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Load Inter excepton "+e.getMessage());

			isActive = false;
			stopTimer();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);

		}
	}

	public void stopTimer() {
		if (null != timer) {
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean showinterstitial() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unityi incentive Event ShowInter");

		AdmofiView.percentVideoWatched = 0;
		setUnityListener();
		//UnityAds.setZone(ZONE_ID);
		if(UnityAds.isReady() && UnityAds.isReady(ZONE_ID)) {
			adEventImpression();
			stopTimer();
			isActive = false;
			//UnityAds.setZone(ZONE_ID);
			UnityAds.show(mActivity, ZONE_ID);
			return true;
		}
		return false;
	}

	private void setUnityListener() {
		UnityAds.setListener(new IUnityAdsListener() {

			@Override
			public void onUnityAdsError(UnityAdsError arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnityAdsFinish(String arg0, FinishState arg1) {
				
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unityi incentive Event onVideoCompleted :: "+arg1);
					
					if(isActive) {			
						isActive = false;
						stopTimer();
					}
					
					stopTimer();
					if(arg1 == FinishState.COMPLETED) {
						//AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unityi checked");
						AdmofiView.percentVideoWatched = 100;
						adEventRewardSuccess(new AdmofiReward("unity reward", UNITY_REWARD_POINTS ,true,"unity Reward Success"));
					}
					
					adEventCompleted();
					
				} catch (Exception e) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi unityi incentive onUnityAdsFinish exception :: "+e);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
				}
				
				

			}

			@Override
			public void onUnityAdsReady(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onUnityAdsStart(String arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onAdmDestroy() {
		super.onAdmDestroy();	
		/*if(mActivity!=null)
			 UnityAds.changeActivity(mActivity);*/
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi unityi incentive Event destroy");
	}

	@Override
	public void onAdmPause() {
		super.onAdmPause();
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