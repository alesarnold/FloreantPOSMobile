package com.floreantpos.v14.mobile.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.floreantpos.response.LoginResponse;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.tasks.LoginTask;
import com.floreantpos.v14.mobile.utils.ActivityInvoker;
import com.floreantpos.v14.mobile.utils.MySharedPreference;

public class LoginActivity extends BaseActivity {

    public static TextView messageTV;
    public static EditText secretKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GV.URL = MySharedPreference.getServerAddress(getApplicationContext());

        setContentView(R.layout.login);

        final View textFieldBand = findViewById(R.id.merchantBand);
        secretKey = (EditText) findViewById(R.id.secretKey);
        messageTV= (TextView) findViewById(R.id.message);

        if (null == MySharedPreference.getServerAddress(getApplicationContext())) {
            messageTV.setText(R.string.server_url_requires_config);
            messageTV.setVisibility(View.VISIBLE);
        } else {
            messageTV.setVisibility(View.GONE);
        }

        textFieldBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secretKey.requestFocus();
            }
        });

        secretKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (4 <= s.length()) {

                    secretKey.setEnabled(false);

                    LoginTask task = new LoginTask(secretKey.getText().toString()) {
                        @Override
                        public void onSuccess(LoginResponse loginResponse) {

                            MySharedPreference.putInteger(getApplicationContext(),"userId", Integer.valueOf(loginResponse.userId));

                            ActivityInvoker.goToOpenTickets(getApplicationContext());

                        }

                        @Override
                        public void onFailed(String msg) {
                            secretKey.setEnabled(true);
                            secretKey.getText().clear();
                            messageTV.setText(msg);
                            messageTV.setVisibility(View.VISIBLE);
                        }
                    };

                    task.execute();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (GV.URL!=null) {
            Toast.makeText(getApplicationContext(), GV.URL, Toast.LENGTH_LONG).show();
        }

        if (-1 != (MySharedPreference.getInteger(getApplicationContext(),"userId"))) {
            ActivityInvoker.goToOpenTickets(getApplicationContext());
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        MenuItem clockOut = menu.findItem(R.id.clockOutMenu);
        clockOut.setVisible(false);

        MenuItem logout = menu.findItem(R.id.logoutMenu);
        logout.setVisible(false);

        MenuItem home = menu.findItem(R.id.homeMenu);
        home.setVisible(false);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingMenu:
                ActivityInvoker.showSettings(getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        GV.URL = MySharedPreference.getServerAddress(getApplicationContext());

        if (null == MySharedPreference.getServerAddress(getApplicationContext())) {
            if (messageTV!=null) messageTV.setText(R.string.server_url_requires_config);
            if (messageTV!=null) messageTV.setVisibility(View.VISIBLE);
        } else {
            if (messageTV!=null) messageTV.setText("");
            if (messageTV!=null) messageTV.setVisibility(View.VISIBLE);
        }

        if (secretKey!=null) {
            secretKey.setEnabled(true);
            secretKey.setText("");
        }
    }
}
