package Tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Model.Board;
import Model.player;
import Model.Findings.Fresco;
import Model.Findings.RareFinding;
import Model.Findings.SnakeGoddess;
import Model.pawn.pawn;

public class PlayerScoreTest {

	@Test
	public void freshPlayerHasZeroScore() {
		Board board = new Board();
		player p = new player("Test");
		assertEquals(0, p.calculateScore(board));
	}

	@Test
	public void frescoesAndRareFindingsAddUp() {
		Board board = new Board();
		player p = new player("Test");
		p.addPhotographedFresco(new Fresco("Fresco A", "desc", "a.jpg", 20));
		p.addRareFinding(new RareFinding("Δίσκος Φαιστού", "desc", "b.jpg", "phaistos", 35));
		assertEquals(55, p.calculateScore(board));
	}

	@Test
	public void photographingTheSameFrescoTwiceCountsOnce() {
		player p = new player("Test");
		Fresco f = new Fresco("Fresco A", "desc", "a.jpg", 20);
		p.addPhotographedFresco(f);
		p.addPhotographedFresco(f);
		assertEquals(1, p.getPhotographedFrescoes().size());
	}

	@Test
	public void statueScoreFollowsTheOfficialTable() {
		Board board = new Board();
		player p = new player("Test");
		// 0 statues -> 0 points
		assertEquals(0, p.calculateScore(board));
		// 1 statue -> -20 points (yes, negative - matches the rules table)
		p.addStatue(new SnakeGoddess("Θεά", "desc", "c.jpg"));
		assertEquals(-20, p.calculateScore(board));
		// 4 statues -> +15 points
		p.addStatue(new SnakeGoddess("Θεά", "desc", "c.jpg"));
		p.addStatue(new SnakeGoddess("Θεά", "desc", "c.jpg"));
		p.addStatue(new SnakeGoddess("Θεά", "desc", "c.jpg"));
		assertEquals(15, p.calculateScore(board));
	}

	@Test
	public void theseusScoresDoubleThePositionPoints() {
		Board board = new Board();
		player p = new player("Test");
		pawn theseus = p.getTheseus();
		theseus.setPath(0);
		theseus.setPosition(3); // Board.POINTS[3] == 5
		assertEquals(10, p.calculateScore(board));
	}
}
