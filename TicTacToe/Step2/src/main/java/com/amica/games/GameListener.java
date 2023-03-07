package com.amica.games;

/**
* An observer on a running game.
* 
* @author Will Provost
*/
public interface GameListener {

	/**
	 * Called each time a move is made.
	 */
	public void moveMade(int row, int col, Board.Square player);
}
