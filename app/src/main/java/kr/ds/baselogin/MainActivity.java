package kr.ds.baselogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.plus.Plus;

import kr.ds.login.GoogleLoginUtil;

/**
 * Created by Administrator on 2016-02-25.
 */
public class MainActivity extends Activity {
    Button button;
    private GoogleLoginUtil googleLoginUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if(googleLoginUtil == null) {
            googleLoginUtil = new GoogleLoginUtil(MainActivity.this);
        }
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                googleLoginUtil.signIn();
            }
        });
    }

    public void onStart() {
        super.onStart();
        Log.i("TEST","onStart()");
        googleLoginUtil.getmGoogleApiClient().connect();
    }

    public void onStop() {
        super.onStop();
        Log.i("TEST", "onStop()");
       if (googleLoginUtil.getmGoogleApiClient().isConnected()) {
           Plus.AccountApi.clearDefaultAccount(googleLoginUtil.getmGoogleApiClient());
           googleLoginUtil.getmGoogleApiClient().disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleLoginUtil.onResult(requestCode, resultCode);

    }
}
