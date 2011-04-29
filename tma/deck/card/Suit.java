package tma.deck.card;

/**
 * Suit class is an enum for all the card suits. The only other method
 * is convert(), which returns the Suit with the passed-in enum value
 * 
 * @author Tyler Ambroziak
 * @version 1.0
 * @since 4/27/2011
 *
 */
public enum Suit {
	Clubs, Diamonds, Hearts, Spades;
	
	/**
	 * Returns a Suit value based on it's enum value
	 * @param suit enum value of Suit
	 * @return Suit value
	 */
	public static Suit convert(int suit) {
		return Suit.class.getEnumConstants()[suit];
	}
}

