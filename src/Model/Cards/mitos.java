package Model.Cards;

/**
 * Ariadne's thread card. Moves the pawn 2 steps forward on the matching
 * palace path. Cannot be used to start a new path, and does not count
 * towards the ascending-order rule of numbercard.
 */
public class mitos extends specialcard {

	private static final long serialVersionUID = 1L;

	public static final int STEPS = 2;

	public mitos(int number, String image, String palace) {
		super(number, image, palace);
	}
}
