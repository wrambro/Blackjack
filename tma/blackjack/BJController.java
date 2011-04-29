package tma.blackjack;
import tma.deck.*;

import java.util.*;

/**
 * The BJController class contains the bulk of the business logic for the 
 * Blackjack game. All methods are static to avoid unnecessary instantiation of 
 * a BJController object. Used in several areas of Blackjack game, so consider
 * all significant changes carefully.
 * 
 * @author Tyler Ambroziak
 * @version 1.0
 */
public class BJController {

	//
	// Utility methods
	//
	
	/**
	 * Returns the value of a Card, as used in blackjack. Aces are returned as 11,
	 * face cards are returned as 10, and all other cards at face value.
	 * @param card Card to evaluate
	 * @return int value of card
	 */
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

	/**
	 * handValue takes a hand and calculates it's total value. It also determines
	 * whether or not a hand contains a soft ace.
	 * <br><br>
	 * If a hand has one or more Aces, the value of the Ace dynamically 
	 * determined to maximize the hand value without going over 21 (unless it 
	 * cannot be avoided).
	 * <br><br>
	 * The array returned by this method is structured as follows:
	 * <ul>
	 * <li>ScoreArray[0] = Hand Score</li>
	 * <li>ScoreArray[1] = Soft Ace flag (1 if hand contains soft ace)</li>
	 * </ul>
	 * @param hand Hand to evalue
	 * @return int array containing hand score and soft ace flag
	 */
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
		score[0] = sum;					// hand score
		if (aceSum > 10) score[1] = 1;	// soft ace flag
		else score[1] = 0;
	
		return score;
	}

	/**
	 * Deals a card to a player, for a specific hand
	 * @param card Card to deal to player
	 * @param player Player receiving the card
	 * @param hand Hand that the card should be added to
	 */
	public static void dealCardToPlayer(Card card, BJPlayer player, int hand) {
		player.getHand(hand).add(card);
	}

	
	/**
	 * Executes a given move for a hand and recalculates the new hand's score.
	 * Based on the new hand's score, if there is a required move, it also executes
	 * that. In this way, it terminates human player's hands when they must be,
	 * and recursively executes dealer's moves until dealer must stop..
	 * 
	 * @param player Player to execute move for
	 * @param hand Hand to execute move on
	 * @param move Move to execute on hand
	 * @param deck Desk to draw cards from
	 * @return True when done executing move (and subsequent required moves)
	 */
	public static boolean executeMove(BJPlayer player, int hand, Move move, Deck deck) {
		if (move == null) return true;
		performMove(player, hand, move, deck);
		player.calculateHandScore(hand);
		Move nextMove = getRequiredMove(player, hand);
		return executeMove(player, hand, nextMove, deck);
	}

	/**
	 * Determines if player is forced into a certain move for a given hand. For 
	 * instance, if the player had busted, the only move they can take is Stay. 
	 * This is also used to determine the dealer's move. Default logic is that 
	 * dealer hits on soft 17.
	 * 
	 * @param player Player to evaluate
	 * @param hand Hand to evaluate for required move
	 * @return Move, if required Move exists for Hand/Player
	 */
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

	/**
	 * Determine if a given move can be performed by player on a given hand
	 * 
	 * @param player Player to evaluate
	 * @param hand Hand to evaluate
	 * @param move Move to evaluate legality of
	 * @return True if move is legal given the hand, False if it is not
	 */
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

	/**
	 * Determines if player can no longer play a given hand. This is true if they:
	 * <ul>
	 * <li>Has Blackjack (don't allow hit on blackjack)</li>
	 * <li>Busted (score > 21)</li>
	 * <li>Chose either to Stay or Double Down</li>
	 * </ul>
	 * @param player Player to evaluate
	 * @param hand Hand to evaluate
	 * @return True if player cannot make a subsequent move on the specified
	 * hand; otherwise, False
	 */
	public static boolean playerIsDoneWithHand(BJPlayer player, int hand) {
		return 
		player.didBust(hand) || 
		player.hasBlackjack(hand) ||
		player.getLastMove(hand).equals(Move.Stay) ||
		player.getLastMove(hand).equals(Move.Double);
	}

	/**
	 * displayHand is used by the CLI game to display a given hand, one card per
	 * line, using the card's description.
	 * @param hand Hand to display
	 * @param isDealer Flag indicating that the passed-in hand is the dealer's 
	 * hand. In that case, we only want to display one card.
	 */
	public static void displayHand(ArrayList<Card> hand, boolean isDealer) {
		for (int i = 0; i < hand.size(); i++) {
			if (i > 0 && isDealer) break;
			System.out.println("\t" + hand.get(i).toString());
		}
	}

	/**
	 * Wrapper for CLI hand display. This is what should be called from CLI to
	 * print a player's hand
	 * @param player Player
	 * @param i int indicating hand to print
	 */
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

	/**
	 * Print's final score for a player's hand. Used by CLI.
	 * 
	 * @param player Player
	 * @param hand int indicating which of the player's hand scores to print
	 */
	public static void printFinalScoreForHand(BJPlayer player, int hand) {
		String scoreStr = player.getName() + "'s ";
		if (!player.isDealer()) scoreStr = scoreStr + "Hand " + (hand+1);
		scoreStr = scoreStr + " score: " + player.getHandScore(hand);
		System.out.println(scoreStr);
		
	}

	/**
	 * Parses CLI input, determines move to execute, and executes move
	 * @param player Player making the move
	 * @param hand Hand that move applies to
	 * @param deck Deck to pull card from (if hit)
	 * @return true if move was executed successfully, false otherwise
	 */
	public static boolean processMoveFor(BJPlayer player, int hand, Deck deck) {
		Move move = parseInputFor(player, hand);
		if (move == Move.Quit) return false;
		return executeMove(player, hand, move, deck);
	}

	
	//
	// Private helper methods
	//
	
	
	/**
	 * Used to gather and validate player's move selection
	 * 
	 * @param player Player to select move for
	 * @param hand Hand that move should apply to
	 * @return Move 
	 */
	private static Move parseInputFor(BJPlayer player, int hand) {
		Scanner stdIn = new Scanner(System.in);
		boolean isValid = false;
		String input = "";
		Move move = null;
		while (!isValid) {
			
			// construct prompt
			String prompt = player.getName() + ": ";
			for (Move m : Move.values()) {
				prompt = prompt + m.name() + " (" + (m.ordinal() + 1) + "), ";
			}
			prompt = prompt.substring(0, (prompt.length() - 2)) + "? ";
			System.out.print(prompt);
			input = null;
			
			//gather input
			try {
				input = stdIn.nextLine();
				if (input.length() == 0)  isValid = false;
				else {
					move = Move.convert(Integer.parseInt(input) - 1);
					isValid = isLegalMove(player, hand, move);
				}
			// if input is not a number, try matching it to Move by String value
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

	/**
	 * Performs actions for a given move. Assumes Move has been validated, and
	 * is a legal move.
	 * 
	 * @param player Player to perform move for
	 * @param hand Hand to perform hand on
	 * @param move Move to perform
	 * @param deck Deck to draw cards from
	 * @return true if move successful, false otherwise
	 */
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

	/**
	 * Print header for a given hand. Used by CLI. Header can be any of the
	 * following:
	 * <ul>
	 * <li>"Dealer has Blackjack!"</li>
	 * <li>"Dealer is showing:"</li>
	 * <li>"[Player]'s Hand X:"</li>
	 * </ul>
	 * @param player Player to print hand info for
	 * @param hand Hand to print info for
	 * @return Header string
	 */
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
