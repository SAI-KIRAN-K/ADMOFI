package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class CustomAdapteradcolony extends CustomAdapterImpl {
	
	 private String APP_ID  = "";
	 private String ZONE_ID = "";
	 private Activity ac = null;	 
	 private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	 private boolean isActive = true;
	 private Timer timer = null;
	 
	 private AdColonyInterstitialListener  listener = null;
	 private AdColonyInterstitial ad = null;
	 
	public CustomAdapteradcolony(Context context) {
		super(context);
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony class invoked");
			Class.forName("com.adcolony.sdk.AdColony");
			Class.forName("com.adcolony.sdk.AdColonyInterstitialListener");
			Class.forName("com.adcolony.sdk.AdColonyInterstitial");					
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
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony loadInterstitialV called");
			ac = (Activity)context;
			AdColonyAppOptions app_options = new AdColonyAppOptions().setUserID( "version:1.0,store:google" );
			AdColony.configure( ac, app_options, APP_ID, ZONE_ID );
			
			//avatharam added,updated version
			listener = new AdColonyInterstitialListener() {
				@Override
				public void onRequestFilled(AdColonyInterstitial arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony AdReady");
					if(isActive){
						isActive = false;
						stopTimer();
						if (arg0 != null){
							ad = arg0;
							adEventReady(null);
						} else {
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony AdFailed");
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
						}
					}
				}
				@Override
				public void onRequestNotFilled(AdColonyZone zone) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony onRequestNotFilled :: ");
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				}
				@Override
				public void onExpiring(AdColonyInterstitial ad) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Ad Expired");
					isActive = false;
					stopTimer();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}
				
				@Override
				public void onOpened(AdColonyInterstitial ad) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Ad Opened");
					isActive = false;
					stopTimer();
				}
				
				@Override
				public void onClicked(AdColonyInterstitial ad) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony AdClicked");
					adEventClicked();
				}
				
				@Override
				public void onClosed(AdColonyInterstitial ad) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony AdCompleted");
					AdmofiView.percentVideoWatched = 100;
					adEventCompleted();
				}
			};
			
			AdColony.requestInterstitial( ZONE_ID, listener,null);
			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					stopTimer();
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Timer");
					if(isActive){					
						isActive = false;
						 if((ad != null)){
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Timer Ad Ready");
							adEventReady(null); 
						 } else {
							 AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Timer Failed Ad");
						 	adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
						 }
					}
				}
			}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony loadInterstitialV failed with exception "+e);
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}	
	}
	    
		@Override
		public boolean showinterstitial() {
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (ad != null)) {				
				try{
					new Runnable() {
						public void run() {
							isActive = false;
							AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony showinterstitial called");
							AdmofiView.percentVideoWatched = 0;							
							ad.show();
						}
					}.run();
					adEventImpression();
					return true;
				}catch(Exception e) {
					isActive = false;
					stopTimer();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					return false;
				}
			}
			return false;
		}

		@Override
		public void onAdmPause() {
			super.onAdmPause();

		}

		@Override
		public void onAdmResume() {
			super.onAdmResume();
		}


	public void stopTimer() {
			if (null != timer) {
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Adcolony Stop Timer");
					timer.cancel();
					timer.purge();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
				
}