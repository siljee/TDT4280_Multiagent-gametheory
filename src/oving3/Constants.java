package oving3;

/**
 * Constants used by the agents and items.
 */


public interface Constants {
	// Constants used in TradeableItems
	public static final int NUMBER_OF_ITEMS = 50;
	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 8;
	public static final int MIN_VALUE = 200;
	public static final int MAX_VALUE = 1000;
	public static final String VOWELS = "aeiou";					// without y to get prettier names
	public static final String CONSONANTS = "bdfgklmnprstv"; 		// without c, h, j, q, w, x and z
	
	// Constants used in GeneralAgent
	public static final int NUMBER_OF_AGENTS = 3;
	public static final int START_MONEY = 5000;
	public static final int MIN_INVENTORY_LIST_LENGTH = 2;
	public static final int MAX_INVENTORY_LIST_LENGTH = 20;
	public static final int MIN_DESIRED_LIST_LENGTH = 3;
	public static final int MAX_DESIRED_LIST_LENGTH = 10;
	public static final String AGENT_TYPE = "negotiation";
}
