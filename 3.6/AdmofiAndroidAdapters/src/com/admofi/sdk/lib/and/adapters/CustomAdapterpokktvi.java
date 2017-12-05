package com.admofi.sdk.lib.and.adapters;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;


import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.admofi.sdk.lib.and.offers.AdmofiReward;
import com.pokkt.PokktAds;

public class CustomAdapterpokktvi extends CustomAdapterImpl  {

	private Context mContext = null;
	private boolean isActive = true;
	private int iAdTimeout = AdmofiConstants.THIRDPARTY_TIMEOUT;
	private String screenName = "screenname";

	public CustomAdapterpokktvi(Context context) {
		super(context);
	}

	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi checking classes");
			Class.forName("com.pokkt.PokktAds");

		} catch (Exception e) {
			super.setSupported(false);
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			return;
		}
		super.setSupported(true);
		mContext = super.mContext;
		if (mAdShown.getTpTimeout() > 0) {
			iAdTimeout = mAdShown.getTpTimeout();
		}
		if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_BANNER) {
			loadBanner(super.mContext, mAdShown);
		} else if (mAdShown.getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) {
			loadInterstitial(super.mContext, mAdShown);
		} else {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}

	private void loadBanner(Context context, AdmofiAd mAdCurrent) {
		adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
	}

	private void loadInterstitial(final Context context, AdmofiAd mAdCurrent) {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi loadInterstitial");
			try {
				if (!mAdCurrent.getAdapterKey(2).equalsIgnoreCase("")) {
					screenName = mAdCurrent.getAdapterKey(2);
				}

			}catch (Exception e){
				e.printStackTrace();
			}

			PokktAds.setThirdPartyUserId("123456");
			PokktAds.setPokktConfig(mAdCurrent.getAdapterKey(1), mAdCurrent.getAdapterKey(0), ((Activity) context));
			PokktAds.VideoAd.cacheRewarded(screenName);
			setCallbacks();

		} catch (Exception e) {
			e.printStackTrace();
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}

	@Override
	public boolean showinterstitial() {
		try {
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi incentive ShowInter");
			AdmofiView.percentVideoWatched = 0;
			if ((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (mContext!=null)) {
				PokktAds.VideoAd.showRewarded(screenName);
				return true;
			} else {
				AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi incentive ShowInter failed");
				adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi incentive ShowInter failed");
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
		return false;
	}

	private void setCallbacks(){
		try {
			PokktAds. VideoAd. setDelegate(new PokktAds.VideoAd.VideoAdDelegate() {
				@Override
				public void videoAdCachingCompleted(String screenName, boolean isRewarded, double reward) {
                    AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdCachingCompleted :: "+screenName);
					if (isActive) {
						adEventReady(null);
					}
				}

				@Override
				public void videoAdCachingFailed(String s, boolean b, String s1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdFailedToShow :: "+ s);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				}

				@Override
				public void videoAdDisplayed(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdDisplayed :: " + b);
					adEventImpression();
				}

				@Override
				public void videoAdFailedToShow(String s, boolean b, String s1) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdFailedToShow :: "+ s);
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
				}

				@Override
				public void videoAdClosed(String screenName, boolean isRewarded) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdClosed is Active :: " + isActive);
					 if (isActive) {
						 isActive = false;
						adEventCompleted();
					 }
				}

				@Override
				public void videoAdSkipped(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdSkipped :: " + b);
				}

				@Override
				public void videoAdCompleted(String s, boolean b) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdCompleted :: " + b);
				}

				@Override
				public void videoAdGratified(String s, boolean b, double v) {
					try {
						AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi incentive ad reward response :: " + v);
						float coins = (float) v;
						if (mContext != null) {

							int val = 0;
							try {
								AdmofiUtil.logMessage(null, Log.DEBUG,"Admofi pokktvi Event points earned response  :: " + coins);
								float val1 = coins;
								val = (int) val1;
							} catch (Exception e) {
								AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi Event points earned ::: " + e);
								e.printStackTrace();
							}
							if (val>0) {
								AdmofiView.percentVideoWatched = 100;
								adEventRewardSuccess(new AdmofiReward("Pokkt Points", val, true, "Pokkt Reward Success"));

							}
							//AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi Event points earned :: " + val);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void videoAdAvailabilityStatus(String screenName, boolean isRewarded, boolean availability) {
					AdmofiUtil.logMessage(null, Log.DEBUG, "Admofi pokktvi videoAdAvailabilityStatus :: " + availability);
					if (!availability) {
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);
					}
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
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