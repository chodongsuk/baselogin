
package kr.ds.baselogin;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{


    private String TAG = LoginActivity.class.getSimpleName();
    // 구글
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    private static final int PROFILE_PIC_SIZE = 400;
    /* Is there a ConnectionResult resolution in progress? */

    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private Button mPlusSignInButton;
    private ConnectionResult mConnectionResult;

    // 페이스북
    private Button mFacebookLoginButton;
    private CallbackManager mCallbackManager;

    // 공통
    private ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());// Button 불러오기전에 ..
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login);

        // Google+ Login
        mPlusSignInButton = (Button) findViewById(R.id.g_sign_in_button);
        mPlusSignInButton.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        // Facebook Login
        mFacebookLoginButton = (Button)findViewById(R.id.f_sign_in_button);
        mFacebookLoginButton.setOnClickListener(this);
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    /**fackbook end **/

    protected void onStart() {
        super.onStart();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        Log.i(TAG,"resolveSignInError");
        if (mConnectionResult.hasResolution()) {
            try {
                Log.i(TAG,"try");
                mIntentInProgress = true; //처음 성공하면 보내고
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {//취소하거나 더 이상 요청을 실행 할 수 없습니다 PendingIntent를 통해 보내려고 할 때 예외가 발생합니다.
                Log.i(TAG,"IntentSender.SendIntentException");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG,"onConnectionFailed");
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {//실패했을때
            // Store the ConnectionResult for later usage
            mConnectionResult = result;
            if (mSignInClicked) {//클릭했을때..
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();//다시실행..
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        Log.i(TAG, "onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }else{//페이스북
            mCallbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.i(TAG,"onConnected");
        mSignInClicked = false;
        next();
        //Plus.AccountApi.getAccountName(mGoogleApiClient);
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        //getProfileInformation();

    }
//    private void getProfileInformation() {
//        try {
//            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//                String personName = currentPerson.getDisplayName();
//                String id = currentPerson.getId();
//                String personPhotoUrl = currentPerson.getImage().getUrl();
//                String personGooglePlusProfile = currentPerson.getUrl();
//                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//
//                Log.i(TAG, "id" +id +"Name: " + personName + ", plusProfile: "
//                        + personGooglePlusProfile + ", email: " + email
//                        + ", Image: " + personPhotoUrl);
//                // by default the profile url gives 50x50 px image only
//                // we can replace the value with whatever dimension we want by
//                // replacing sz=X
//                personPhotoUrl = personPhotoUrl.substring(0,personPhotoUrl.length() - 2)
//                        + PROFILE_PIC_SIZE;
//
//               //setView(personPhotoUrl,personName);
//
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Person information is null", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.i(TAG,"onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

//    public void setView(String url, String name){
//        new LoadProfileImage(this.imageView).execute(url);
//        this.textView.setText(name);
//    }



//    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public LoadProfileImage(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }

    public void next(){
        Intent nextintent = new Intent(this, MainActivity.class);
        startActivity(nextintent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.g_sign_in_button:
                signInWithGplus();
                break;
            case R.id.f_sign_in_button:
                LoginManager loginManager = LoginManager.getInstance();
                List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
                loginManager.logInWithReadPermissions(this, permissionNeeds);
                loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        next();

                        // App code
//                        GraphRequest request = GraphRequest.newMeRequest(
//                                loginResult.getAccessToken(),
//                                new GraphRequest.GraphJSONObjectCallback() {
//                                    @Override
//                                    public void onCompleted(
//                                            JSONObject object,
//                                            GraphResponse response) {
//                                        // Application code
//                                        try {
//                                            Log.i(TAG, object.getString("name"));
//                                            Log.i(TAG, object.getString("id"));
//                                            JSONObject picture = object.getJSONObject("picture");
//                                            picture = picture.getJSONObject("data");
//                                            Log.i(TAG, picture.getString("url"));
//
//                                            //setView(picture.getString("url")+"?type=large", object.getString("name"));
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                });

//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "id,name,email,gender,birthday,picture");
//                        request.setParameters(parameters);
//                        request.executeAsync();

                        Log.i(TAG, "onSuccess");
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "User cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "Error on Login, check your facebook app_id", Toast.LENGTH_LONG).show();
                    }

                });
                break;
        }
    }


}
