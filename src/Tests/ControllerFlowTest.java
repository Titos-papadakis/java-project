package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import Controller.controller;
import Model.Board;
import Model.Cards.minotayros;
import Model.Cards.numbercard;
import Model.pawn.pawn;
import Model.pawn.thiseas;
import Model.player;

/**
 * Exercises the Controller with a FakeGui so no real dialog windows pop up.
 * Covers turn order, the ascending-card rule, Minotaur attacks and the
 * end-of-game / winner logic.
 */
public class ControllerFlowTest {

	private controller ctrl;

	@Before
	public void setUp() {
		ctrl = new controller();
		ctrl.attachTestView(new FakeGui(ctrl));
	}

	@Test
	public void discardingACardPassesTheTurnToTheOtherPlayer() {
		player first = ctrl.getCurrentPlayer();
		ctrl.onCardDiscard(first, 0);
		assertTrue(ctrl.getCurrentPlayer() != first);
	}

	@Test
	public void startingAPathPlacesTheFirstUnusedPawn() {
		player current = ctrl.getCurrentPlayer();
		current.getHand().set(0, new numbercard(4, "dummy.jpg", "knossos"));

		ctrl.onCardPlay(current, 0);

		pawn onKnossos = current.getPawnOnPath(0);
		assertTrue(onKnossos != null);
		assertEquals(0, onKnossos.getPosition());
		assertEquals(4, onKnossos.getLastCardValue());
	}

	@Test
	public void continuingAPathRequiresAnEqualOrHigherCard() {
		player current = ctrl.getCurrentPlayer();
		current.getHand().set(0, new numbercard(5, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(current, 0); // starts the path at position 0, switches turn

		player owner = ctrl.getPlayer1() == current ? ctrl.getPlayer1() : ctrl.getPlayer2();
		// play out the opponent's turn so it's "owner"'s turn again
		ctrl.getCurrentPlayer().getHand().set(0, new numbercard(1, "dummy.jpg", "malia"));
		ctrl.onCardDiscard(ctrl.getCurrentPlayer(), 0);

		// too low a value must be rejected and NOT move the pawn
		owner.getHand().set(0, new numbercard(3, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(owner, 0);
		assertEquals(0, owner.getPawnOnPath(0).getPosition());

		// equal or higher must be accepted
		owner.getHand().set(0, new numbercard(5, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(owner, 0);
		assertEquals(1, owner.getPawnOnPath(0).getPosition());
	}

	@Test
	public void minotaurCardSendsAnArchaeologistTwoStepsBack() {
		player attacker = ctrl.getCurrentPlayer();
		player defender = attacker == ctrl.getPlayer1() ? ctrl.getPlayer2() : ctrl.getPlayer1();

		pawn defenderPawn = defender.getPawns()[0]; // an archaeologist
		defenderPawn.setPath(0);
		defenderPawn.setPosition(4);

		attacker.getHand().set(0, new minotayros(-1, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(attacker, 0);

		assertEquals(2, defenderPawn.getPosition());
		assertTrue(defenderPawn.isRevealed());
	}

	@Test
	public void minotaurCardOnlyStunsTheseusInsteadOfMovingHim() {
		player attacker = ctrl.getCurrentPlayer();
		player defender = attacker == ctrl.getPlayer1() ? ctrl.getPlayer2() : ctrl.getPlayer1();

		thiseas theseus = defender.getTheseus();
		theseus.setPath(0);
		theseus.setPosition(4);

		attacker.getHand().set(0, new minotayros(-1, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(attacker, 0);

		assertEquals(4, theseus.getPosition()); // unchanged
		assertTrue(theseus.isStunned());
	}

	@Test
	public void minotaurCannotTargetAPawnPastTheCheckpoint() {
		player attacker = ctrl.getCurrentPlayer();
		player defender = attacker == ctrl.getPlayer1() ? ctrl.getPlayer2() : ctrl.getPlayer1();

		pawn defenderPawn = defender.getPawns()[0];
		defenderPawn.setPath(0);
		defenderPawn.setPosition(Board.CHECKPOINT_INDEX);

		attacker.getHand().set(0, new minotayros(-1, "dummy.jpg", "knossos"));
		ctrl.onCardPlay(attacker, 0);

		// no valid target -> card play is rejected, pawn stays exactly on the checkpoint
		assertEquals(Board.CHECKPOINT_INDEX, defenderPawn.getPosition());
	}

	@Test
	public void gameEndsWhenFourPawnsReachTheCheckpoint() {
		assertTrue(!ctrl.isEndOfGame());

		pawn[] p1pawns = ctrl.getPlayer1().getPawns();
		pawn[] p2pawns = ctrl.getPlayer2().getPawns();
		p1pawns[0].setPath(0);
		p1pawns[0].setPosition(Board.CHECKPOINT_INDEX);
		p1pawns[1].setPath(1);
		p1pawns[1].setPosition(Board.CHECKPOINT_INDEX);
		p2pawns[0].setPath(2);
		p2pawns[0].setPosition(Board.CHECKPOINT_INDEX);
		p2pawns[1].setPath(3);
		p2pawns[1].setPosition(Board.CHECKPOINT_INDEX);

		assertTrue(ctrl.isEndOfGame());
	}

	@Test
	public void tiedScoresProduceNoWinner() {
		assertNull(ctrl.getWinner()); // both players start at 0-0
	}
}
