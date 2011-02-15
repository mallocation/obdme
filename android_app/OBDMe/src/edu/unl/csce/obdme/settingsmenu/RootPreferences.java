package edu.unl.csce.obdme.settingsmenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.unl.csce.obdme.R;

/**
 * The Class Preferences.
 */
public class RootPreferences extends PreferenceActivity {
	 
    /** The root view. */
    private LinearLayout rootView;
 
    /** The preference view. */
    private ListView preferenceView;
    
    /** The vehiclePref Preference. */
    private Preference vehiclePref;
    
    /** The accountPref Preference. */
    private Preference accountPref;
    
    /** The usermodePref Preference. */
    private Preference usermodePref;
    
    /** The displayPref Preference. */
    private Preference displayPref;
    
    /** The collectionPref Preference. */
    private Preference collectionPref;
    
    /** The uploadPref Preference. */
    private Preference uploadPref;
    
    /** The unitsPref Preference. */
    private CheckBoxPreference unitsPref;
    
    /** Shared Preferences */
	SharedPreferences sharedPrefs;
    
	/** Modes of Operation */
	public static final int BASIC_USER_MODE = 0;
	public static final int ADVANCED_USER_MODE = 1;
	
	/** Data upload options */
	public static final int DATAUPLOAD_WIFI_ONLY = 0;
	public static final int DATAUPLOAD_NETWORK_ONLY = 1;
	public static final int DATAUPLOAD_BOTH = 2;
 
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);

        rootView = new LinearLayout(this); 
        rootView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        rootView.setOrientation(LinearLayout.VERTICAL);
       
        preferenceView = new ListView(this);
        preferenceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        preferenceView.setId(android.R.id.list);
        
        PreferenceScreen screen = createPreferenceHierarchy();
        screen.bind(preferenceView);
        preferenceView.setAdapter(screen.getRootAdapter());
 
        rootView.addView(preferenceView);

        this.setContentView(rootView);
        setPreferenceScreen(screen);

    }

    /**
     * Creates the preference hierarchy.
     *
     * @return the preference screen
     */
    private PreferenceScreen createPreferenceHierarchy() {
 
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this); 
        
        createAccountCategory(root);
        createApplicationCategory(root);
        createDeviceCategory(root);
 
        return root;
 
    }
    
    /**
     * Creates the account category.
     *
     * @param root the root
     * @return the preference category
     */
    private PreferenceCategory createAccountCategory(PreferenceScreen root) {
    	PreferenceCategory accountCategory = new PreferenceCategory(this);
        accountCategory.setTitle(getResources().getString(R.string.menu_prefs_category_account));
        root.addPreference(accountCategory);
        
        accountPref = new Preference(this);
        accountPref.setTitle(getResources().getString(R.string.menu_prefs_accountinfo_title));
        accountPref.setSummary(getResources().getString(R.string.menu_prefs_accountinfo_summary));
        accountPref.setPersistent(false);
        accountCategory.addPreference(accountPref);
        
        vehiclePref = new Preference(this);
        vehiclePref.setTitle(getResources().getString(R.string.menu_prefs_vehicles_title));
        vehiclePref.setSummary(getResources().getString(R.string.menu_prefs_vehicles_summary));
        accountCategory.addPreference(vehiclePref);
        
        return accountCategory;
    	
    }
    
    /**
     * Creates the application category.
     *
     * @param root the root
     * @return the preference category
     */
    private PreferenceCategory createApplicationCategory(PreferenceScreen root) {
    	
    	PreferenceCategory applicationCategory = new PreferenceCategory(this);
        applicationCategory.setTitle(getResources().getString(R.string.menu_prefs_category_application));
        root.addPreference(applicationCategory);
        
        usermodePref = new Preference(this);
        usermodePref.setTitle(getResources().getString(R.string.menu_prefs_usermode_title));
        usermodePref.setSummary(getResources().getString(R.string.menu_prefs_usermode_summary));
        applicationCategory.addPreference(usermodePref);
        
        displayPref = new Preference(this);
        displayPref.setTitle(getResources().getString(R.string.menu_prefs_displayopts_title));
        displayPref.setSummary(getResources().getString(R.string.menu_prefs_displayopts_summary));
        applicationCategory.addPreference(displayPref);
        
        collectionPref = new Preference(this);
        collectionPref.setTitle(getResources().getString(R.string.menu_prefs_collectionopts_title));
        collectionPref.setSummary(getResources().getString(R.string.menu_prefs_collectionopts_summary));
        applicationCategory.addPreference(collectionPref);
        
        uploadPref = new Preference(this);
        uploadPref.setTitle(getResources().getString(R.string.menu_prefs_datausage_title));
        uploadPref.setSummary(getResources().getString(R.string.menu_prefs_datausage_summary));
        applicationCategory.addPreference(uploadPref);
        
        unitsPref = new CheckBoxPreference(this);
        unitsPref.setTitle(getResources().getString(R.string.menu_prefs_units_title));
        unitsPref.setChecked(true); //TODO
        unitsPref.setSummary(getResources().getString(R.string.menu_prefs_units_summary));
        applicationCategory.addPreference(unitsPref);
        
        return applicationCategory;
    	
    }
    
    /**
     * Creates the device category.
     *
     * @param root the root
     * @return the preference category
     */
    private PreferenceCategory createDeviceCategory(PreferenceScreen root) {
    	
    	PreferenceCategory deviceCategory = new PreferenceCategory(this);
        deviceCategory.setTitle(getResources().getString(R.string.menu_prefs_category_device));
        root.addPreference(deviceCategory);
        
    	return deviceCategory;
    	
    }

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	
		if (preference == usermodePref) {
			final String[] items = {getResources().getString(R.string.settings_modeselect_basicusermode), getResources().getString(R.string.settings_modeselect_advancedusermode)};

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle(getResources().getString(R.string.settings_modeselect_title));
	    	builder.setSingleChoiceItems(items, sharedPrefs.getInt(getResources().getString(R.string.prefs_mode), 0), new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	    	SharedPreferences.Editor prefEditor = sharedPrefs.edit();
	    	        switch(item){
	    	        	case BASIC_USER_MODE:
	    	        		prefEditor.putInt(getResources().getString(R.string.prefs_mode), BASIC_USER_MODE);
	    	        		prefEditor.commit();
	    	        		break;
	    	        	case ADVANCED_USER_MODE:
	    	        		prefEditor.putInt(getResources().getString(R.string.prefs_mode), ADVANCED_USER_MODE);
	    	        		prefEditor.commit();
	    	        		break;
	    	        	default:
	    	        		break;
	    	        }
	    	    }
	    	});
	    	AlertDialog alert = builder.create();
	    	
	    	alert.show();
		} else if (preference == uploadPref) {
			final String[] items = {getResources().getString(R.string.datausage_wifionly), getResources().getString(R.string.datausage_phonenetworkonly), getResources().getString(R.string.datausage_wifiandphonenetwork)};

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle(getResources().getString(R.string.datausage_title));
	    	builder.setSingleChoiceItems(items, sharedPrefs.getInt(getResources().getString(R.string.prefs_dataupload), 0), new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	    	SharedPreferences.Editor prefEditor = sharedPrefs.edit();
	    	        switch(item){
	    	        	case DATAUPLOAD_WIFI_ONLY:
	    	        		prefEditor.putInt(getResources().getString(R.string.prefs_dataupload), DATAUPLOAD_WIFI_ONLY);
	    	        		prefEditor.commit();
	    	        		break;
	    	        	case DATAUPLOAD_NETWORK_ONLY:
	    	        		prefEditor.putInt(getResources().getString(R.string.prefs_dataupload), DATAUPLOAD_NETWORK_ONLY);
	    	        		prefEditor.commit();
	    	        		break;
	    	        	case DATAUPLOAD_BOTH:
	    	        		prefEditor.putInt(getResources().getString(R.string.prefs_dataupload), DATAUPLOAD_BOTH);
	    	        		prefEditor.commit();
	    	        		break;
	    	        	default:
	    	        		break;
	    	        }
	    	    }
	    	});
	    	AlertDialog alert = builder.create();
	    	
	    	alert.show();
		} else {
			return false;
		}
		
/*		if (preference == vehiclePref) {

		} else if (preference == accountPref) {
			
		}  else if (preference == displayPref) {
			
		} else if (preference == collectionPref) {
			
		} else if (preference == unitsPref) {
*/			
		
		
		return true;
	}
    
    
}