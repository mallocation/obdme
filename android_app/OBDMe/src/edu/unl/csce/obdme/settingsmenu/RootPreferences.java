package edu.unl.csce.obdme.settingsmenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.unl.csce.obdme.OBDMe;
import edu.unl.csce.obdme.R;

/**
 * The Class RootPreferences.
 */
public class RootPreferences extends PreferenceActivity {
	 
    /** The Constant USER_QUIT_SETTINGS. */
    public static final int USER_QUIT_SETTINGS = 113518329;

	/** The root view. */
    private LinearLayout rootView;
 
    /** The preference view. */
    private ListView preferenceView;
    
    /** The shared prefs. */
	SharedPreferences sharedPrefs;
 
    /* (non-Javadoc)
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 
    	//Get rid of that ugly title bar... GTFO TITLE BAR.  NO ONE LIKES YOU!
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        sharedPrefs = getSharedPreferences(getResources().getString(R.string.prefs_tag), MODE_PRIVATE);

        //Init the root view
        rootView = new LinearLayout(this); 
        rootView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        rootView.setOrientation(LinearLayout.VERTICAL);
       
        //Init the preference view
        preferenceView = new ListView(this);
        preferenceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        preferenceView.setId(android.R.id.list);
        
        //Set up the preference screen from our created preference hierarchy
        PreferenceScreen screen = createPreferenceHierarchy();
        screen.bind(preferenceView);
        preferenceView.setAdapter(screen.getRootAdapter());
 
        //Compose that shit
        rootView.addView(preferenceView);
        
        //BAM! PREFERENCE IN YO FACE
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
    	
    	//Create the account preference group (category)
    	PreferenceCategory accountCategory = new PreferenceCategory(this);
        accountCategory.setTitle(getResources().getString(R.string.menu_prefs_category_account));
        root.addPreference(accountCategory);
        
        //Account info settings
        Preference accountPref = new Preference(this);
        accountPref.setTitle(getResources().getString(R.string.menu_prefs_accountinfo_title));
        accountPref.setSummary(getResources().getString(R.string.menu_prefs_accountinfo_summary));
        accountPref.setPersistent(false);
        accountCategory.addPreference(accountPref);
        
        //Vehicle settings
        Preference vehiclePref = new Preference(this);
        vehiclePref.setTitle(getResources().getString(R.string.menu_prefs_vehicles_title));
        vehiclePref.setSummary(getResources().getString(R.string.menu_prefs_vehicles_summary));
        vehiclePref.setIntent(new Intent(this, VehicleInformation.class));
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
    	
    	//Create the application preference group (category)
    	PreferenceCategory applicationCategory = new PreferenceCategory(this);
        applicationCategory.setTitle(getResources().getString(R.string.menu_prefs_category_application));
        root.addPreference(applicationCategory);
        
        //Make the usermode preference
        IntegerListPreference usermodePref = new IntegerListPreference(this);
        usermodePref.setTitle(getResources().getString(R.string.menu_prefs_usermode_title));
        usermodePref.setSummary(getResources().getString(R.string.menu_prefs_usermode_summary));
        usermodePref.setDialogTitle(getResources().getString(R.string.menu_prefs_usermode_dialog_title));

        usermodePref.setPersistent(true);
        usermodePref.setKey(getResources().getString(R.string.prefs_mode));
        
        usermodePref.setEntries(new String[] {getResources().getString(R.string.menu_prefs_usermode_basic), 
        		getResources().getString(R.string.menu_prefs_usermode_advanced)});
        usermodePref.setEntryValues(new String[] {Integer.toString(OBDMe.BASIC_USER_MODE), 
        		Integer.toString(OBDMe.ADVANCED_USER_MODE)});
       
        applicationCategory.addPreference(usermodePref);
        
        //Display options
        Preference displayPref = new Preference(this);
        displayPref.setTitle(getResources().getString(R.string.menu_prefs_displayopts_title));
        displayPref.setSummary(getResources().getString(R.string.menu_prefs_displayopts_summary));
        displayPref.setIntent(new Intent(this, DisplayedPIDList.class));
        applicationCategory.addPreference(displayPref);
        
        //Data collection options
        Preference collectionPref = new Preference(this);
        collectionPref.setTitle(getResources().getString(R.string.menu_prefs_collectionopts_title));
        collectionPref.setSummary(getResources().getString(R.string.menu_prefs_collectionopts_summary));
        collectionPref.setIntent(new Intent(this, CollectedPIDList.class));
        applicationCategory.addPreference(collectionPref);
                
        //Upload options
        IntegerListPreference uploadPref = new IntegerListPreference(this);
        uploadPref.setTitle(getResources().getString(R.string.menu_prefs_datausage_title));
        uploadPref.setSummary(getResources().getString(R.string.menu_prefs_datausage_summary));
        uploadPref.setDialogTitle(getResources().getString(R.string.menu_prefs_datausage_dialog_title));
        
        uploadPref.setPersistent(true);
        uploadPref.setKey(getResources().getString(R.string.prefs_dataupload));
        
        uploadPref.setEntries(new String[] {getResources().getString(R.string.menu_prefs_datausage_wifionly), 
        		getResources().getString(R.string.menu_prefs_datausage_phonenetworkonly),
        		getResources().getString(R.string.menu_prefs_datausage_wifiandphonenetwork)});
        
        uploadPref.setEntryValues(new String[] {Integer.toString(OBDMe.DATA_USAGE_WIFI_ONLY), 
        		Integer.toString(OBDMe.DATA_USAGE_NETWORK_ONLY),
        		Integer.toString(OBDMe.DATA_USAGE_WIFI_AND_NETWORK)});
        
        applicationCategory.addPreference(uploadPref);
       
        //English/Metric option
        CheckBoxPreference unitsPref = new CheckBoxPreference(this);
        unitsPref.setPersistent(true);
        unitsPref.setTitle(getResources().getString(R.string.menu_prefs_units_title));
        unitsPref.setKey(getResources().getString(R.string.prefs_englishunits));
        unitsPref.setSummary(getResources().getString(R.string.menu_prefs_units_summary));
        applicationCategory.addPreference(unitsPref);
        
        //GPS/Location option
        CheckBoxPreference gpsPref = new CheckBoxPreference(this);
        gpsPref.setPersistent(true);
        gpsPref.setTitle(getResources().getString(R.string.menu_prefs_gps_title));
        gpsPref.setKey(getResources().getString(R.string.prefs_gps));
        gpsPref.setSummary(getResources().getString(R.string.menu_prefs_gps_summary));
        
        applicationCategory.addPreference(gpsPref);
        
        return applicationCategory;
    	
    }
    
    /**
     * Creates the device category.
     *
     * @param root the root
     * @return the preference category
     */
    private PreferenceCategory createDeviceCategory(PreferenceScreen root) {
    	
    	//Create the device preference group (category)
    	PreferenceCategory deviceCategory = new PreferenceCategory(this);
        deviceCategory.setTitle(getResources().getString(R.string.menu_prefs_category_device));
        root.addPreference(deviceCategory);
        
    	return deviceCategory;
    	
    }

    
    /**
     * Return error to caller.
     *
     * @param bundle the bundle
     */
    public void returnErrorToCaller(Bundle bundle) {
    	
    	  // sets the result for the calling activity
    	  setResult(USER_QUIT_SETTINGS);

    	  // equivalent of 'return'
    	  finish();
    }
 
}