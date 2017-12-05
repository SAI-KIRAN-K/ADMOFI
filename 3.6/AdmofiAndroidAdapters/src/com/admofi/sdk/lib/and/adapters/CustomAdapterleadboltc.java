package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.apptracker.android.listener.AppModuleListener;
import com.apptracker.android.track.AppTracker;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;


public class CustomAdapterleadboltc  extends CustomAdapterImpl {
	
	private Context mContext = null;
	private Activity mActivity = null;	
	private boolean isActive = true;
	private Timer timer = null;	
	private String AFW_API_KEY 	= "";
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT ;	
	private static final String AFW_LOCATION = "inapp";

	public CustomAdapterleadboltc(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView,
			AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.apptracker.android.track.AppTracker");

		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		this.mContext = super.mContext;
		mActivity = (Activity)this.mContext;
		if(mAdShown.getTpTimeout()>0){
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			//isActive = false;
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			isActive = false;
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}
	
	private void loadInterstitial(final Context context, AdmofiAd mAdCurrent) {
		
		try {
			AFW_API_KEY = mAdCurrent.getAdapterKey(0); 
			AppTracker.startSession(context, AFW_API_KEY);			
			AppTracker.setModuleListener(listener);		
			AppTracker.loadModuleToCache(mActivity, AFW_LOCATION);
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect load Inter");
			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					stopTimer();
					AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt Timer");
					if(isActive){
						isActive = false;
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);						
					}
				}
			}, (long)(iAdTimeout * 1000), (long)(iAdTimeout * 1000));
		} catch (Exception e) {
			isActive = false;
			stopTimer();
		}		
	}

	private AppModuleListener listener = new AppModuleListener() {
		@Override
		public void onModuleClosed(String s, boolean b) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect Ad completed");
			isActive = false;
			adEventCompleted();
		}

		@Override
		public void onModuleFailed(String s, String s1, boolean b) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect load failed :: "+s+" :: "+s1);
			if(isActive){
				isActive = false;
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				stopTimer();
			}
		}

		@Override
		public void onModuleCached(String s) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect Ad Ready :: "+s);
			if(isActive){
				isActive = false;
				adEventReady(null);
				stopTimer();
			}
		}

		@Override
		public void onModuleClicked(String s) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect onModuleClicked");
			adEventClicked();
		}

		@Override
		public void onModuleLoaded(String s) {
			AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt connect Ad Shown");

		}
	};

	
	public void stopTimer() {
		if (null != timer) {
			try {
				AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi leadbolt Stop Timer");
				timer.cancel();
				timer.purge();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean showinterstitial() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi leadbolt Event ShowInter");
		if(mActivity!=null){
			isActive = false;
			AppTracker.loadModule(mActivity, AFW_LOCATION);
			adEventImpression();
			return true;		
		}
		return false;
	}

	@Override
	public void onAdmDestroy() {
		
		 if ((mContext!=null) && (mActivity!=null) && (mActivity.isFinishing()))
	        {
	            AppTracker.closeSession(mContext, true);
	        }
		super.onAdmDestroy();	
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi leadbolt Event destroy");
	}

	@Override
	public void onAdmPause() {
		 if ((mContext!=null) && (mActivity!=null) && (!mActivity.isFinishing()))
	        {
	            AppTracker.pause(mContext);
	        }
		super.onAdmPause();
	}

	@Override
	public void onAdmResume() {
		if (mContext!=null) {
			AppTracker.resume(mContext);
		}
		super.onAdmResume();
		
	}

}

