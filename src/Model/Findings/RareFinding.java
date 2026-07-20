package Model.Findings;

/**
 * One of the 4 rare findings (Phaistos Disc, Ring of Minos, Malia Jewel,
 * Zakros Rhyton). Each one always belongs to its own palace's path.
 */
public class RareFinding extends Finding {

	private static final long serialVersionUID = 1L;

	private final String palace;
	private final int value;

	public RareFinding(String name, String description, String image, String palace, int value) {
		super(name, description, image);
		this.palace = palace;
		this.value = value;
	}

	public String getPalace() {
		return palace;
	}

	@Override
	public int getValue() {
		return value;
	}
}
