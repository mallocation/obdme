package edu.unl.csce.obdme.settingsmenu;

import android.app.Activity;
import android.app.ProgressDialog;
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
import edu.unl.csce.obdme.OBDMeApplication;
import edu.unl.csce.obdme.R;
import edu.unl.csce.obdme.api.ObdMeService;
import edu.unl.csce.obdme.api.entities.User;
import edu.unl.csce.obdme.api.entities.UserVehicle;
import edu.unl.csce.obdme.client.http.handler.BasicObjectHandler;
import edu.unl.csce.obdme.utilities.AppSettings;

/**
 * The Class settingsVehicle.
 */
public class VehicleInformation extends Activity {

	/** The web framework. */
	private ObdMeService webFramework;

	/** The app settings. */
	private AppSettings appSettings;

	private ProgressDialog webDialog;
	
	/** The Constant SETUP_VEHICLE_RESULT_OK. */
	public static final int SETUP_VEHICLE_RESULT_OK = 10;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getResources().getBoolean(R.bool.debug)) Log.e(getResources().getString(R.string.debug_tag_settings_vehicle),
		"Starting the OBDMe Vehicle Settings Activity.");

		setContentView(R.layout.settings_vehicle);

		webFramework = ((OBDMeApplication)getApplication()).getWebFramework();
		appSettings = ((OBDMeApplication)getApplication()).getApplicationSettings();
		
		setTextValues();
		

		//Setup the button on-click listener
		Button next = (Button) findViewById(R.id.settings_vehicle_button);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				Button changeButton = (Button) findViewById(R.id.settings_vehicle_button);
				changeButton.setVisibility(View.GONE);
				
//				webFramework.getVehicleService().addVehicle(
//						appSettings.getAccountVIN(),
//						appSettings.getAccountUID(),
//						eventHandler);
				
				webFramework.getVehicleService().addUpdateVehicleToUserAsync(appSettings.getAccountVIN(), appSettings.getAccountUsername(), appSettings.getAccountVehicleAlias(), addUpdateVehicleHandler);
				
				if(webDialog == null){
					webDialog = ProgressDialog.show(VehicleInformation.this, "", getResources().getString(R.string.settings_vehicle_dialog), true);
				}

			}
		});
		
		//Password box event listener
		final EditText aliasText = (EditText) findViewById(R.id.settings_vehicle_alias_edit);
		aliasText.addTextChangedListener(new TextWatcher() { 
			@Override
			public void afterTextChanged(Editable s) {

				//If there's any change in the password, run a verify
				Button changeButton = (Button) findViewById(R.id.settings_vehicle_button);
				if(!aliasText.getText().equals(appSettings.getAccountVehicleAlias())) {
					changeButton.setVisibility(View.VISIBLE);
				}
				else {
					changeButton.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { } 
		});
		
		aliasText.setSelected(false);

	}
	
	private void setTextValues() {
		
		TextView vinText = (TextView) findViewById(R.id.settings_vehicle_vin);
		vinText.setText(appSettings.getAccountVIN());
		
		TextView vehicleAliasText = (TextView) findViewById(R.id.settings_vehicle_alias_edit);
		vehicleAliasText.setText(appSettings.getAccountVehicleAlias());
		
	}
	
	/*
	private final Handler eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			//Messsage from BT service indicating a connection state change
			case 0:
				if(webDialog != null){
					webDialog.dismiss();
				}
				
				break;
				
			}
				
		}
	};
	*/
	
	private final BasicObjectHandler<UserVehicle> addUpdateVehicleHandler = new BasicObjectHandler<UserVehicle>(UserVehicle.class) {

		@Override
		public void onCommException(String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onObdmeException(String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOperationCompleted(UserVehicle result) {
			// TODO Auto-generated method stub
			// add/update vehicle was successful
			if(webDialog != null){
				webDialog.dismiss();
			}
		}
		
	};

}

