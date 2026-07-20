package Model;

/**
 * A plain square on a path, with no archaeological finding.
 */
public class SimplePosition extends Position {

	private static final long serialVersionUID = 1L;

	public SimplePosition(int index, int points) {
		super(index, points);
	}

	@Override
	public boolean hasFinding() {
		return false;
	}
}
