package com.amica.games;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * This application configures a small graph of objects that collaborate
 * to carry out a game of Tic-Tac-Toe: the {@ Game} itself, two
 * pluggable {@link Player}s, a game {@link Board}, and some number of
 * {@link GameListener}s who can report on or depict the game in progress.
 * 
 * @author Will Provost
 */
@SpringBootApplication
public class Application {

	/**
	 * We create a game listener with a simple lambda expression
	 * that writes a line to the console for each move.
	 */
	@Bean
	public GameListener reporter() {
		return (row, col, player) -> {
			System.out.format
				("%s played at row %d, column %d.%n", player, row, col);
		};
	}
	
	/**
	 * Player 1 plays randomly. 
	 */
	@Bean
	public Player player1() {
		return new RandomPlayer(Board.Square.X);
	}
	
	/**
	 * Player 2 plays randomly. 
	 */
	@Bean
	public Player player2() {
		return new BruteForcePlayer(Board.Square.O);
	}
	
	/**
	 * One of the game's dependencies is auto-wired -- the {@link Board}
	 * object -- but the players can't be auto-wired, because 
	 * there are multiple possible implementations. So we explicitly
	 * set the players into a new Game object, and then as we return
	 * it from a @Bean method any auto-wiring happens ... well, automatically.
	 */
	@Bean
	public Game game() {
		Game game = new Game();
		game.setPlayer1(player1());
		game.setPlayer2(player2());
		return game;
	}

	/**
	 * Create a Spring context as configured by the this class.
	 * Get the {@link Game} bean and run the game.
	 * Then get the {@link Board} bean and report the outcome.  
	 */
	public static void main(String[] args) {
		try (ConfigurableApplicationContext context =
				SpringApplication.run(Application.class); ) {
			context.getBean(Game.class).run();
			Board board = context.getBean(Board.class);
			System.out.println(board);
			System.out.println(board.describeOutcome());
		}
	}
}
