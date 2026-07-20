package Model.pawn;

/**
 * Theseus: the player's special pawn. Can destroy findings instead of
 * collecting them (up to 3 total), scores double points at the end of the
 * game, but a Minotaur attack "tires him out" and he skips his next turn.
 */
public class thiseas extends pawn {

	private static final long serialVersionUID = 1L;

	private static final int MAX_DESTROYED = 3;

	private int destroyedFindings = 0;
	private boolean stunned = false; // true = skips his next turn (after a Minotaur attack)

	public thiseas() {
		super("thiseas");
	}

	public boolean canDestroyMore() {
		return destroyedFindings < MAX_DESTROYED;
	}

	public void registerDestroyedFinding() {
		destroyedFindings++;
	}

	public int getDestroyedFindings() {
		return destroyedFindings;
	}

	public boolean isStunned() {
		return stunned;
	}

	public void setStunned(boolean stunned) {
		this.stunned = stunned;
	}

	@Override
	public int getScoreMultiplier() {
		return 2;
	}
}
