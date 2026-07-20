package Tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import Controller.controller;
import Model.Cards.numbercard;
import Model.pawn.pawn;
import Model.player;

/**
 * Bonus 3: saving/loading must round-trip the important parts of the game
 * state (pawn positions, hands, score-affecting collections).
 */
public class SaveLoadTest {

	private static final String SAVE_FILE = "savegame.dat";

	@After
	public void cleanUp() {
		new File(SAVE_FILE).delete();
	}

	@Test
	public void savedGameCanBeReloadedWithPawnPositionsIntact() {
		controller ctrl = new controller();
		ctrl.attachTestView(new FakeGui(ctrl));

		player before = ctrl.getCurrentPlayer();
		pawn pawnBefore = before.getPawns()[0];
		pawnBefore.setPath(1);
		pawnBefore.setPosition(3);
		before.getHand().set(0, new numbercard(7, "dummy.jpg", "malia"));

		ctrl.saveGame();
		ctrl.newGame(); // simulate closing and starting a different game

		ctrl.loadGame();

		player after = ctrl.getCurrentPlayer();
		assertEquals(before.getName(), after.getName());
		assertEquals(1, after.getPawns()[0].getPath());
		assertEquals(3, after.getPawns()[0].getPosition());
		assertEquals(7, ((numbercard) after.getHand().get(0)).getvalue());
	}
}
