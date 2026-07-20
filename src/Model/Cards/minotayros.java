package Model.Cards;

/**
 * Minotaur card. Played against the opponent for a specific palace, sends
 * their pawn 2 steps back (unless it's Theseus, or the opponent already
 * reached/passed the Check Point on that path).
 */
public class minotayros extends specialcard {

	private static final long serialVersionUID = 1L;

	public static final int ATTACK_STEPS = 2;

	public minotayros(int number, String image, String palace) {
		super(number, image, palace);
	}
}
