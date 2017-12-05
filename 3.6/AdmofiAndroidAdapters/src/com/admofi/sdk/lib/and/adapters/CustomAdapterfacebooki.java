package com.admofi.sdk.lib.and.adapters;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;
import com.admofi.sdk.lib.and.offers.AdmofiReward;

public class CustomAdapterfacebooki extends CustomAdapterImpl  {
	
	Activity mAct = null;
	RewardedVideoAd rewardedVideoAd = null;
	
	private String PLACEMENT_ID = "";
	RelativeLayout adContainer = null;
	public  int REWARD_POINTS = 1;


	public CustomAdapterfacebooki(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {			
				Class.forName("com.facebook.ads.RewardedVideoAd");				
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}		
		super.setSupported(true);
		if(mAdShown!=null){
			REWARD_POINTS = mAdShown.getCPVValue();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_NATIVE) {
			loadNative(super.mContext, mAdShown);
		} else {		
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);	
	}
	
	
	private void loadNative(final Context context,final  AdmofiAd mAdCurrent) {
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive LoadInter");
		PLACEMENT_ID = mAdCurrent.getAdapterKey(0);
		rewardedVideoAd = new RewardedVideoAd(context, PLACEMENT_ID);
		rewardedVideoAd.setAdListener(new S2SRewardedVideoAdListener() {
			@Override
			public void onAdClicked(Ad arg0) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive Event onAdClicked ");
				adEventClicked();
			}
			@Override
			public void onAdLoaded(Ad arg0) {

				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive Event Ad ready");
				adEventReady(null);

			}
			@Override
			public void onError(Ad arg0, AdError arg1) {

				if(arg1!=null){
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook  incentive Event Ad failed :: "+ arg1.getErrorCode() + " :: "+arg1.getErrorMessage());
					destroyAd();
				}
				if(arg1.getErrorCode() == AdError.NO_FILL_ERROR_CODE) {
					destroyAd();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				} else {
					destroyAd();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}
			}



			@Override
			public void onLoggingImpression(Ad arg0) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive Event Ad onInterstitialDisplayed");
				adEventImpression();

			}
			@Override
			public void onRewardedVideoClosed() {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive Event Ad Dismissed");
				destroyAd();
				adEventCompleted();
			}

			@Override
			public void onRewardedVideoCompleted() {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive AdCompleted");
			}

			@Override
			public void onRewardServerFailed() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi facebook incentive Failed Ad");
				try{
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRewardServerSuccess() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi facebook incentive Reward success::"+REWARD_POINTS);
				try {
					AdmofiView.percentVideoWatched = 100;
					adEventRewardSuccess(new AdmofiReward("unity reward", REWARD_POINTS, true, "unity Reward Success"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		rewardedVideoAd.loadAd();
	}	

	@Override
	public boolean showinterstitial() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook incentive Show Inter");
		if ((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (rewardedVideoAd!=null)) {
			rewardedVideoAd.show();			
			return true;
		}
		return false;
	}
	
	@Override
	public void onAdmPause() {
		super.onAdmPause();
	}
	
	@Override
	public void onAdmDestroy() {
		if (rewardedVideoAd != null) {
			rewardedVideoAd.destroy();
		  }
		super.onAdmDestroy();
	}
	
	@Override
	public void onAdmResume() {
		super.onAdmResume();
		
	}
	@Override
	public void vAdmofiCleanup() {
		super.vAdmofiCleanup();
	}


	private void destroyAd(){
		try {
			if (rewardedVideoAd != null) {
				rewardedVideoAd.setAdListener(null);
				rewardedVideoAd = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}