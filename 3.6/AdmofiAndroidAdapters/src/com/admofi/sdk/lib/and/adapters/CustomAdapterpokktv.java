package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.pokkt.PokktAds;


public class CustomAdapterpokktv extends CustomAdapterImpl {
	
	private Context mContext = null;
	Timer timeout_timer = null;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;
	private boolean isActive = true;
	public boolean isPokktSDKReady = false;
	private Timer timer = null;
	private String screenName = "screenname";
	
	public CustomAdapterpokktv(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView,AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  checking classes");
			Class.forName("com.pokkt.PokktAds");
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		mContext = super.mContext;
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			isActive = false;
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		isActive = false;
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}

	private void loadInterstitial(final Context context, AdmofiAd mAdCurrent) {
	 try {

		 try {
			 if (!mAdCurrent.getAdapterKey(2).equalsIgnoreCase("")) {
				 screenName = mAdCurrent.getAdapterKey(2);
			 }

		 }catch (Exception e){
			 e.printStackTrace();
		 }

		 	AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv LoadInter");
		 	PokktAds.setThirdPartyUserId("123456");
		 	//PokktAds.Debugging.shouldDebug(mContext, true);
		 	PokktAds.setPokktConfig(mAdCurrent.getAdapterKey(1), mAdCurrent.getAdapterKey(0), ((Activity) context));

		 	PokktAds.VideoAd.cacheNonRewarded(screenName);
			setCallbacks();

		 timer = new Timer();
		 timer.schedule(new TimerTask() {
			 @Override
			 public void run() {
				 isActive = false;
				 stopTimer();
				 AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  Timer");

					 if (isPokktSDKReady){
						 AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  Timer Ad ready");
						 adEventReady(null);
					 } else {
						 AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  Timer Failed Ad");
						 adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
					 }
			 }
		 }, (long) (iAdTimeout * 1000), (long) (iAdTimeout * 1000));

		} catch (Exception e) {
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}

	public void stopTimer() {
		if (null != timer) {
			try {
				//AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi pokktv Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private void setCallbacks(){
		try {
			PokktAds.VideoAd.setDelegate(new PokktAds.VideoAd.VideoAdDelegate() {
				@Override
				public void videoAdCachingCompleted(String s, boolean b, double v) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdCachingCompleted ad Ready ");
					if (isActive) {
						stopTimer();
						isPokktSDKReady =true;
						adEventReady(null);
					}
				}

				@Override
				public void videoAdCachingFailed(String s, boolean b, String s1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdFailedToShow :: " + s);
					if(isActive) {
						isActive = false;
						stopTimer();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
					}
				}

				@Override
				public void videoAdDisplayed(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdDisplayed :: " + b);
					if(isActive) {
						adEventImpression();
					}
				}

				@Override
				public void videoAdFailedToShow(String s, boolean b, String s1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdFailedToShow :: " + s);
					if(isActive) {
						isActive = false;
						stopTimer();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
					}
				}

				@Override
				public void videoAdClosed(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdClosed :: " + isActive);
					if(isActive) {
						isActive = false;
						stopTimer();
						adEventCompleted();
					}
				}

				@Override
				public void videoAdSkipped(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdSkipped :: " + b);
				}

				@Override
				public void videoAdCompleted(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdCompleted :: " + b);
				}

				@Override
				public void videoAdGratified(String s, boolean b, double v) {

				}

				@Override
				public void videoAdAvailabilityStatus(String screenName, boolean isRewarded, boolean availability) {
					if(isActive) {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv videoAdAvailabilityStatus :: " + availability);
						if (!availability) {
							isActive = false;
							stopTimer();
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
						}
					}
				}
			});
		}catch (Exception e){
			isActive = false;
			stopTimer();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			e.printStackTrace();
		}
	}

	
	@Override
	public boolean showinterstitial() {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  ShowInter");
			AdmofiView.percentVideoWatched = 0;
			if ((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (mContext!=null)) {
				PokktAds.VideoAd.showNonRewarded(screenName);
				return true;
			}else{
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  ShowInter failed");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktv  ShowInter failed");
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
		return false;
	}

	@Override
	public void onAdmDestroy() {
		super.onAdmDestroy();
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