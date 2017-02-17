/*
 *  Bud D. Seemann
 *  CIS 2818
 *  MatchIt app
 *  Settings Class
 *  Used to get level preference from the user
 */

//Package Name
package net.budsapps.matchit;
//imports
import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * Settings Activity
 */
public class Settings extends PreferenceActivity {

	/*
	 * Called when the activity is first created.
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    //Adds preference options from preferences xml file
	    addPreferencesFromResource(R.xml.preferences);
	}//end onCreate Method
}//end Settings class
