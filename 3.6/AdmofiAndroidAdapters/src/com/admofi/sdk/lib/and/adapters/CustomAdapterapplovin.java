package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinSdk;


public class CustomAdapterapplovin extends CustomAdapterImpl {
	  
	 private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	 private boolean isActive = true;	 
	 private Timer timer = null;
	 AppLovinInterstitialAdDialog adDialog = null;
	 
	public CustomAdapterapplovin(Context context) {
		super(context);
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.applovin.sdk.AppLovinSdk");
						
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		
		super.setSupported(true);		 
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			isActive = false;
			loadBanner(super.mContext, mAdShown);
			
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent){
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);		
	}
		
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent){
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin loadInterstitial called");			
			AppLovinSdk.initializeSdk(context);
			
			adDialog = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(context),(Activity) context);
			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					stopTimer();
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin Timer");
					if(isActive){					
						isActive = false;
						 if((adDialog!=null) &&(adDialog.isAdReadyToDisplay())){
							 AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin Timer Ad Ready");
							adEventReady(null); 
						 } else {
							 AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin Timer Failed Ad");
						 	adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
						 }
					}
				}
			}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
			
			// Alternatively, you can create a new listener.
			adDialog.setAdDisplayListener(new AppLovinAdDisplayListener() {
			    
				@Override
				public void adDisplayed(AppLovinAd arg0) {
					
					
				}
				@Override
				public void adHidden(AppLovinAd arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin adHidden called");	
					adEventCompleted();
				}
			});
			
			adDialog.setAdLoadListener(new AppLovinAdLoadListener() {
				
				@Override
				public void failedToReceiveAd(int arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin failedToReceiveAd called :: "+arg0);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				}
				
				@Override
				public void adReceived(AppLovinAd arg0) {	
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin adReceived called");
					adEventReady(null);
				}
			});
			
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin loadInterstitial failed with exception "+e);
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}	
	}
	    
		@Override
		public boolean showinterstitial() {
			isActive = false;			
			stopTimer();
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (adDialog!=null) && (adDialog.isAdReadyToDisplay())) {				
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin showinterstitial ");
				adDialog.show();
				adEventImpression();
				return true;
			}
			
			return false;
		}
		

		public void stopTimer() {
			if (null != timer) {
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Applovin Stop Timer");
					timer.cancel();
					timer.purge();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
}