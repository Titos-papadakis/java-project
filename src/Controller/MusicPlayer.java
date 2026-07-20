package Controller;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Plays whichever .wav track matches the player whose turn it is, using
 * the javax.sound API as suggested in the assignment. Since there are only
 * 2 short tracks that just swap on turn changes, there's no need for a
 * separate thread.
 */
public class MusicPlayer {

	private final String player1Track;
	private final String player2Track;
	private Clip currentClip;

	public MusicPlayer(String player1Track, String player2Track) {
		this.player1Track = player1Track;
		this.player2Track = player2Track;
	}

	public void playForPlayer1() {
		play(player1Track);
	}

	public void playForPlayer2() {
		play(player2Track);
	}

	private void play(String path) {
		stop();
		try {
			File file = new File(path);
			if (!file.exists()) {
				return; // an leipoun ta wav, to paixnidi synexizei kanonika xwris hxo
			}
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			currentClip = AudioSystem.getClip();
			currentClip.open(stream);
			currentClip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			System.out.println("Could not play music: " + e.getMessage());
		}
	}

	public void stop() {
		if (currentClip != null) {
			currentClip.stop();
			currentClip.close();
			currentClip = null;
		}
	}
}
