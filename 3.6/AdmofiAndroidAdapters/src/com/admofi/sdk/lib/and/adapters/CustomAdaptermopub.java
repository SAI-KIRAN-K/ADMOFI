/*
package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;


import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.MoPubView.BannerAdListener;

public class CustomAdaptermopub extends CustomAdapterImpl {
	private MoPubView bannerAdView;
	private MoPubInterstitial mInterstitial;
	Context incontext = null;
	
	public CustomAdaptermopub(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			
			Class.forName("com.mopub.mobileads.MoPubView");
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		incontext = super.mContext;
		if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			//Nothing else implemented
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}
	
	private void loadBanner(final Context context,final  AdmofiAd mAdCurrent) {		
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event Load Banner ");
			bannerAdView = new MoPubView(context);
			bannerAdView.setAdUnitId(mAdCurrent.getAdapterKey(0)); 
			bannerAdView.setBannerAdListener(new BannerAdListener() {
				
				@Override
				public void onBannerLoaded(MoPubView arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onBannerLoaded :: "+arg0);
					adEventReady(bannerAdView);
				}
				
				@Override
				public void onBannerFailed(MoPubView arg0, MoPubErrorCode arg1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onInterstitialFailed :: "+arg1);
					if(arg1 == MoPubErrorCode.NO_FILL)
					{
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
					}
					
					else if(arg1 == MoPubErrorCode.ADAPTER_NOT_FOUND)
					{
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOADAPTER);
					}
					
					else if(arg1 == MoPubErrorCode.NETWORK_TIMEOUT)
					{
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_TIMEOUT);
					}
					
					else {
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
					}
				}
				
				@Override
				public void onBannerExpanded(MoPubView arg0) {
					
					
				}
				
				@Override
				public void onBannerCollapsed(MoPubView arg0) {
					
					
				}
				
				@Override
				public void onBannerClicked(MoPubView arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onBannerClicked");
					adEventClicked();
				}
			});
			bannerAdView.loadAd();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}
			
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event Load Interstitial ");
			mInterstitial = new MoPubInterstitial((Activity)context,mAdCurrent.getAdapterKey(0) );	
			mInterstitial.setInterstitialAdListener(new InterstitialAdListener() {
				
				@Override
				public void onInterstitialShown(MoPubInterstitial arg0) {
					adEventImpression();
					
				}
				
				@Override
				public void onInterstitialLoaded(MoPubInterstitial arg0) {
					try {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onInterstitialLoaded :: "+arg0.isReady());
						if(mInterstitial!=null && mInterstitial.isReady()){
							adEventReady(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					}
					
				}
				
				@Override
				public void onInterstitialFailed(MoPubInterstitial arg0, MoPubErrorCode arg1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onInterstitialFailed "+arg1);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);					
				}
				
				@Override
				public void onInterstitialDismissed(MoPubInterstitial arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onInterstitialDismissed ");
					adEventCompleted();
				}
				
				@Override
				public void onInterstitialClicked(MoPubInterstitial arg0) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event onInterstitialClicked  ");
					adEventClicked();					
				}
			});
			mInterstitial.load();
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			e.printStackTrace();
		}		
	}

	
	@Override
	public boolean showinterstitial() {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi mopub Event showinterstitial :: ");
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL ) && (mInterstitial!=null) && (mInterstitial.isReady())) {
				mInterstitial.show();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}		
		
	}
}
*/
