package com.amica.games;

import org.springframework.beans.factory.annotation.Autowired;

/**
* The game has not much logic of its own, since the {@link Board} evaluates
* wins and ties and is the game over or not. We're really just making the
* trains run: alternate between two players and have them move, until
* we hear from the board itself that the game is over. 
* 
* @author Will Provost
*/
public class Game {

	@Autowired
	private Board board;
	
	private Player[] players = { null, null };

	/**
	 * We don't try to auto-wire the players, because there will be 
	 * multiple implementations, and we won't want the same bean to
	 * act as both player 1 and player 2. So, old-school, manual injection
	 * it is! by way of this setter method.
	 */
	public void setPlayer1(Player player) {
		players[0] = player;
	}
	
	/**
	 * We don't try to auto-wire the players, because there will be 
	 * multiple implementations, and we won't want the same bean to
	 * act as both player 1 and player 2. So, old-school, manual injection
	 * it is! by way of this setter method.
	 */
	public void setPlayer2(Player player) {
		players[1] = player;
	}

	/**
	 * Run the game: alternate player moves until it's over.
	 */
	public void run() {
		if (players[0] == null || players[1] == null) {
			throw new IllegalStateException
				("Can't start the game without both players.");
		}
		
		int current = 0;
		while(!board.isGameOver()) {
			players[current].move();
			current = 1 - current;
		}
	}
}
