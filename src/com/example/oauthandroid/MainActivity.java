package com.example.oauthandroid;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String authtoken = "AUTHTOKEN";
	TextView result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b = (Button) findViewById(R.id.btn);
		result = (TextView) findViewById(R.id.resulttext);
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final AccountManager manager = AccountManager
						.get(MainActivity.this);
				final Account accounts[] = manager.getAccounts();
				if (accounts.length == 0)
					Toast.makeText(MainActivity.this, "You need to register",
							Toast.LENGTH_LONG).show();
				else {
					final String accountsname[] = new String[accounts.length];
					for (int i = 0; i < accounts.length; i++)
						accountsname[i] = accounts[i].type;
					AlertDialog.Builder b = new AlertDialog.Builder(
							MainActivity.this);
					b.setTitle("Choose an account");
					b.setItems(accountsname,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									Account a = accounts[arg1];
									Bundle options = new Bundle();
									options.putString("client_id",
											"1040389963428.apps.googleusercontent.com");
									options.putString("client_secret",
											"PL4AaBd-OaCI1q2l-zp77H4V");
									options.putString("grant_type",
											"authorization_code");
									options.putString("redirect_uri",
											"urn:ietf:wg:oauth:2.0:oob");
									options.putString("response_type", "code");
									/*
									 * options.putString("scope",
									 * "https://www.googleapis.com/auth/tasks");
									 */
									options.putString("scope",
											"https://www.googleapis.com/auth/userinfo.email");
									manager.invalidateAuthToken("GOOGLE", null);
									manager.getAuthToken(a, "oauth2:https://www.googleapis.com/auth/userinfo.email", options,
											MainActivity.this,
											new OnTokenRecieved(), null);
									Toast.makeText(MainActivity.this,
											"Sending via:" + a.name,
											Toast.LENGTH_LONG).show();

								}
							});
					AlertDialog dialog = b.create();
					dialog.show();

				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public class OnTokenRecieved implements AccountManagerCallback<Bundle> {

		@Override
		public void run(AccountManagerFuture<Bundle> arg0) {
			// TODO Auto-generated method stub
			try {
				Bundle b = arg0.getResult();
				/*
				 * HttpGet get = new HttpGet(
				 * "https://www.googleapis.com/tasks/v1/users/@me/lists?key=AIzaSyBRTI-bydZr8Q9AczoTb5wCPZYkT4Fmw7M"
				 * );
				 */

				HttpGet get = new HttpGet(
						"https://www.googleapis.com/oauth2/v1/userinfo?key=AIzaSyBRTI-bydZr8Q9AczoTb5wCPZYkT4Fmw7M");

				get.addHeader("client_id",
						"1040389963428.apps.googleusercontent.com");
				get.addHeader("client_secret", "PL4AaBd-OaCI1q2l-zp77H4V");
				get.addHeader("Authorization",
						"OAuth " + b.getString(AccountManager.KEY_AUTHTOKEN));
				Log.e("token:", b.getString(AccountManager.KEY_AUTHTOKEN));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				String s = "";
				String k = "";
				while ((k = reader.readLine()) != null)
					s += k;
				Gson g = new Gson();
				email e = g.fromJson(s,email.class);
				
				result.setText(s + " : " + e.verified_email);
				Log.e("logging", s);

				Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();

				Toast.makeText(getBaseContext(),
						b.getString(AccountManager.KEY_AUTHTOKEN),
						Toast.LENGTH_LONG).show();
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
