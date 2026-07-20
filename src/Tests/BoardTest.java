package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import Model.Board;
import Model.FindingPosition;
import Model.Path;
import Model.Cards.Cards;
import Model.Cards.minotayros;
import Model.Cards.mitos;
import Model.Cards.numbercard;
import Model.Findings.Finding;
import Model.Findings.RareFinding;

public class BoardTest {

	@Test
	public void deckHasExactlyOneHundredCards() {
		Board board = new Board();
		assertEquals(100, board.getDeckSize());
	}

	@Test
	public void deckHasTheRightCardBreakdown() {
		Board board = new Board();
		int numbers = 0, ariadne = 0, minotaur = 0;
		for (Cards c : board.getDeck()) {
			if (c instanceof numbercard) {
				numbers++;
			} else if (c instanceof mitos) {
				ariadne++;
			} else if (c instanceof minotayros) {
				minotaur++;
			}
		}
		assertEquals(80, numbers); // 20 per palace x 4
		assertEquals(12, ariadne); // 3 per palace x 4
		assertEquals(8, minotaur); // 2 per palace x 4
	}

	@Test
	public void everyPathHasNinePositions() {
		Board board = new Board();
		for (int i = 0; i < 4; i++) {
			assertEquals(9, board.getPath(i).getPositions().length);
		}
	}

	@Test
	public void checkpointIsTheSeventhStep() {
		assertEquals(6, Board.CHECKPOINT_INDEX);
	}

	@Test
	public void eachRareFindingIsPlacedInsideItsOwnPalacePath() {
		Board board = new Board();
		for (int i = 0; i < Board.PALACES.length; i++) {
			Path path = board.getPath(i);
			for (int slot : Board.FINDING_SLOTS) {
				FindingPosition fp = (FindingPosition) path.getPosition(slot);
				if (fp.getFinding() instanceof RareFinding) {
					assertEquals("Rare finding must sit on its own palace's path", path.getPalace(), ((RareFinding) fp.getFinding()).getPalace());
				}
			}
		}
	}

	@Test
	public void allTwentyFindingsAreOnTheBoardExactlyOnce() {
		Board board = new Board();
		Set<Finding> seen = new HashSet<>();
		int rareCount = 0;
		for (int i = 0; i < Board.PALACES.length; i++) {
			for (int slot : Board.FINDING_SLOTS) {
				FindingPosition fp = (FindingPosition) board.getPath(i).getPosition(slot);
				assertTrue("Every excavation slot should have a finding", fp.getFinding() != null);
				seen.add(fp.getFinding());
				if (fp.getFinding() instanceof RareFinding) {
					rareCount++;
				}
			}
		}
		assertEquals(20, seen.size());
		assertEquals(4, rareCount);
	}
}
