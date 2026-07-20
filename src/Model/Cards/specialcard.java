package Model.Cards;

/**
 * Common abstract base for the "special" cards (Ariadne's thread,
 * Minotaur), which don't follow the ascending-order rule that numbercard
 * does.
 */
public abstract class specialcard extends Cards {

	private static final long serialVersionUID = 1L;

	public specialcard(int number, String image, String palace) {
		super(number, image, palace);
	}

	@Override
	public boolean canPlay() {
		return true;
	}
}
