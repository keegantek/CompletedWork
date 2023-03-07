package com.amica.games;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* Encapsulates the state of the game board, along with logic to determine
* the game outcome: is it over? If so, who won? Or is it a tie game?
*
* @author Will Provost
*/
@Component
public class Board {

	/**
	 * Three-state enumeration that can be used to represent the
	 * current state of one square on the board, and also the outcome
	 * of  a game as either X or O winning or EMPTY to indicate a tie.
	 */
	public enum Square {
		X, O, EMPTY;

		@Override
		public String toString() {
			return this == X ? "X" : (this == O ? "O" : " ");
		}
	};

	public static final int SIZE = 3;

  @Autowired(required=false)
  private List<GameListener> listeners = new ArrayList<>();

	private Square[][] squares;

	private int count = 0;
	private Square outcome = null;

	/**
	 * Fill the board with the EMPTY state for each square.
	 */
	public Board() {
		squares = new Square[SIZE][];
		for (int row = 0; row < SIZE; ++row) {
			squares[row] = new Square[SIZE];
			for (int col = 0; col < SIZE; ++col) {
				squares[row][col] = Square.EMPTY;
			}
		}
	}

	/**
	 * Report the current state of the given square.
	 */
	public Square getSquare(int row, int col) {
		return squares[row][col];
	}

	/**
	 * Check the legality of the attempted move: you can't play EMPTY,
	 * and you can't play on an occupied square. (We don't check for the
	 * correct alternation of X and O moves.) pdate the board state and
	 * move count. Report the new move to any and all listeners.
	 */
	public void move(int row, int col, Square XorO) {
		if (XorO == Square.EMPTY) {
			throw new IllegalArgumentException("Must play X or O.");
		}
		if (squares[row][col] != Square.EMPTY) {
			throw new IllegalStateException(String.format
					("Row %d, column %d is occupied.", row, col));
		}

		squares[row][col] = XorO;
		++count;

		for (GameListener listener : listeners) {
			listener.moveMade(row,  col,  XorO);
		}
	}

	/**
	 * Helper method to check any series of squares to see if they are
	 * all occupied by the same player's pieces. If so, that player is
	 * marked as the winner.
	 */
	private void winIfYouMadeARow(Square... squares) {
		if (outcome == null && Stream.of(squares).distinct().count() == 1) {
			Square occupant = Stream.of(squares).distinct().findAny().get();
			if (occupant != Square.EMPTY) {
				outcome = occupant;
			}
		}
	}

	/**
	 * Check for all either possible wins -- three rows, three columns,
	 * and two diagonals. If there is no win, check the move count, and
	 * if there have been nine legal moves, the game ends in a tie.
	 */
	public Square getOutcome() {

		// Don't decide this over and over; can't "un-win" a game
		if (outcome == null) {
			for (int row = 0; row < SIZE; ++row) {
				winIfYouMadeARow(squares[row]);
			}

			for (int col = 0; col < SIZE; ++col) {
				winIfYouMadeARow(squares[0][col], squares[1][col], squares[2][col]);
			}

			winIfYouMadeARow(squares[0][0], squares[1][1], squares[2][2]);
			winIfYouMadeARow(squares[0][2], squares[1][1], squares[2][0]);

			if (outcome == null && count >= SIZE * SIZE) {
				outcome = Square.EMPTY;
			}
		}

		return outcome;
	}

	/**
	 * Provide a user-friendly string representing the game state.
	 */
	public String describeOutcome() {
		if (outcome != null) {
			if (outcome == Square.EMPTY) {
				return "Tie game.";
			} else {
				return "" + outcome + " wins.";
			}
		} else {
			return "Game not over yet.";
		}
	}

	/**
	 * Returns true for any win or tie game, false if still playing.
	 */
	public boolean isGameOver() {
		getOutcome();
		return outcome != null;
	}

	/**
	 * Produces a user-friendly depiction of the game board.
	 */
	@Override
	public String toString() {
		return Stream.of(squares)
				.map(row -> Stream.of(row).map(Square::toString).collect(Collectors.joining("")))
				.collect(Collectors.joining("\n"));
	}
}
