package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;


/**
 * Created by apple on 07/09/17.
 */

public class CustomAdapterironsourcesdk extends CustomAdapterImpl {

    private String APP_KEY = "";
    Activity mAct = null;

    public CustomAdapterironsourcesdk(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource checking classes");
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


    private void loadBanner(Context context, AdmofiAd mAdCurrent) { //yet to implement
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource Load Banner");
            adEventLoadFailed();
            /*IronSource.init(mAct, APP_KEY);
            adContainer = new RelativeLayout(context);
            mIronSourceBannerLayout = IronSource.createBanner(mAct, EBannerSize.BANNER);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mIronSourceBannerLayout.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    try {
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource Banner Event onAdLoaded");
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
                        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource Banner ad failed");
                        adEventLoadFailed();
                        destroyBannerAd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBannerAdClicked() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource Banner ad clicked");
                    adEventClicked();
                }

                @Override
                public void onBannerAdScreenPresented() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource Banner ad shown");
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
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Ironsource Load Interstitial");
            mAct = (Activity) context;
            IronSource.init(mAct, APP_KEY, IronSource.AD_UNIT.INTERSTITIAL);
            setcallback();
            IronSource.loadInterstitial();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }


    private void setcallback() {
        try {
            IronSource.setInterstitialListener(new InterstitialListener() {
                @Override
                public void onInterstitialAdReady() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource ad ready");
                    adEventReady(null);
                }

                @Override
                public void onInterstitialAdLoadFailed(IronSourceError error) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource failed::" + error);
                    adEventLoadFailed();
                }

                @Override
                public void onInterstitialAdOpened() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource ad opened");
                    adEventImpression();
                }

                @Override
                public void onInterstitialAdClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource ad closed");
                    adEventCompleted();
                }

                @Override
                public void onInterstitialAdShowSucceeded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource shown");
                }

                @Override
                public void onInterstitialAdShowFailed(IronSourceError error) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource show failed::" + error);
                    adEventLoadFailed();
                }

                @Override
                public void onInterstitialAdClicked() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi IronSource ad clicked");
                    adEventClicked();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsource Show Interstitial");
            if (mContext != null && IronSource.isInterstitialReady()) {
                IronSource.showInterstitial();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsource Show Interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi ironsource Show Interstitial failed");
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

    @Override
    public void onAdmDestroy() {
        super.onAdmDestroy();
    }
}

