package com.admofi.sdk.lib.and.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;

public class CustomAdaptertapjoyoi extends CustomAdapterImpl {
		
	private Context cntxt = null;	
	private AdmofiView admview = null;
	private AdmofiAd mAdmAd = null;	
	private TJPlacement offerwallPlacement = null;
	
		
	public CustomAdaptertapjoyoi(Context context) {
		super(context);
	}
	
	public void loadAd(Handler loadingCompletedHandler,  AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler,  madView, mAdShown, sAdIdentifier);
		try {			
			Class.forName("com.tapjoy.TJPlacement");
			Class.forName("com.tapjoy.TJConnectListener");			
						
		} catch (Exception e) {			
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);		
		cntxt = super.mContext;
		
		admview = madView;
		mAdmAd = mAdShown;
		Tapjoy.connect(cntxt, mAdShown.getAdapterKey(0), null, new TJConnectListener() {
			@Override
			public void onConnectSuccess() {
				loadInterstitial();
			}

			@Override
			public void onConnectFailure() {
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
			}
		});		
	}
	
	private void loadInterstitial() {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyoi Event loadInterstitial ");
			Tapjoy.setUserID("Tapjoy_x_"+admview.getUniqId()+"_x_"+admview.getWalletInstance().getAppId()+"_x_"+AdmofiUtil.getApplicationPackage(cntxt)+"_x_"+mAdmAd.getRequestId());
			Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener()
			{
				@Override
				public void onEarnedCurrency(String arg0, int amount) {
					AdmofiUtil.logMessage("admofi tapjoyoi: ", Log.DEBUG, "earnnn : "+amount);					
					if(amount!=0){
						adEventRewardSuccess(new AdmofiReward("Tap Points", amount ,true,"Tapjoy success"));
					} else {
						adEventRewardFailed(new AdmofiReward("Tap Points", amount ,false,"Tapjoy reward failed"));
					}
				}
			});
			  offerwallPlacement = new TJPlacement(cntxt, "offerwall_unit", new TJPlacementListener() {
		          @Override
		          public void onRequestSuccess(TJPlacement placement) {
		             
		             if (!placement.isContentAvailable()) {
		               adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
		             }		             
		          }

		          @Override
		          public void onRequestFailure(TJPlacement placement, TJError error) {
		        	  AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyoi Event onRequestFailure ::: "+error);
		        	  adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		          }

		          @Override
		          public void onContentReady(TJPlacement placement) {
		        	AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyoi Event onContentReady");            
		            adEventReady(null);
		          }

		          @Override
		          public void onContentShow(TJPlacement placement) {
		             
		          }

		          @Override
		          public void onContentDismiss(TJPlacement placement) {
		             adEventCompleted();
		          }

		          @Override
		          public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
		          }

		          @Override
		          public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
		          }
		      });
		      offerwallPlacement.requestContent();
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}	

	@Override
	public boolean showinterstitial() {
		if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && offerwallPlacement!=null && offerwallPlacement.isContentAvailable()) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyoi Event showinterstitial");  
			offerwallPlacement.showContent();
			adEventImpression();
			return true;
		}
		return false;
	}
	
	@Override
	public void onAdmStart() {		
		super.onAdmStart();		
	}
	
	@Override
	public void onAdmStop() {
		super.onAdmStop();		
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