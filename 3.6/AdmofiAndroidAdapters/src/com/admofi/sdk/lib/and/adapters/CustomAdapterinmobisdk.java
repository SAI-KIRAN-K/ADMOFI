package com.admofi.sdk.lib.and.adapters;

import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import android.util.Log;
import android.widget.RelativeLayout;
import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiBanner.BannerAdListener;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.InMobiNative.NativeAdListener;
import com.inmobi.sdk.InMobiSdk;

public class CustomAdapterinmobisdk extends CustomAdapterImpl {
	private InMobiBanner bannerAdView;
	private InMobiInterstitial interstitial;
	private RelativeLayout adContainer =null;
		
	private String ACCOUNT_ID = "";
	private String PLACEMENT_ID = "";
	Context incontext = null;
	
	public CustomAdapterinmobisdk(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi classs invoked :: ");
			Class.forName("com.inmobi.ads.InMobiBanner");			
			Class.forName("com.inmobi.ads.InMobiInterstitial");
			
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
		}  else if(mAdShown.getAdType() == AdmofiConstants.AD_TYPE_NATIVE) {
			loadNative(super.mContext, mAdShown);
		}  else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}		
	}	
	
	
	
	private void loadNative(final Context context,final  AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event Load Native");
			
			ACCOUNT_ID = mAdCurrent.getAdapterKey(0);
			PLACEMENT_ID = mAdCurrent.getAdapterKey(1);
	    					
			long lPlacementid = -1;
			try {
				lPlacementid = Long.parseLong(PLACEMENT_ID);
			} catch (Exception e) {
				e.printStackTrace();
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi loading Ad failed invalid placement id");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
				return ;
			}
			
			InMobiSdk.init((Activity)context, ACCOUNT_ID);

			NativeAdListener nListener = new NativeAdListener() {
				@Override
				public void onAdLoadSucceeded(InMobiNative paramInMobiNative) {

					try {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Native onAdLoadSucceeded");

						paramInMobiNative.getCustomAdContent();//edited


						adEventReady(null);
					} catch (Exception e) {
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
						e.printStackTrace();

					}
				}

				@Override
				public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {

					try {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Native onAdLoadFailed :: "+inMobiAdRequestStatus.getMessage());

						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);

					} catch (Exception e) {
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
						e.printStackTrace();
					}
				}

				@Override
				public void onAdFullScreenDismissed(InMobiNative inMobiNative) {

				}

				@Override
				public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {

				}

				@Override
				public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {

				}

				@Override
				public void onUserWillLeaveApplication(InMobiNative inMobiNative) {

				}

				@Override
				public void onAdImpressed( InMobiNative inMobiNative) {

				}

				@Override
				public void onAdClicked(InMobiNative inMobiNative) {

				}

				@Override
				public void onMediaPlaybackComplete(InMobiNative inMobiNative) {

				}

				@Override
				public void onAdStatusChanged(InMobiNative inMobiNative) {

				}
			};


			
			InMobiNative nativeAd = new InMobiNative((Activity)context,lPlacementid, nListener);
			nativeAd.load();
			
			
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			e.printStackTrace();
		}
	}	
	
	private void loadBanner(final Context context,final  AdmofiAd mAdCurrent) {	
		
		((Activity)context).runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				try {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event Load Banner");
					ACCOUNT_ID = mAdCurrent.getAdapterKey(0);
					PLACEMENT_ID = mAdCurrent.getAdapterKey(1);
			    					
					long lPlacementid = -1;
					try {
						lPlacementid = Long.parseLong(PLACEMENT_ID);
					} catch (Exception e) {
						e.printStackTrace();
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi loading Ad failed invalid placement id");
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
						return ;
					}
					
					InMobiSdk.init((Activity)context, ACCOUNT_ID);
					int width = mAdCurrent.getWidth();//toPixelUnits(context,mAdCurrent.getunScaledWidth());
		            int height= mAdCurrent.getHeight();//toPixelUnits(context,mAdCurrent.getunScaledHeight());     
		            
					adContainer = new RelativeLayout(context);
					RelativeLayout.LayoutParams paramparent = new RelativeLayout.LayoutParams(width,height);
					adContainer.setLayoutParams(paramparent);
			        
					 RelativeLayout.LayoutParams bannerLayoutParams =
			                    new RelativeLayout.LayoutParams(width,height);
			            //bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			            //bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			            			
					
					bannerAdView = new InMobiBanner((Activity)context, lPlacementid);					
					bannerAdView.setEnableAutoRefresh(false);
					bannerAdView.setRefreshInterval(60);
					bannerAdView.setAnimationType(InMobiBanner.AnimationType.ANIMATION_OFF);
					adContainer.addView(bannerAdView, bannerLayoutParams);
					bannerAdView.setListener(new BannerAdListener() {
						
						@Override
						public void onUserLeftApplication(InMobiBanner arg0) {
							
											
						}
						
						@Override
						public void onAdRewardActionCompleted(InMobiBanner arg0,
								Map<Object, Object> arg1) {
												
						}
						
						@Override
						public void onAdLoadSucceeded(InMobiBanner arg0) {
							try {
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Ad Event onAdLoadSucceeded "+arg0);
								
								adEventReady(adContainer);
							} catch (Exception e) {
								e.printStackTrace();
							}							
						}
						
						@Override
						public void onAdLoadFailed(InMobiBanner arg0, InMobiAdRequestStatus arg1) {
							try {
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onAdLoadFailed Message :: "+arg1.getMessage());
								if(arg1!=null && arg1.getStatusCode() == StatusCode.NO_FILL) {
									adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
								} else {
									adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
								}
							} catch (Exception e) {
								e.printStackTrace();
								adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
							}
						}
						
						@Override
						public void onAdInteraction(InMobiBanner arg0, Map<Object, Object> arg1) {
							try{
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onBannerAdClicked");
								adEventClicked();
							}catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						
						@Override
						public void onAdDisplayed(InMobiBanner arg0) {
							
							
						}
						
						@Override
						public void onAdDismissed(InMobiBanner arg0) {
							
							AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event AdDismissed");
						}
					});
					bannerAdView.load();
					
				} catch (Exception e) {			
					e.printStackTrace();
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);			
				}
				
			}
		});
		
	
	}
	
	private int toPixelUnits(Context ctx,int dipUnit) {
		try {
			float density = ctx.getResources().getDisplayMetrics().density;
	        return Math.round(dipUnit * density);
		} catch (Exception e) {
			e.printStackTrace();
			return dipUnit;
		}        
    }
	
	
	
	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {
			
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event Load Interstitial");
			ACCOUNT_ID = mAdCurrent.getAdapterKey(0);
			PLACEMENT_ID = mAdCurrent.getAdapterKey(1);
	        
			//InMobiSdk.init(context, ACCOUNT_ID);
			InMobiSdk.init((Activity)context, ACCOUNT_ID);
			long lPlacementid = -1;
			try {
				lPlacementid = Long.parseLong(PLACEMENT_ID);
			} catch (Exception e) {
				e.printStackTrace();
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Invalid placement id :: "+PLACEMENT_ID);
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
				return ;
			}
			
			interstitial = new InMobiInterstitial((Activity)context, lPlacementid, new InMobiInterstitial.InterstitialAdListener2() {
				
				@Override
				public void onUserLeftApplication(InMobiInterstitial arg0) {
					
				}
				
				@Override
				public void onAdWillDisplay(InMobiInterstitial arg0) {
					
				}
				
				@Override
				public void onAdDisplayed(InMobiInterstitial arg0) {
					
				}
				
				@Override
				public void onAdRewardActionCompleted(InMobiInterstitial arg0, Map<Object, Object> arg1) {
					
				}
				
				@Override
				public void onAdReceived(InMobiInterstitial arg0) {
					// both onadreceived and onadloadsucceeded are same callbacks
					
				}
				
				@Override
				public void onAdLoadSucceeded(final InMobiInterstitial paramInMobiInterstitial) {
					
					try {
						((Activity)incontext).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								try {
									AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onAdLoadSucceeded isReady :: "+paramInMobiInterstitial.isReady());
									if(paramInMobiInterstitial!=null && paramInMobiInterstitial.isReady()){
										adEventReady(null);
									} else {
										adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
									}	
								} catch (Exception e) {
									e.printStackTrace();
									adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
								}
														
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					}	
				}
				
				@Override
				public void onAdLoadFailed(InMobiInterstitial arg0, InMobiAdRequestStatus paramInMobiAdRequestStatus) {
					try {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onAdLoadFailed "+paramInMobiAdRequestStatus.getMessage());
						if(paramInMobiAdRequestStatus!=null && paramInMobiAdRequestStatus.getStatusCode() == StatusCode.NO_FILL) {
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
						} else {
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
						}
					} catch (Exception e) {
						e.printStackTrace();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					}
					
				}
				
				@Override
				public void onAdInteraction(InMobiInterstitial arg0, Map<Object, Object> arg1) {
					try{
						
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onAdDisplayFailed(InMobiInterstitial arg0) {
					try{
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onAdDisplayFailed ");
						if(arg0 != null) {
							adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
						} 
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onAdDismissed(InMobiInterstitial arg0) {
					try{
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event onAdDismissed :: ");
						adEventCompleted();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
			interstitial.load();
			
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}		
	}

	
	@Override
	public boolean showinterstitial() {
		try {
			if(getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
				if((interstitial != null) && (interstitial.isReady())) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi Inmobi Event ShowInter"+interstitial);
					interstitial.show();
					adEventImpression();				
					return true;
				} else {
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}		
		return false;
	}
}

