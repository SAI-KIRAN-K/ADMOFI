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

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.OfferwallListener;

/**
 * Created by apple on 14/09/17.
 */

public class CustomAdapterironsourceosdki extends CustomAdapterImpl {

    private String APP_KEY = "";
    Activity mAct = null;

    public CustomAdapterironsourceosdki(Context context) {
        super(context);
    }


    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi checking classes");
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
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi loadBanner");
            adEventLoadFailed();

        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }



    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi load Offerwall");
            mAct = (Activity) context;
            IronSource.init(mAct, APP_KEY, IronSource.AD_UNIT.OFFERWALL);
            setcallback();
            IronSource.loadInterstitial();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }

    private void setcallback() {
        try{
            IronSource.setOfferwallListener(new OfferwallListener() {
                @Override
                public void onOfferwallAvailable(boolean isAvailable) {

                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallAvailable");
                    if (isAvailable) {
                        adEventReady(null);
                    } else {
                        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
                    }
                }

                @Override
                public void onOfferwallOpened() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallOpened");
                    adEventImpression();
                }

                @Override
                public void onOfferwallShowFailed(IronSourceError error) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallShowFailed with error" + error);
                    adEventLoadFailed();
                }

                @Override
                public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallAdCredited");
                    adEventRewardSuccess(new AdmofiReward("Ironsource Points", totalCredits, true, "IronSource offerwall success"));
                    return false;
                }

                @Override
                public void onGetOfferwallCreditsFailed(IronSourceError error) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource onGetOfferwallCreditsFailed with error::" + error);
                    adEventRewardSuccess(new AdmofiReward("Ironsource Points", 0, false, "IronSource offerwall success"));
                }

                @Override
                public void onOfferwallClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallClosed");
                    adEventCompleted();
                }
            });
        }catch (Exception e){
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi onOfferwallShowFailed with error" + e);
            adEventLoadFailed();
            e.printStackTrace();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi Show interstitial");
            if (mContext != null && IronSource.isOfferwallAvailable()) {
                IronSource.showOfferwall();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi Show interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSourceoi Show interstitial failed");
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
