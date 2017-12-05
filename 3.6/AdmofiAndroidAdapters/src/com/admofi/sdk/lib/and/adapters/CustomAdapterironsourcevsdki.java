package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;


/**
 * Created by apple on 07/09/17.
 */

public class CustomAdapterironsourcevsdki extends CustomAdapterImpl {

    private String APP_KEY = "";
    Activity mAct = null;
    private RelativeLayout adContainer = null;

    public CustomAdapterironsourcevsdki(Context context) {
        super(context);
    }


    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei checking classes");
            Class.forName("com.ironsource.mediationsdk.IronSource");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        APP_KEY = mAdShown.getAdapterKey(0);
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
            try {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Load Banner");
                adEventLoadFailed();
               /* IronSource.init(mAct, APP_KEY);
                adContainer = new RelativeLayout(context);
                mIronSourceBannerLayout = IronSource.createBanner(mAct, EBannerSize.BANNER);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mIronSourceBannerLayout.setBannerListener(new BannerListener() {
                    @Override
                    public void onBannerAdLoaded() {
                        try {
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Banner Event onAdLoaded");
                            if (adContainer != null) {
                                adEventReady((View) adContainer);
                            } else {
                                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
                        }
                    }

                    @Override
                    public void onBannerAdLoadFailed(IronSourceError error) {
                        try {
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Banner ad failed");
                            adEventLoadFailed();
                            destroyBannerAd();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBannerAdClicked() {
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Banner ad clicked");
                        adEventClicked();
                    }

                    @Override
                    public void onBannerAdScreenPresented() {
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Banner ad shown");
                        adEventImpression();
                    }

                    @Override
                    public void onBannerAdScreenDismissed() {
                        adEventCompleted();
                    }

                    @Override
                    public void onBannerAdLeftApplication() {
                        adEventCompleted();
                    }
                });
                IronSource.loadBanner(mIronSourceBannerLayout);*/

            } catch (Exception e) {
                e.printStackTrace();
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            }
    }

   /* private void destroyBannerAd(){
        try {
            if (mIronSourceBannerLayout != null) {
                mIronSourceBannerLayout. setBannerListener(null);
                mIronSourceBannerLayout = null;
            }
            if (adContainer != null) {
                adContainer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei load interstitial");
            mAct = (Activity) context;
            IronSource.init(mAct, APP_KEY, IronSource.AD_UNIT.REWARDED_VIDEO);
            setcallback();
            IronSource.loadInterstitial();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }

    private void setcallback() {

        try{
            IronSource.setRewardedVideoListener(new RewardedVideoListener() {
                @Override
                public void onRewardedVideoAdOpened() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei onRewardedVideoAdOpened");
                    adEventImpression();
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei onRewardedVideoAdClosed");
                    adEventCompleted();
                }

                @Override
                public void onRewardedVideoAvailabilityChanged(boolean available) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei on ad ready :: "+available);
                    if (available){
                        adEventReady(null);
                    } else {
                        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
                    }

                }

                @Override
                public void onRewardedVideoAdStarted() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei onRewardedVideoAdStarted");
                }

                @Override
                public void onRewardedVideoAdEnded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei onRewardedVideoAdEnded");
                    adEventCompleted();
                }

                @Override
                public void onRewardedVideoAdRewarded(Placement placement) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei onRewardedVideoAdRewarded with reward::" + placement.getRewardAmount());
                    if (placement != null) {
                        String rewardName = placement.getRewardName();
                        int rewardAmount = placement.getRewardAmount();
                        if (rewardAmount>0) {
                            AdmofiView.percentVideoWatched = 100;
                            adEventRewardSuccess(new AdmofiReward("Ironsource Points", rewardAmount, true, "Ironsource Reward Success"));
                        }
                    }
                }

                @Override
                public void onRewardedVideoAdShowFailed(IronSourceError error) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource onRewardedVideoAdShowFailed with error::" + error);
                    adEventRewardFailed(new AdmofiReward("Ironsource Points", 0, false, "Ironsource Reward failed"));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Show interstitial");
            if (mContext != null && IronSource.isRewardedVideoAvailable()) {
                IronSource.showRewardedVideo();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Show interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsourcei Show interstitial failed with exception :: e");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onAdmResume() {
        super.onAdmResume();
        if (mAct != null) {
            IronSource.onResume(mAct);
        }
    }

    @Override
    public void onAdmPause() {
        super.onAdmPause();
        if (mAct != null) {
            IronSource.onPause(mAct);
        }
    }


}
