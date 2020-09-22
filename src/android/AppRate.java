package org.pushandplay.cordova.apprate;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class AppRate extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException
	{
		try {
			PackageManager packageManager = this.cordova.getActivity().getPackageManager();

			if (action.equals("getAppVersion")){
				callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
				return true;
			}
			if (action.equals("getAppTitle")) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.cordova.getActivity().getApplicationContext().getApplicationInfo().packageName, 0);
				final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");

                callbackContext.success(applicationName);

                return true;
			}
			if (action.equals("launchAndroidInAppReview")) {
				Context context = this.cordova.getActivity().getApplicationContext();
				ReviewManager manager = ReviewManagerFactory.create(context);
				Task<ReviewInfo> request = manager.requestReviewFlow();
				request.addOnCompleteListener(task -> {
					// The flow has finished. The API does not indicate whether the user
					// reviewed or not, or even whether the review dialog was shown. Thus, no
					// matter the result, we continue our app flow.
					if (task.isSuccessful()) {
						// We can get the ReviewInfo object
						ReviewInfo reviewInfo = task.getResult();
						callbackContext.success(reviewInfo.toString());
					} else {
						// There was some problem, continue regardless of the result.
						callbackContext.error("error");
					}
				});
                
                return true;
			}
			
			return false;
		} catch (NameNotFoundException e) {
			callbackContext.success("N/A");
			return true;
		}
	}
}
