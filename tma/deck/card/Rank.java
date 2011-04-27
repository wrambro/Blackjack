package tma.deck.card;

/**
 * Rank class is an enum for all card ranks. The only other method
 * is convert(), which returns the Rank with the passed-in enum value
 * @author Tyler Ambroziak
 *
 */
public enum Rank {
	Joker, Ace, Two, Three, Four, Five, Six,
	Seven, Eight, Nine, Ten, Jack, Queen, King;
	
	/**
	 * Returns a Rank value based on it's enum value
	 * @param rank enum value of Rank
	 * @return Rank value
	 */
	public static Rank convert(int rank) {
		return Rank.class.getEnumConstants()[rank];
	}
}
