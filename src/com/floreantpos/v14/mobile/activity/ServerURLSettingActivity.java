package com.floreantpos.v14.mobile.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;
import com.floreantpos.v14.mobile.R;
import com.floreantpos.v14.mobile.utils.MySharedPreference;

public class ServerURLSettingActivity extends StyledBaseActivity {

    private boolean dataChanged = false;
    static String URL= null;
    public ServerURLSettingActivity() {
        super();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.server_url_setting);

        final EditText url = (EditText) findViewById(R.id.url_tv);
        URL = MySharedPreference.getServerAddress(getApplicationContext());

        url.setText(URL==null?"http://10.10.10.10/floreant":URL);

        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                URL = url.getText().toString();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onPause() {
        if (dataChanged && URL!=null) {
            MySharedPreference.setServerAddress(getApplicationContext(), URL);
            Toast.makeText(getApplicationContext(), "CHANGES SAVED", Toast.LENGTH_LONG).show();
        }
        super.onPause();
    }
}
