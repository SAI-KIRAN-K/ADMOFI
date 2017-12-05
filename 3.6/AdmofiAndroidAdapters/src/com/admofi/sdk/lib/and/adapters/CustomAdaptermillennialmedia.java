package com.admofi.sdk.lib.and.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.admofi.sdk.lib.and.AdmofiAd;
import com.admofi.sdk.lib.and.AdmofiConstants;
import com.admofi.sdk.lib.and.AdmofiUtil;
import com.admofi.sdk.lib.and.AdmofiView;
import com.millennialmedia.AppInfo;
import com.millennialmedia.InlineAd;
import com.millennialmedia.InlineAd.InlineErrorStatus;
import com.millennialmedia.InlineAd.InlineListener;
import com.millennialmedia.InterstitialAd;
import com.millennialmedia.InterstitialAd.InterstitialErrorStatus;
import com.millennialmedia.InterstitialAd.InterstitialListener;
import com.millennialmedia.MMException;
import com.millennialmedia.MMSDK;

public class CustomAdaptermillennialmedia extends CustomAdapterImpl {


	private InterstitialAd interAD = null;

	private RelativeLayout adRelativeLayout;


	private InterstitialAd interstitalAd = null;

	public CustomAdaptermillennialmedia(Context context) {
		super(context);
	}
	public void loadAd(Handler loadingCompletedHandler, AdmofiView madView, AdmofiAd mAdShown, String sAdIdentifier) {
		super.loadAd(loadingCompletedHandler, madView, mAdShown, sAdIdentifier);
		try {
			Class.forName("com.millennialmedia.InlineAd");
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
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_UNKNOWN);
		}
	}


	private void loadBanner(final Context context, AdmofiAd mAdCurrent)
	{
		try
		{
			MMSDK.initialize((Activity)context);
			AppInfo appInfo = new AppInfo();
			// Only applicable if migrating from Nexage
			appInfo.setSiteId(mAdCurrent.getAdapterKey(1));
			MMSDK.setAppInfo(appInfo);
			AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : load banner");
			final InlineAd adView;
			int width =  mAdCurrent.getWidth();
			int height= mAdCurrent.getHeight();

			adRelativeLayout = new RelativeLayout(context);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height);
			adRelativeLayout.setLayoutParams(layoutParams);
			adView = InlineAd.createInstance ( mAdCurrent.getAdapterKey(0), (ViewGroup) adRelativeLayout);

			//The AdRequest instance is used to pass additional metadata to the server to improve ad selection
			final InlineAd.InlineAdMetadata inlineAdMetadata = new InlineAd.InlineAdMetadata().setAdSize(InlineAd.AdSize.BANNER);
			adView.request(inlineAdMetadata);
			adView.setListener(new InlineListener() {
				@Override
				public void onResized(InlineAd paramInlineAd, int paramInt1, int paramInt2,
									  boolean paramBoolean) {

				}

				@Override
				public void onResize(InlineAd paramInlineAd, int paramInt1, int paramInt2)
				{

				}

				@Override
				public void onRequestSucceeded(InlineAd paramInlineAd)
				{
					try
					{
						if(mContext!=null){
							((Activity)mContext).runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{

									try {
										if(adRelativeLayout!=null) {
											AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onRequestSucceeded ");
											adEventReady(adRelativeLayout);
										}
									} catch (Exception e) {
										e.printStackTrace();
										adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
									}

								}
							});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
					}
				}

				@Override
				public void onRequestFailed(InlineAd paramInlineAd,InlineErrorStatus paramInlineErrorStatus)
				{
					AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onRequestFailed :: " + paramInlineErrorStatus.getDescription() + "::" + paramInlineErrorStatus.getErrorCode());
					adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_NOFILL);

				}

				@Override
				public void onExpanded(InlineAd paramInlineAd)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onCollapsed(InlineAd paramInlineAd)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onClicked(InlineAd paramInlineAd)
				{
					adEventClicked();

				}

				@Override
				public void onAdLeftApplication(InlineAd paramInlineAd)
				{
					// TODO Auto-generated method stub

				}
			});

		}
		catch (Exception e)
		{
			AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : failed with exception " + e.getMessage());
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}
	}

	private void loadInterstitial(Context context, AdmofiAd mAdCurrent) {
		try {

			AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : load inter");
			MMSDK.initialize((Activity) context);
			AppInfo appInfo = new AppInfo();
			appInfo.setSiteId(mAdCurrent.getAdapterKey(1));
			MMSDK.setAppInfo(appInfo);
			System.out.println("admofi mmedia id::"+mAdCurrent.getAdapterKey(0));
			interAD = InterstitialAd.createInstance(mAdCurrent.getAdapterKey(0));
			InterstitialAd.InterstitialAdMetadata interstitialAdMetadata = new InterstitialAd.InterstitialAdMetadata();

			interAD.setListener(new InterstitialListener() {

				@Override
				public void onShown(InterstitialAd paramInterstitialAd) {
					AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onShown ");
				}

				@Override
				public void onShowFailed(InterstitialAd paramInterstitialAd,
										 InterstitialErrorStatus paramInterstitialErrorStatus) {
					AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onShowFailed ::  " + paramInterstitialErrorStatus);
				}

				@Override
				public void onLoaded(InterstitialAd paramInterstitialAd) {

					if(paramInterstitialAd!=null && paramInterstitialAd.isReady()){
						AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onLoaded ::  " + paramInterstitialAd.isReady());
						adEventReady(null);
					}
				}

				@Override
				public void onLoadFailed(InterstitialAd paramInterstitialAd,
										 InterstitialErrorStatus paramInterstitialErrorStatus) {
					try {
						AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onLoadFailed ::  " + paramInterstitialErrorStatus.getDescription());
						if(paramInterstitialErrorStatus.getErrorCode() == InterstitialErrorStatus.LOAD_FAILED){
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
				public void onExpired(InterstitialAd paramInterstitialAd) {
					AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onExpired ::  ");

				}

				@Override
				public void onClosed(InterstitialAd paramInterstitialAd) {
					AdmofiUtil.logMessage("admofi mmedia: ", Log.DEBUG, "admofi mmedia : onClosed");
					adEventCompleted();

				}

				@Override
				public void onClicked(InterstitialAd paramInterstitialAd) {
					adEventClicked();

				}

				@Override
				public void onAdLeftApplication(InterstitialAd paramInterstitialAd) {
					// TODO Auto-generated method stub

				}
			});
			interAD.load(context, interstitialAdMetadata);
		} catch (Exception e) {
			adEventLoadFailed(AdmofiConstants.ADM_TPFAILED_EXCEPTION);
		}

	}

	@Override
	public boolean showinterstitial() {
		try {
			if((getAd().getAdType() == AdmofiConstants.AD_TYPE_INTERSTITIAL) && (mContext!=null) && (interAD!=null) && (interAD.isReady())) {
				adEventImpression();
				try {
					interAD.show(mContext);
				} catch (MMException e) {
					e.printStackTrace();
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	protected boolean canFit(int adWidth,Context context) {
		try {
			int adWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adWidth, context.getResources().getDisplayMetrics());
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			return metrics.widthPixels >= adWidthPx;
		} catch (Exception e) {
			return false;
		}
	}

}