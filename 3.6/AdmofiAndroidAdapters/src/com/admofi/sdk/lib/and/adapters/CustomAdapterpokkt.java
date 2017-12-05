package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.pokkt.PokktAds;
import java.util.Timer;
import java.util.TimerTask;

public class CustomAdapterpokkt extends CustomAdapterImpl {
    private String APP_ID = "";
    private String SECURITY_KEY = "";
    Activity mAct = null;
    private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
    private Timer timer = null;
    public  boolean isPokktSDKReady = false;
    private boolean isActive = true;
    private String screenName = "screenname";

    public CustomAdapterpokkt(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt checking classes");
            Class.forName("com.pokkt.PokktAds");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        APP_ID = mAdShown.getAdapterKey(0);
        SECURITY_KEY = mAdShown.getAdapterKey(1);
        super.setSupported(true);
        if(mAdShown.getTpTimeout()>0){
            iAdTimeout = mAdShown.getTpTimeout();
        }
        if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
            loadBanner(super.mContext, mAdShown);
        } else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
            loadInterstitial(super.mContext, mAdShown);
        } else {
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }

    private void loadBanner(Context context, AdmofiAd mAdCurrent) {
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt loadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            try {
                if (!mAdCurrent.getAdapterKey(2).equalsIgnoreCase("")) {
                    screenName = mAdCurrent.getAdapterKey(2);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt LoadInterstitial");
            mAct = (Activity) context;
            PokktAds.setThirdPartyUserId("123456"); // optional
            //PokktAds.Debugging.shouldDebug(mContext, true);
            PokktAds.setPokktConfig(SECURITY_KEY, APP_ID, ((Activity) super.mContext));

            PokktAds.Interstitial.cacheNonRewarded(screenName);
            PokktAds.Interstitial.setDelegate(new PokktAds.Interstitial.InterstitialDelegate() {
                @Override
                public void interstitialCachingCompleted(String screenName, boolean isRewarded,double reward)  {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Pokkt interstitial CachingCompleted ad Ready ");
                    if (isActive) {
                            stopTimer();
                            isPokktSDKReady = true;
                            adEventReady(null);
                    }
                }

                @Override
                public void interstitialCachingFailed(String s, boolean b, String s1) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialCachingFailed: " + s1 + "::" + s);
                    if (isActive) {
                        isActive = false;
                        stopTimer();
                        adEventLoadFailed();
                    }
                }

                @Override
                public void interstitialDisplayed(String s, boolean b) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialDisplayed");
                    if (isActive) {
                        adEventImpression();
                    }
                }

                @Override
                public void interstitialFailedToShow(String s, boolean b, String s1) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialFailedToShow:: " + s + ":::" + s1);
                    if (isActive) {
                        isActive = false;
                        stopTimer();
                        adEventLoadFailed();
                    }
                }

                @Override
                public void interstitialClosed(String s, boolean b) {
                    if (isActive) {
                        isActive = false;
                        stopTimer();
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialClosed");
                        adEventCompleted();
                    }
                }

                @Override
                public void interstitialSkipped(String s, boolean b) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialSkipped");
                }

                @Override
                public void interstitialCompleted(String s, boolean b) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialCompleted");
                }

                @Override
                public void interstitialGratified(String s, boolean b, double v) {

                }

                @Override
                public void interstitialAvailabilityStatus(String screenName, boolean isRewarded,
                                                           boolean availability) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt interstitialAvailabilityStatus "+availability);
                    if (!availability && isActive) {
                        isActive = false;
                        stopTimer();
                        adEventLoadFailed();
                    }
                }
            });

            timer = new Timer();
           timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isActive = false;
                    stopTimer();
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt Timer");
                        if (isPokktSDKReady) {
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt  Timer Ad ready");
                            adEventReady(null);
                        } else {
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt  Timer Failed Ad");
                            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
                        }

                }
            }, (long) (iAdTimeout * 1000), (long) (iAdTimeout * 1000));

        }catch (Exception e){
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }

    public void stopTimer() {

        if (null != timer) {
            try {
                //AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi pokkt Stop Timer");
                timer.cancel();
                timer.purge();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean showinterstitial() {
        try{
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt Show Interstitial");
            if ((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (mContext!=null)) {
                PokktAds.Interstitial.showNonRewarded(screenName);
            }else{
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt Show Interstitial failed");
                adEventLoadFailed();
            }
        }catch (Exception e){
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokkt Show Interstitial failed");
            adEventLoadFailed();
            e.printStackTrace();
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
