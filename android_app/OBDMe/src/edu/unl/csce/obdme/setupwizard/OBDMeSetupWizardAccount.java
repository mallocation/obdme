package edu.unl.csce.obdme.setupwizard;

import edu.unl.csce.obdme.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OBDMeSetupWizardAccount extends Activity {

	private static boolean NEW_ACCOUNT = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizrd_account),
		"Starting the OBDMe Account Setup Wizard Activity.");

		setContentView(R.layout.setupwizard_account);

		final EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		emailText.addTextChangedListener(new TextWatcher() { 
			public void afterTextChanged(Editable s) { 
				if(emailText.getText().length() > 0 ) {
					verifyAccountStatus();
				}
			} 
			public void beforeTextChanged(CharSequence s, int start, int count, 
					int after) { 
				//XXX do something 
			} 
			public void onTextChanged(CharSequence s, int start, int before, int count) { 
				//XXX do something 
			}
		});

		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		passwordText.addTextChangedListener(new TextWatcher() { 
			public void afterTextChanged(Editable s) {
				if(emailText.getText().length() > 0 ) {
					verifyPassword();
				}
			} 
			public void beforeTextChanged(CharSequence s, int start, int count, 
					int after) { 
				//XXX do something 
			} 
			public void onTextChanged(CharSequence s, int start, int before, int count) { 
				//XXX do something 
			}
		});

		EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
		confirmPasswordText.addTextChangedListener(new TextWatcher() { 
			public void afterTextChanged(Editable s) {
				if(emailText.getText().length() > 0) {
					checkConfirmPassword();
				}
			} 
			public void beforeTextChanged(CharSequence s, int start, int count, 
					int after) { 
				//XXX do something 
			} 
			public void onTextChanged(CharSequence s, int start, int before, int count) { 
				//XXX do something 
			}
		});

		Button next = (Button) findViewById(R.id.setupwizard_account_button);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(view.getContext(), OBDMeSetupWizardBluetooth.class);
				finish();
				startActivity(intent);
			}

		});

	}

	protected void checkConfirmPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);

		if(passwordText.getText().equals(confirmPasswordText.getText())) {
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
		}
		else {
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
		}
	}

	protected void verifyPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		// TODO If the account already exists

		if (NEW_ACCOUNT) {
			if(true) {
				passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			}
		}

	}

	@SuppressWarnings("unused")
	protected void verifyAccountStatus() {
		EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		// TODO If the account already exists
		if(emailText.getText().length() > 5 ) {
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			TextView comfirmPasswordTitle = (TextView) findViewById(R.id.setupwizard_account_confirmpassword);
			comfirmPasswordTitle.setVisibility(View.INVISIBLE);
			EditText comfirmPasswordInput = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
			comfirmPasswordInput.setVisibility(View.INVISIBLE);
			NEW_ACCOUNT = false;
		}
		else {
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blue_plus, 0);
			TextView comfirmPasswordTitle = (TextView) findViewById(R.id.setupwizard_account_confirmpassword);
			comfirmPasswordTitle.setVisibility(View.VISIBLE);
			EditText comfirmPasswordInput = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
			comfirmPasswordInput.setVisibility(View.VISIBLE);
			NEW_ACCOUNT = true;	
		}

	}

}
