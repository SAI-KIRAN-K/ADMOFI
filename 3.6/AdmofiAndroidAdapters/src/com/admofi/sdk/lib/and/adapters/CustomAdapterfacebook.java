package com.admofi.sdk.lib.and.adapters;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;


public class CustomAdapterfacebook extends CustomAdapterImpl{

	Activity mAct = null;
	InterstitialAd interstitialAd = null;
	private AdView adViewBanner;
	private String PLACEMENT_ID = "";
	RelativeLayout adContainer = null;

	public CustomAdapterfacebook(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {

			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook class invoked :: ");
			Class.forName("com.facebook.ads.InterstitialAd");
		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_NATIVE) {
			loadNative(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {

		   PLACEMENT_ID = mAdCurrent.getAdapterKey(0);

		  int width = mAdCurrent.getWidth();//toPixelUnits(context,mAdCurrent.getunScaledWidth());
		  int height= mAdCurrent.getHeight();
		  adContainer = new RelativeLayout(context);
		  RelativeLayout.LayoutParams paramparent = new RelativeLayout.LayoutParams(width,height);
		  adContainer.setLayoutParams(paramparent);
		  RelativeLayout.LayoutParams bannerLayoutParams =new RelativeLayout.LayoutParams(width,height);
		  int requiredHeight = mAdCurrent.getunScaledHeight();
		  if(requiredHeight == 50){
			  adViewBanner = new AdView(context, PLACEMENT_ID,AdSize.BANNER_HEIGHT_50);
		  } else if (requiredHeight == 90) {
			  adViewBanner = new AdView(context, PLACEMENT_ID,AdSize.BANNER_HEIGHT_90);
		  } else {
			  adViewBanner = new AdView(context, PLACEMENT_ID,AdSize.BANNER_320_50);
		  }

		  adViewBanner.setAdListener(new AdListener() {
			  @Override
			  public void onError(Ad paramAd, AdError arg1) {
				  try {
					  if(arg1!=null) {
						  AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Banner Ad failed :: "+ arg1.getErrorCode() + " :: "+arg1.getErrorMessage());

						  if(arg1.getErrorCode() == AdError.NO_FILL_ERROR_CODE) {
							  adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
						  } else {
							  adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
						  }
					  }
					  destroyBannerAd();
				  } catch (Exception e) {
					  e.printStackTrace();
				  }
			  }

			  @Override
			  public void onAdLoaded(Ad ad) {
				  try {
					  AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Banner Event onAdLoaded");
					  if(adContainer!=null){
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
			  public void onAdClicked(Ad ad) {
				  AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Banner Event onAdClicked");
			  }

			  @Override
			  public void onLoggingImpression(Ad ad) {

			  }
		  });


		         // Initiate a request to load an ad.
		         adViewBanner.loadAd();
		  adContainer.addView(adViewBanner, bannerLayoutParams);
		 }


	private void loadNative(final Context context,final  AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Load Native");
			PLACEMENT_ID = mAdCurrent.getAdapterKey(0);
			NativeAd nativeAd = new NativeAd(context, PLACEMENT_ID);
			  nativeAd.setAdListener(new AdListener() {

			    @Override
			    public void onError(Ad ad, AdError error) {
			    	AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event onError :: "+error.getErrorCode());
			    	adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
			    }

			    @Override
			    public void onAdLoaded(Ad ad) {
			    	AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event onAdLoaded ");

			    	adEventReady(null);

			    }

			    @Override
			    public void onAdClicked(Ad ad) {

			    }

				  @Override
				  public void onLoggingImpression(Ad ad) {

				  }
			  });

			  nativeAd.loadAd();


		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			e.printStackTrace();
		}
	}

	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {

		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook LoadInter");
		PLACEMENT_ID = mAdCurrent.getAdapterKey(0);
		interstitialAd = new InterstitialAd(context, PLACEMENT_ID);
		interstitialAd.setAdListener(new InterstitialAdListener() {
			@Override
			public void onInterstitialDismissed(Ad arg0) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Ad Dismissed");
				destroyAd();
				adEventCompleted();
			}
			@Override
			public void onInterstitialDisplayed(Ad arg0) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Ad onInterstitialDisplayed");
				adEventImpression();
			}

			@Override
			public void onAdClicked(Ad arg0) {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event onAdClicked ");
				adEventClicked();
			}

			@Override
			public void onLoggingImpression(Ad ad) {

			}

			@Override
			public void onAdLoaded(Ad arg0) {

				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Ad ready");
				adEventReady(null);

			}
			@Override
			public void onError(Ad arg0, AdError arg1) {

				if(arg1!=null){
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Event Ad failed :: "+ arg1.getErrorCode() + " :: "+arg1.getErrorMessage());
					destroyAd();
				}
				if(arg1.getErrorCode() == AdError.NO_FILL_ERROR_CODE) {
					destroyAd();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				} else {
					destroyAd();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
				}
			}
		});
		//AdSettings.addTestDevice("b06073aa589d6606cf0db89b6228c30d");

		interstitialAd.loadAd();
	}

	@Override
	public boolean showinterstitial() {
		AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi facebook Show Inter");
		if ((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (interstitialAd!=null)) {
			interstitialAd.show();
			return true;
		}
		return false;
	}

	@Override
	public void onAdmPause() {
		super.onAdmPause();
	}

	@Override
	public void onAdmDestroy() {
		if (interstitialAd != null) {
		    interstitialAd.destroy();
		  }
		super.onAdmDestroy();
	}

	@Override
	public void onAdmResume() {
		super.onAdmResume();

	}


	@Override
	public void vAdmofiCleanup() {
		super.vAdmofiCleanup();
	}



	private void destroyBannerAd(){
		try {
			if (adViewBanner != null) {
				adViewBanner.setAdListener(null);
				adViewBanner.destroy();
				adViewBanner = null;
			}
			if (adContainer != null) {
				adContainer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void destroyAd(){
		try {
			if (interstitialAd != null) {
				interstitialAd.setAdListener(null);
				interstitialAd = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}