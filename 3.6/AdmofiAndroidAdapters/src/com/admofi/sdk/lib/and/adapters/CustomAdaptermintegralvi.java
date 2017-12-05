package com.admofi.sdk.lib.and.adapters;

import java.util.Map;
import java.util.Timer;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.mobvista.msdk.MobVistaSDK;
import com.mobvista.msdk.out.MVRewardVideoHandler;
import com.mobvista.msdk.out.MobVistaSDKFactory;
import com.mobvista.msdk.out.RewardVideoListener;
import com.mobvista.msdk.videocommon.download.NetStateOnReceive;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

public class CustomAdaptermintegralvi extends CustomAdapterImpl {

	 private String APP_ID  = "";
	 private String AD_UNIT_ID = "";
	 private String API_KEY = "";
	 private String REWARD_ID = "";
	 
	 private Activity ac = null;	 
	 private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	 private Timer timer = null;
	 
	 private NetStateOnReceive mNetStateOnReceive;
	 private MVRewardVideoHandler mMvRewardVideoHandler;
	 
	 public static boolean isInitialised = false;
	
	public CustomAdaptermintegralvi(Context ctx) {
		super(ctx);
		
	}
	
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {			
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi incentive class invoked");
			Class.forName("com.mobvista.msdk.MobVistaSDK");
			Class.forName("com.mobvista.msdk.out.MobVistaSDKFactory");
			Class.forName("com.mobvista.msdk.out.MVRewardVideoHandler");
			Class.forName("com.mobvista.msdk.videocommon.download.NetStateOnReceive");
		} catch (Exception e) {
			super.setSupported(false);			
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		
		APP_ID = mAdShown.getAdapterKey(0);
		API_KEY = mAdShown.getAdapterKey(1);
		AD_UNIT_ID = mAdShown.getAdapterKey(2);
		REWARD_ID = mAdShown.getAdapterKey(3);
		
		super.setSupported(true);
		mContext = super.mContext;
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitialV(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}
	
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Mobvistavi LoadBanner");
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitialV(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi load incentive called");
		ac = (Activity)context;
		
		if(!isInitialised){
			CustomAdaptermintegralvi.isInitialised = true;
			MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
			Map<String, String> map = sdk.getMVConfigurationMap(APP_ID, API_KEY);
			sdk.init(map, ac);
			
			
		}
		
		if (mNetStateOnReceive == null) {
			mNetStateOnReceive = new NetStateOnReceive();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			ac.registerReceiver(mNetStateOnReceive, filter);
		}
		
		
		
		mMvRewardVideoHandler = new MVRewardVideoHandler(ac, AD_UNIT_ID);
		mMvRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {
			
			@Override
			public void onVideoLoadSuccess() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi incentive Ad Ready");
				adEventReady(null);
			}
			
			@Override
			public void onVideoLoadFail() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi load incentive Ad Failed");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			}
			
			@Override
			public void onVideoAdClicked(String unitId) {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi incentive Ad Clicked");
				adEventClicked();
			}
			
			@Override
			public void onShowFail(String error) {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi show incentive Ad Failed:::"+error);
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			}
			
			@Override
			public void onAdShow() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi incentive Ad Opened");
				adEventImpression();
			}
			
			@Override
			public void onAdClose(boolean isCompleteView, String RewardName, float RewardAmout) {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi incentice Ad Closed");
				if(isCompleteView){
					
					int amount = 0;
					if(RewardAmout!=0){
						AdmofiUtil.logMessage("admofi Mobvistavi reward: ", Log.DEBUG, "earn : "+RewardAmout);
						AdmofiView.percentVideoWatched = 100;
						try{
							amount = (int)RewardAmout;
						}catch (Exception e) {
							e.printStackTrace();
						}
						adEventRewardSuccess(new AdmofiReward("Tap Points", amount ,true,"Mobvista success"));
					} else {
						adEventRewardFailed(new AdmofiReward("Tap Points", amount ,false,"Mobvista reward failed"));
					}		
				}
				adEventCompleted();
			}
		});
		mMvRewardVideoHandler.load();
		
		
	}
	
	
	@Override
	public boolean showinterstitial() {
		
		if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) 
				&& (mMvRewardVideoHandler != null)
				&& (mMvRewardVideoHandler.isReady())) {
			
			try{
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistavi showinterstitial called");
				AdmofiView.percentVideoWatched = 0;	
				
				mMvRewardVideoHandler.show(REWARD_ID);
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
	public void onAdmDestroy() {
		super.onAdmDestroy();
		CustomAdaptermintegralvi.isInitialised = false;
	}
	
	@Override
	public void onAdmPause() {
		super.onAdmPause();
	}
	
	@Override
	public void onAdmResume() {
		super.onAdmResume();
	}
	
}
