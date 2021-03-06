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
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJVideoListener;
import com.tapjoy.Tapjoy;

public class CustomAdaptertapjoyi extends CustomAdapterImpl {
		
	private Context mcontexti = null;
	TJPlacement directPlayPlacement = null; 
	public CustomAdaptertapjoyi(Context context) {
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
		mcontexti = super.mContext;		
		Tapjoy.connect(mcontexti, mAdShown.getAdapterKey(0), null, new TJConnectListener() {
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
			AdmofiUtil.logMessage(null, Log.DEBUG, "admofi tapjoyi LoadInter");
			directPlayPlacement = new TJPlacement(mcontexti, "video_unit", new TJPlacementListener() {
				
				@Override
				public void onRewardRequest(TJPlacement arg0, TJActionRequest arg1,	String arg2, int arg3) {
					
				}
				
				@Override
				public void onRequestSuccess(TJPlacement arg0) {
					
					
				}
				
				@Override
				public void onRequestFailure(TJPlacement arg0, TJError arg1) {
					
					
				}
				
				@Override
				public void onPurchaseRequest(TJPlacement arg0, TJActionRequest arg1,
						String arg2) {
					
					
				}
				
				@Override
				public void onContentShow(TJPlacement arg0) {
					
					
				}
				
				@Override
				public void onContentReady(TJPlacement arg0) {
					
					if(arg0.isContentReady()){
						adEventReady(null);
					}
				}
				
				@Override
				public void onContentDismiss(TJPlacement arg0) {
					try{
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyi onViewDidClose");
						Tapjoy.getCurrencyBalance(coinsListner);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			});
			directPlayPlacement.requestContent();

			// NOTE:  The get/spend/award currency methods will only work if your virtual currency
			// is managed by Tapjoy.
			//
			// For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
			// must be called after requestTapjoyConnect.

			// Setup listener for Tapjoy currency callbacks
			Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
				@Override
				public void onEarnedCurrency(String currencyName, int amount) {
					
				}
			});

			// Setup listener for Tapjoy view callbacks
		
			
			
			/*Tapjoy.setTapjoyViewListener(new TJViewListener() {
				@Override
				public void onViewWillOpen(int viewType) {
					
				}

				@Override
				public void onViewWillClose(int viewType) {
					
				}

				@Override
				public void onViewDidOpen(int viewType) {
					
				}

				@Override
				public void onViewDidClose(int viewType) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoy onViewDidClose");
					Tapjoy.getCurrencyBalance(coinsListner);
				}
			});*/

			// Setup listener for Tapjoy video callbacks
			Tapjoy.setVideoListener(new TJVideoListener() {
				@Override
				public void onVideoStart() {
					
				}

				@Override
				public void onVideoError(int statusCode) {
					
				}

				@Override
				public void onVideoComplete() {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Tapjoyi onVideoComplete");

					// Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
					Tapjoy.getCurrencyBalance(coinsListner);
				}
			});
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}		
	}
	
	
	
	TJGetCurrencyBalanceListener coinsListner = new TJGetCurrencyBalanceListener() {
		
		@Override
		public void onGetCurrencyBalanceResponseFailure(String arg0) {
										
		}
		
		@Override
		public void onGetCurrencyBalanceResponse(String arg0, int amount) {
			AdmofiUtil.logMessage("admofi tapjoyi: ", Log.DEBUG, "earnnn : "+amount);					
			if(amount!=0){
				AdmofiView.percentVideoWatched = 100;
				adEventRewardSuccess(new AdmofiReward("Tap Points", amount ,true,"Tapjoy success"));
			} else {
				adEventRewardFailed(new AdmofiReward("Tap Points", amount ,false,"Tapjoy reward failed"));
			}		
		}
	};
	
/*	
	private void loadBanner(){
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "admofi tapjoyi LoadBanner");
			
		} catch (Exception e) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "admofi tapjoyi Event Failed");
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}		
	}*/
	
	@Override
	public boolean showinterstitial() {		
		if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (directPlayPlacement!=null) && (directPlayPlacement.isContentReady())) {
			AdmofiUtil.logMessage(null, Log.DEBUG, "admofi tapjoyi ShowInter");
			AdmofiView.percentVideoWatched = 0;
			directPlayPlacement.showContent();
			return true;			
		}
		return false;
	}
	
	@Override
	public void onAdmStart() 
	{		
		super.onAdmStart();		
	}
	
	@Override
	public void onAdmStop() 
	{
		super.onAdmStop();		
	}
}