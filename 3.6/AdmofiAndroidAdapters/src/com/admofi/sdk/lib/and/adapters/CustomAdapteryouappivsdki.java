package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.youappi.ai.sdk.YouAPPi;
import com.youappi.ai.sdk.ads.RewardedVideoAd;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apple on 13/09/17.
 */

public class CustomAdapteryouappivsdki extends CustomAdapterImpl {

    private String APP_TOKEN = "";
    private static boolean isinitialised = false;
    RewardedVideoAd rewardedVideoAd;
    private int cpVal = 0;

    public CustomAdapteryouappivsdki(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "YouAppi vi checking classes  ");
            Class.forName("com.youappi.ai.sdk.YouAPPi");
            Class.forName("com.youappi.ai.sdk.ads.RewardedVideoAd");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        APP_TOKEN = mAdShown.getAdapterKey(0);
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    int timecout1 = 0;
    final Timer timer = new Timer();
    boolean isActive = true;

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi load interstitial");
            cpVal = mAdCurrent.getCPVValue();
            YouAPPi.init(mContext, APP_TOKEN);
            rewardedVideoAd = YouAPPi.getInstance().rewaredVideoAd();
            setcallback();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isActive) {
                        timecout1 = timecout1 + 1;
                        if (timecout1 <= 5) {
                            if (rewardedVideoAd != null && rewardedVideoAd.isAvailable()) {
                                System.out.println("Admofi Youappi vi timer count ::" + timecout1);
                                stopTimer();
                                adEventReady(null);
                            }
                        } else {
                            System.out.println("Admofi Youappi vi ad failed stop timer::");
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
            System.out.println("Admofi Youappi vi ad stoptimer");
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
            rewardedVideoAd.setRewardedListener(new RewardedVideoAd.RewardedVideoAdListener() {
                @Override
                public void onRewarded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onRewarded");
                    if (cpVal > 0) {
                        AdmofiView.percentVideoWatched = 100;
                        adEventRewardSuccess(new AdmofiReward("YouAppi Points", cpVal, true, "YouAppi Reward Success"));
                    }
                }

                @Override
                public void onVideoStart() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onVideoStart");
                    adEventImpression();
                }

                @Override
                public void onVideoEnd() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onVideoEnd");
                }

                @Override
                public void onVideoSkipped(int i) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onVideoSkipped");
                }

                @Override
                public void onCardShow() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onCardShow");
                }

                @Override
                public void onCardClose() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onCardClose");
                    stopTimer();
                    adEventCompleted();
                }

                @Override
                public void onCardClick() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onCardClick");
                    adEventClicked();
                }

                @Override
                public void onInitSuccess() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onInitSuccess");
                }

                @Override
                public void onLoadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onLoadFailed:" + e);
                    adEventLoadFailed();
                }

                @Override
                public void onPreloadFailed(Exception e) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onPreloadFailed:" + e);
                    stopTimer();
                    adEventLoadFailed();
                }

                @Override
                public void onAvailabilityChanged(boolean b) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onAvailabilityChanged");
                    stopTimer();
                    if(rewardedVideoAd != null && rewardedVideoAd.isAvailable() && b  ) {
                        adEventReady(null);
                    }
                }
            });
        }catch (Exception e){
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi onPreloadFailed:" + e);
            adEventLoadFailed();
            e.printStackTrace();
        }
    }

    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi Show rewarded video");
            if ( rewardedVideoAd != null && rewardedVideoAd.isAvailable()) {
                rewardedVideoAd.show();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi Show rewarded video failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi YouAppi vi Show rewarded video failed");
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
}
