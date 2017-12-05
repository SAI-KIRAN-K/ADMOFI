package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.youappi.ai.sdk.YouAPPi;
import com.youappi.ai.sdk.ads.CardAd;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 13/09/17.
 */

public class CustomAdapteryouappisdk extends CustomAdapterImpl {

    private String APP_ID = "";
    CardAd cardAd = null;

    public CustomAdapteryouappisdk(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi checking classes  ");
            Class.forName("com.youappi.ai.sdk.YouAPPi");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        APP_ID = mAdShown.getAdapterKey(0);
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    int timecout1 = 0;
    final Timer timer = new Timer();
    boolean isActive = true;
    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi LoadInterstitial");
            YouAPPi.init(mContext, APP_ID);
            cardAd = YouAPPi.getInstance().cardAd();
            setcallback();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isActive) {
                        timecout1 = timecout1 + 1;
                        if (timecout1 <= 5) {
                            if (cardAd != null && cardAd.isAvailable()) {
                                System.out.println("Admofi youappi timer ::"+timecout1);
                                stopTimer();
                                adEventReady(null);
                            }
                        } else {
                            System.out.println("Admofi youappi ad failed stop timer::");
                            stopTimer();
                            adEventLoadFailed();
                        }
                    }else {
                        stopTimer();
                    }
                }
            }, 2000, 2000);

        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }

    private void stopTimer(){
        try{
            System.out.println("Admofi youappi stoptimer");
            isActive = false;
            if(timer != null){
                timer.purge();
                timer.cancel();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setcallback() {
        try{
            cardAd.setCardAdListener(new CardAd.CardAdListener() {
                @Override
                public void onCardShow() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onCardShow");
                    adEventImpression();
                }

                @Override
                public void onCardClose() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onCardClose");
                    stopTimer();
                    adEventCompleted();
                }

                @Override
                public void onCardClick() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onCardClick");
                    adEventClicked();
                }

                @Override
                public void onInitSuccess() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onInitSuccess");
                }

                @Override
                public void onLoadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onInitSuccess:" + e);
                    stopTimer();
                    adEventLoadFailed();
                }

                @Override
                public void onPreloadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onPreloadFailed");
                    stopTimer();
                    adEventLoadFailed();
                }

                @Override
                public void onAvailabilityChanged(boolean b) {
                    stopTimer();
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onAvailabilityChanged before :: "+b );
                    if(cardAd != null && cardAd.isAvailable() && b  ) {
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onAvailabilityChanged after:: "+b);
                        adEventReady(null);
                    }
                }
            });
        }catch (Exception e){
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onInitSuccess:" + e);
            adEventLoadFailed();
            e.printStackTrace();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi Show Interstitial::");
            if (cardAd != null && cardAd.isAvailable()) {
                cardAd.show();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi Show Interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi Show Interstitial failed");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAdmPause() {
        super.onAdmPause();
        if(mContext != null) {
            YouAPPi.getInstance().onPause((Activity) mContext);
        }
    }

    @Override
    public void onAdmDestroy() {
        super.onAdmDestroy();
        if(mContext != null) {
            YouAPPi.getInstance().onDestroy((Activity) mContext);
        }
    }

    @Override
    public void onAdmResume() {
        super.onAdmResume();
        if(mContext != null) {
            YouAPPi.getInstance().onResume((Activity)mContext);
        }
    }
}
