package com.admofi.sdk.lib.and.adapters;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;

import nativesdk.ad.rw.IRewardedVideoAd;
import nativesdk.ad.rw.RewardedVideoAd;
import nativesdk.ad.rw.RewardedVideoAdListener;
import nativesdk.ad.rw.mediation.RewardItem;

/**
 * Created by apple on 12/09/17.
 */

public class CustomAdapteravazuvsdki extends CustomAdapterImpl {

    private String APP_ID = "";
    private static boolean isinitialised = false;
    private IRewardedVideoAd rewardedVideoAd;

    public CustomAdapteravazuvsdki(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG,"Avazui checking classes  ");
            Class.forName("nativesdk.ad.rw.IRewardedVideoAd");
            Class.forName("nativesdk.ad.rw.RewardedVideoAd");
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {

        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi LoadInterstitial");
            rewardedVideoAd = new RewardedVideoAd(mContext, APP_ID, new RewardedVideoAdListener() {
                @Override
                public void onInitSuccess() {
                    CustomAdapteravazuvsdki.isinitialised = true;
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi intialize success");
                }

                @Override
                public void onInitFailed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi intialize failed");
                    CustomAdapteravazuvsdki.isinitialised = false;
                    adEventLoadFailed();

                }

                @Override
                public void onRewardedVideoAdLoaded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi ad loaded");
                    adEventReady(null);
                }

                @Override
                public void onRewardedVideoAdOpened() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi ad opened");
                    adEventImpression();

                }

                @Override
                public void onRewardedVideoStarted() {

                }

                @Override
                public void onRewardedVideoAdClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi ad completed");
                    adEventCompleted();
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                    if(rewardItem != null){

                        int amount = rewardItem.getAmount();
                        if(amount !=0 ) {
                            AdmofiUtil.logMessage("Admofi Avazuvi reward: ", Log.DEBUG, "earn : "+rewardItem.getAmount());
                            try{
                                amount = (int)rewardItem.getAmount();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            adEventRewardSuccess(new AdmofiReward("Tap Points", amount ,true,"Avazui success"));

                        }else {
                            adEventRewardFailed(new AdmofiReward("Tap Points", amount ,false,"Avazui reward failed"));
                        }
                    }
                    adEventCompleted();
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi intialize failed");
                    adEventLoadFailed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
        if(CustomAdapteravazuvsdki.isinitialised) {
            rewardedVideoAd.loadAd();
        }else{
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi intialize failed");
            adEventLoadFailed();
        }
    }


    @Override
    public boolean showinterstitial() {

        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Avazuvi Show Interstitial");
            if ( rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
                rewardedVideoAd.show();
            }else{
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            e.printStackTrace();
        }
        return false;
    }

}
