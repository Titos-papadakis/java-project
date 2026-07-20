package Controller;

import java.io.Serializable;

import Model.Board;
import Model.player;

/**
 * Simple container to save/load the whole game state at once (Bonus 3 -
 * saving the game), using plain Java serialization.
 */
public class GameSaveData implements Serializable {

	private static final long serialVersionUID = 1L;

	public final Board board;
	public final player player1;
	public final player player2;
	public final boolean player1Turn;

	public GameSaveData(Board board, player player1, player player2, boolean player1Turn) {
		this.board = board;
		this.player1 = player1;
		this.player2 = player2;
		this.player1Turn = player1Turn;
	}
}
