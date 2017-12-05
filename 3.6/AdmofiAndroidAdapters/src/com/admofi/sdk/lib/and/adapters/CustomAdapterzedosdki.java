package com.admofi.sdk.lib.and.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;


import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.zedo.watchandengagesdk.ZedoWatchAndEngage;
import com.zedo.watchandengagesdk.listener.AdBehaviorDelegate;

/**
 * Created by apple on 13/09/17.
 */

public class CustomAdapterzedosdki extends CustomAdapterImpl {

    String adconfigUrl = "";
    private static boolean isinitialised = false;
    private static AdBehaviorDelegate zedoDelegate;
    private boolean isActive = true;
    private int cpVal = 0;

    public CustomAdapterzedosdki(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ZEDO checking classes  ");

            Class.forName("com.zedo.watchandengagesdk.ZedoWatchAndEngage");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        /*if (hashMap == null){
            hashMap = new HashMap<Integer,Boolean>();
        }*/
        cpVal = mAdShown.getCPVValue();
        adconfigUrl = mAdShown.getAdapterKey(0);
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ZEDO appid  " +adconfigUrl);

        super.setSupported(true);

        if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
            loadBanner(super.mContext, mAdShown);
        } else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
            loadInterstitial(super.mContext, mAdShown);
        } else {
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }

    private void loadBanner(Context context, AdmofiAd mAdCurrent) {
        AdmofiUtil.logMessage(null, Log.DEBUG, " Zedo LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    private void disable() {
        //this.isActive = false;
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo LoadInterstitial :: ");

            if (!CustomAdapterzedosdki.isinitialised) {
                setcallback();
                ZedoWatchAndEngage.initialize(adconfigUrl, zedoDelegate, mContext);
            }
            else if (CustomAdapterzedosdki.isinitialised) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo Ad Requested");
                    ZedoWatchAndEngage.  loadAd(mContext);
            }

        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }

    private void setcallback() {

        zedoDelegate = new AdBehaviorDelegate() {
            @Override
            public void onAdLoaded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onAdLoaded :: "+isActive );
                    adEventReady(null);
            }

            @Override
            public void onAdOpened() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onAdOpened");
                adEventImpression();
            }

            @Override

            public void onVideoStarted() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onVideoStarted");

            }

            @Override

            public void onAdClicked() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onAdClicked");

                adEventClicked();
            }

            @Override

            public void onAdLeftApplication() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onAdLeftApplication");

            }

            @Override

            public void onRewarded() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onRewarded");

                try {
                    if (cpVal > 0) {
                        adEventRewardSuccess(new AdmofiReward("Zedo Reward", cpVal, true, "Zedo Reward Success"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override

            public void onAdClosed() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onAdClosed ");

                if (CustomAdapterzedosdki.this.isActive) {
                    disable();
                    adEventCompleted();
                }
            }

            @Override

            public void onError() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onError ");
                if (CustomAdapterzedosdki.this.isActive) {
                    disable();
                    adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
                }
            }

            @Override
            public void onInitialized() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onInitialized");
                if (!CustomAdapterzedosdki.isinitialised) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo Ad Requested");
                    ZedoWatchAndEngage.loadAd(mContext);
                }
                CustomAdapterzedosdki.isinitialised = true;
            }

            @Override
            public void onInitializationFailed() {
                CustomAdapterzedosdki.isinitialised = false;
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo onInitializationFailed");

            }
        };
    }

    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo Show Interstitial Ad available :: "+ZedoWatchAndEngage.isAdAvailable());
            if (ZedoWatchAndEngage.isAdAvailable() && CustomAdapterzedosdki.isinitialised) {
                ZedoWatchAndEngage.showAd(mContext);
                return true;
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zedo Show Interstitial Ad Not available");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}