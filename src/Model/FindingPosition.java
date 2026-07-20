package Model;

import Model.Findings.Finding;

/**
 * An "excavation" square. Holds a finding hidden underneath until some
 * player uncovers it, and tracks whether it's still available.
 */
public class FindingPosition extends Position {

	private static final long serialVersionUID = 1L;

	private Finding finding;
	private boolean available = true; // false = a player took it, or Theseus destroyed it
	private player discoveredBy; // who found/photographed it (only used for bookkeeping/UI)

	public FindingPosition(int index, int points, Finding finding) {
		super(index, points);
		this.finding = finding;
	}

	@Override
	public boolean hasFinding() {
		return finding != null;
	}

	public Finding getFinding() {
		return finding;
	}

	public boolean isAvailable() {
		return available;
	}

	/**
	 * Removes the finding from the board, either because a player collected
	 * it (archaeologist) or because Theseus destroyed it.
	 */
	public void consume(player by) {
		this.available = false;
		this.discoveredBy = by;
	}

	public player getDiscoveredBy() {
		return discoveredBy;
	}
}
