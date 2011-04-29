package tma.deck;

import tma.blackjack.BJController;
import tma.deck.card.*;

/**
 * Card class represents a playing card. Stores info about the rank, suit, and
 * what game/context that card is being used in
 * 
 * @author Tyler Ambroziak
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
public class Card implements Comparable {
	private Suit suit;
	private Rank rank;
	private CardContext context;
	
	/**
	 * Constructs a card, setting the rank, suit, and context (game)
	 * @param suit
	 * @param rank
	 * @param context
	 */
	public Card(Suit suit, Rank rank, CardContext context) {
		this.suit = suit;
		this.rank = rank;
		this.context = context;
	}
	
	/**
	 * Gets card's Suit value
	 * @return Suit
	 */
	public Suit getSuit() {
		return this.suit;
	}
	
	/**
	 * Get card's Rank value
	 * @return Rank
	 */
	public Rank getRank() {
		return this.rank;
	}
	
	/**
	 * Returns name of suit
	 * @return Suit name
	 */
	public String getDisplaySuit() {
		return suit.name();
	}
	
	/**
	 * Returns name of rank
	 * @return Rank display name
	 */
	public String getDisplayRank() {
		if ((rank.ordinal() < 2) || (rank.ordinal() > 10)) return rank.name();
		else return Integer.toString(rank.ordinal());
	}

	/**
	 * Determine if two cards are equal. For to cards to be equal, they must
	 * match both in suit and in value	
	 * @param card Card to check for match
	 * @return True if cards are equal, false otherwise
	 */
	public boolean equals(Card card) {
		return this.suit == card.getSuit() && this.rank == card.getRank();
	}
	
	/**
	 * Returns display name of card (i.e. 10 of Clubs, Jack of Diamonds)
	 * @return Description of card
	 */
	public String toString() {
		return getDisplayRank() + " of " + getDisplaySuit();
	}
	
	/**
	 * Comparable implementation. Context dependent. For blackjack, determines
	 * card order based on the cardValue method in the BJController class. Other
	 * contexts are yet to be defined
	 * @param o Object to compare this to
	 * @return Difference between two card values
	 */
	public int compareTo(Object o) {
		if (o.getClass() != this.getClass()) return -1;
		
		Card card = (Card) o;
		
		if (context == CardContext.Blackjack) {
			return BJController.cardValue(this) - BJController.cardValue(card);
		}
		
		return -1;
		
	}
}
