package net.budsapps.matchit;

import java.io.Serializable;
import java.text.DecimalFormat;





@SuppressWarnings("serial")
public class StatsData  implements Serializable {
	
	private int[] iTotalClicks;
	private int[] iTotalGames;
	private int[] iMinClicks;
	private int[] iMaxClicks;
	private int[] iGamesQuit;
	private int[] iStatLevel;
	
	
	public StatsData(){
		iTotalClicks = new int[GlobalConstants.NO_OF_LEVELS];
		iTotalGames = new int[GlobalConstants.NO_OF_LEVELS];
		iMinClicks = new int[GlobalConstants.NO_OF_LEVELS];
		iMaxClicks = new int[GlobalConstants.NO_OF_LEVELS];
		iGamesQuit = new int[GlobalConstants.NO_OF_LEVELS];
		iStatLevel = new int[GlobalConstants.NO_OF_LEVELS];
		
		for (int i = 0; i < GlobalConstants.NO_OF_LEVELS; i++) {
			iTotalClicks[i] = 0;
			iTotalGames[i] = 0;
			iMinClicks[i] = 0;
			iMaxClicks[i] = 0;
			iGamesQuit[i] = 0;
		}
		//Not sure the best way to handle this
		iStatLevel[0] = GlobalConstants.LVL_VERY_EASY;
		iStatLevel[1] = GlobalConstants.LVL_EASY;
		iStatLevel[2] = GlobalConstants.LVL_MEDIUM;		
		iStatLevel[3] = GlobalConstants.LVL_HARD;
		iStatLevel[4] = GlobalConstants.LVL_VERY_HARD;
	}
	
	public int getTotalClicks(int iLevel) {
		int statIdx = getLevelIndex(iLevel);
		
		return iTotalClicks[statIdx];
	}
	public int getTotalGames(int iLevel) {
		int statIdx = getLevelIndex(iLevel);
		
		return iTotalGames[statIdx];
	}
	public int getMinClicks(int iLevel) {
		int statIdx = getLevelIndex(iLevel);
		
		return iMinClicks[statIdx];
	}
	public int getMaxClicks(int iLevel) {
		int statIdx = getLevelIndex(iLevel);
		
		return iMaxClicks[statIdx];
	}
	public int getGamesQuit(int iLevel) {
		int statIdx = getLevelIndex(iLevel);
		
		return iGamesQuit[statIdx];
	}
	
	//Only used for display so I can return this as a String
	public String getAveClicks(int iLevel){
		double dAveClicks;
		
		int statIdx = getLevelIndex(iLevel);
		DecimalFormat decForm = new DecimalFormat( "#0.00" );

		if (iTotalGames[statIdx] > 0)
			dAveClicks = ((double) iTotalClicks[statIdx]) / ((double) iTotalGames[statIdx]);
		else
			dAveClicks = 0;
		
		return decForm.format(dAveClicks);
	}	
	
	//Get's the array index of the specified level
	private int getLevelIndex(int iLevel){
		for (int i = 0; i < GlobalConstants.NO_OF_LEVELS; i++){
			if (iStatLevel[i] == iLevel)
				return i;
		}
		//Shouldn't happen
		return -1;
	}
	
	//take given info and updates the given stat level
		public void updateRecord(int iLevel, int iClicks){

			int statIdx = getLevelIndex(iLevel);
			
			iTotalClicks[statIdx] = iTotalClicks[statIdx] + iClicks;
			iTotalGames[statIdx]++;
			
	        if ((iClicks < iMinClicks[statIdx]) || (iMinClicks[statIdx] <=0))
	        	iMinClicks[statIdx] = iClicks;
	        	
	        if ((iClicks > iMaxClicks[statIdx]) || (iMaxClicks[statIdx] <=0))
	        	iMaxClicks[statIdx] = iClicks; 
		}
	
	

		//updates games ended field for specified level
		public void updateQuits(int iLevel) {

			int statIdx = getLevelIndex(iLevel);
			iGamesQuit[statIdx]++;
		}
		
		
		//Reset stats for current level
		public void resetStats(int iLevel) {
			int statIdx = getLevelIndex(iLevel);
			
			iTotalClicks[statIdx] = 0;
			iTotalGames[statIdx] = 0;
			iMinClicks[statIdx] = 0;
			iMaxClicks[statIdx] = 0;
			iGamesQuit[statIdx] = 0;
			
		}

	
}
