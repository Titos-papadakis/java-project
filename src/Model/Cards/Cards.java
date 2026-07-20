package Model.Cards;

import java.io.Serializable;

/**
 * Abstract base for every card in the game (numbercards, Ariadne's thread,
 * Minotaur). Every card belongs to one of the 4 palaces.
 */
public abstract class Cards implements Serializable {

	private static final long serialVersionUID = 1L;

	private int number;
	private String image;
	private String palace;

	/**
	 * @param number face value of the card (not very meaningful for the special cards)
	 * @param image  path to the card's image
	 * @param palace which palace the card belongs to (knossos/malia/phaistos/zakros)
	 */
	public Cards(int number, String image, String palace) {
		this.number = number;
		this.image = image;
		this.palace = palace;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPalace() {
		return palace;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Basic sanity check for the card itself (not a check against the current
	 * game state - that logic lives in the Controller).
	 */
	public abstract boolean canPlay();
}
