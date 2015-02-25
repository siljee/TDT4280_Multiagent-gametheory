package oving3;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class consists of a list of all tradeable items. It also keeps track 
 * of which of these items are already owned by agents. The items are generated randomly. 
 * The class is made as a SINGLETON, to make sure all agents are linked to the same list.
 * Read about singleton here: http://en.wikipedia.org/wiki/Singleton_pattern.
 * This class is needed to make sure no agents desire items that no agents have in their
 * inventory 
 * 
 * @author Hansen and Fagerland
 *
 */
public class GlobalItems implements Constants{
	// This is a singleton class
	private static final GlobalItems INSTANCE = new GlobalItems();
	
	private ArrayList<Item> items;		// A list of all existing items
	private ArrayList<Item> ownedItems;	// A list of all items that are owned by agents
	private Random random;
	
	/* *************************************** *
	 *       Initialization Methods
	 * *************************************** */
	
	private GlobalItems() {
		random = new Random();
		ownedItems = new ArrayList<Item>();
		items = new ArrayList<Item>();
		initializeItems(NUMBER_OF_ITEMS);
	}

	// Singleton
	public static GlobalItems getInstance(){
		return INSTANCE;
	}
	
	// Initializes items as a list of random Item.
	private void initializeItems(int numberOfItems) {
		for (int i = 0; i < numberOfItems; i++) {
			items.add(makeRandomItem());
		}
	}
	
	private Item makeRandomItem() {
		int itemValue = MIN_VALUE + random.nextInt(MAX_VALUE - MIN_VALUE );
		String itemName = makeRandomName();
		
		return new Item(itemName, itemValue);
	}
	
	// Makes a string which alternate between a vowel and consonant 
	private String makeRandomName() {
		String name = "";
		int nameLength = MIN_NAME_LENGTH + random.nextInt(MAX_NAME_LENGTH-MIN_NAME_LENGTH);
		boolean isVowel = random.nextBoolean();
		
		for (int i = 0; i  < nameLength; i++) {
			if (isVowel) {
				name += VOWELS.charAt(random.nextInt(VOWELS.length()));
			} else {
				name += CONSONANTS.charAt(random.nextInt(CONSONANTS.length()));
			}
			isVowel = !isVowel;
		}
		return name;
	}
	
	/* *************************************** *
	 *              Agent Methods
	 * *************************************** */
	
	public ArrayList<Item> makeInventoryList() {
		ArrayList<Item> inventoryList = new ArrayList<Item>();
 		int inventorySize = MIN_INVENTORY_LIST_LENGTH + random.nextInt(MAX_INVENTORY_LIST_LENGTH - MIN_DESIRED_LIST_LENGTH);
		Item item;
 		
		for (int i = 0; i < inventorySize; i++) {
			item = items.get(random.nextInt(items.size()));
			inventoryList.add(item);
			ownedItems.add(item);
		}
		return inventoryList;
	}
	
	public ArrayList<Item> makeDesiredList() {
		ArrayList<Item> desiredList = new ArrayList<Item>();
 		int desiredSize = MIN_DESIRED_LIST_LENGTH + random.nextInt(MAX_DESIRED_LIST_LENGTH - MIN_DESIRED_LIST_LENGTH);
		Item item;
 		
		for (int i = 0; i < desiredSize; i++) {
			item = items.get(random.nextInt(ownedItems.size()));
			desiredList.add(item);
			// MAYBE: If number of agents desiring an item cannot be larger than the number of 
			// agents owing an item, then this has to be included:
			// ownedItems.remove(item);
		}
		return desiredList;
	}
}

