package edu.unl.csce.obdme.setupwizard;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.User;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;
import edu.unl.csce.obdme.utilities.AppSettings;

/**
 * The Class SetupWizardAccount.
 */
public class SetupWizardAccount extends Activity {

	/** The NE w_ account. */
	private static boolean NEW_ACCOUNT = false;

	/** The VERIFIE d_ email. */
	private static boolean VERIFIED_EMAIL = false;

	/** The VERIFIE d_ password. */
	private static int VERIFIED_PASSWORD = 0;

	/** The VERIFIE d_ confir m_ password. */
	private static boolean VERIFIED_CONFIRM_PASSWORD = false;

	/** The Constant SETUP_ACCOUNT_RESULT_OK. */
	public static final int SETUP_ACCOUNT_RESULT_OK = 10;

	/** The busy dialog. */
	private Dialog busyDialog;

	/** The web framework. */
	private ObdMeService webFramework;
	
	/** The email reg ex. */
	private Pattern emailRegEx;

	/** The app settings. */
	private AppSettings	appSettings;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_setupwizard_account),
		"Starting the OBDMe Account Setup Wizard Activity.");
		
		emailRegEx = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		webFramework = ((OBDMeApplication)getApplication()).getWebFramework();
		appSettings = ((OBDMeApplication)getApplication()).getApplicationSettings();

		setContentView(R.layout.setupwizard_account);

		//Email input box event listener
		final EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		emailText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) { 

				//If there is any change in the email input box
				if(emailText.getText().length() > 0 ) {

					//Run a verify
					verifyAccountStatus();
				}
			} 
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { } 

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});

		//Password box event listener
		final EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		passwordText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) {

				//If there's any change in the password, run a verify
				if(emailText.getText().length() > 0 ) {
					verifyPassword();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { } 
		});

		//Confirm password box event listener
		final EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
		confirmPasswordText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) {

				//If there's any change in the confirm password, run a verify
				if(emailText.getText().length() > 0 ) {
					checkConfirmPassword();
				}
			} 
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { } 
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});

		//Log in / create account button action listener
		Button next = (Button) findViewById(R.id.setupwizard_account_button);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//User the ready variable to count each verification step
				int ready = 0;

				//If the email is verified
				if (VERIFIED_EMAIL) {
					ready++;
				}

				//Otherwise show the user a toast message about the unverified email
				else {
					Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_email_error_bad), 
							getResources().getInteger(R.integer.setupwizard_toast_time)).show();
					emailText.requestFocus();

				}

				//If the email address is verified
				if (ready == 1) {

					//Check the state of the password(s)
					switch(VERIFIED_PASSWORD) {

					//Existing account, password confirmed
					case 1:
						ready++;
						break;

						//Existing account, incorrect password
					case 0:

						//Show the user a toast message about the bad password
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_incorrect), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						passwordText.requestFocus();
						break;

						//Existing account, bad password
					case -1:
						//Show the user a toast message about the bad password
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_bad), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						passwordText.requestFocus();
						break;
					}
				}

				//If both the email address and password are correct
				if (ready == 2) {

					//New accounts: if the confirmation password matches the password
					if (VERIFIED_CONFIRM_PASSWORD) {

						//Increment the ready count
						ready++;
					}

					//Otherwise, show the user a toast message about the bad password
					else {
						Toast.makeText(view.getContext(), getResources().getString(R.string.setupwizard_account_password_error_match), 
								getResources().getInteger(R.integer.setupwizard_toast_time)).show();
						confirmPasswordText.requestFocus();
					}
				}

				//If the ready count indicated all the verification steps have passed
				if(ready == 3 && NEW_ACCOUNT == true) {

					//Check the existing users password
					EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
					EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);

					busyDialog = ProgressDialog.show(SetupWizardAccount.this, "", getResources().getString(R.string.setupwizard_account_dialog_creating), true);

					//Send the request to the webservice to create this user
					//webFramework.getUsersService().createUser(emailText.getText().toString(), confirmPasswordText.getText().toString(), createAccountHander);
					webFramework.getUsersService().createUserAsync(emailText.getText().toString(), confirmPasswordText.getText().toString(), createAccountHandler);
					
				}

				else if (ready == 3 && NEW_ACCOUNT == false) {
					//Check the existing users password
					EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
					EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);

					busyDialog = ProgressDialog.show(SetupWizardAccount.this, "", getResources().getString(R.string.setupwizard_account_dialog_loggingin), true);

					//Send the request to the webservice to get this users credentials
					webFramework.getUsersService().validateUserCredentials(emailText.getText().toString(), passwordText.getText().toString(), getAccountCredentialsHandler);
				}
			}

		});

	}

	/**
	 * Account creation successful.
	 *
	 * @param newUserObject the new user object
	 */
	public void accountCreationSuccessful(User newUserObject) {
		
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		
		//Update the local preferences
		appSettings.setAccountUID(newUserObject.getId());
		appSettings.setAccountUsername(newUserObject.getEmail());
		appSettings.setAccountPassword(passwordText.getText().toString());
		
		if (busyDialog != null) {
			busyDialog.dismiss();
		}

		//Start the bluetooth setup wizzard
		Intent intent = new Intent(getApplicationContext(), SetupWizardBluetooth.class);
		startActivityForResult(intent, SetupWizardBluetooth.SETUP_BLUETOOTH_RESULT_OK);
	}

	/**
	 * Account validation successful.
	 *
	 * @param newUserObject the new user object
	 */
	public void accountValidationSuccessful(User newUserObject) {
		
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		
		//Update the local preferences
		appSettings.setAccountUID(newUserObject.getId());
		appSettings.setAccountUsername(newUserObject.getEmail());
		appSettings.setAccountPassword(passwordText.getText().toString());
		
		if (busyDialog != null) {
			busyDialog.dismiss();
		}

		//Start the bluetooth setup wizzard
		Intent intent = new Intent(getApplicationContext(), SetupWizardBluetooth.class);
		startActivityForResult(intent, SetupWizardBluetooth.SETUP_BLUETOOTH_RESULT_OK);

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SetupWizardBluetooth.SETUP_BLUETOOTH_RESULT_OK:
			if (resultCode == Activity.RESULT_OK) {

				//Backprop
				setResult(Activity.RESULT_OK);

				//Finish the activity
				finish();
			}
			break;
		}
	}

	/**
	 * Check confirm password.
	 */
	protected void checkConfirmPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		EditText confirmPasswordText = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);

		//Make sure that the passwords match
		if(passwordText.getText().toString().equals(confirmPasswordText.getText().toString())  && NEW_ACCOUNT) {

			//Show the green check in the confirmation box
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			VERIFIED_CONFIRM_PASSWORD = true;
		}

		//Otherwise, show the red check in the confirmation box
		else if (NEW_ACCOUNT){
			confirmPasswordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			VERIFIED_CONFIRM_PASSWORD = false;
		}
	}

	/**
	 * Verify password.
	 */
	protected void verifyPassword() {
		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		//Choose between satisfying minimum password length or verifying password
		if (passwordText.getText().length() >= 6) {
			if(NEW_ACCOUNT) {
				//New account, satisfies minimum length
				passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
				//Check the confirm password again in case user goes back to fix original password to match the confirm password
				checkConfirmPassword();
				VERIFIED_PASSWORD = 1;
			} else {

				//Check the existing users password
				webFramework.getUsersService().validateUserCredentials(emailText.getText().toString(), passwordText.getText().toString(), confirmRegisteredPasswordHandler);
			}
		} else { //Password does not meet the minimum length requirement so it can't be correct
			//Show the red x in the password box
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

		//Format checking to make sure that the email address is valid (syntax wise)
		if (emailRegEx.matcher(emailText.getText()).matches()) {
			//Verify if the user is registered
			//webFramework.getUsersService().isUserRegistered(emailText.getText().toString(), userIsRegistered);
			webFramework.getUsersService().getUserByEmailAsync(emailText.getText().toString(), userIsRegistered);
		}

		//Email address is not formatted correctly, show the red x in the email box
		else {
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			comfirmPasswordTitle.setVisibility(View.GONE);
			comfirmPasswordInput.setVisibility(View.GONE);
			VERIFIED_EMAIL = false;
		}

	}
	
	/** The user is registered handler */
	private final BasicObjectHandler<User> userIsRegistered = new BasicObjectHandler<User>(User.class) {

		EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
		TextView comfirmPasswordTitle = (TextView) findViewById(R.id.setupwizard_account_confirmpassword);
		EditText comfirmPasswordInput = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
		Button button = (Button) findViewById(R.id.setupwizard_account_button);
		
		@Override
		public void onCommException(String message) {

			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Comm Exception in userIsRegistered.");
			}
		}

		@Override
		public void onObdmeException(String message) {
			// user is not registered
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_plus, 0);
			comfirmPasswordTitle.setVisibility(View.VISIBLE);
			comfirmPasswordInput.setVisibility(View.VISIBLE);
			button.setText(R.string.setupwizard_account_button_createaccount_text);
			NEW_ACCOUNT = true;	
			VERIFIED_EMAIL = true;
		}

		@Override
		public void onOperationCompleted(User result) {
			// user is regestered
			emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			comfirmPasswordTitle.setVisibility(View.GONE);
			comfirmPasswordInput.setVisibility(View.GONE);
			button.setText(R.string.setupwizard_account_button_signin_text);
			NEW_ACCOUNT = false;
			VERIFIED_CONFIRM_PASSWORD = true;
			VERIFIED_EMAIL = true;
		}
		
	};

	/** The user is registered. NOW DEFINED ABOVE*/
	/*
	private final Handler userIsRegistered = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			EditText emailText = (EditText) findViewById(R.id.setupwizard_account_email_input);
			TextView comfirmPasswordTitle = (TextView) findViewById(R.id.setupwizard_account_confirmpassword);
			EditText comfirmPasswordInput = (EditText) findViewById(R.id.setupwizard_account_confirmpassword_input);
			Button button = (Button) findViewById(R.id.setupwizard_account_button);

			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:

				if ((Boolean)msg.obj) {
					emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
					comfirmPasswordTitle.setVisibility(View.GONE);
					comfirmPasswordInput.setVisibility(View.GONE);
					button.setText(R.string.setupwizard_account_button_signin_text);
					NEW_ACCOUNT = false;
					VERIFIED_CONFIRM_PASSWORD = true;
					VERIFIED_EMAIL = true;
				}
				else {
					emailText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_plus, 0);
					comfirmPasswordTitle.setVisibility(View.VISIBLE);
					comfirmPasswordInput.setVisibility(View.VISIBLE);
					button.setText(R.string.setupwizard_account_button_createaccount_text);
					NEW_ACCOUNT = true;	
					VERIFIED_EMAIL = true;
				}
				break;



			}
		}
	};
	*/
	
	private final BasicObjectHandler<User> confirmRegisteredPasswordHandler = new BasicObjectHandler<User>(User.class) {

		EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
		
		@Override
		public void onCommException(String message) {

			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Comm Exception in confirmRegisteredPasswordHandler");
			}
		}

		@Override
		public void onObdmeException(String message) {
			// password is incorrect
			passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
			VERIFIED_PASSWORD = 0;
		}

		@Override
		public void onOperationCompleted(User result) {
			// password is correct
			passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
			//Check the confirm password again in case user goes back to fix original password to match the confirm password
			checkConfirmPassword();
			VERIFIED_PASSWORD = 1;
			
			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Confirm registered password successful.");
			}
		}
		
	};

	/** The confirm registered password handler. NOW DEFINED ABOVE*/
	/*
	private final Handler confirmRegisteredPasswordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			EditText passwordText = (EditText) findViewById(R.id.setupwizard_account_password_input);
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:
				if(msg.obj != null) {
					passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
					//Check the confirm password again in case user goes back to fix original password to match the confirm password
					checkConfirmPassword();
					VERIFIED_PASSWORD = 1;
				} else {
					passwordText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_x, 0);
					VERIFIED_PASSWORD = 0;
				}
				break;



			}
		}
	};
	*/

	/** The create account handler. */
	private final BasicObjectHandler<User> createAccountHandler = new BasicObjectHandler<User>(User.class) {

		@Override
		public void onCommException(String message) {
			
			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Comm Exception in createAccountHandler.");
			}
			
			//Show alert dialog, the app must exit.  This is not recoverable
			AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
			builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_create_failure))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					SetupWizardAccount.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onObdmeException(String message) {
			//Show alert dialog, the app must exit.  This is not recoverable
			AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
			builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_create_failure))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					SetupWizardAccount.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onOperationCompleted(User result) {
			// account created successfully
			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Account creation successful.");
			}
		}
		
	};
	
	/*
	private final Handler createAccountHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:
				if(msg.obj != null) {
					accountCreationSuccessful((User)msg.obj);

				} 
				else {

					if (busyDialog != null) {
						busyDialog.dismiss();
					}

					//Show alert dialog, the app must exit.  This is not recoverable
					AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
					builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_create_failure))
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							SetupWizardAccount.this.finish();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;



			}
		}
	};
	*/
	
	/** The getAccountCredentials handler. */
	private final BasicObjectHandler<User> getAccountCredentialsHandler = new BasicObjectHandler<User>(User.class) {

		@Override
		public void onCommException(String message) {
			//Show alert dialog, the app must exit.  This is not recoverable
			AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
			builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_validate_failure))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					SetupWizardAccount.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onObdmeException(String message) {
			// User does not exist
			//Show alert dialog, the app must exit.  This is not recoverable
			AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
			builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_validate_failure))
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					SetupWizardAccount.this.finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		@Override
		public void onOperationCompleted(User result) {
			// get account credentials successful
			if(getResources().getBoolean(R.bool.debug)) {
				Log.d(getResources().getString(R.string.debug_tag_obdme),
				"Account validation successful.");
			}
		}
		
	};
	
	/** The get account credentials handler. NOW DEFINED ABOVE*/
	/*
	private final Handler getAccountCredentialsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:
				if(msg.obj != null) {
					accountValidationSuccessful((User)msg.obj);
				} else {
					if (busyDialog != null) {
						busyDialog.dismiss();
					}

					//Show alert dialog, the app must exit.  This is not recoverable
					AlertDialog.Builder builder = new AlertDialog.Builder(SetupWizardAccount.this);
					builder.setMessage(getResources().getString(R.string.setupwizard_account_dialog_account_validate_failure))
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							SetupWizardAccount.this.finish();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;



			}
		}
	};
	*/
	
}
