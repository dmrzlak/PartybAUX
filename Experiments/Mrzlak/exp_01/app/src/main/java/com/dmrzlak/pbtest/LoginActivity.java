package com.dmrzlak.pbtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String CLIENT_ID="6c8d86bc00b54291955a9f6857d37ff2";
    private static final String CLIENT_SEC="d361a62b4318412ba8ea6ce1db7590c2";
    private static final String REDIRECT_URI="http://pbtest.com/callback/";
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    private static final int REQUEST_CODE= 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginAttempt= findViewById(R.id.loginButton);
        loginAttempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });
    }
    public void auth() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"playlist-read", "playlist-read-private", "user-read-private"}
                //More Scopes may be added later, for now this'll do
                )
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    startMainActivity(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }

    private void startMainActivity(String token) {
        Intent intent = MainActivity.createIntent(this);
        intent.putExtra(MainActivity.EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(LoginActivity.class.getSimpleName(), msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(LoginActivity.class.getSimpleName(), msg);
    }
}

