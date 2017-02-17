/*
 *  Bud D. Seemann
 *  CIS 2818
 *  MatchIt app
 *  MatchItBudSeemannActivity Class
 *  Main activity created on startup
 */

//Package Name
package net.budsapps.matchit;

//Imports

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



/*
 * MatchItBudSeemannActivity
 */
public class MatchItActivity extends Activity {
    
	/*
	 * Constants
	 */
	//String for card back image file name
	private static final String CARDBACK_STR = "back.png";

	private static final int MAX_PIECES = 8;
	
	/*
	 * Class variables
	 */	
	//number of pieces for game - also used to denote difficulty level
	private static int noOfPieces;
	
	//game object
	private GameEngine game;
	
	//Used when board is created and when to flip a piece back over
	private Bitmap cardBack;
	private Bitmap[] cardBmps;
	
	//Context object
	public static Context appContext;
	//AssetManager object - used to get image files dynamically
	private AssetManager assetManager;
	
	
	//Table Rows to hold game pieces
	private TableRow[] bdRows;
	//Image view to show each game piece in (or back when not selected)
	private ImageView[] gamePieces;
	
	//Made a call to prefs or stats
	private boolean bShowPrefsStats;
	
	/*
     * onCreate Method
     * Called when the activity is first created. 
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.main);
        
        ObjectInputStream ois;
        
        //Get reference to context object
        appContext = getApplicationContext();    
    	assetManager = appContext.getAssets();
        
    	try {
			ois = new ObjectInputStream(
					openFileInput(GlobalConstants.STATS_FILE));
			GlobalConstants.gameStats = (StatsData) ois.readObject();
		} catch (Exception e) {	
			e.printStackTrace();
			GlobalConstants.gameStats = new StatsData();
		}
    	bShowPrefsStats = false;
    }
    
    
    @Override
    public void onStart(){    	
    	super.onStart();
    	
    	boolean bGameRestored = false;
        ObjectInputStream ois;
        
        Log.v("*** onStart ***", "In onStart method");
        if (!bShowPrefsStats) {
    		try {
				ois = new ObjectInputStream(
						openFileInput(GlobalConstants.GAME_FILE));
	    		game = (GameEngine) ois.readObject();
	    		
	    		bGameRestored = true;
	    		Log.v("*** onStart ***","Got Game from File");
			} catch (Exception e) {
				Log.v("*** onStart ***", "Exception");
				e.printStackTrace();
			}
        } else {
        	bShowPrefsStats = false;
        }
        		
    		if (!bGameRestored) {
	   			//Reference to shared preference object
	   			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
	    		//Get number of pieces from preferences, default is 8 (Easy)
	    		noOfPieces = Integer.valueOf(prefs.getString("NO_OF_PAIRS", "8"));
					
				game = new GameEngine(noOfPieces);
	    		Log.v("*** onStart ***","Created new game");    			
    		}
    		
    		
    		
    		//Create the piece array and add them to the layout with cardback showing
    		loadBmps();
    		createPieces();
    		drawBoard();
    		if (bGameRestored){
    			Log.v ("*** onStart ***", "gameRestored = true");
    			resumeGame();
    		} 	
    }
    
    @Override
    public void onStop(){
    	super.onStop();
		Log.v("*** onStop ***", "In onStop Method");
		ObjectOutputStream oos;   	
    	if (!bShowPrefsStats) {
    		try {
    			oos = new ObjectOutputStream(
    					openFileOutput(GlobalConstants.GAME_FILE, Context.MODE_PRIVATE));
			
    			//Do I want to put them both in the same file or should I use different files?
    			oos.writeObject(game);
    			oos.close(); 		
    			Log.v("*** onStop ***", "Stored game in file");
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    			Log.v("*** onStop ***", "FNF exception - game");			
    		} catch (IOException e) {
    			Log.v("*** onStop ***", "IO Exception - game");
    			e.printStackTrace();
    		}
    		
    		//reset booleans?
    	}
		try {
			oos = new ObjectOutputStream(
					openFileOutput(GlobalConstants.STATS_FILE, Context.MODE_PRIVATE));
			
			//Do I want to put them both in the same file or should I use different files?
			oos.writeObject(GlobalConstants.gameStats);  
			oos.close(); 		
			Log.v("*** onStop ***", "Stored stats in file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.v("*** onStop ***", "FNF exception - stats");			
		} catch (IOException e) {
			Log.v("*** onStop ***", "IO Exception - stats");
			e.printStackTrace();
		}
		removePieces();
    }
    
    
    /*
     * loadBmps method
     * load all card bitmaps into array
     * currently loading all 8, regardless of board size
     */
    public void loadBmps(){
    	
    	String fileName;
    	cardBmps = new Bitmap[MAX_PIECES];
    	InputStream inputStream = null;
    	
    	for (int i = 1; i <= MAX_PIECES; i++) {
    		fileName = String.valueOf(i) + ".png";
    		try {
    			inputStream = assetManager.open(fileName);
    		} catch (IOException e) {
    			return;
    		}
    		
    		cardBmps[i - 1] = BitmapFactory.decodeStream(inputStream);
    		try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
    }
    
    /* 
     * createPieces method
     * crates ImageView array to represent game pieces
     */
    public void createPieces() {

    	
    	InputStream inputStream = null;
		try {
			inputStream = assetManager.open(CARDBACK_STR);
		} catch (IOException e) {
			//e.printStackTrace();
			Log.v("*** Try/Catch ***", "IO error on getting card");
			return;
		}
	
    	cardBack = BitmapFactory.decodeStream(inputStream);
    	
    	if (cardBack == null)
    		Log.v("*** createPieces ***", "Number of pieces: " + game.getLevel());
            gamePieces = new ImageView[game.getLevel()];
            for (int i = 0; i < gamePieces.length; i++){
                gamePieces[i] = new ImageView(this);
                
                gamePieces[i].setPadding(5, 5, 5, 5);
				gamePieces[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                gamePieces[i].setImageBitmap(cardBack);
                gamePieces[i].setOnClickListener(new GameClickHandler());
            }
    }//end createPieces method
    
    /*
     * drawBoard method
     * places game pieces into rows and columns on the board
     */
    public void drawBoard(){

    	int gameRows;
    	int gameCols;
    	int addPieceIndex;
    	
    	//I would normally lock the orientation in the layout file to keep a consistent
    	//user interface.
    	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    		switch (game.getLevel()){
    			case 6:
    				gameRows = 3;
    				gameCols = 2;
    				break;
    			case 8:
    				gameRows = 4;
    				gameCols = 2;
    				break;
    			case 10:
    				gameRows = 5;
    				gameCols = 2;
    				break;
    			case 12:
    				gameRows = 4;
    				gameCols = 3;
    				break;
    			case 16:
    				gameRows = 4;
    				gameCols = 4;
    				break;
    			default:
    				gameRows = 0;
    				gameCols = 0;
    		}
    	} else {
    		//assume landscape    		
    		switch (noOfPieces) {
			case 6:
				gameRows = 2;
				gameCols = 3;
				break;
			case 8:
				gameRows = 2;
				gameCols = 4;
				break;
			case 10:
				gameRows = 2;
				gameCols = 5;
				break;
			case 12:
				gameRows = 3;
				gameCols = 4;
				break;
			case 16:
				gameRows = 2;
				gameCols = 8;
				break;
			default:
				gameRows = 0;
				gameCols = 0;
    		} 
    	}    	
    	TableLayout gameBd = (TableLayout) findViewById(R.id.rlBoard);
    	

    	bdRows = new TableRow[gameRows];
    	//rows on the outside
    	for (int i = 0; i < bdRows.length; i++) {
    		bdRows[i] = new TableRow(this);
    		
    		bdRows[i].setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));

        	//cols on the inside 
        	for (int j = 0; j < gameCols; j++) {       		
        		addPieceIndex = (i * gameCols) + j;
        		bdRows[i].addView(gamePieces[addPieceIndex]);
        	}
        	
    		gameBd.addView(bdRows[i], new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));    		

    	}
    }//end drawBoard method    
    
    /*
     * removePieces method
     * clears the game area so it can be recreated up return
     * from preferences menu
     */
    
	private void removePieces() {

    	//get reference to TableLayout
    	TableLayout gameBd = (TableLayout) findViewById(R.id.rlBoard);
    	//remove all child views (Rows)
    	gameBd.removeAllViews();
    	//void piece and row arrays
    	gamePieces = null;
    	bdRows = null;
	}//end removePieces method       
    
    private void resetBoard() {
    	//Loop through ImageView array
        for (int i = 0; i < gamePieces.length; i++){
        	//set image to card back bitmap
            gamePieces[i].setImageBitmap(cardBack);
            //set background color back to black (change if I change board colors)
            gamePieces[i].setBackgroundColor(Color.BLACK);
        }//end for loop
	}//end resetBoard method     
    
    
    private void resumeGame() {
    	int pieceValue;
    	int selIndx; 	
		//Need to loop through cards to get matched and selected
		noOfPieces = game.getLevel();
		if (game.getState()){
			for(int i = 0; i < noOfPieces; i++){

				if (game.isMatched(i)){
					pieceValue = game.getPieceVal(i);
	    		    
					gamePieces[i].setImageBitmap(cardBmps[pieceValue - 1]);
					gamePieces[i].setBackgroundColor(Color.YELLOW);
				}
	    	
				if (game.getCardSelected()){
					selIndx = game.getSelectedIndex();
					pieceValue = game.getPieceVal(selIndx);
					gamePieces[selIndx].setImageBitmap(cardBmps[pieceValue - 1]);
				}	
			}
		}
	
		showClicks();
    }
    
    	
	
    public void resetGame(){
		game = null;
		game = new GameEngine(noOfPieces);
		resetBoard();
		showClicks();	    
    }
    
    
    /*
     * indexOf method
     * used to get index of selected piece
     */
	private int indexOf(ImageView iv){
		//check selected ImageView against array of image views
		//the counter value of the one that matches is the index
        for (int i = 0; i < gamePieces.length; i++)
            if (gamePieces[i] == iv)
                return i;
        //not found, return -1 (Not currently handled)
        return -1;
    }//end indexOf method

    private void showClicks() {
    	//get reference to click display
    	int iShowClicks;
    	TextView txtClicks = (TextView) findViewById(R.id.txtClicksNo);
    	
    	
    	if (game != null)
    		iShowClicks = game.getClicks();
    	else
    		iShowClicks = 0;
    	
    	//update click display
    	txtClicks.setText(String.valueOf(iShowClicks));

    }//end showClicks method

    
	public void handleSelection(ImageView selPiece) {
    	int gpIndex = indexOf(selPiece);
    	int selValue;
    	//String showCard;
    	//InputStream inputStream = null;
    	//Bitmap dispCard;
    	
    	//skip handling a piece if it's already matched
    	if (game.isMatched(gpIndex))
    		return; 
	
    	//skip handling a piece if it's already shown
    	if (game.isSelected(gpIndex) && game.isPieceSelected())
    		return;
    	
    	//Check if a piece is selected
    	if (! game.isPieceSelected()) {
    		//**  show the current piece  **
    		selValue = game.getPieceVal(gpIndex);


    		gamePieces[gpIndex].setImageBitmap(cardBmps[selValue - 1]);
    		game.selectPiece(gpIndex);
            
    	} else {
    		//** a piece is already selected - check the new piece against the 
    		//   selected piece **
    		int oldPieceIdx = game.getSelectedIndex();
    		
    		boolean checkMatch = game.checkMatch(gpIndex);
    		
    		if (checkMatch) {
    			//cards are a match
    			//show both
    			//highlight both
        		selValue = game.getPieceVal(gpIndex);
        		
        		gamePieces[gpIndex].setImageBitmap(cardBmps[selValue-1]);
        		gamePieces[gpIndex].setBackgroundColor(Color.YELLOW);
        		gamePieces[oldPieceIdx].setBackgroundColor(Color.YELLOW);
    			    	
    		} else {
    			//hide previous piece
    			//show new piece
        		selValue = game.getPieceVal(gpIndex);

        		gamePieces[gpIndex].setImageBitmap(cardBmps[selValue-1]);
        		gamePieces[oldPieceIdx].setImageBitmap(cardBack);
    		}    		
    	}
		showClicks();
		
    	//if (noOfMatchedPairs == noOfPairs) {
    	if (game.gameWon()){
		//
    		
    		//put in a dialog or a toast    	   	
    		int iDlgClicks = game.getClicks();
    		
            Toast toast = Toast.makeText(appContext, "It took " + iDlgClicks + " clicks.", Toast.LENGTH_LONG);
            toast.show();
    		
    		//recordStats(GAME_FINISHED);
    		game.gameOver(GlobalConstants.GAME_FINISHED);
  		
    		resetGame();
    	}    
	}
    
    
    /*
     * onCreateOptionsMenu method
     * used to inflate Icon menu from xml file
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true; 
    }//end onCreateOptionsMenu method
  
    /*
     * onOptionsItemSelected method
     * used to handle each menu option
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
        	//Game was ended
        	case R.id.itmEnd:        		        		
        		//Record a quit game
        		game.gameOver(GlobalConstants.GAME_ENDED); 
        		resetGame();
        		return true;
        	//show stats screen
        	case R.id.itmStats:
        		//don't recreate the game when viewing stats.
        		bShowPrefsStats = true;
        		//show stats page
        		intent = new Intent(this, Stats.class);
        		intent.putExtra(GlobalConstants.INTENT_LEVEL, noOfPieces);
        		startActivity(intent);
        		return true;
        	//Reset stats option	
        	case R.id.itmReset:
        		//Reset stats, only for current level
        		GlobalConstants.gameStats.resetStats(noOfPieces);
        		return true;
        	//Show preferences screen
        	case R.id.itmPrefs:
        		//used to check preferences on return from screen
        		bShowPrefsStats = true;
        		//show preferences page
        		intent = new Intent(this, Settings.class);
        		startActivity(intent);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    
    }// end onOptionsItemSelected method

    /*
     * onPrepareOptionsMenu method
     * 
     */
	public boolean onPrepareOptionsMenu(Menu menu){
    	//Check if game is running
    	//if (bRunning) {
		if (game.getState()){
    		//If game is running only end game option should be enabled
    		
    		//get reference to menu items
    		MenuItem miEnd = menu.findItem(R.id.itmEnd);
    		MenuItem miStat = menu.findItem(R.id.itmStats);
    		MenuItem miReset = menu.findItem(R.id.itmReset);
    		MenuItem miPrefs = menu.findItem(R.id.itmPrefs);
    		//enable end game option, disable others
    		miEnd.setEnabled(true);
    		miStat.setEnabled(false);
    		miReset.setEnabled(false);
    		miPrefs.setEnabled(false);
    	} else {
    		//Game isn't running so disable end game and show others
    		
    		//get reference to menu items
    		MenuItem miEnd = menu.findItem(R.id.itmEnd);
    		MenuItem miStat = menu.findItem(R.id.itmStats);
    		MenuItem miReset = menu.findItem(R.id.itmReset);
    		MenuItem miPrefs = menu.findItem(R.id.itmPrefs);
    	    //disable end game option, enable others		
    		miEnd.setEnabled(false);
    		miStat.setEnabled(true);
    		miReset.setEnabled(true);
    		miPrefs.setEnabled(true);    		
    	}    	
    	
    	return super.onPrepareOptionsMenu(menu);
    }//end onPrepareOptionsMenu method

    
    /*
     * GameClickHandler class
     * named inner class that implements OnClickListener
     * to handle when the user clicks on a game piece
     */ 
    class GameClickHandler implements OnClickListener {
        /*
         * onClick method
         * handles the click on the specified view object
         */
    	@Override
        public void onClick(View v){
        	
    		//get the image piece that was clicked on
            ImageView clickedPiece = (ImageView) v;
            //pass to method to handle selection
            handleSelection(clickedPiece);

        }//end onClick method
        
    }//End GameClickHandler class

    
}//End MatchItBudSeemannActivity class
