package com.floreantpos.v14.mobile.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.floreantpos.v14.mobile.R;

public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        ImageView imgV = (ImageView) findViewById(R.id.imageView);

        imgV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getApplicationContext(), "Developed by PT. indoCERT", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            String versionCode = pInfo.versionName;

            TextView ver = (TextView) findViewById(R.id.textViewVersion);

            if (ver != null) {
                ver.setText(getString(R.string.version) + " " + versionCode);
            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

}
