package net.budsapps.matchit;



public class GlobalConstants {
	//make all items final
	
	
	
	
	//Intent Strings
	public static final String INTENT_LEVEL = "INTENT_LEVEL";
	
	
	//Level Constants
	public static final int LVL_VERY_EASY = 6;
	public static final int LVL_EASY = 8;
	public static final int LVL_MEDIUM = 10;
	public static final int LVL_HARD = 12;
	public static final int LVL_VERY_HARD = 16;
	
	
	public static final int NO_OF_LEVELS = 5;
	
	
	//game end type
	public static final int GAME_ENDED = -1;
	public static final int GAME_FINISHED = 1;


	public static final String GAME_FILE = "migame.dat";	
	public static final String STATS_FILE = "mistats.dat";		
	
	//Can I used this to hold a global object reference?
	public static StatsData gameStats;

}
