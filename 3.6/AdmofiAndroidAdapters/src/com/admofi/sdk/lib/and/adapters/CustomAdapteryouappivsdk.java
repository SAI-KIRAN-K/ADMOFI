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
import com.youappi.ai.sdk.ads.VideoAd;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 13/09/17.
 */

public class CustomAdapteryouappivsdk extends CustomAdapterImpl {

    private String APP_ID = "";
    VideoAd videoAd = null;

    public CustomAdapteryouappivsdk(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video checking classes  ");
            Class.forName("com.youappi.ai.sdk.YouAPPi");
            Class.forName("com.youappi.ai.sdk.ads.VideoAd");
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
        try{
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video LoadBanner");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    int timecout1 = 0;
    final Timer timer = new Timer();
    boolean isActive = true;

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video load intestitial");
            YouAPPi.init(mContext, APP_ID);
            videoAd = YouAPPi.getInstance().videoAd();
            setcallback();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isActive) {
                        timecout1 = timecout1 + 1;
                        if (timecout1 <= 5) {
                            if (videoAd != null && videoAd.isAvailable()) {
                                System.out.println("Admofi youappi video timer count ::" + timecout1);
                                stopTimer();
                                adEventReady(null);
                            }
                        } else {
                            System.out.println("Admofi youappi video ad failed stop timer::");
                            stopTimer();
                            adEventLoadFailed();
                        }
                    } else {
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
            System.out.println("Admofi youappi video ad stoptimer");
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
            videoAd.setVideoAdListener(new VideoAd.VideoAdListener() {
                @Override
                public void onVideoStart() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onVideoStart");
                    adEventImpression();
                }

                @Override
                public void onVideoEnd() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onVideoEnd");
                }

                @Override
                public void onVideoSkipped(int i) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onVideoSkipped");
                }

                @Override
                public void onCardShow() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onCardShow");
                }

                @Override
                public void onCardClose() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onCardClose");
                    stopTimer();
                    adEventCompleted();
                }

                @Override
                public void onCardClick() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onCardClick");
                    adEventClicked();
                }

                @Override
                public void onInitSuccess() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi onInitSuccess");
                }

                @Override
                public void onLoadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onLoadFailed::" + e);
                    stopTimer();
                    adEventLoadFailed();
                }
                @Override
                public void onPreloadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onPreloadFailed::" + e);
                    stopTimer();
                    adEventLoadFailed();
                }

                @Override
                public void onAvailabilityChanged(boolean b) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onAvailabilityChanged::" + b);
                    stopTimer();
                    if(videoAd != null && videoAd.isAvailable() && b  ) {
                        adEventReady(null);
                    }
                }
            });
        }catch (Exception e){
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video onPreloadFailed::" + e);
            adEventLoadFailed();
            e.printStackTrace();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video Show Interstitial");
            if (videoAd != null && videoAd.isAvailable()) {
                videoAd.show();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video Show Interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi youappi video Show Interstitial failed");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAdmResume() {
        super.onAdmResume();
        if(mContext != null) {
            YouAPPi.getInstance().onResume((Activity)mContext);
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
    public void onAdmPause() {
        super.onAdmPause();
        if(mContext != null) {
            YouAPPi.getInstance().onPause((Activity) mContext);
        }
    }

}
