package com.amica.games;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Player that moves randomly (but always legally).
* 
* @author Will Provost
*/
public class RandomPlayer implements Player {

	private Board.Square me;
	
	@Autowired
	private Board board;
	
	private Random generator = new SecureRandom();
	
	/**
	 * Store who we are.
	 */
	public RandomPlayer(Board.Square sideToPlay) {
		this.me = sideToPlay;
	}
	
	/**
	 * Accessor for who we are.
	 */
	public Board.Square getSideToPlay() {
		return me;
	}
	
	/**
	 * Accessor for the game board.
	 */
	public Board getBoard() {
		return board;
	}
	
	@Override
	/**
	 * Read over the board and count up the empty squares, converting row
	 * and column to an index value from 0 to 8. Randomly choose from
	 * among those options and make the move, converting the index
	 * back to row and column.
	 * 
	 */
	public void move() {
		List<Integer> openSquares = new ArrayList<>();
		for (int i = 0; i < Board.SIZE * Board.SIZE; ++i) {
			if (board.getSquare(i / 3, i % 3) == Board.Square.EMPTY) {
				openSquares.add(i);
			}
		}
		
		int squareToPlay = openSquares.get
				((int) (generator.nextDouble() * openSquares.size()));
		board.move(squareToPlay / 3,  squareToPlay % 3,  me);
	}

}
