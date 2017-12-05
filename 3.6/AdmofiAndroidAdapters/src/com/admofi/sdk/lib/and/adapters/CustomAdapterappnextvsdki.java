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
import com.appnext.ads.AdsError;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appnext.base.Appnext;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.core.callbacks.OnAdOpened;
import com.appnext.core.callbacks.OnVideoEnded;
/**
 * Created by apple on 12/09/17.
 */

public class CustomAdapterappnextvsdki extends CustomAdapterImpl {

    private String PLACEMENT_ID = "";
    private RewardedVideo rewarded_ad = null;
    private int cpVal = 0;


    public CustomAdapterappnextvsdki(Context context) {
        super(context);
    }

    public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
        super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi checking classes  ");
            Class.forName("com.appnext.base.Appnext");
            Class.forName("com.appnext.ads.fullscreen.RewardedVideo");
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
        AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi LoadBanner");
        adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
    }

    private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
        try {
            cpVal = mAdCurrent.getCPVValue();
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi load interstitial");
            Appnext.init(context);
            if(rewarded_ad == null) {
                rewarded_ad = new RewardedVideo(mContext, PLACEMENT_ID);
            }
            setcallback();
            rewarded_ad.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
        }
    }


    private void setcallback() {
        rewarded_ad.setOnAdLoadedCallback(new OnAdLoaded() {
			@Override
			public void adLoaded() {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi adLoaded");
                adEventReady(null);
				
			}
        });
        rewarded_ad.setOnAdOpenedCallback(new OnAdOpened() {
            @Override
            public void adOpened() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi adOpened");
                adEventImpression();
            }
        });
        rewarded_ad.setOnAdClickedCallback(new OnAdClicked() {
            @Override
            public void adClicked() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi adClicked");
                adEventClicked();
            }
        });
        rewarded_ad.setOnAdClosedCallback(new OnAdClosed() {
            @Override
            public void onAdClosed() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi onAdClosed");
                adEventCompleted();
            }
        });
        rewarded_ad.setOnAdErrorCallback(new OnAdError() {
            @Override
            public void adError(String error) {

				if (error.equals(AdsError.NO_ADS)) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi onfailed:: NO FILL");
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				} else if (error.equals(AdsError.CONNECTION_ERROR)) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi onfailed: INTERNAL ERROR");
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
				} else {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi onfailed: UNKNOWN");
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}

			}
		});
        rewarded_ad.setOnVideoEndedCallback(new OnVideoEnded() {
            @Override
            public void videoEnded() {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi Ad Ended && Rewarded");
                try {
                    if (cpVal > 0) {
                        AdmofiView.percentVideoWatched = 100;
                        adEventRewardSuccess(new AdmofiReward("Appnext Points", cpVal, true, "Appnext Reward Success"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean showinterstitial() {
        try {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi Ad Show interstitial");
            if (rewarded_ad != null && rewarded_ad.isAdLoaded()) {
                rewarded_ad.showAd();
            } else {
                AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Appnext video Ad Show interstitial failed");
                adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            }
        } catch (Exception e) {
            AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi appnextvi Ad Show interstitial failed");
            adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onAdmResume() {
        super.onAdmResume();
    }

    @Override
    public void onAdmDestroy() {
        super.onAdmDestroy();
    }

    @Override
    public void onAdmPause() {
        super.onAdmPause();
    }
}
