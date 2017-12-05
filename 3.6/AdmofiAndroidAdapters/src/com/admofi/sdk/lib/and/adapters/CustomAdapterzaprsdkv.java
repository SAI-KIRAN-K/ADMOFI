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
import com.redbricklane.zapr.videosdk.ZaprVideoAd;
import com.redbricklane.zapr.videosdk.ZaprVideoAdEventListener;
import com.redbricklane.zapr.videosdk.net.VideoAdResponse;
import com.redbricklane.zaprSdkBase.Zapr;

/**
 * Created by apple on 13/09/17.
 */

public class CustomAdapterzaprsdkv extends CustomAdapterImpl {

    private String ADUNIT_ID = "";
    private static boolean isinitialised = false;
    ZaprVideoAd mVideoAd = null;
    private ZaprBannerAd mBannerAd = null;
    private RelativeLayout adContainer = null;

    public CustomAdapterzaprsdkv(Context context) {
        super(context);
    }


    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Zapr video checking classes  ");
            Class.forName("com.redbricklane.zapr.videosdk.ZaprVideoAd");
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

            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video loadInterstitial");
            Zapr.start(context);
            if(mVideoAd == null){
                mVideoAd = new ZaprVideoAd(mContext);
            }
            mVideoAd.setAdUnitId(ADUNIT_ID);
            setcallback();
            mVideoAd.setRequestForPermissionsEnabled(false);
            mVideoAd.loadAd();

            /*if (!CustomAdapterzaprsdkv.isinitialised) {
                mVideoAd = new ZaprVideoAd(mContext);
                mVideoAd.setAdUnitId(ADUNIT_ID);
                CustomAdapterzaprsdkv.isinitialised = true;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
        }
    }


    private void setcallback() {

        try{
            ZaprVideoAdEventListener adEventListener = new ZaprVideoAdEventListener() {
                @Override
                public void onVideoAdError(int i, String s) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video Ad Failed ::"+i+":::"+ s);
                    adEventLoadFailed();
                }

                @Override
                public void onResponseReceived(VideoAdResponse videoAdResponse) {

                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video ad response receieved");
                }

                @Override
                public void onAdReady(VideoAdResponse videoAdResponse, String s) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video onAdReady");
                    adEventReady(null);
                }

                @Override
                public void onVideoAdStarted() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video onVideoAdStarted");
                    adEventImpression();
                }

                @Override
                public void onVideoAdClicked() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video onVideoAdClicked");
                    adEventClicked();
                }

                @Override
                public void onVideoAdFinished() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video onVideoAdFinished");
                    adEventCompleted();
                }

                @Override
                public void onVideoPlayerClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video onVideoPlayerClosed");
                }
            };
            mVideoAd.setZaprVideoAdEventListener(adEventListener);
        }catch (Exception e){
            e.printStackTrace();
            adEventLoadFailed();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video showinterstitial");
            if (mVideoAd != null &&mVideoAd.isVideoAdLoaded()) {
                mVideoAd.showVideoAd();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video showinterstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi zapr video showinterstitial failed");
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
        try {
            if (mVideoAd != null) {
                mVideoAd.destroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
