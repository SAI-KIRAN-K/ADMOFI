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
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.sdk.SSAFactory;

public class CustomAdaptersupersonicv extends CustomAdapterImpl {
			
	private String userId = "";
    public static boolean rewardedVideoInitSuccess = false;
	Activity mAct = null;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;
	private final String FALLBACK_USER_ID = "userId";

	private static String advertisingId = "100";
	private static String ADVT_ID = "100";
	
	public CustomAdaptersupersonicv(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.ironsource.adapters.supersonicads.SupersonicConfig");
			Class.forName("com.ironsource.mediationsdk.IronSource");
			Class.forName("com.ironsource.mediationsdk.model.Placement");
		
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		userId = madView.getUniqId();
		
		mContext = super.mContext;
		mAct = (Activity)super.mContext;
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
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
	
	private void loadInterstitial(Context context,final AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicv Event Load Interstitial");

			if(!advertisingId.equalsIgnoreCase(CustomAdaptersupersonicv.ADVT_ID)){
				requestInterstitial(mAdCurrent.getAdapterKey(0),advertisingId);
			}else{
				AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						return IronSource.getAdvertiserId(mAct);
					}
					@Override
					protected void onPostExecute(String advertisingId) {
						if (TextUtils.isEmpty(advertisingId)) {
							advertisingId = FALLBACK_USER_ID;
						}
						SSAFactory.getAdvertiserInstance().reportAppStarted(mAct);
						requestInterstitial(mAdCurrent.getAdapterKey(0),advertisingId);
					}
				};
				task.execute();
			}

		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicv Event Load Interstitial Exception :: "+e.getLocalizedMessage());
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}


	private void requestInterstitial(String apikey,String advId){
		try{
			IronSource.setRewardedVideoListener(rewardListener);
			SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
			IronSource.setUserId(advId);

			if(CustomAdaptersupersonicv.rewardedVideoInitSuccess){
				if( IronSource.isRewardedVideoAvailable()){
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Event videoadready ");
					adEventReady(null);
				} else {
					starttimer();
				}
			}else{
				CustomAdaptersupersonicv.rewardedVideoInitSuccess = true;
				IronSource.init(mAct, apikey);
				starttimer();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	
	private void starttimer(){
		try {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(IronSource.isOfferwallAvailable()){
						stopTimer();
						adEventReady(null);
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Timer Event Ad available");
					} else {
						stopTimer();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Timer Event Ad failed");
					}
				}
			}, (long)(1000 * iAdTimeout), (long)(1000 * iAdTimeout));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Timer timer = null;
	public void stopTimer() {
		if (null != timer) {
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi supersonicv Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	public void onAdmBackPressed() {
				
	}
		
	@Override
	public boolean showinterstitial() {
		try {			
			AdmofiView.percentVideoWatched = 0;
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) &&(IronSource.isRewardedVideoAvailable())) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicv Event showInterstitial");
				IronSource.showRewardedVideo();
				return true;
			}			
		} catch (Exception e) {
			return false;
		}		
		return false;
	}
	RewardedVideoListener rewardListener = new RewardedVideoListener() {
		@Override
		public void onRewardedVideoAdOpened() {

		}

		@Override
		public void onRewardedVideoAdClosed() {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Event onRewardedVideoAdClosed  ");
			adEventCompleted();
		}

		@Override
		public void onRewardedVideoAvailabilityChanged(boolean b) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Event onVideoAvailabilityChanged :: "+b);
		}

		@Override
		public void onRewardedVideoAdStarted() {

		}

		@Override
		public void onRewardedVideoAdEnded() {

		}

		@Override
		public void onRewardedVideoAdRewarded(Placement placement) {
			try {
				String rewardName = placement.getRewardName();
				int rewardAmount = placement.getRewardAmount();
				AdmofiView.percentVideoWatched = 100;
				adEventRewardSuccess(new AdmofiReward("Admofi supersonicv Points "+rewardName, rewardAmount ,true,"supersonicvideo Reward Success"));
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Event onRVAdCredited :: "+rewardName + " ::: "+rewardAmount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
			try {
				int errorCode =  ironSourceError.getErrorCode();
				String errorMessage = ironSourceError.getErrorMessage();
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicvi Event onRewardedVideoInitFail :: error code :: " +errorCode + "reason :: "+errorMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}