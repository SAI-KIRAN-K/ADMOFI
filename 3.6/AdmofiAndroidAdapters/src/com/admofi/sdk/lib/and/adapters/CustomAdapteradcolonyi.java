package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.offers.AdmofiReward;

public class CustomAdapteradcolonyi extends CustomAdapterImpl {
	
	 private String APP_ID  = "";
	 private String ZONE_ID = "";
	 private Activity ac = null;	 
	 private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	 private boolean isActive = true;
	 private Timer timer = null;

	private AdColonyInterstitialListener listener = null;
	private AdColonyInterstitial ad = null;
	 
	public CustomAdapteradcolonyi(Context context) {
		super(context);
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony incentive class invoked");
			Class.forName("com.adcolony.sdk.AdColony");
			Class.forName("com.adcolony.sdk.AdColonyInterstitialListener");
			Class.forName("com.adcolony.sdk.AdColonyInterstitial");
			Class.forName("com.adcolony.sdk.AdColonyRewardListener");

		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		
		APP_ID = mAdShown.getAdapterKey(0);
		ZONE_ID = mAdShown.getAdapterKey(1);
		
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		
		super.setSupported(true);		 
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			isActive = false;
			loadBanner(super.mContext, mAdShown);
			
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitialV(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent){
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);		
	}
	
	
	private void loadInterstitialV(Context context, AdmofiAd mAdCurrent){				    
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Adcolony Incent loadInterstitialV");
				ac = (Activity)context;
				AdColonyAppOptions app_options = new AdColonyAppOptions().setUserID("version:1.0,store:google");
				AdColony.configure(ac, app_options, APP_ID, ZONE_ID);
				//avatharam added,updated version


				listener = new AdColonyInterstitialListener() {
					@Override
					public void onRequestFilled(AdColonyInterstitial arg0) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdReady");
						if(isActive){
							isActive = false;
							stopTimer();
							if (arg0 != null){
								ad = arg0;
								adEventReady(null);
							} else {
								AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdFailed");
								adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
							}
						}
					}
					@Override
					public void onRequestNotFilled(AdColonyZone zone) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent onRequestNotFilled");
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
					}
					@Override
					public void onExpiring(AdColonyInterstitial ad) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdExpired");
						isActive = false;
						stopTimer();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
					}

					@Override
					public void onOpened(AdColonyInterstitial ad) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdOpened");
						isActive = false;
						stopTimer();
					}

					@Override
					public void onClicked(AdColonyInterstitial ad) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdClicked");
						adEventClicked();
					}

					@Override
					public void onClosed(AdColonyInterstitial ad) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent AdCompleted");
						AdmofiView.percentVideoWatched = 100;
						//adEventCompleted();
					}
				};

				AdColony.setRewardListener(new AdColonyRewardListener() {
					@Override
					public void onReward(AdColonyReward adColonyReward) {
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent reward");
						AdmofiReward madmofiReward = null;
						if (adColonyReward!=null && adColonyReward.success()) {
							AdmofiView.percentVideoWatched = 100;
							madmofiReward = new AdmofiReward(adColonyReward.getRewardName(), adColonyReward.getRewardAmount() ,true,"admofi adcolony reward success");
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent rewarded::"+adColonyReward.getRewardName()+":::"+adColonyReward.getRewardAmount());
							adEventRewardSuccess(madmofiReward);
						} else {
							madmofiReward = new AdmofiReward("NA", 0,false,"admofi adcolony reward failed");
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent reward failed");
							adEventRewardFailed(madmofiReward);
						}
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent adEventCompleted");
						adEventCompleted();
					}
				});


				AdColony.requestInterstitial(ZONE_ID, listener, null);
				//avatharam added ending

				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						stopTimer();
						AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent Timer");
						if(isActive){
							isActive = false;
							 if((ad != null)){
								 AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent Timer Ad Ready");
								adEventReady(null); 
							 } else {
								 AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent Timer Failed Ad");
							 	adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
							 }
						}
					}
				}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
			} catch (Exception e) {
				e.printStackTrace();
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			}
		}
	    
		@Override
		public boolean showinterstitial() {
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (ad != null) ) {
				try{
					new Runnable() {
						public void run() {
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent showinterstitial");
							AdmofiView.percentVideoWatched = 0;
							isActive = false;
							ad.show();
						}
					}.run();
					adEventImpression();
					return true;
				}catch(Exception e){
					isActive = false;
					stopTimer();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					return false;
				}
			}
			return false;
		}

		public void stopTimer() {
			if (null != timer) {
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Incent Stop Timer");
					timer.cancel();
					timer.purge();
				} catch (Exception e) {
				}
			}
		}
		
		@Override
		public void onAdmPause() {			
			super.onAdmPause();			

		}
		
		@Override
		public void onAdmResume() {
			super.onAdmResume();
			if(ac!=null){

			}
		}



		/*@Override
		public void onReward(AdColonyReward adColonyReward) {

		}*/
}
