package HowdyIO.PartybAUX.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/23/2019.
 */

public class LogInActivity extends AppCompatActivity {

        private Toolbar toolbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            //setUpApp();
        }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
