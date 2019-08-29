package com.traksity.stars;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    TextView versionText;
    Document login;
    EditText usernameEditText;
    EditText passwordEditText;

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
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(loginButtonListener);
        passwordEditText = findViewById(R.id.password_input);
        usernameEditText = findViewById(R.id.username_input);
        getLogin();
    }

    private void getLogin(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                final StringBuilder stringBuilder = new StringBuilder();
                try{
                    login = Jsoup.connect("https://stars.bilkent.edu.tr/srs")
                            .userAgent("Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 Mobile Safari/537.36\n")
                            .get();
                    stringBuilder.append(login.toString());
                }
                catch(IOException ex){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.no_internet_connection)
                            .setNegativeButton(R.string.ok, null);
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                    }
                });
            }
        }).start();
    }

    private void getSMS(final FormElement form){
        new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    Document doc = form.submit().post();
                    if (doc.toString().contains("Wrong") || doc.toString().contains("Security Code")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.error)
                                .setMessage(R.string.wrong_id_or_password)
                                .setNegativeButton(R.string.ok, null);
                        builder.show();
                    }
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.no_internet_connection)
                            .setNegativeButton(R.string.ok, null);
                    builder.show();
                }
            }
        }).start();
    }

    private View.OnClickListener loginButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            try{
                FormElement loginForm = (FormElement) login.getElementById("login-form");
                Element username = loginForm.getElementById("LoginForm_username");
                Element password = loginForm.getElementById("LoginForm_password");
                username.val(usernameEditText.getText().toString());
                password.val(passwordEditText.getText().toString());
                getSMS(loginForm);
            }
            catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.no_internet_connection)
                        .setNegativeButton(R.string.ok, null);
                builder.show();
                Log.d("error",e.toString());
            }
        }
    };
}

