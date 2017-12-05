package com.admofi.sdk.lib.and.adapters;

import seventynine.sdk.DisplayAds;
import seventynine.sdk.SeventynineAdSDK;
import seventynine.sdk.SeventynineAdSDK.SeventynineCallbackListener;
import seventynine.sdk.SeventynineConstants;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;



import java.util.Timer;
import java.util.TimerTask;

public class CustomAdapterseventyninesdkv extends CustomAdapterImpl implements SeventynineCallbackListener  {
	
			
	SeventynineAdSDK seventynineAdSDK=null;
	Context iContext=null;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;
	String zoneId="";
	public static boolean isSeventySDKReady = true;
	
	public CustomAdapterseventyninesdkv(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {			
			Class.forName("seventynine.sdk.SeventynineAdSDK");
		
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		iContext = super.mContext;
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		try {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}
	
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {
			 AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev Event LoadInter");
			 SeventynineConstants.strPublisherId =mAdCurrent.getAdapterKey(0);
			 SeventynineConstants.appContext = context;
			 seventynineAdSDK = new SeventynineAdSDK();
			 
			 if(CustomAdapterseventyninesdkv.isSeventySDKReady || CustomAdapterseventyninesdkvi.isSeventySDKReady){
				 CustomAdapterseventyninesdkv.isSeventySDKReady=false;
				 CustomAdapterseventyninesdkvi.isSeventySDKReady=false;
				 seventynineAdSDK.init(context);				 
			 }
			 
			 try {
				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							if(seventynineAdSDK!=null && iContext!=null && seventynineAdSDK.isAdReady(zoneId, iContext,"","mid")){
								stopTimer();
								adEventReady(null);
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi seventy ninev Timer Event Ad available");								
							} else {
								stopTimer();
								adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi seventy ninev Timer Event Ad failed");	
							}
						} catch (Exception e) {
							e.printStackTrace();
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
						}
					}
				}, (long)(1000 * iAdTimeout), (long)(1000 * iAdTimeout));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi seventy ninev Event Load Interstitial Exception :: "+e.getLocalizedMessage());
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}
	
	private Timer timer = null;
	public void stopTimer() {
		if (null != timer) {
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi seventy ninev Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
		
	@Override
	public boolean showinterstitial() {
		try {	
			AdmofiView.percentVideoWatched = 0;
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev Event showinterstitial");
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && seventynineAdSDK!=null && iContext!=null && seventynineAdSDK.isAdReady(zoneId, iContext,"","mid")){
				SeventynineConstants.strSkipButtonByDeveloper = "5";
				seventynineAdSDK.setCallbackListener(this);			
				Intent intent = new Intent();
				intent.setClass(iContext,DisplayAds.class);
				iContext.startActivity(intent);
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return false;
		}		
	}
	
	@Override
	public void onAdClick() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev Event onInterstitialClicked :: ");
		adEventClicked();
	}
	@Override
	public void onAdFinished() {
		AdmofiView.percentVideoWatched = 100;
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev  Event onAdFinished");
		adEventCompleted();
		
	}
	@Override
	public void onAdStarted() {
		
		
	}
	@Override
	public void onClose() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev Event onClose ::");
		adEventCompleted();
		
	}
	@Override
	public void onErrorReceived() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Seventy Ninev Event onErrorReceived ::");
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);	
		
	}
	@Override
	public void onSkipEnable() {
		
		
	}
	@Override
	public void onVideoView25() {
		AdmofiView.percentVideoWatched = 25;
		
	}
	@Override
	public void onVideoView50() {
		
		AdmofiView.percentVideoWatched = 50;
	}
	@Override
	public void onVideoView75() {
		AdmofiView.percentVideoWatched = 75;
		
	}
}