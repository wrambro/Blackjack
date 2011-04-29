package tma.blackjack;

import java.io.*;
import java.util.*;
import tma.deck.Card;

/**
 * The BJStrategy class serves as the AI for non-human players. It can also
 * be used to give tips to human players. It handles reading and parsing the
 * strategy table, detemining which table to use given a hand, etc.
 * 
 * @author Tyler Ambroziak
 *
 */

public class BJStrategy {
	
	private final String FILE;
	// char[player][dealer]
	private char[][] oneAce;
	private char[][] pair;
	private char[][] sum;

	/**
	 * Default constructor. No real configuration required, and allowing a configurable
	 * strategy grid would be difficult, and in general, not a good idea
	 */
	public BJStrategy() {
		FILE = "strategyGrid";
		oneAce = new char[9][10];
		pair = new char[10][10];
		sum = new char[16][10];
		buildStrategyTables();
	}

	/**
	 * Reads the strategy grid from a flat file, parses it into arrays, and
	 * handles all potential exceptions
	 * <br><br>
	 * Strategy grid contains certain qualifiers:
	 * <ul>
	 * <li>// - comment in the file; these lines are ignored</li>
	 * <li>;; - dealer qualifier; next line contains the dealer's cards</li>
	 * <li>;# - player qualifier; next line contains the player's card value. This
	 * also implicitly signfies the start of a new table</li>
	 * </ul>
	 * Lines without qualifiers are data that actually is read into the program.
	 * Each int represents a card value, and each char represents a move.
	 * <br><br>
	 * The chars correspond to the following moves:
	 * <ul>
	 * <li>h - Hit</li>
	 * <li>s - Stay</li>
	 * <li>p - Split</li>
	 * <li>d - Double Down</li>
	 * </ul>
	 * 
	 * @return True if tables build successfully, False otherwise
	 */
	@SuppressWarnings("hiding")
	public boolean buildStrategyTables() {
		File in = null;
		int[] pTable = null;
		int[] dTable = null;
		Scanner fileIn = null;
		Scanner lineIn = null;
		String line = "";
		String qualifier = "";
		int pIdx, dIdx, table;
		char temp;
		table = pIdx = dIdx = 0;
		
		in = new File(FILE);
		
		try {
			fileIn = new Scanner(in);
			while (fileIn.hasNextLine()) {
				line = fileIn.nextLine();
				qualifier = line.substring(0,2);
				if (qualifier.equals("//"));		// ignore comments (semicolon is intentional)
				else if (qualifier.equals(";;")) 
					dTable = buildAuxTable(fileIn.nextLine());	// store dealer values
				else if (qualifier.equals(";#")) {
					table++;
					pIdx = 0;
					pTable = buildAuxTable(fileIn.nextLine());
					
				} else {
					// if we get here, we know we have data we want to load
					dIdx = 0;
					lineIn = new Scanner(line);
					lineIn.useDelimiter(",");
					while (lineIn.hasNext()) {
						temp = lineIn.next().charAt(0);
						// put char into the correct table
						switch (table) {
						case 1: 
							oneAce[pTable[pIdx]-2][dTable[dIdx]-2] = temp;
							break;
						case 2:
							pair[pTable[pIdx]-2][dTable[dIdx]-2] = temp;
							break;
						case 3:
							sum[pTable[pIdx]-5][dTable[dIdx]-2] = temp;
							break;
						}
						// move one column left for every char read
						dIdx++;
					}
					lineIn.close();
					// move down one row when done with a complete line
					pIdx++;
				}
			}
			fileIn.close();
		} catch (FileNotFoundException e) {
			System.out.println("Strategy table file not found");
			return false;
		} catch (IOException e) {
			System.out.println("Error parsing strategy table");
			return false;
		} catch (Exception e) {
			System.out.println("Error occurred loading strategy table");
			return false;
		} finally {
			// sanity check
			if (fileIn != null) fileIn.close();
			if (lineIn != null) lineIn.close();
		}
		
		return true;
	}

	/**
	 * This method looks at the passed-in hand, determines which strategy table
	 * it should use, and returns the recommended move based on the table. The
	 * work of choosing the table is actually done in the chooseTable method. 
	 * <br><br>
	 * This method also calculates the value to pass to findMove for use as a 
	 * subscript for the lookup table. 
	 * <br><br>
	 * The subscript value depends on which table is used:
	 * <ul>
	 * <li>1 - use value of the non-Ace card</li>
	 * <li>2 - use value of either card (since they are the same)</li>
	 * <li>3 - use the sum of all card values</li>
	 * </ul>
	 * 
	 * @param hand The hand (ArrayList of Cards) that we should recommend a move for
	 * @param dealer Card that the dealer is showing (not the value)
	 * @return Move if a table could be determined, Null otherwise
	 */
	public Move getRecommendedMove(ArrayList<Card> hand, Card dealer) {
		int value,table;
		
		table = chooseTable(hand);
		if (table == 3) value = BJController.handValue(hand)[0];
		else if (table == 2) value = BJController.cardValue(hand.get(0));
		else if (table == 1) 
			value = Math.min(BJController.cardValue(hand.get(0)), 
							 BJController.cardValue(hand.get(1)));
		else return null;
		
		return findMove(table, value, BJController.cardValue(dealer));
		
	}

	/**
	 * This method encapsulates the table lookup code, based on the size of each
	 * table, as well as the number of known valid values for a given table
	 * 
	 * @param table Which table to use (see chooseTable documentation for details
	 * about how this calculate)
	 * @param pVal Player card value to lookup (see getRecommendedMove documentation
	 * for details about how this value is calculated)
	 * @param dVal int value of Dealer's up card
	 * @return Move if found from valid table, null otherwise
	 */
	private Move findMove(int table, int pVal, int dVal) {
		
		switch (table) {
		case 1: 
			return moveFromChar(oneAce[pVal-2][dVal-2]);
		case 2:
			return moveFromChar(pair[pVal-2][dVal-2]);
		case 3: 
			return moveFromChar(sum[pVal-5][dVal-2]);
		default:
			return null;
		}
	}

	/**
	 * This method translates a given char into the Move it represents.
	 * @param c char representing a Move
	 * @return Move represented by a char, or null if invalid char
	 */
	private Move moveFromChar(char c) {
	
		switch (c) {
		case 'p':
			return Move.Split;
		case 's':
			return Move.Stay;
		case 'd':
			return Move.Double;
		case 'h':
			return Move.Hit;
		default: 
			return null;
		}
	}

	/**
	 * This method evaluates a hand and chooses which table should be used to
	 * look up the recommended move. The possible return values are as follows:
	 * <ol>
	 * <li>1 - Used when hand has a single Ace</li>
	 * <li>2 - Used when hand is a pair of cards</li>
	 * <li>3 - Used any time a hand has 3 or more cards, or does not meet the
	 * 		   conditions for tables 1 or 2</li>
	 * </ol>
	 * @param hand Hand to evaluate
	 * @return int indicating which table should be used for this hand.
	 */
	private int chooseTable(ArrayList<Card> hand) {
		if (hand.size() > 2) return 3;
		else if (BJController.cardValue(hand.get(0)) 
			  == BJController.cardValue(hand.get(1))) return 2;
		else return 1;
	}

	/**
	 * This method builds auxiliary arrays that are needed in for building the
	 * strategy tables. 
	 * @param line String containing comma-delimited integer values to put into
	 * auxiliary array
	 * @return int[] array containing values from line
	 */
	private int[] buildAuxTable(String line) {
		ArrayList<Integer> array = new ArrayList<Integer>();
		Scanner input = new Scanner(line);
		input.useDelimiter(",");
		
		for (; input.hasNextInt(); array.add(input.nextInt())) {}
		
		input.close();
		
		int[] ret = new int[array.size()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = array.get(i);
		}
		
		return ret;
	}
}
