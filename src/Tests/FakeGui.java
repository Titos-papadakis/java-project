package Tests;

import Controller.controller;
import View.Gui;

/**
 * Test double for the real Gui: never pops up an actual JOptionPane, so the
 * JUnit tests can drive the Controller without a human clicking anything.
 * All dialog calls just return canned answers instead.
 */
public class FakeGui extends Gui {

	private boolean nextConfirmAnswer = true;
	private String lastMessageTitle;
	private String lastMessageText;

	public FakeGui(controller ctrl) {
		super(ctrl);
	}

	public void setNextConfirmAnswer(boolean answer) {
		this.nextConfirmAnswer = answer;
	}

	@Override
	public boolean confirm(String title, String text) {
		return nextConfirmAnswer;
	}

	@Override
	public String chooseOption(String title, String text, String[] options) {
		return options != null && options.length > 0 ? options[0] : null;
	}

	@Override
	public void showMessage(String title, String text) {
		lastMessageTitle = title;
		lastMessageText = text;
	}

	public String getLastMessageTitle() {
		return lastMessageTitle;
	}

	public String getLastMessageText() {
		return lastMessageText;
	}
}
