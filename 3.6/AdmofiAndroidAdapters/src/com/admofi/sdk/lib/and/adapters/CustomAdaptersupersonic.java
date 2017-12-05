package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.sdk.SSAFactory;


public class CustomAdaptersupersonic extends CustomAdapterImpl {
			
	
    private String applicationKey = "";
	Activity mAct = null;
	private static boolean interstitialInitiated = false;
	private final String FALLBACK_USER_ID = "userId";
	private static String advertisingId = "100";
	private static String ADVT_ID = "100";
	private String API_KEY = "";
	
	public CustomAdaptersupersonic(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic class invoked");
			Class.forName("com.ironsource.mediationsdk.IronSource");
			Class.forName("com.ironsource.adapters.supersonicads.SupersonicConfig");
			Class.forName("com.ironsource.sdk.SSAFactory");

		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}

		super.setSupported(true);
		
		applicationKey = mAdShown.getAdapterKey(0);
		mContext = super.mContext;
		mAct = (Activity)super.mContext;
		API_KEY = mAdShown.getAdapterKey(0);
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) 
		{
			loadBanner(super.mContext, mAdShown);
		} 
		else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) 
		{
			loadInterstitial(super.mContext, mAdShown);
		} 
		else 
		{
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitial(final Context context, final AdmofiAd mAdCurrent) {
		try {
			
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event Load Interstitial");

			//getting advertising id
			if(!CustomAdaptersupersonic.advertisingId.equalsIgnoreCase(CustomAdaptersupersonic.ADVT_ID)) {
				requestInterstitial();
			}else{

				AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						return IronSource.getAdvertiserId(mAct);
					}
					@Override
					protected void onPostExecute(String advertId) {
						if (TextUtils.isEmpty(advertId)) {
							advertisingId = FALLBACK_USER_ID;
						}else{
							advertisingId = advertId;
						}
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic adv id::" + advertId);
						SSAFactory.getAdvertiserInstance().reportAppStarted(mAct);
						requestInterstitial();
					}
				};
				task.execute();
			}
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event Load Interstitial Exception :: "+e.getLocalizedMessage());
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}


	private void requestInterstitial(){
		try{
			SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
			IronSource.setInterstitialListener(mInterstitialListener);
			IronSource.setUserId(advertisingId);

			if(CustomAdaptersupersonic.interstitialInitiated) {
				if(IronSource.isInterstitialReady()){
					adEventReady(null);
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event Ad available");
				} else {
					IronSource.loadInterstitial();
				}
			}else {
				CustomAdaptersupersonic.interstitialInitiated = true;
				IronSource.init(mAct,API_KEY);
				IronSource.loadInterstitial();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void stopTimer() {
		
	}
	
	@Override
	public void onAdmResume() {		
		IronSource.onResume(mAct);
		super.onAdmResume();
	}
	
	@Override
	public void onAdmPause() {
		IronSource.onPause(mAct);
		super.onAdmPause();
	}
	
	@Override
	public void onAdmDestroy() {
		super.onAdmDestroy();
	}
	
	@Override
	public boolean showinterstitial() {	
		try {
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) &&  (IronSource.isInterstitialReady())) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event showInterstitial");
				IronSource.showInterstitial();
				adEventImpression();
				return true;
			}			
		} catch (Exception e) {
			return false;
		}		
		return false;
	}


	InterstitialListener mInterstitialListener = new InterstitialListener() {
		@Override
		public void onInterstitialAdReady() {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialAvailability :: " + "true");
			adEventReady(null);
		}

		@Override
		public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {			
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialInitFail :: " + ironSourceError.toString());

		}

		@Override
		public void onInterstitialAdOpened() {			
			adEventImpression();
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialShowSuccess");
		}

		@Override
		public void onInterstitialAdClosed() {
			adEventCompleted();
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialAdClosed");

		}

		@Override
		public void onInterstitialAdShowSucceeded() {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialInitSuccess ");
		}

		@Override
		public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialShowFail :: "+ironSourceError.toString());

		}

		@Override
		public void onInterstitialAdClicked() {
			adEventClicked();
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event onInterstitialAdClicked ");

		}
	};

}