package Model.Findings;

import java.io.Serializable;

/**
 * Abstract base for every archaeological finding in the game (rare
 * findings, frescoes, Snake Goddess statues).
 */
public abstract class Finding implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private String image;

	public Finding(String name, String description, String image) {
		this.name = name;
		this.description = description;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	/**
	 * Points this finding is worth on its own. Statues don't have a fixed
	 * per-item value (they're scored in bulk based on how many a player has
	 * collected), so SnakeGoddess returns 0 here.
	 */
	public abstract int getValue();
}
