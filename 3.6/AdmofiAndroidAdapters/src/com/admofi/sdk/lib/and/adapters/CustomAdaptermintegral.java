package com.admofi.sdk.lib.and.adapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.mobvista.msdk.MobVistaConstans;
import com.mobvista.msdk.MobVistaSDK;
import com.mobvista.msdk.out.InterstitialListener;
import com.mobvista.msdk.out.MVInterstitialHandler;
import com.mobvista.msdk.out.MobVistaSDKFactory;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class CustomAdaptermintegral extends CustomAdapterImpl {

	 private String APP_ID  = "";
	 private String AD_UNIT_ID = "";
	 private String API_KEY = "";
	 private Activity ac = null;	 
	 private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	 private Timer timer = null;
	 
	 private MVInterstitialHandler mInterstitialHandler;
	 private boolean isAdReady = false;
	 
	 private static boolean isInitialised = false;
	
	public CustomAdaptermintegral(Context ctx) {
		super(ctx);
		
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista class invoked");
			Class.forName("com.mobvista.msdk.MobVistaSDK");
			Class.forName("com.mobvista.msdk.out.MobVistaSDKFactory");
			Class.forName("com.mobvista.msdk.out.MVInterstitialHandler");	
			Class.forName("com.mobvista.msdk.MobVistaConstans");
			
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		
		APP_ID = mAdShown.getAdapterKey(0);
		API_KEY = mAdShown.getAdapterKey(1);
		AD_UNIT_ID = mAdShown.getAdapterKey(2);
		
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		super.setSupported(true);		 
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
			
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitialV(super.mContext, mAdShown);
			
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent){
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);		
	}
	
	private void loadInterstitialV(Context context, AdmofiAd mAdCurrent){
		
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista load Interstitial called");
			ac = (Activity)context;
			
			if(!isInitialised){
				CustomAdaptermintegral.isInitialised = true;
				MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
				Map<String, String> map = sdk.getMVConfigurationMap(APP_ID, API_KEY);
				sdk.init(map, ac);
				
			}
			
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put(MobVistaConstans.PROPERTIES_UNIT_ID, AD_UNIT_ID);
			mInterstitialHandler = new MVInterstitialHandler(ac.getApplicationContext(), hashMap);
			mInterstitialHandler.setInterstitialListener(new InterstitialListener() {
				
				@Override
				public void onInterstitialShowSuccess() {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista Interstitial Ad Opened");
					adEventImpression();
				}
				
				@Override
				public void onInterstitialShowFail(String errorMsg) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista show Interstitial Ad Failed::"+errorMsg);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}
				
				@Override
				public void onInterstitialLoadSuccess() {
					isAdReady = true;
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista Interstitial Ad Ready:::"+isAdReady);
					adEventReady(null);
				}
				
				@Override
				public void onInterstitialLoadFail(String errorMsg) {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista load Interstitial Ad Failed::"+errorMsg);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				}
				
				@Override
				public void onInterstitialClosed() {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista Interstitial Ad Closed");
					adEventCompleted();
				}
				
				@Override
				public void onInterstitialAdClick() {
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista Interstitial Ad Clicked");
					adEventClicked();
				}
				
			});
			mInterstitialHandler.preload();
			
		}catch (Exception e) {
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
				
	}
	
	@Override
	public boolean showinterstitial() {
		
		AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvista showinterstitial called::: ad ready status::"+isAdReady);
		if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (mInterstitialHandler != null) && isAdReady ) {				
			try {
				isAdReady = false;
				mInterstitialHandler.show();
				/*new Runnable() {
					public void run() {
						
					}
				}.run();*/
				return true;
			}catch(Exception e) {
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
	public void onAdmDestroy() {
		super.onAdmDestroy();
		CustomAdaptermintegral.isInitialised = false;
	}
	
	@Override
	public void onAdmResume() {
		super.onAdmResume();
	}
	
	
}
