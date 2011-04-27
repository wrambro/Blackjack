package tma.deck.card;

/**
 * CardContext class is an enum for possible card games. Currently only contains
 * Blackjack, but other values are welcome. When adding a value here, make sure
 * to add appropriate logic in the Card class' compareTo() method.
 * @author Tyler Ambroziak
 *
 */
public enum CardContext {
	Blackjack;
	
	/**
	 * Returns a CardContext value based on it's enum value
	 * @param context enum value of CardContext
	 * @return CardContext value
	 */
	public static CardContext convert(int context) {
		return CardContext.class.getEnumConstants()[context];
	}
}
