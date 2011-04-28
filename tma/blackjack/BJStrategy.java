package tma.blackjack;

import java.io.*;
import java.util.*;

import tma.deck.Card;

public class BJStrategy {

	private final String FILE;
	// char[player][dealer]
	private char[][] oneAce;
	private char[][] pair;
	private char[][] sum;

	public BJStrategy() {
		FILE = "strategyGrid";
		oneAce = new char[9][10];
		pair = new char[10][10];
		sum = new char[16][10];
	}

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
				if (qualifier.equals("//"));		// ignore comments
				else if (qualifier.equals(";;")) 
					dTable = buildAuxTable(fileIn.nextLine());	// store dealer values
				else if (qualifier.equals(";#")) {
					table++;
					pIdx = 0;
					pTable = buildAuxTable(fileIn.nextLine());
					
				}
				else {
					dIdx = 0;
					lineIn = new Scanner(line);
					lineIn.useDelimiter(",");
					while (lineIn.hasNext()) {
						temp = lineIn.next().charAt(0);
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
						dIdx++;
					}
					lineIn.close();
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
			if (fileIn != null) fileIn.close();
			if (lineIn != null) lineIn.close();
		}
		
		return true;
	}

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

	private int chooseTable(ArrayList<Card> hand) {
		if (hand.size() > 2) return 3;
		else if (BJController.cardValue(hand.get(0)) 
			  == BJController.cardValue(hand.get(1))) return 2;
		else return 1;
	}

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
