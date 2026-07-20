package Model.Findings;

/**
 * One of the 10 Snake Goddess statues. Not worth any fixed value on its
 * own - the score depends on how many a player ends up with in total
 * (see player.calculateScore).
 */
public class SnakeGoddess extends Finding {

	private static final long serialVersionUID = 1L;

	public SnakeGoddess(String name, String description, String image) {
		super(name, description, image);
	}

	@Override
	public int getValue() {
		return 0;
	}
}
