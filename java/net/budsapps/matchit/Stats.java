/*
 *  Bud D. Seemann
 *  CIS 2818
 *  MatchIt app
 *  Stats Class
 *  Used to show the stats for the current level
 */

//Package Name
package net.budsapps.matchit;
//imports
//import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * Stats activity
 */
public class Stats extends Activity {

    private TextView noOfGames;
    private TextView minClicks;
    private TextView maxClicks;
    private TextView aveClicks;
    private TextView gamesQuit;
    
    private int iShowLevel;
	
	/*
	 * onCreate method
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        
        Intent intent = this.getIntent();

        //Stat variables
        int iDefLevel; //Will be level of current game
        iDefLevel = intent.getIntExtra(GlobalConstants.INTENT_LEVEL, 8);
               
        //get references to TextView objects to store stats
        noOfGames = (TextView) findViewById(R.id.tvGamesVal);
        minClicks = (TextView) findViewById(R.id.tvMinVal);
        maxClicks = (TextView) findViewById(R.id.tvMaxVal);
        aveClicks = (TextView) findViewById(R.id.tvAveVal);
        gamesQuit = (TextView) findViewById(R.id.tvQuitVal);
        	
        //Update spinner control
        Spinner spinner = (Spinner) findViewById(R.id.spinLevel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.pairs_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        spinner.setSelection(GameEngine.getPostion(iDefLevel));

	}//end of onCreate method

	/*
	 * 
	 */
	private void showStats(){

        noOfGames.setText(String.valueOf(GlobalConstants.gameStats.getTotalGames(iShowLevel)));
        minClicks.setText(String.valueOf(GlobalConstants.gameStats.getMinClicks(iShowLevel)));
        maxClicks.setText(String.valueOf(GlobalConstants.gameStats.getMaxClicks(iShowLevel)));
        aveClicks.setText(GlobalConstants.gameStats.getAveClicks(iShowLevel));
        gamesQuit.setText(String.valueOf(GlobalConstants.gameStats.getGamesQuit(iShowLevel)));				
	}
	
	
    /*
     * onCreateOptionsMenu method
     * used to inflate Icon menu from xml file
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.statsmenu, menu);
    	return true; 
    }//end onCreateOptionsMenu method
  
    /*
     * onOptionsItemSelected method
     * used to handle each menu option
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
        	//Reset current level stats
        	case R.id.itmResetLvl:
        		//reset stats for current level
        		GlobalConstants.gameStats.resetStats(iShowLevel);
        		showStats();
        		return true;
        	//reset all stats
        	case R.id.itmResetAll:
        		//reset all stats
        		GlobalConstants.gameStats = null;
        		GlobalConstants.gameStats = new StatsData();
        		showStats();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    
    }// end onOptionsItemSelected method
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {
    	public void onItemSelected(AdapterView<?> parent,
    			View view, int pos, long id) {
    		iShowLevel = GameEngine.getLevelByPos(pos);
            showStats();	
    	}
    	
    	public void onNothingSelected(AdapterView<?> parent) {
    		// Do nothing.    
    	}    	
    }

}//End of Stats class