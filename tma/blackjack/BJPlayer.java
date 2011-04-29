package tma.blackjack;
import java.util.*;
import tma.deck.*;

/**
 * BJPlayer class contains infastructure that describes a Blackjack player, that
 * player's hands, their contents, the player's winnings, and that player's most
 * recent move. 
 * 
 * @author Tyler Ambroziak
 * @version 1.0
 *
 */
public class BJPlayer {
	private String name;
	private boolean isDealer;
	private int winnings;
	private ArrayList<ArrayList<Card>> hands;
	private ArrayList<Integer> handScore;
	private ArrayList<Integer> handWager;
	private ArrayList<Boolean> softAce;
	private ArrayList<Move> lastMove;
	
	/**
	 * Creates a blackjack player with a name and dealer flag (if player is the
	 * dealer)
	 * @param playerName Name of this player
	 * @param dealerFlag True if player is the dealer, false otherwise
	 */
	public BJPlayer(String playerName, boolean dealerFlag) {
		name = playerName;
		winnings = 500;
		isDealer = dealerFlag;
		hands = new ArrayList<ArrayList<Card>>();
		handScore = new ArrayList<Integer>();
		handWager = new ArrayList<Integer>();
		softAce = new ArrayList<Boolean>();
		lastMove = new ArrayList<Move>();
		reset();
	}
	
	/**
	 * Can this player split a given hand? Yes, if it contains exactly 2 cards
	 * and they have the same value. 
	 * @param hand Position of hand to evaluate
	 * @return True if player can split hand; otherwise, false
	 */
	public boolean canSplit(int hand) {
		if (isDealer) return false;
		return (getHand(hand).size() == 2) &&
		(getHand(hand).get(0).compareTo(getHand(hand).get(1)) == 0);
		
	}
	
	/**
	 * Can this player double down on a given hand? Yes if the player has exactly
	 * two cards
	 * @param hand Position of hand to evaluate
	 * @return True if player can double down; otherwise, false
	 */
	public boolean canDoubleDown(int hand) {
		if (isDealer) return false;
		if (hasBlackjack(hand)) return false;
		return (getHand(hand).size() == 2);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Card> burnCards() {
		ArrayList<Card> burnCards = new ArrayList<Card>();

		for (ArrayList<Card> hand: hands) {
			burnCards.addAll(hand);
			hand.clear();
		}
		reset();
		
		return burnCards;
	}
	
	/**
	 * Returns a hand at a given index
	 * @param hand Position of hand to return
	 * @return Hand at given position
	 */
	public ArrayList<Card> getHand(int hand) {
		return hands.get(hand);
	}
	
	/**
	 * Does the hand contain a soft ace? 
	 * @param hand Position of hand to return
	 * @return Soft ace flag for given hand
	 */
	public boolean hasSoftAce(int hand) {
		return softAce.get(hand);
	}
	
	/**
	 * Returns the score for a given hand
	 * @param hand Position of hand to evaluate
	 * @return Score of hand at give position
	 */
	public int getHandScore(int hand) {
		return handScore.get(hand);
	}
	
	/**
	 * Player's winnings. Not yet in use.
	 * @return player's winnings
	 */
	public int getWinnings() {
		return winnings;
	}
	
	/**
	 * Change winnings by passed in amount. Not yet in use.
	 * @param amount Amount to change winnings by
	 */
	public void addWinnings(int amount) {
		winnings = winnings + amount;
	}
	
	/**
	 * Get player's name
	 * @return Player's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Calculate hand score and soft ace flag for a given hand
	 * @param hand Position of hand to score
	 */
	public void calculateHandScore(int hand) {
		int [] score = null;
		score = BJController.handValue(getHand(hand));
		
		handScore.set(hand, score[0]);
		softAce.set(hand, (score[1] == 1));
	}
	
	/**
	 * How many hands does a player currently have active?
	 * @return Number of active hands
	 */
	public int numActiveHands() {
		return hands.size();
	}
	
	/**
	 * Get dealer flag value for a player. Is player the dealer?
	 * @return
	 */
	public boolean isDealer() {
		return isDealer;
	}
	
	/**
	 * Split a hand. Checks if hand can be split first.
	 * 
	 * @param from Position of source hand.
	 * @param to Position of target hand
	 * @return true if hand successfully split; false otherwise
	 */
	public boolean splitCards(int from, int to) {
		if (!canSplit(from)) return false;
		if (getHand(to).size() != 0) return false;
		// do actual split
		return getHand(to).add(getHand(from).remove(1));
	}
	
	/**
	 * What was the last move a player made for a given hand?
	 * @param hand Position of hand
	 * @return Last move performed for that hand
	 */
	public Move getLastMove(int hand) {
		return lastMove.get(hand);
	}
	
	/**
	 * Set the last move performed on a given hand
	 * @param move Move that was performed on hand.
	 * @param hand Hand that move was performed on.
	 */
	public void setLastMove(Move move, int hand) {
		lastMove.set(hand, move);
	}
	
	/**
	 * Does player have blackjack? Only if they hit 21 with just 2 cards.
	 * @param hand Position of hand to evaluate
	 * @return true if player has blackjack; false otherwise
	 */
	public boolean hasBlackjack(int hand) {
		return (handScore.get(hand) == 21) && (getHand(hand).size() == 2);
	}

	/**
	 * Did player bust? Yes, if their score is over 21.
	 * @param hand Hand to evaluate
	 * @return true if hand score > 21; false otherwise
	 */
	public boolean didBust(int hand) {
		boolean didBust = (handScore.get(hand) > 21);
		if (!didBust) return didBust;
		String str = name + " busted on hand ";
		if (!isDealer) str = str + (hand+1) + " ";
		str = str + "with a score of " + handScore.get(hand);
		if (didBust) System.out.println(str);
		return didBust;
	}
	
	/**
	 * Initialize a new hand
	 * 
	 * @return Index of new hand in hands ArrayList
	 */
	public int nextHand() {
		hands.add(new ArrayList<Card>());
		handScore.add(0);
		handWager.add(50);
		softAce.add(false);
		lastMove.add(Move.Hit);
		return hands.size() - 1;
	}
	
	/**
	 * Reset game after a round of blackjack. Clears soft ace flags, hand 
	 * scores, wager amount, last moves, and hands
	 */
	private void reset() {
		softAce.clear();
		softAce.add(false);
		handScore.clear();
		handScore.add(0);
		handWager.clear();
		handWager.add(5);
		lastMove.clear();
		lastMove.add(Move.Hit);
		hands.clear();
		hands.add(new ArrayList<Card>());
	}
}
