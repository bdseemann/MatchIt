package net.budsapps.matchit;

import java.io.Serializable;
import java.util.Random;


@SuppressWarnings("serial")
public class GameEngine implements Serializable {




	/**
	 * 
	 */
	//private static final long serialVersionUID = 31632L;
	
	//difficulty level
	private int iLevel;
	//total number of pairs
	private int iPairs;
	//if game is running, set to true on first click
	private boolean bRunning;
	//game clicks
	private int iClicks;
	
	
	//List of numbers corresponding to game pieces
	private int[] memCards;
	//keeps track of matched pairs so further clicks aren't processed
	private boolean[] piecesMatched;
	
	//If a piece is currently selected	
	private boolean bPieceSelected;
	//Index of piece currently selected
	private int iSelPieceIndx;

	//Number of matched pairs
	private int iMatchedPairs;
	
	private boolean bGameWon;
	
	//Constructor creates a new instance of the game.
	public GameEngine(int newLevel)
	{
		iLevel = newLevel;
		iPairs = iLevel / 2;
		bRunning = false;
		iClicks = 0;
		bPieceSelected = false;
		iSelPieceIndx = -1;
		iMatchedPairs = 0;
		bGameWon = false;
		
		setPieces();
	
	}
	
	
	/*
	 * getLevel method 
	 * Instead of a global variable in the main activity
	 * get the level from the GameEngine object each time
	 */
	public int getLevel (){
		return iLevel;
	}
	
	/*
	 * getState method
	 * returns true if the game has started
	 */
	public boolean getState() {
		return bRunning;
	}
	
	
	public boolean isMatched(int pieceIdx){
		return piecesMatched[pieceIdx];
	}
	
	public boolean isSelected(int pieceIdx){
		return iSelPieceIndx == pieceIdx;
	}
	
	public boolean isPieceSelected(){
		return bPieceSelected;		
	}
	/*
	 * getPiece method
	 * return the int of the current piece
	 * called anytime a piece is clicked
	 * 
	 */
	//public int selectPiece(int iIndex){
	public void selectPiece(int iIndex){
		//
		bRunning = true;
		iClicks++;
		iSelPieceIndx = iIndex;
		bPieceSelected = true;
		//return memCards[iIndex];
		
		
	}
	
	public int getPieceVal(int iIndex) {
		return memCards[iIndex];
	}
	
	public int getSelectedIndex(){
		return iSelPieceIndx;
	}
	
	
	public boolean getCardSelected() {
		return bPieceSelected;
	}
	
	public int getClicks(){
		return iClicks;
	}
	
	public boolean checkMatch(int iIndex){
		iClicks++;
		bRunning = true;
		if (memCards[iIndex] == memCards[iSelPieceIndx]) {
		
			piecesMatched[iIndex] = true;
			piecesMatched[iSelPieceIndx] = true;
			iMatchedPairs++;
		
			//reset
			bPieceSelected = false;
			iSelPieceIndx = -1;
			//updateClicks(INCREMENT_CLICKS);
			if (iMatchedPairs == iPairs)
				bGameWon = true;
			return true;
		} else {
            bPieceSelected = true;
            iSelPieceIndx = iIndex;
			return false;
		}

		
	}
	
	//How do I want to handle this?
	public boolean gameWon(){
		return bGameWon;
	}
	
	public void gameOver(int endType){
		//update game quit stats
		if (endType == GlobalConstants.GAME_ENDED){
			//StatsData.updateQuits(iLevel);
			GlobalConstants.gameStats.updateQuits(iLevel);
		}
		
		if (endType == GlobalConstants.GAME_FINISHED){
			//StatsData.updateRecord(iLevel, iClicks);
			GlobalConstants.gameStats.updateRecord(iLevel, iClicks);
		}
			
	}
	
	

	//Was there a better way to do this?
	public static int getPostion(int iGetLevel){
		
		switch (iGetLevel){
			case GlobalConstants.LVL_VERY_EASY:
				return 0;
			case GlobalConstants.LVL_EASY:
				return 1;
			case GlobalConstants.LVL_MEDIUM:
				return 2;
			case GlobalConstants.LVL_HARD:
				return 3;
			case GlobalConstants.LVL_VERY_HARD:
				return 4;
			default:
				return 1;
		}

	}
	
	public static int getLevelByPos(int iPos){
		switch (iPos){
		case 0:
			return GlobalConstants.LVL_VERY_EASY;
		case 1:
			return GlobalConstants.LVL_EASY;
		case 2:
			return GlobalConstants.LVL_MEDIUM;
		case 3:
			return GlobalConstants.LVL_HARD;
		case 4:
			return GlobalConstants.LVL_VERY_HARD;
		default:
			return GlobalConstants.LVL_EASY;
		}
	}
	
	
	//************************************ notes **********************************
	/* 
	 * app creates game
	 * constructor creates game based on level and creates piece array of random numbers
	 * (app also lays out image pieces to represent numbers)
	 * 
	 * when user clicks a piece -
	 * 		app gets image based on piece clicked
	 * 		app gets piece based on image index to show clicked piece
	 * 
	 * 		app checks if piece is selected
	 * 			if no piece selected
	 * 		
	 * 
	*/
	
	
	
	
	
	//--------------------------------  Private below here -------------------------------------------
	/*
     * setPieces method
     * used to create list of random number pairs
     * only used internally. When game ends
     * it will be replaced by a new instance.
     */
    private void setPieces(){
    	Random getNums = new Random();
    	int newNum = 0;
    	boolean goodNum = false;
    	int instCnt;
  	
    	memCards = new int[iLevel];
    	piecesMatched = new boolean[iLevel];
    	
    	for (int i = 0; i < iLevel; i++) {
        	//  get random num between 1 and noOfPairs
    		
    		while (!goodNum) {
    			newNum = (Math.abs (getNums.nextInt()) % iPairs) + 1;
    		
    			//if it's the first 2 numbers you don't need to check if there are already 2
    			if (i < 2) {
    				goodNum = true;
    			} else {
    				instCnt = 0;
    				for (int j = 0; j < i; j++) {
    					if (memCards[j] == newNum) {
    						instCnt++;
    					}
    				}
    				
    				if (instCnt < 2)
    					goodNum = true;    				
    			}
    		}
    		
    		memCards[i] = newNum;
    		piecesMatched[i] = false;
    		goodNum = false;
    		
    		
    	}
    }// end setPieces method

	
	

}
