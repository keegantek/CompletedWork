package com.amica.games;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
* Unit test for the {@link Board} class. Note that it's easy enough
* to test this class without using Spring at all -- though if we 
* wanted to verify the broadcasts to listeners, we would need to
* put a test configuration together, as we do in {@link GameTest}.
* 
* @author Will Provost
*/
public class BoardTest {

	/**
	 * We can use this pre-set game board for a few test cases.
	 */
	public static Board setUpXWins() {
		Board board = new Board();
		board.move(0, 0, Board.Square.X);
		board.move(0, 1, Board.Square.O);
		board.move(1, 1, Board.Square.X);
		board.move(1, 2, Board.Square.O);
		board.move(2, 2, Board.Square.X);
		return board;
	}

	/**
	 * Test that the board correctly reports the win for X.
	 */
	@Test
	public void testGetOutcomeXWins() {
		Board board = setUpXWins();
		assertThat(board.isGameOver(), is(true));
		assertThat(board.getOutcome(), is(Board.Square.X));
	}
	
	/**
	 * Test the string output.
	 */
	@Test
	public void testToString() {
		assertThat(setUpXWins().toString(), is("XO \n XO\n  X"));
	}
	
	/**
	 * Test that the board correctly rejects an attempt to play
	 * the EMPTY value as if it were a legal piece.
	 */
	@Test
	public void testPlayEmpty() {
		assertThrows(IllegalArgumentException.class,
				() -> new Board().move(0, 0, Board.Square.EMPTY));
	}
	
	/**
	 * Test that the board correctly rejects an attempt to play
	 * at an occupied square.
	 */
	@Test
	public void testPlayOccupied() {
		Board board = new Board();
		board.move(0, 0, Board.Square.X);
		assertThrows(IllegalStateException.class, 
				() -> board.move(0, 0, Board.Square.O));
	}
}
