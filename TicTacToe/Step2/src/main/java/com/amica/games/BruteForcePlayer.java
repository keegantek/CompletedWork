package com.amica.games;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

/**
* Player that relies on a pre-generated dictionary of best moves for any
* game position. Extends {@link RandomPlayer} as a fallback in case the
* moves dictionary is not found at runtime.
* 
* @author Will Provost
*/
public class BruteForcePlayer extends RandomPlayer {

	private Properties bestMoves = new Properties();
	
	/**
	 * Call the superclass constructor.
	 * Load the dictionary from a properties file found on the class path.
	 */
	public BruteForcePlayer(Board.Square sideToPlay) {
		super(sideToPlay);

		try {
			bestMoves.load(new ClassPathResource("bestMoves.properties")
					.getInputStream());
		} catch (IOException ex) {
			System.out.println("Can't load best-moves dictionary; will play randomly.");
			bestMoves = null;
		}
	}
	
	/**
	 * Translate the board representation to match the format of the
	 * moves dictionary, e.g. "--XOX---O", and look up the best move.
	 * Translate the value into row and column indices, and make the move.
	 */
	@Override
	public void move() {
		if (bestMoves != null) {
			String currentState = getBoard().toString()
					.replace(" ", "-").replace("\n", "");
			String[] bestMove = bestMoves.getProperty(currentState).split(",");
			int row = Integer.parseInt(bestMove[0]);
			int col = Integer.parseInt(bestMove[1]);
			getBoard().move(row, col, getSideToPlay());
		} else {
			super.move();
		}
	}

}
