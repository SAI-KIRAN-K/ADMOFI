package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.InterstitialAd;


public class CustomAdapteramazon extends CustomAdapterImpl {
	InterstitialAd interstitialAd;	 
	
	public CustomAdapteramazon(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.amazon.device.ads.Ad");
			Class.forName("com.amazon.device.ads.AdError");
			Class.forName("com.amazon.device.ads.AdLayout");
			Class.forName("com.amazon.device.ads.AdProperties");
			Class.forName("com.amazon.device.ads.DefaultAdListener");
			Class.forName("com.amazon.device.ads.AdRegistration");	
			Class.forName("com.amazon.device.ads.InterstitialAd");
			
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(Context context, AdmofiAd mAdCurrent) {	
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Load Banner");
        AdLayout amAdView = new AdLayout((Activity) context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        amAdView.setLayoutParams(params);        
        amAdView.setListener(new DefaultAdListener(){

			@Override
			public void onAdCollapsed(Ad ad) {				
				super.onAdCollapsed(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Completed");
				adEventCompleted();
			}

			@Override
			public void onAdDismissed(Ad ad) {
				super.onAdDismissed(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Completed");
				adEventCompleted();
			}

			@Override
			public void onAdExpanded(Ad ad) {				
				super.onAdExpanded(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Clicked");
				adEventClicked();
			}

			@Override
			public void onAdFailedToLoad(Ad ad, AdError error) {				
				super.onAdFailedToLoad(ad, error);		
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Failed");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);				
			}

			@Override
			public void onAdLoaded(Ad ad, AdProperties adProperties) {				
				super.onAdLoaded(ad, adProperties);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Ready");
				adEventReady((View)ad);
			}
        	
        });
        
        try {      
            AdRegistration.setAppKey(mAdCurrent.getAdapterKey(0));
        } catch (final Exception e) {       	
            
            return;
        }
        amAdView.loadAd();
	}
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon LoadInter");
	    interstitialAd = new InterstitialAd((Activity) context);
        interstitialAd.setListener(new DefaultAdListener(){

			@Override
			public void onAdCollapsed(Ad ad) {				
				super.onAdCollapsed(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Completed");
				adEventCompleted();
			}

			@Override
			public void onAdDismissed(Ad ad) {
				super.onAdDismissed(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Completed");
				adEventCompleted();
			}

			@Override
			public void onAdExpanded(Ad ad) {
				super.onAdExpanded(ad);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Clicked");
				adEventClicked();
			}

			@Override
			public void onAdFailedToLoad(Ad ad, AdError error) {				
				super.onAdFailedToLoad(ad, error);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Failed");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			}

			@Override
			public void onAdLoaded(Ad ad, AdProperties adProperties) {
				super.onAdLoaded(ad, adProperties);
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event Ready");
				adEventReady(null);
			}        	
        });
        
        try {        	
            AdRegistration.setAppKey(mAdCurrent.getAdapterKey(0));
        } catch (final IllegalArgumentException e) {            
            return;
        }
        interstitialAd.loadAd();
	}
	@Override
	public boolean showinterstitial() {	
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Amazon Event ShowInter");
			if(getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
				if(interstitialAd != null) {
					interstitialAd.showAd();
					adEventImpression();
					return true;
				}
			}
		} catch (Exception e) {	
			return false;
		}		
		return false;
	}
}

