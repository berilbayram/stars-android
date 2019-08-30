/*
 * Copyright (c) Traksity 2019.
 * Written by beratalp
 */

package com.traksity.stars;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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
import java.text.Normalizer;

public class LoginActivity extends AppCompatActivity {

    TextView versionText;
    Document login;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText smsEntryEditText;
    Handler messageHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
                   displayNetworkError();
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
                    login = form.submit().post();
                    if (login.toString().contains("Wrong password or Bilkent ID number.") ){
                        displayWrongNameOrIdError();
                    }
                } catch (Exception e){
                    displayNetworkError();
                }
            }
        }).start();
    }



    public void displayNetworkError(){
        Runnable doDisplayError = new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.no_internet_connection)
                        .setNegativeButton(R.string.ok, null);
                builder.show();
            }
        };
        messageHandler.post(doDisplayError);
    }

    public void displayWrongNameOrIdError(){
        Runnable doDisplayError = new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.wrong_id_or_password)
                        .setNegativeButton(R.string.ok, null);
                builder.show();
            }
        };
        messageHandler.post(doDisplayError);
    }

    private void enterSMS(){
        Runnable enterSMSCode = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.enter_sms);
                smsEntryEditText = new EditText(LoginActivity.this);
                smsEntryEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(smsEntryEditText);
                builder.setPositiveButton(R.string.ok, smsOkButtonListener);
                builder.show();
            }
        };
        messageHandler.post(enterSMSCode);
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
                enterSMS();
            }
            catch(Exception e){
                displayNetworkError();
                Log.d("error",e.toString());
            }
        }
    };

    private DialogInterface.OnClickListener smsOkButtonListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i){
            int SMSCode;
            try{
                SMSCode = Integer.parseInt(smsEntryEditText.getText().toString());
            }
            catch (Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.no_verification_entered)
                        .setNegativeButton(R.string.ok, null);
                builder.show();
            }
            try{
                FormElement smsForm = (FormElement) login.getElementById("");
            }
            catch (Exception e){

            }
        }
    };
}

