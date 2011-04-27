package tma.blackjack;
import tma.deck.*;

import java.util.*;

public class BJController {
	public static int cardValue(Card card) {
		int ret;
		switch (card.getRank()) {
		case Ace: 
			ret = 11; 
			break;
		case Ten: 
		case Jack:
		case Queen: 
		case King: 
			ret = 10; 
			break;
		default: 
			ret = card.getRank().ordinal();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static int[] handValue(ArrayList<Card> hand) {
		int[] score = new int[2];
		if (hand == null) {
			score[0] = score[1] = 0;
			return score;
		}
		int val, sum, aceSum;
		Collections.sort(hand);
		sum = 0;
		aceSum = 0;
	
		for (Card card : hand) {
			val = BJController.cardValue(card);
			if (val < 11) sum = sum + val;
	
			// handle Aces
			else {
				// if adding 11 does not bring it over 21, add 11 to aceSum
				if (sum + val < 22) aceSum = aceSum + 11;
				// else add 1 to ace sum
				else aceSum = aceSum + 1;
				// if aceSum + sum > 21 && aceSum > 10 aceSum = aceSum - 10;
			}
		}
		// check ace values. adjust down if we have a high ace and are over 21
		if ((aceSum + sum) > 21 && (aceSum > 10)) aceSum = aceSum - 10;
	
		// add calculated value of aces
		sum = sum + aceSum;
		score[0] = sum;
		if (aceSum > 10) score[1] = 1;
		else score[1] = 0;
	
		return score;
	}

	public static void displayHand(ArrayList<Card> hand, boolean isDealer) {
		for (int i = 0; i < hand.size(); i++) {
			if (i > 0 && isDealer) break;
			System.out.println("\t" + hand.get(i).toString());
		}
	}

	public static void displayHandForPlayer(BJPlayer player, int i) {

		// calculate score and score string
		String score = "";
		player.calculateHandScore(i);
		if (player.hasSoftAce(i)) score = "Soft ";
		score = score + player.getHandScore(i);
		if (score.equalsIgnoreCase("Soft 21")) score = "Blackjack!";

		//print player (and score if appropriate)
		System.out.print(playerHandHeader(player,i));
		if (!player.isDealer()) System.out.print(score);
		System.out.println();

		// display the hand
		if (player.isDealer() && player.hasBlackjack(i)) {
			BJController.displayHand(player.getHand(i), false);
		}
		else BJController.displayHand(player.getHand(i),player.isDealer());

		System.out.println();
	}

	public static void printFinalScoreForHand(BJPlayer player, int hand) {
		String scoreStr = player.getName() + "'s ";
		if (!player.isDealer()) scoreStr = scoreStr + "Hand " + (hand+1);
		scoreStr = scoreStr + " score: " + player.getHandScore(hand);
		System.out.println(scoreStr);
		
	}

	public static void dealCardToPlayer(Card card, BJPlayer player, int hand) {
		player.getHand(hand).add(card);
	}

	public static boolean processMoveFor(BJPlayer player, int hand, Deck deck) {
		Move move = parseInputFor(player, hand);
		if (move == Move.Quit) return false;
		return executeMove(player, hand, move, deck);
	}

	public static Move getRequiredMove(BJPlayer player, int hand) {
		Move previousMove = player.getLastMove(hand);
		player.calculateHandScore(hand);
		if (previousMove == Move.Stay) return null;
		if (player.isDealer()) {
			// if dealer, hit on < 17 as well as soft 17
			if ((player.getHandScore(hand) < 17) || 
					((player.getHandScore(hand) == 17) && (player.hasSoftAce(hand)))) {
				return Move.Hit;
			} else return Move.Stay;
		} else if (player.getHandScore(hand) >= 21) return Move.Stay;

		return null;
	}

	public static boolean isLegalMove(BJPlayer player, int hand, Move move) {
		switch(move) {
		case Quit:
		case Stay: 
			return true; 
		case Hit:
			return player.getHandScore(hand) < 21;
		case Double: 
			return player.canDoubleDown(hand);
		case Split: 
			return player.canSplit(hand);
		default: 
			return false;
		}
	}

	public static boolean executeMove(BJPlayer player, int hand, Move move, Deck deck) {
		if (move == null) return true;
		System.out.println("With score of " + player.getHandScore(hand) + ", " + 
				player.getName() + " chose to " + move.name());
		performMove(player, hand, move, deck);
		player.calculateHandScore(hand);
		Move nextMove = getRequiredMove(player, hand);
		return executeMove(player, hand, nextMove, deck);
	}

	public static boolean playerIsDoneWithHand(BJPlayer player, int hand) {
		return 
		player.didBust(hand) || 
		player.hasBlackjack(hand) ||
		player.getLastMove(hand).equals(Move.Stay) ||
		player.getLastMove(hand).equals(Move.Double);
	}
	
	private static Move parseInputFor(BJPlayer player, int hand) {
		Scanner stdIn = new Scanner(System.in);
		boolean isValid = false;
		String input = "";
		Move move = null;
		while (!isValid) {
			String prompt = player.getName() + ": ";
			for (Move m : Move.values()) {
				prompt = prompt + m.name() + " (" + (m.ordinal() + 1) + "), ";
			}
			prompt = prompt.substring(0, (prompt.length() - 2)) + "? ";
			System.out.print(prompt);
			input = null;
			try {
				input = stdIn.nextLine();
				if (input.length() == 0)  isValid = false;
				else {
					move = Move.convert(Integer.parseInt(input) - 1);
					isValid = isLegalMove(player, hand, move);
				}
			} catch (NumberFormatException ex) {
				String str = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
				try {
					move = Move.valueOf(str);
					isValid = isLegalMove(player, hand, move);
				} catch (Exception exception) {
					isValid = false;
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				move = null; input = null; isValid = false;
			} catch (Exception ex) {
				move = null; input = null; isValid = false;
			}

			if (!isValid) System.out.println("Invalid move, please try again");

		}
		return move;

	}

	// shouldn't have to handle quit
	// shouldn't have to verify that move can be done either
	private static boolean performMove(BJPlayer player, int hand, Move move, Deck deck) {
		switch (move) {
		case Stay:
			player.setLastMove(move, hand);
			player.calculateHandScore(hand);
			return true;
		case Double: 
			// must add code to increase wager
			// otherwise, same as hit
		case Hit:
			dealCardToPlayer(deck.drawCard(), player, hand);
			player.setLastMove(move, hand);
			player.calculateHandScore(hand);
			return true;
		case Split:
			int next = player.nextHand();
			player.splitCards(hand, next);
			player.setLastMove(move, hand);
			dealCardToPlayer(deck.drawCard(), player, hand);
			dealCardToPlayer(deck.drawCard(), player, next);
			player.calculateHandScore(hand);
			player.calculateHandScore(next);
			return true;
		}
		return false;

	}

	private static String playerHandHeader(BJPlayer player, int hand) {
		if (player.isDealer() && player.hasBlackjack(hand)) 
			return "Dealer has Blackjack!";
		if (player.isDealer()) 
			return player.getName() + " is showing: ";
		String header = player.getName() + "'s ";
		header = header + "Hand " + (hand + 1) + ": ";
		return header;
	}


}
