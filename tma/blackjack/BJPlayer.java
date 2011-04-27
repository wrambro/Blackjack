package tma.blackjack;
import java.util.*;
import tma.deck.*;

public class BJPlayer {
	private String name;
	private boolean isDealer;
	private int winnings;
	private ArrayList<ArrayList<Card>> hands;
	private ArrayList<Integer> handScore;
	private ArrayList<Integer> handWager;
	private ArrayList<Boolean> softAce;
	private ArrayList<Move> lastMove;
	
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
	
	public boolean canSplit(int hand) {
		if (isDealer) return false;
		return (getHand(hand).size() == 2) &&
		(getHand(hand).get(0).compareTo(getHand(hand).get(1)) == 0);
		
	}
	
	public boolean canDoubleDown(int hand) {
		if (isDealer) return false;
		if (hasBlackjack(hand)) return false;
		return (getHand(hand).size() == 2);
	}
	
	public ArrayList<Card> burnCards() {
		ArrayList<Card> burnCards = new ArrayList<Card>();

		for (ArrayList<Card> hand: hands) {
			burnCards.addAll(hand);
			hand.clear();
		}
		reset();
		
		return burnCards;
	}
	
	public ArrayList<Card> getHand(int hand) {
		return hands.get(hand);
	}
	
	public boolean hasSoftAce(int hand) {
		return softAce.get(hand);
	}
	
	public int getHandScore(int hand) {
		return handScore.get(hand);
	}
	
	public int getWinnings() {
		return winnings;
	}
	
	public void addWinnings(int amount) {
		winnings = winnings + amount;
	}
	
	public String getName() {
		return name;
	}
	
	
	public void calculateHandScore(int hand) {
		int [] score = null;
		score = BJController.handValue(getHand(hand));
		
		handScore.set(hand, score[0]);
		softAce.set(hand, (score[1] == 1));
	}
	
	public int numActiveHands() {
		return hands.size();
	}
		
	public boolean isDealer() {
		return isDealer;
	}
	
	public boolean splitCards(int from, int to) {
		if (!canSplit(from)) return false;
		if (getHand(to).size() != 0) return false;
		// do actual split
		return getHand(to).add(getHand(from).remove(1));
	}
	
	public Move getLastMove(int hand) {
		return lastMove.get(hand);
	}
	
	public void setLastMove(Move move, int hand) {
		lastMove.set(hand, move);
	}
	
	public boolean hasBlackjack(int hand) {
		return (handScore.get(hand) == 21) && (getHand(hand).size() == 2);
	}

	public boolean didBust(int hand) {
		boolean didBust = (handScore.get(hand) > 21);
		if (!didBust) return didBust;
		String str = name + " busted on hand ";
		if (!isDealer) str = str + (hand+1) + " ";
		str = str + "with a score of " + handScore.get(hand);
		if (didBust) System.out.println(str);
		return didBust;
	}
	
	public int nextHand() {
		hands.add(new ArrayList<Card>());
		handScore.add(0);
		handWager.add(50);
		softAce.add(false);
		lastMove.add(Move.Hit);
		return hands.size() - 1;
	}
	
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
