package Model.Cards;

/**
 * A card with a value from 1 to 10. There are 20 per palace (2 of each
 * value). To keep moving along a path, the next numbercard played must
 * have a value >= the previous one played on that path (ties allowed).
 */
public class numbercard extends Cards {

	private static final long serialVersionUID = 1L;

	private int value;

	public numbercard(int value, String image, String palace) {
		super(value, image, palace);
		this.value = value;
	}

	public int getvalue() {
		return value;
	}

	@Override
	public boolean canPlay() {
		return value >= 1 && value <= 10;
	}

	/**
	 * Checks whether this card can legally follow the previous one played on
	 * the same path (ascending order, or a tie).
	 * @param lastValue value of the last card played on that path (0 if none yet)
	 */
	public boolean isValidContinuation(int lastValue) {
		return value >= lastValue;
	}
}
