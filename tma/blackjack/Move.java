package tma.blackjack;

/**
 * Move class is an enum for all the legal blackjack moves. The only other method
 * is convert(), which returns the Move with the passed-in enum value
 * @author Tyler Ambroziak
 * @version 1.0
 *
 */
public enum Move {
	Hit, Stay, Double, Split, Quit;

	/**
	 * Returns a Move value based on it's enum value
	 * @param move enum value of Move
	 * @return Move value
	 */
	public static Move convert(int move) {
		return Move.class.getEnumConstants()[move];
	}
	
}
