package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging.Level;
import com.chartboost.sdk.Model.CBError.CBClickError;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

public class CustomAdapterchartboostoi extends CustomAdapterImpl {
	
	private Activity act = null;
	String TAG = "chartboost";
	
	public CustomAdapterchartboostoi(Context context) {
		super(context);		
	}
	public void loadAd(Handler loadingCompletedHandler,  AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {			
			Class.forName("com.chartboost.sdk.Chartboost");			
				
		} catch (Exception e) {			
			super.setSupported(false);			
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);		
		act = (Activity) super.mContext;
		
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implementeds
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
		
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ChartBoostma loadInterstitial");
			Chartboost.startWithAppId((Activity)context, mAdCurrent.getAdapterKey(0), mAdCurrent.getAdapterKey(1));
			Chartboost.setLoggingLevel(Level.ALL);
			Chartboost.setDelegate(delegate);
			Chartboost.onCreate((Activity)context);
			Chartboost.onStart(act);
			Chartboost.cacheMoreApps(CBLocation.LOCATION_GAME_SCREEN);
		} catch (Exception e) {
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}
	
	@Override
	public boolean showinterstitial() {
		if(getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {			
			
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ChartBoostma ShowInter");
			Chartboost.showMoreApps(CBLocation.LOCATION_GAME_SCREEN);			
			adEventImpression();
			return true;			
		}
		return false;
	}
	
	@Override
	public void onAdmStart() {		
		super.onAdmStart();	
		//Chartboost.onStart(act);
	}
	
	@Override
	public void onAdmStop() {
		super.onAdmStop();
		Chartboost.onStop(act);
	}
	@Override
	public void onAdmCreate() {
		super.onAdmCreate();		
	}
	
	private ChartboostDelegate delegate = new ChartboostDelegate() {

		@Override
		public boolean shouldRequestInterstitial(String location) {
					
			return true;
		}
	
		@Override
		public boolean shouldDisplayInterstitial(String location) {
			
			return true;
		}
	
		@Override
		public void didCacheInterstitial(String location) {
			
		}
	
		@Override
		public void didFailToLoadInterstitial(String location, CBImpressionError error) {
			
		}
	
		@Override
		public void didDismissInterstitial(String location) {
			
		}
	
		@Override
		public void didCloseInterstitial(String location) {
			
		}
	
		@Override
		public void didClickInterstitial(String location) {
			
		}
	
		@Override
		public void didDisplayInterstitial(String location) {
			
		}
	
		@Override
		public boolean shouldRequestMoreApps(String location) {
			
			return true;
		}
	
		@Override
		public boolean shouldDisplayMoreApps(String location) {
			
			return true;
		}
	
		@Override
		public void didFailToLoadMoreApps(String location, CBImpressionError error) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi ChartBoostma didFailToLoadMoreApps :: "+error.name());
			if(error == CBImpressionError.NO_AD_FOUND){		
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			} else {
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);				
			}
		}
	
		@Override
		public void didCacheMoreApps(String location) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi ChartBoostma didCacheMoreApps :: "+location);
			adEventReady(null);
			
		}
	
		@Override
		public void didDismissMoreApps(String location) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi ChartBoostma didDismissMoreApps :: "+location);
			adEventCompleted();
			
		}
	
		@Override
		public void didCloseMoreApps(String location) {
			
		}
	
		@Override
		public void didClickMoreApps(String location) {
			adEventClicked();
			
		}
	
		@Override
		public void didDisplayMoreApps(String location) {
			
		}
	
		@Override
		public void didFailToRecordClick(String uri, CBClickError error) {
					
		}
	
		@Override
		public boolean shouldDisplayRewardedVideo(String location) {
			
			return true;
		}
	
		@Override
		public void didCacheRewardedVideo(String location) {
		
		}
	
		@Override
		public void didFailToLoadRewardedVideo(String location,
				CBImpressionError error) {
		
			
		}
	
		@Override
		public void didDismissRewardedVideo(String location) {
		
		}
	
		@Override
		public void didCloseRewardedVideo(String location) {
			
		}
	
		@Override
		public void didClickRewardedVideo(String location) {
			
		}
	
		@Override
		public void didCompleteRewardedVideo(String location, int reward) {
			
		}
		
		@Override
		public void didDisplayRewardedVideo(String location) {
			
		}
		
		@Override
		public void willDisplayVideo(String location) {
			
		}
		
	};
	
	}

