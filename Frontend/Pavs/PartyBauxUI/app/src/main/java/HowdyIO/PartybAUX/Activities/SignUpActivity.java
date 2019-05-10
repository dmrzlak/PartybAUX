package HowdyIO.PartybAUX.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Tools.EmailValidator;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Views.CustomEditText;
import HowdyIO.PartybAUX.Views.CustomTextView;
import PartyBauxUI.R;

/**
 * Created by Chris on 2/14/2019.
 */

public class SignUpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView signUpButton;
    CustomEditText email;
    CustomEditText username;
    CustomEditText password;
    CustomEditText passwordMatch;
    CustomTextView emailError;
    CustomTextView usernameError;
    CustomTextView passwordError;
    String emailText;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        root = findViewById(R.id.root_layout);
        signUpButton = findViewById(R.id.button_sign_up);
        email = findViewById(R.id.edittext_email);
        username = findViewById(R.id.edittext_username);
        password = findViewById(R.id.edittext_password);
        passwordMatch = findViewById(R.id.edittext_password_match);
        emailError = findViewById(R.id.textview_error_email);
        usernameError = findViewById(R.id.textview_error_username);
        passwordError = findViewById(R.id.textview_error_password);

        passwordMatch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    signUpButton.callOnClick();
                    handled = true;
                }
                return handled;
            }
        });

        setUpOnClicks();
        //setUpApp();
    }

    private void setUpOnClicks() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkError()) return;
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();

                DataProvider.addUser(
                        usernameInput,
                        email.getText().toString().trim(),
                        passwordInput, new Callback() {
                            @Override
                            public void onResult(String obj) {
                                final int result = Integer.valueOf(obj);
                                switch (result) {
                                    case -1:
                                        emailError.setText("Email is already taken");
                                        emailError.setVisibility(View.VISIBLE);
                                        return;
                                    case -2:
                                        usernameError.setText("Username is already taken");
                                        usernameError.setVisibility(View.VISIBLE);
                                        return;
                                    case -3:
                                        Snackbar.make(root,
                                                "Trouble Connecting to Server, Please Check Your Internet Connection",
                                                Snackbar.LENGTH_LONG).show();
                                        return;
                                    default:
                                        QuickTools.userName = usernameInput;
                                        User user = new User(usernameInput, passwordInput, "");
                                        QuickTools.saveUser(getBaseContext(), user);
                                        setResult(RESULT_OK);
                                        finish();
                                        break;
                                }
                            }
                        });
            }
        });
    }

    private boolean checkError() {
        boolean wasError = false;
        if (!EmailValidator.isEmail(email.getText().toString().trim())) {
            wasError = true;
            emailError.setText("* Please enter a valid email");
            emailError.setVisibility(View.VISIBLE);
        } else
            emailError.setVisibility(View.GONE);
        if (username.getText().toString().isEmpty()) {
            wasError = true;
            usernameError.setText("* Please enter a valid username");
            usernameError.setVisibility(View.VISIBLE);
        }
        else if (!isAlpha(username.getText().toString().trim())) {
            wasError = true;
            usernameError.setText("* Username needs to be aplhanumerical.");
            usernameError.setVisibility(View.VISIBLE);
        }
        else if (username.getText().toString().trim().length() < 4 || username.getText().toString().trim().length() > 16) {
            wasError = true;
            usernameError.setText("* Username needs to be be between 4 and 16 characters long.");
            usernameError.setVisibility(View.VISIBLE);
        } else
            usernameError.setVisibility(View.GONE);

        if (password.getText().toString().isEmpty()) {
            wasError = true;
            passwordError.setText("* Please enter a valid password");
            passwordError.setVisibility(View.VISIBLE);
        } else
            passwordError.setVisibility(View.GONE);


        if (!(password.getText().toString().equals(passwordMatch.getText().toString()))) {
            wasError = true;
            passwordError.setText("* Passwords do not match");
            passwordError.setVisibility(View.VISIBLE);
        } else
            passwordError.setVisibility(View.GONE);

        return wasError;
    }

    private boolean isAlpha(String text) {
        for(int i = 0; i < text.length(); i++){
            char cur = text.charAt(i);
            if(  (cur < '0' || cur > '9') && (cur < 'A' || cur > 'Z')&& (cur < 'a' || cur > 'z')){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
