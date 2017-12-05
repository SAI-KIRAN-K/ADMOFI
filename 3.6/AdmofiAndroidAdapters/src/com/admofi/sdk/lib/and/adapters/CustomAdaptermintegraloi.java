package com.admofi.sdk.lib.and.adapters;

import java.util.HashMap;
import java.util.Map;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.mobvista.msdk.MobVistaConstans;
import com.mobvista.msdk.MobVistaSDK;
import com.mobvista.msdk.out.MVOfferWallHandler;
import com.mobvista.msdk.out.MobVistaSDKFactory;
import com.mobvista.msdk.out.OfferWallListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

public class CustomAdaptermintegraloi extends CustomAdapterImpl{

	
	private String APP_ID  = "";
	private String API_KEY = "";
	private String AD_UNIT_ID = "";
	
	private Activity ac = null;	
	private Context cntxt = null;
	private String userId = "";
	private MVOfferWallHandler mOfferWallHandler;
	 private static boolean isInitialised = false;
	 
	public CustomAdaptermintegraloi(Context ctx) {
		super(ctx);
	
	}
	
	public void loadAd(Handler loadingCompletedHandler,  AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {			
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi offerwall class invoked");
			Class.forName("com.mobvista.msdk.MobVistaSDK");
			Class.forName("com.mobvista.msdk.out.MobVistaSDKFactory");
			Class.forName("com.mobvista.msdk.MobVistaConstans");	
			Class.forName("com.mobvista.msdk.out.MVOfferWallHandler");
			Class.forName("com.mobvista.msdk.out.OfferWallRewardInfo");
			
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);		
		cntxt = super.mContext;
		
		userId = madView.getUniqId();
		APP_ID = mAdShown.getAdapterKey(0);
		API_KEY = mAdShown.getAdapterKey(1);
		AD_UNIT_ID = mAdShown.getAdapterKey(2);
		
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitialV(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}	
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Mobvistaoi LoadBanner");
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitialV(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi load offerwall called");
		ac = (Activity)context;
		
		if(!isInitialised){
			CustomAdaptermintegraloi.isInitialised = true;
			MobVistaSDK sdk = MobVistaSDKFactory.getMobVistaSDK();
			Map<String, String> map = sdk.getMVConfigurationMap(APP_ID, API_KEY);
			sdk.init(map, ac);
		}
		
		
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(MobVistaConstans.PROPERTIES_UNIT_ID, AD_UNIT_ID);
		hashMap.put(MobVistaConstans.OFFER_WALL_USER_ID, userId);
		hashMap.put(MobVistaConstans.OFFER_WALL_TITLE_BACKGROUD_COLOR, Color.parseColor("#ffffff"));
		hashMap.put(MobVistaConstans.OFFER_WALL_TITLE_TEXT, "MobistaOfferWall");
		hashMap.put(MobVistaConstans.OFFER_WALL_TITLE_FONT_COLOR, Color.parseColor("#ff2bac5f"));
		hashMap.put(MobVistaConstans.OFFER_WALL_TITLE_FONT_SIZE, 50);
		hashMap.put(MobVistaConstans.OFFER_WALL_TITLE_FONT_TYPEFACE, MobVistaConstans.TITLE_TYPEFACE_DEFAULT_BOLD);
		// Set offerWall reward video did not see whether to open the dialog
		// box，default false
		hashMap.put(MobVistaConstans.OFFER_WALL_REWARD_OPEN_WARN, true);
		// Set offerWall reward video Dialog box to close the text description
		hashMap.put(MobVistaConstans.OFFER_WALL_REWARD_VIDEO_STOP_TEXT, "Stop");
		// Set offerWall reward video Dialog box to cancel the text description
		hashMap.put(MobVistaConstans.OFFER_WALL_REWARD_VIDEO_RESUME_TEXT, "Resume");
		// Set offerWall reward video Dialog prompt info
		hashMap.put(MobVistaConstans.OFFER_WALL_REWARD_VIDEO_WARN_TEXT,"No rewards earned yet! Complete video to earn rewards ?");
		// instantiation MVOfferWallHandler Object
		mOfferWallHandler = new MVOfferWallHandler(ac, hashMap);
		mOfferWallHandler.setOfferWallListener(new OfferWallListener() {
			
			@Override
			public void onOfferWallShowFail(String error) {
			
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi show offerwall Ad Failed:::"+error);
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			}
			
			@Override
			public void onOfferWallOpen() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi offerwall Ad Opened");
				adEventImpression();
				
			}
			
			@Override
			public void onOfferWallLoadSuccess() {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi offerwall Ad Ready");
				adEventReady(null);
				
			}
			
			@Override
			public void onOfferWallLoadFail(String error) {
				
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi load offerwall Ad Failed:::"+error);
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			}
			
			@Override
			public void onOfferWallCreditsEarned(String arg0, int RewardAmout) {
				AdmofiUtil.logMessage("admofi Mobvistaoi offerwall reward: ", Log.DEBUG, "earnnn : "+RewardAmout);
				if(RewardAmout!=0){
					adEventRewardSuccess(new AdmofiReward("Tap Points", RewardAmout ,true,"Mobvista offerwall success"));
				} else {
					adEventRewardFailed(new AdmofiReward("Tap Points", RewardAmout ,false,"Mobvista offerwall reward failed"));
				}		
			}
			
			@Override
			public void onOfferWallClose() {
				
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi offerwall Ad Closed");
				adEventCompleted();
			}
			
			@Override
			public void onOfferWallAdClick() {
				
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi offerwall Ad Clicked");
				adEventClicked();
			}
		});
		mOfferWallHandler.load();
	}

	@Override
	public boolean showinterstitial() {
		
		if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) 
				&& (mOfferWallHandler != null)) {
			
			try{
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi Mobvistaoi showofferwall called");					
				mOfferWallHandler.show();
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
		CustomAdaptermintegraloi.isInitialised = false;
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
