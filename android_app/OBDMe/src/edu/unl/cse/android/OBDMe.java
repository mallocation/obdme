package edu.unl.cse.android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class OBDMe extends Activity {
    
	private EditText commandEditTextField;
	private TextView outputTextView;
	BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        commandEditTextField = (EditText)findViewById(R.id.commandEditText);
        
        // Prompt the user to turn on Bluetooth if it is not enabled already
        if (!bluetooth.isEnabled()) {
        	String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        	startActivityForResult(new Intent(enableBT), 0);
        }
    }
    
    public void sendButtonMethod(View view) {
    	Context context = getApplicationContext();
    	CharSequence text = commandEditTextField.getText();
    	
    	commandEditTextField.setText("");
    }
}