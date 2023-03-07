package com.amica.games;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
* Unit test for the {@link Game} class. Since the class under test has
* auto-wired dependencies, we use our own test configuration to supply
* appropriate mock objects, which lets us control the inputs to the
* game and to verify that it makes the right outbound calls.
* 
* @author Will Provost
*/
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=GameTest.Config.class)
public class GameTest {

	public static final Player PLAYER1 = Mockito.mock(Player.class);
	public static final Player PLAYER2 = Mockito.mock(Player.class);
	
	/**
	 * The test configuration replaces the usual board and players
	 * with mock objects.
	 */
	@Configuration
	public static class Config {

		/**
		 * This board doesn't capture any actual game state;
		 * it just reports that the game is over after three moves.
		 */
		@Bean
		public Board board() {
			Board board = Mockito.mock(Board.class);
			Mockito.when(board.isGameOver())
					.thenReturn(false)
					.thenReturn(false)
					.thenReturn(false)
					.thenReturn(true);
			return board;
		}
		
		/**
		 * These players don't do anything when asked to move -- 
		 * what would be the point? -- but they can be used to verify
		 * the game's behavior.
		 */
		@Bean
		public Game game() {
			Game game = new Game();
			game.setPlayer1(PLAYER1);
			game.setPlayer2(PLAYER2);
			return game;
		}
	}
	
	@Autowired
	private Game game;
	
	/**
	 * Run the mock, three-move game, and verify that the game object
	 * did call on player 1 to move twice, and player 2 once. 
	 */
	@Test
	public void testRun() {
		game.run();
		Mockito.verify(PLAYER1, Mockito.times(2)).move();
		Mockito.verify(PLAYER2).move();
	}
}
