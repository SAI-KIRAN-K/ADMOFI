package com.admofi.sdk.lib.and.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.redbricklane.zapr.bannersdk.ZaprBannerAd;
import com.redbricklane.zapr.bannersdk.ZaprBannerAdEventListener;
import com.redbricklane.zapr.bannersdk.ZaprInterstitialAd;
import com.redbricklane.zapr.bannersdk.ZaprInterstitialAdEventListener;
import com.redbricklane.zapr.basesdk.Log.LOG_LEVEL;
import com.redbricklane.zaprSdkBase.Zapr;

/**
 * Created by apple on 08/09/17.
 */

public class CustomAdapterzaprsdk extends CustomAdapterImpl {

    private String ADUNIT_ID = "";
    private ZaprInterstitialAd mInterstitialAd = null;
    private ZaprBannerAd mBannerAd = null;
    private RelativeLayout adContainer = null;

    public CustomAdapterzaprsdk(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zapr checking classes  ");
            Class.forName("com.redbricklane.zapr.bannersdk.ZaprInterstitialAd");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        ADUNIT_ID = mAdShown.getAdapterKey(0);
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zapr Load Banner");

        int width = mAdCurrent.getWidth();
        int height= mAdCurrent.getHeight();
        adContainer = new RelativeLayout(context);
        RelativeLayout.LayoutParams paramparent = new RelativeLayout.LayoutParams(width,height);
        adContainer.setLayoutParams(paramparent);
        RelativeLayout.LayoutParams bannerLayoutParams =new RelativeLayout.LayoutParams(width,height);
        int requiredHeight = mAdCurrent.getunScaledHeight();
        mBannerAd = new ZaprBannerAd(context);
        mBannerAd.setRequestForPermissionsEnabled(false);
        mBannerAd.setAdUnitId(ADUNIT_ID);
        mBannerAd.setAdRefreshTime(15);
        mBannerAd.enableAutoRetryOnError(true);

        ZaprBannerAdEventListener eventListener = new ZaprBannerAdEventListener() {
            @Override
            public void onBannerAdLoaded() {
                try {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Banner Event onAdLoaded");
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
            public void onBannerAdClicked() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Banner Event onAdClicked");
                adEventClicked();
            }

            @Override
            public void onFailedToLoadBannerAd(int i, String s) {
                try {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Event Banner Ad failed :: " + i + " :: " + s);
                    adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
                    destroyBannerAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mBannerAd.setBannerAdEventListener(eventListener);
        mBannerAd.loadAd();
        adContainer.addView(mBannerAd, bannerLayoutParams);
    }

    private void destroyBannerAd(){
        try {
            if (mBannerAd != null) {
                mBannerAd.setBannerAdEventListener(null);
                mBannerAd.destroy();
                mBannerAd = null;
            }
            if (adContainer != null) {
                adContainer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            Zapr.start(context);
            com.redbricklane.zapr.basesdk.Log.setLogLevel(LOG_LEVEL.debug);
            
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zapr Load Interstitial");
            if(mInterstitialAd == null){
                mInterstitialAd = new ZaprInterstitialAd(mContext);
            }
            mInterstitialAd.setTestModeEnabled(true);
            mInterstitialAd.setAdUnitId(ADUNIT_ID);
            mInterstitialAd.setAdServerUrl("http://asg.zapr.in/zapr");
            mInterstitialAd.setRequestForPermissionsEnabled(false);
            mInterstitialAd.loadInterstitialAd();
            
            setcallback();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }


    private void setcallback() {
        try{

            ZaprInterstitialAdEventListener adEventListener = new ZaprInterstitialAdEventListener() {
                @Override
                public void onInterstitialAdLoaded() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad loaded");
                    adEventReady(null);
                }

                @Override
                public void onInterstitialAdShown() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr ad shown");
                    adEventImpression();

                }

                @Override
                public void onInterstitialAdClicked() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad Clicked");
                    adEventClicked();
                }

                @Override
                public void onInterstitialAdClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad closed");
                    adEventCompleted();
                }

                @Override
                public void onFailedToLoadInterstitialAd(int i, String s) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad failed to load ::"+i+"::"+s);
                    adEventLoadFailed();
                }
            };
            if(mInterstitialAd != null) {
                mInterstitialAd.setInterstitialAdEventListener(adEventListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Show Interstitial");
            if ( mInterstitialAd != null && mInterstitialAd.isInterstitialAdLoaded()) {
                mInterstitialAd.showInterstitialAd();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad failed to show");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr Ad failed to show");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAdmPause() {
        super.onAdmPause();
    }

    @Override
    public void onAdmResume() {
        super.onAdmResume();
    }

    @Override
    public void onAdmDestroy() {
        super.onAdmDestroy();
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }

        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }
}
