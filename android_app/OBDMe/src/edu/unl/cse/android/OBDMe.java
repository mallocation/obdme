package edu.unl.cse.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OBDMe extends Activity {
    
	private EditText commandEditTextField;
	private TextView outputTextView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        commandEditTextField = (EditText)findViewById(R.id.commandEditText);
    }
    
    public void sendButtonMethod(View view) {
    	Context context = getApplicationContext();
    	CharSequence text = commandEditTextField.getText();
    	
    	commandEditTextField.setText("");
    }
}