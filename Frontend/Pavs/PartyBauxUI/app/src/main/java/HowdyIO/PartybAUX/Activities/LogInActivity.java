package HowdyIO.PartybAUX.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Views.CustomEditText;
import HowdyIO.PartybAUX.Views.CustomTextView;
import PartyBauxUI.R;

/**
 * Created by Chris on 1/23/2019.
 */

public class LogInActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView loginButton;
    private CustomTextView signUpButton;

    CustomEditText username;
    CustomEditText password;
    CustomTextView usernameError;
    CustomTextView passwordError;
    private View root;
    boolean showOffline = false;

    private final static int REQUEST_CODE_SIGN_UP = 5193;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        root = findViewById(R.id.root_layout);
        loginButton = findViewById(R.id.button_log_in);
        signUpButton = findViewById(R.id.button_sign_up);
        username = findViewById(R.id.edittext_username);
        password = findViewById(R.id.edittext_password);
        usernameError = findViewById(R.id.textview_error_username);
        passwordError = findViewById(R.id.textview_error_password);
        setUpOnClicks();

        if(showOffline)
            showOfflineNotif();

        //setUpApp();
    }

    private void showOfflineNotif() {
        if(root != null && root.getParent() != null)
            Snackbar.make(root,
                    "Issues Connecting to Server, Please Check Your Internet Connection",
                    Snackbar.LENGTH_LONG).show();

        showOffline = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        attemptAsyncLogUserIn();
    }

    private void attemptAsyncLogUserIn(){
        User user = QuickTools.getSavedUser(getBaseContext());
        if (user == null) return;

        DataProvider.logUserIn(user.getUsername(), user.getPassword(), new Callback() {
            @Override
            public void onResult(String obj) {
                final int result = Integer.valueOf(obj);
                switch (result) {
                    case -1:
                    case -2:
                        return;
                    case -3:
                        showOffline = true;
                        showOfflineNotif();
                        return;
                    default:

                        break;
                }

                QuickTools.userName = user.getUsername();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void setUpOnClicks() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkError()) return;
                String usernameInput = username.getText().toString().split(" ")[0]; //Remove spaces in username
                String passwordInput = password.getText().toString();

                DataProvider.logUserIn(usernameInput, passwordInput, new Callback() {
                    @Override
                    public void onResult(String obj) {
                        final int result = Integer.valueOf(obj);
                        switch (result) {
                            case -1:
                                passwordError.setText("Incorrect Password");
                                passwordError.setVisibility(View.VISIBLE);
                                return;
                            case -2:
                                usernameError.setText("Incorrect Username");
                                usernameError.setVisibility(View.VISIBLE);
                                return;
                            case -3:
                                showOfflineNotif();
                                return;
                            default:
                                QuickTools.userName = usernameInput;
                                User user = new User(usernameInput, passwordInput, "");
                                QuickTools.saveUser(getBaseContext(), user);
                                setResult(RESULT_OK);
                                finish();
                                break;
                        }
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });

            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
    }
    
    private void openSignUpActivity() {
        startActivityForResult(new Intent(this, SignUpActivity.class), REQUEST_CODE_SIGN_UP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_SIGN_UP) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    setResult(Activity.RESULT_OK);
                    finish();
                    break;
                default:

                    break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
    }
    private boolean checkError() {
        boolean wasError = false;

        if (username.getText().toString().isEmpty()) {
            wasError = true;
            usernameError.setText("* Please enter a valid username");
            usernameError.setVisibility(View.VISIBLE);
        } else
            usernameError.setVisibility(View.GONE);

        if (password.getText().toString().isEmpty()) {
            wasError = true;
            passwordError.setText("* Please enter a valid password");
            passwordError.setVisibility(View.VISIBLE);
        } else
            passwordError.setVisibility(View.GONE);

        return wasError;
    }
}
