package com.example.facebookshareapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

public class MainActivity extends Activity {

	private UiLifecycleHelper uiHelper;
	private Button shareButton;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private Session.StatusCallback sessionStatusCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Toast.makeText(this, "onCreate()", Toast.LENGTH_LONG).show();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);

		shareButton = (Button) findViewById(R.id.shareButton);
	}

	public void onClick(View v) {

		
		/*String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
				+ "play.google.com/store/apps/details?id=com.sim.socialproject";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
		startActivity(intent);*/
		Session currentSession = Session.getActiveSession();
        currentSession.addCallback(sessionStatusCallback);
		
		Session.OpenRequest openRequest = new Session.OpenRequest(this);
        openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
        openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
        List<String> permissions = currentSession.getPermissions();
		if (!isSubsetOf(PERMISSIONS, permissions)) {
			pendingPublishReauthorization = true;
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					this, PERMISSIONS);
			currentSession.requestNewPublishPermissions(newPermissionsRequest);
			return;
		}
        currentSession.openForRead(openRequest);
		/*
		 * Session session = Session.getActiveSession();
		 * 
		 * if (session != null) { FacebookDialog shareDialog = new
		 * FacebookDialog.ShareDialogBuilder( this) .setLink(
		 * "https://play.google.com/store/apps/details?id=com.sim.socialproject"
		 * ) .build(); uiHelper.trackPendingDialogCall(shareDialog.present()); }
		 */
		// publishStory();
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption",
					"Build great social apps and get more installs.");
			postParams
					.putString(
							"description",
							"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			postParams.putString("link",
					"https://developers.facebook.com/android");
			postParams
					.putString("picture",
							"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {

					}
					FacebookRequestError error = response.getError();
					if (error != null) {

					} else {

					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Toast.makeText(this, "onActivityResult()", Toast.LENGTH_LONG).show();

		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
					}
				});
	}

	@Override
	protected void onResume() {
		Toast.makeText(this, "onResume()", Toast.LENGTH_LONG).show();

		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Toast.makeText(this, "onSaveInstanceState()", Toast.LENGTH_LONG).show();

		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		Toast.makeText(this, "onPause()", Toast.LENGTH_LONG).show();

		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "onDestroy()", Toast.LENGTH_LONG).show();

		super.onDestroy();
		uiHelper.onDestroy();
	}

}
