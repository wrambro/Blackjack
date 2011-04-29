package tma.blackjack;
import tma.deck.*;
import tma.deck.card.*;

/**
 * The Blackjack class serves as the entrypoint for the CLI version of this game.
 * 
 * @author Tyler Ambroziak
 * @version 1.0
 */
public class Blackjack {
	public static void main(String[] args) {
		// create players, deck
		int numPlayers = 2; 
		boolean ret = true;
		BJPlayer[] player = new BJPlayer[2];
		player[0] = new BJPlayer("Tyler", false);
		player[1] = new BJPlayer("Dealer", true);
		Deck deck = new Deck(6,CardContext.Blackjack);
		final int DEALER = numPlayers - 1;
		
		
		/* to do: support adding/defining players */
		while (ret) {
			// first round of dealing.. deal a card to each player, burn one, & repeat
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < numPlayers; j++) {
					BJController.dealCardToPlayer(deck.drawCard(), player[i], 0);
				}
				deck.burnCard(deck.drawCard());
			}
			// calculate dealer's score first. player hand scores get calculate
			// before each display of the hand
			player[DEALER].calculateHandScore(0);
			// play a hand
			for (int i = 0; i < numPlayers && ret && !player[DEALER].hasBlackjack(0); i++) {
				if (!player[i].isDealer()) {
					for (int j = 0; j < player[i].numActiveHands() && ret; j++) {
						player[i].calculateHandScore(j);
						while (ret && !BJController.playerIsDoneWithHand(player[i], j)) {
							BJController.displayHandForPlayer(player[i], j);
							BJController.displayHandForPlayer(player[DEALER], 0);
							if (!BJController.playerIsDoneWithHand(player[i], j)) {
								ret = BJController.processMoveFor(player[i], j, deck);
							}
						}
					}
				}
				else {
					BJController.executeMove(player[i], 0, BJController.getRequiredMove(player[i], 0), deck);
				}
			}
			
			System.out.println();
			//print final scores and winner
			for (int i = 0; i < numPlayers; i++) {
				for (int j = 0; j < player[i].numActiveHands(); j++) {
					BJController.printFinalScoreForHand(player[i], j);
				}
			}
			
			// burn all hands 
			for (int i = 0; i < numPlayers; i++) {
				deck.burnHand(player[i].burnCards());
			}
			
			if (deck.activeCount() < (numPlayers * 5)) {
				deck.suffleDeck();
				System.out.println("Shuffling deck...");
			}
			System.out.println("-----------");
		}
	}
}
