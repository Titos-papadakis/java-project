package Model;

import java.io.Serializable;

/**
 * One palace path (Knossos, Malia, Phaistos, Zakros). Made up of 9
 * consecutive squares (Position). The path index (0-3) lines up with
 * Board.PALACES.
 */
public class Path implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int index;
	private final String palace;
	private final Position[] positions;

	public Path(int index, String palace, Position[] positions) {
		if (positions.length != Board.PATH_LENGTH) {
			throw new IllegalArgumentException("A path must have exactly " + Board.PATH_LENGTH + " positions.");
		}
		this.index = index;
		this.palace = palace;
		this.positions = positions;
	}

	public int getIndex() {
		return index;
	}

	public String getPalace() {
		return palace;
	}

	public Position getPosition(int i) {
		return positions[i];
	}

	public Position[] getPositions() {
		return positions;
	}
}
