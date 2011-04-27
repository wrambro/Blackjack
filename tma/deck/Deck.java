package tma.deck;

import java.util.*;
import tma.deck.card.*;

/**
 * Deck class is a collection of Cards. Can be instantiated with X number of decks
 * (assuming 52 cards in a deck). Tracks active cards, burned cards, and the 
 * CardContext of the deck. Also contains basic logic for shuffling, drawing,
 * and burning cards.
 * @author Tyler Ambroziak
 *
 */
public class Deck {
	private final int DECK_SIZE = 52;
	private ArrayList<Card> burnPile;
	private Queue<Card> activePile;
	private int packCount;
	private CardContext context;
	
	/**
	 * Default construction. 1 deck, 52 cards, blackjack context
	 */
	public Deck() {
		packCount = 1;
		burnPile = new ArrayList<Card>();
		activePile = new LinkedList<Card>();
		context = CardContext.Blackjack;
		initDeck(packCount, context);
		suffleDeck();
	}
	
	/**
	 * First add-on to default. This constructor allows you to specifiy X
	 * number of decks for this Deck. Meaning this Deck will contain X * 52 cards.
	 * @param cnt # of decks for the Deck
	 */
	public Deck(int cnt) {
		if (cnt < 1) packCount = 1;
		else packCount = cnt;
		burnPile = new ArrayList<Card>();
		activePile = new LinkedList<Card>();
		context = CardContext.Blackjack;
		initDeck(packCount, context);
		suffleDeck();
	}
	
	/**
	 * 3rd constructor. Allows you to specify a deck cound as well as a CardContext
	 * for the deck. Allows deck to be used for games other than blackjack.
	 * @param cnt # of decks for the Deck
	 * @param cont CardContext for deck/cards
	 */
	public Deck(int cnt, CardContext cont) {
		if (cnt < 1) packCount = 1;
		else packCount = cnt;
		burnPile = new ArrayList<Card>();
		activePile = new LinkedList<Card>();
		context = cont;
		initDeck(packCount, context);
		suffleDeck();
	}
	
	/**
	 * How many cards are available to play before shuffling
	 * @return # of cards in active deck
	 */
	public int activeCount() {
		return activePile.size();
	}
	
	/**
	 * How many cards have been played and are inactive until the deck is shuffled
	 * @return # of cards in burn pile
	 */
	public int burnCount() {
		return burnPile.size();
	}
	
	/**
	 * Draws a card from the deck
	 * @return Card from active deck
	 */
	public Card drawCard() {
		return activePile.poll();
	}
	
	/**
	 * Discard a card, add it to the burn pile
	 * @param card Card to burn
	 * @return if card was successfully added to burn pile
	 */
	public boolean burnCard(Card card) {
		return burnPile.add(card);
	}

	/**
	 * Discard an entire hand to the burn pile. Returns empty hand
	 * @param hand Hand to burn
	 * @return Empty hand
	 */
	public ArrayList<Card> burnHand(ArrayList<Card> hand) {
		for (Card card : hand) burnCard(card);
		hand.clear();
		return hand;
	}
	
	/**
	 * Shuffles entire deck (active & burn piles)
	 * Merges all cards into burn pile, shuffles that, and then reassigns it
	 * to the active pile
	 */
	public void suffleDeck() {
		while (!activePile.isEmpty()) {
			burnPile.add(activePile.remove());
		}
		
		activePile = shuffle(burnPile);
		burnPile.clear();
		
	}
	
	/**
	 * Shuffle only cards that are in the active pile. Burn pile is unaffected.
	 */
	public void shuffleActiveOnly() {
		ArrayList<Card> tempDeck = new ArrayList<Card>();
		
		// move all active cards to temp arraylist
		while (!activePile.isEmpty()) {
			tempDeck.add(activePile.poll());
		}
		
		activePile = shuffle(tempDeck);
		
	}
	
	/**
	 * Check if there are still cards in the active pile
	 * @return True if there are cards still in the active pile, false otherwise.
	 */
	public boolean isEmpty() {
		return activePile.isEmpty();
	}
	
	/**
	 * The real meat behind the shuffler. Randomly pulls a card from the burn pile
	 * and queues it up in the active deck
	 * @param cards Cards to shuffle (ArrayList)
	 * @return Shuffled pile (Queue)
	 */
	private Queue<Card> shuffle(ArrayList<Card> cards) {
		int index;
		Random rand = new Random();
		Queue<Card> deck = new LinkedList<Card>();
		
		
		while (!cards.isEmpty()) {
			index = (int) ( rand.nextDouble() * cards.size() );
			deck.add(cards.get(index));
			cards.remove(index);
		}
		
		return deck;
	}

	/**
	 * Creates all cards for Deck, based on number of decks specified in constructor
	 * @param cnt # of decks in Deck
	 * @param context CardContext of Deck/Cards
	 */
	private void initDeck(int cnt, CardContext context) {
		ArrayList<Card> deck = new ArrayList<Card>();
		int numCards = cnt * DECK_SIZE;
		int suit,rank;
		
		for (int i = 0; i < numCards; i++) {
			rank = (i % 13) + 1;
			suit = (i % 52) / 13;
			deck.add(new Card(Suit.convert(suit), Rank.convert(rank), context));
		}

		burnPile = deck;
	}


}
