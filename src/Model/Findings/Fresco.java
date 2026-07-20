package Model.Findings;

/**
 * One of the 6 frescoes. It never becomes any player's property - whoever
 * passes over it can "photograph" it (adding it to their score) without
 * removing it from the board, so the other player can photograph it too.
 */
public class Fresco extends Finding {

	private static final long serialVersionUID = 1L;

	private final int value;

	public Fresco(String name, String description, String image, int value) {
		super(name, description, image);
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
}
