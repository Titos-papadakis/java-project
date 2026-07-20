package Model.pawn;

/**
 * One of the player's 3 archaeologists. Can excavate a finding (adds it to
 * his collection) or ignore it and stay hidden.
 */
public class arxaiologos extends pawn {

	private static final long serialVersionUID = 1L;

	public arxaiologos() {
		super("arxaiologos");
	}

	@Override
	public int getScoreMultiplier() {
		return 1;
	}
}
