import java.util.ArrayList;

import tma.blackjack.BJController;
import tma.deck.*;
import tma.deck.card.CardContext;


/**
 * The purpose of DeckTest is to test all the methods contained within the Card,
 * Deck, BJController, and BJPlayer classes before implementing a blackjack game.
 * There are several possible test modes:
 * 		1-Test dealing, burning, printing card names
 * 		2-Test scoring of random hands ranging in size from 2-5 cards
 * 		3-Test BJPlayer
 * @author Tyler Ambroziak
 *
 */
public class DeckTest {

	private static final int DEFAULT_MODE = 2;
	private static CardContext context = CardContext.Blackjack;
	private static Deck deck = new Deck(6,context);
	private static Card card = null;
	private static ArrayList<Card> activeCards = new ArrayList<Card>();

	public static void main(String[] args) {
		int testMode;
		if (args == null) testMode = DEFAULT_MODE;
		else if (args.length == 0) testMode = DEFAULT_MODE;
		else testMode = Integer.parseInt(args[0]);
		switch (testMode) {
		case 1: testDealing(); break;
		case 2: testScoring(); break;
		}
		return;
	}

	private static void testDealing() {
		//test drawing, burning, and card count
		System.out.println("Active cards: " + deck.activeCount());
		System.out.println("Burned cards: " + deck.burnCount());
		card = deck.drawCard();
		System.out.println("\nDealing card: " + card.toString());
		System.out.println("Active cards: " + deck.activeCount());
		System.out.println("Burned cards: " + deck.burnCount());
		System.out.println("\nBurn card: " + card.toString());
		deck.burnCard(card);
		System.out.println("Active cards: " + deck.activeCount());
		System.out.println("Burned cards: " + deck.burnCount());

		System.out.println("\n\n");

		// deal all cards.. verify shuffling, all cards display properly
		while (!deck.isEmpty()) {
			card = deck.drawCard();
			System.out.println(card.toString());
			activeCards.add(card);

			// just for good measure, let's shuffle active every 26 cards

			if (deck.activeCount() % 52 == 26) {

				System.out.println("Shuffling active..");
				deck.shuffleActiveOnly();
			}

		}

		System.out.println("\n\n");

		// check current card counts before burning all cards
		System.out.println("Active cards: " + deck.activeCount());
		System.out.println("Burned cards: " + deck.burnCount());

		// test what happens when you try to draw from an empty deck
		card = deck.drawCard();
		if (card == null) System.out.println("No cards left in deck");
		else System.out.println(card.toString());

		// burn all cards
		while (!activeCards.isEmpty()) {
			deck.burnCard(activeCards.remove(0));
		}

		System.out.println("\n\n");

		// check current card counts after burning all cards
		System.out.println("Active cards: " + deck.activeCount());
		System.out.println("Burned cards: " + deck.burnCount());
	}


	private static void testScoring() {
		ArrayList<Card> hand = new ArrayList<Card>();

		int iteration = 0;
		int handSize = 2;
		while (!deck.isEmpty()) {

			// determine number of cards that will be in this test hand
			switch (iteration % 4) {
			case 0: handSize = 2; break;
			case 1: handSize = 3; break;
			case 2: handSize = 4; break;
			case 3: handSize = 5; break;
			}
			iteration++;

			if (handSize > deck.activeCount()) handSize = deck.activeCount();
			// deal hand
			for (int i = 0; i < handSize; i++) {
				hand.add(deck.drawCard());
			}
			System.out.println("Hand #" + iteration);
			BJController.displayHand(hand,false);
			System.out.println("  Hand Value: " + 
					BJController.handValue(hand));
			System.out.println("-------------");

			//burn hand
			deck.burnHand(hand);

			hand.clear();

		}
	}
}