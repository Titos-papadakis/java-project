package Controller;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Bonus 2 - per-turn timer. Each turn lasts 30 seconds. Runs on top of
 * javax.swing.Timer (i.e. on the Event Dispatch Thread), so it doesn't
 * block the rest of the program and doesn't need a separate Thread.
 */
public class GameTimer {

	private static final int TURN_SECONDS = 30;

	private final JLabel display;
	private final Runnable onTimeUp;
	private Timer swingTimer;
	private int secondsLeft;

	public GameTimer(JLabel display, Runnable onTimeUp) {
		this.display = display;
		this.onTimeUp = onTimeUp;
	}

	public void restart() {
		if (swingTimer != null) {
			swingTimer.stop();
		}
		secondsLeft = TURN_SECONDS;
		updateLabel();
		swingTimer = new Timer(1000, e -> tick());
		swingTimer.start();
	}

	public void stop() {
		if (swingTimer != null) {
			swingTimer.stop();
		}
	}

	private void tick() {
		secondsLeft--;
		updateLabel();
		if (secondsLeft <= 0) {
			swingTimer.stop();
			onTimeUp.run();
		}
	}

	private void updateLabel() {
		if (display != null) {
			display.setText("Χρόνος: " + secondsLeft + "s");
		}
	}
}
