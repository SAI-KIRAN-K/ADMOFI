package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.appnext.ads.AdsError;
import com.appnext.base.Appnext;
import com.appnext.core.callbacks.OnAdOpened;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;


/**
 * Created by apple on 12/09/17.
 */

public class CustomAdapterappnextsdk extends CustomAdapterImpl {

    private String PLACEMENT_ID = "";
    Interstitial interstitial_Ad = null;
    private static boolean isinitialised = false;

    public CustomAdapterappnextsdk(Context context) {
        super(context);
    }


    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext checking classes  ");
            Class.forName("com.appnext.base.Appnext");
            Class.forName("com.appnext.ads.interstitial.Interstitial");
        } catch (Exception e) {
            super.setSupported(false);
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
            return;
        }
        PLACEMENT_ID = mAdShown.getAdapterKey(0);
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext Load Interstitial");
            Appnext.init(context);
            if(interstitial_Ad == null) {
                interstitial_Ad = new Interstitial(mContext, PLACEMENT_ID);
            }
            setcallback();
            interstitial_Ad.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }


    private void setcallback() {

        try{
            interstitial_Ad.setOnAdLoadedCallback(new OnAdLoaded() {
                @Override
				public void adLoaded() {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext adLoaded");
                    adEventReady(null);
					
				}
            });

            interstitial_Ad.setOnAdOpenedCallback(new OnAdOpened() {
                @Override
                public void adOpened() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext adOpened");
                    adEventImpression();
                }
            });

            interstitial_Ad.setOnAdClickedCallback(new OnAdClicked() {
                @Override
                public void adClicked() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext adClicked");
                    adEventClicked();
                }
            });

            interstitial_Ad.setOnAdClosedCallback(new OnAdClosed() {
                @Override
                public void onAdClosed() {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext onAdClosed");
                    adEventCompleted();
                }
            });

            interstitial_Ad.setOnAdErrorCallback(new OnAdError() {
                @Override
                public void adError(String error) {

                    if (error.equals(AdsError.NO_ADS)) {                    
                        //case AdsError.NO_ADS:
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext onAdFailed: NO_ADS");
                            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
                    } else if(error.equals(AdsError.CONNECTION_ERROR)) {
                        //case AdsError.CONNECTION_ERROR:
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext onAdFailed: NO_ADS");
                            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
                      
                    } else {
                        
                            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext onAdFailed: NO_ADS");
                            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
                    }
                    }
                
            });

        }catch (Exception e){
            e.printStackTrace();
            adEventLoadFailed();
        }
    }


    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext Show Interstitial");
            if ( interstitial_Ad != null && interstitial_Ad.isAdLoaded()) {
                interstitial_Ad.showAd();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext Show Interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAdmDestroy() {
        super.onAdmDestroy();
    }

    @Override
    public void onAdmPause() {
        super.onAdmPause();
    }

    @Override
    public void onAdmResume() {
        super.onAdmResume();
    }
}
