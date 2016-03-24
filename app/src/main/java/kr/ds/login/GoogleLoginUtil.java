package kr.ds.login;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by Administrator on 2016-03-24.
 */
public class GoogleLoginUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private final String TAG = GoogleLoginUtil.class.getSimpleName();
    private int RESULT_OK = 1;
    private static final int RC_SIGN_IN = 0;
    private boolean mIsResolving = false;
    private boolean msignedInClicked = false;
    private GoogleApiClient mGoogleApiClient;
    private Activity activity;

    public GoogleLoginUtil(Activity activity){
        this.activity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(this.activity).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void signIn() {
        msignedInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        msignedInClicked = false;
        Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"onConnectionFailed");
        if (!mIsResolving && msignedInClicked) {
            if (connectionResult.hasResolution()) {
                try {
                    Log.i(TAG,"connectionResult.hasResolution()");
                    connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG,"IntentSender.SendIntentException e");
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }else{
                Toast.makeText(activity, "connectionResult.hasResolution() not", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResult(int requestCode, int resultCode) {
        Log.i(TAG,"onResult");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                msignedInClicked = false;
            }
            mIsResolving = false;
            if (!mGoogleApiClient.isConnecting()) {
                Log.i(TAG,"!mGoogleApiClient.isConnecting()");
                mGoogleApiClient.connect();
            }
        } else {
            Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}
