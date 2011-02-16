package edu.unl.csce.obdme.settingsmenu;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * The Class IntegerListPreference.
 */
public class IntegerListPreference extends ListPreference
{
    
    /**
     * Instantiates a new integer list preference.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public IntegerListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new integer list preference.
     *
     * @param context the context
     */
    public IntegerListPreference(Context context) {
        super(context);
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#persistString(java.lang.String)
     */
    @Override
    protected boolean persistString(String value) {
        if(value != null) {
        	//Store as an integer instead of s string
            return persistInt(Integer.valueOf(value));
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#getPersistedString(java.lang.String)
     */
    @Override
    protected String getPersistedString(String defaultReturnValue) {
        
    	//If the SharedPreferences contains our key, get it
    	if(getSharedPreferences().contains(getKey())) {
        	
        	//if the preference doesn't exist, the default value is zero
            int intValue = getPersistedInt(0);
            
            //Return the stored integer as a string
            return String.valueOf(intValue);
        } 
        
        //Otherwise, return the default integer value
        else {
            return defaultReturnValue;
        }
    }
}