package com.traksity.stars;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the view layout from activity_login.xml
        setContentView(R.layout.activity_login);
        String version;
        //get the current version of the app from the package manager
        try{
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e){
            version = "N/A";
        }
        //Set version information
        versionText = findViewById(R.id.version_info);
        versionText.setText(versionText.getText() + " " + version);
        getLogin();
    }

    private void getLogin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder stringBuilder = new StringBuilder();
                try{
                    Document doc = Jsoup.connect("https://stars.bilkent.edu.tr/srs").get();
                    stringBuilder.append(doc.toString());
                }
                catch(IOException ex){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.no_internet_connection)
                            .setNegativeButton(R.string.ok, null);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        versionText.setText(stringBuilder.toString());
                    }
                });
            }
        }).start();
    }
}

