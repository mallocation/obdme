package edu.unl.csce.obdme.setupwizard;

import java.util.regex.Pattern;

import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.utils.OBDMeSecurity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class OBDMeSetupWizardAccount.
 */
public class OBDMeSetupWizardAccount extends Activity {

	/** New account. */
	private static boolean NEW_ACCOUNT = false;
	
	/** Verified email. */
	private static boolean VERIFIED_EMAIL = false;
	
	/** Verified password. */
	private static int VERIFIED_PASSWORD = 0;
	
	/** Verified confirm password. */
	private static boolean VERIFIED_CONFIRM_PASSWORD = false;
	
	/** The preferences. */
	private SharedPreferences prefs;
	
	/** Email regex. */
	private Pattern emailRegEx;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_account),
		"Starting the OBDMe Account Setup Wizard Activity.");
		
		prefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), 0);
		emailRegEx = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

		setContentView(R.layout.setupwizard_account);

		final EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		emailText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) { 
				if(emailText.getText().length() > 0 ) {
					verifyAccountStatus();
				}
			} 
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { } 
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});

		final EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		passwordText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) {
				if(emailText.getText().length() > 0 ) {
					verifyPassword();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { } 
		});

		final EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
		confirmPasswordText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) {
				checkConfirmPassword();
			} 
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { } 
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});

		Button next = (Button) findViewById(R.id.setupwizard_account_button);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				int ready = 0;

				if (VERIFIED_EMAIL) {
					ready++;
				}
				else {
					Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_email_error_bad), 
							getResources().getInteger(R.integer.setupwizard_toast_time)).show();
					emailText.requestFocus();
					
				}

				if (ready == 1) {
					switch(VERIFIED_PASSWORD) {
					case 1:
						ready++;
						break;
					case 0:
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_incorrect), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						passwordText.requestFocus();
						break;
					case -1:
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_bad), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						passwordText.requestFocus();
						break;
					}
				}
				
				if (ready == 2) {
					if (VERIFIED_CONFIRM_PASSWORD) {
						ready++;
					}
					else {
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_match), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						confirmPasswordText.requestFocus();
					}
				}

				if(ready == 3) {
					
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(getResources().getString(R.string.prefs_account_username), emailText.getText().toString());
					try {
						editor.putString(getResources().getString(R.string.prefs_account_password), OBDMeSecurity.encrypt(emailText.getText().toString()));
					} catch (NotFoundException e) {
						
					} catch (Exception e) {
						
					}
					editor.commit();
					
					Intent intent = new Intent(view.getContext(), OBDMeSetupWizardBluetooth.class);
					finish();
					startActivity(intent);
				}
			}

		});

	}

	/**
	 * Check confirm password.
	 */
	protected void checkConfirmPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);

		if(passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			VERIFIED_CONFIRM_PASSWORD = true;
		}
		else {
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			VERIFIED_CONFIRM_PASSWORD = false;
		}
	}

	/**
	 * Verify password.
	 */
	@SuppressWarnings("unused")
	protected void verifyPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);

		//Choose between satisfying minimum password length or verifying password
		if (passwordText.getText().length() >= 6) {
			if(NEW_ACCOUNT) {
				//New account, satisfies minimum length
				passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
				//Check the confirm password again in case user goes back to fix original password to match the confirm password
				checkConfirmPassword();
				VERIFIED_PASSWORD = 1;
			} else {
				// TODO Account password validation
				if(true) {
					passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
					//Check the confirm password again in case user goes back to fix original password to match the confirm password
					checkConfirmPassword();
					VERIFIED_PASSWORD = 1;
				} else {
					passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
					VERIFIED_PASSWORD = 0;
				}
			}
		} else {
			passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			VERIFIED_PASSWORD = -1;
		}

	}

	/**
	 * Verify account status.
	 */
	@SuppressWarnings("unused")
	protected void verifyAccountStatus() {
		EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		TextView comfirmPasswordTitle = (TextView) findViewById(R.id.setupwizard_account_confirmpassword);
		EditText comfirmPasswordInput = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
		Button button = (Button) findViewById(R.id.setupwizard_account_button);

		//Format checking
		if (emailRegEx.matcher(emailText.getText()).matches()) {

			// TODO Check is the address entered is an existing account
			if(false) {
				emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
				comfirmPasswordTitle.setVisibility(View.GONE);
				comfirmPasswordInput.setVisibility(View.GONE);
				button.setText(R.string.setupwizard_account_button_signin_text);
				NEW_ACCOUNT = false;
				VERIFIED_CONFIRM_PASSWORD = true;
				VERIFIED_EMAIL = true;
			}
			else {
				emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.blue_plus, 0);
				comfirmPasswordTitle.setVisibility(View.VISIBLE);
				comfirmPasswordInput.setVisibility(View.VISIBLE);
				button.setText(R.string.setupwizard_account_button_createaccount_text);
				NEW_ACCOUNT = true;	
				VERIFIED_EMAIL = true;
			}
		}
		else {
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			comfirmPasswordTitle.setVisibility(View.GONE);
			comfirmPasswordInput.setVisibility(View.GONE);
			VERIFIED_EMAIL = false;
		}

	}

}
