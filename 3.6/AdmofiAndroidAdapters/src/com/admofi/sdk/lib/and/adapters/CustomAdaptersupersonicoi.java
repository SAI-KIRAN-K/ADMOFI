package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.sdk.SSAFactory;

public class CustomAdaptersupersonicoi extends CustomAdapterImpl {
			
	private String userId = "";
	private final String FALLBACK_USER_ID = "userId";
	Activity mAct = null;
	private static boolean interstitialInitiated = false;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;

	private static String advertisingId = "100";
	private static String ADVT_ID = "100";

	public CustomAdaptersupersonicoi(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic incentive class invoked");

			Class.forName("com.admofi.sdk.lib.and.offers.AdmofiReward");
			Class.forName("com.ironsource.mediationsdk.IronSource");
			Class.forName("com.ironsource.sdk.SSAFactory");
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		userId = madView.getUniqId();
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		mContext = super.mContext;
		mAct = (Activity)super.mContext;
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
		}		
	}


	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		try {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);	
			//AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic Event Load Banner");
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}



	private void loadInterstitial(Context context,final AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic incentive Load Interstitial");
			//getting advertising id
			if(!advertisingId.equalsIgnoreCase(CustomAdaptersupersonicoi.ADVT_ID)){
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
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic incentive Load Interstitial Exception :: "+e.getLocalizedMessage());
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}		
	}


	private void requestInterstitial(String apikey,String advId){
		try{
			IronSource.setOfferwallListener(offerwallListener);
			IronSource.setUserId(advId);
			if(CustomAdaptersupersonicoi.interstitialInitiated) {
				if (IronSource.isOfferwallAvailable()) {
					adEventReady(null);
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic incentive Ad ready");
				} else {
					starttimer();
				}
			} else {
				CustomAdaptersupersonicoi.interstitialInitiated = true;
				IronSource.init(mAct,apikey );
				starttimer();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	Timer timer;
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
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (IronSource.isOfferwallAvailable())) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonic incentive showInterstitial");
				adEventImpression();
				IronSource.showOfferwall();
				return true;
			}			
		} catch (Exception e) {
			return false;
		}		
		return false;
	}



	OfferwallListener offerwallListener = new OfferwallListener() {
		@Override
		public void onOfferwallAvailable(boolean b) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicoi incentive onOfferwallInitSuccess");			
		}

		@Override
		public void onOfferwallOpened() {

		}

		@Override
		public void onOfferwallShowFailed(IronSourceError ironSourceError) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonicoi incentive onOfferwallInitFail :: "+ironSourceError.getErrorMessage() );
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
		}

		@Override
		public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
			adEventRewardSuccess(new AdmofiReward("Admofi supersonicoi incentive Points", credits ,true,"supersonicofferwall Reward Success"));
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonioi incentive onRVAdCredited :: "+credits);
			return true;
		}

		@Override
		public void onGetOfferwallCreditsFailed(IronSourceError ironSourceError) {

		}

		@Override
		public void onOfferwallClosed() {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi supersonioi incentive onOfferwallClosed ");
			adEventCompleted();
		}
	};
}