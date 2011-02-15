package edu.unl.csce.obdme.settingsmenu;

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
 
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

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
		
		if (preference == vehiclePref) {

		} else if (preference == accountPref) {
			
		} else if (preference == usermodePref) {
			
		} else if (preference == displayPref) {
			
		} else if (preference == collectionPref) {
			
		} else if (preference == uploadPref) {
			
		} else if (preference == unitsPref) {
			
		} else {
			return false;
		}
		
		return true;
	}
    
    
}