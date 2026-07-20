package Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Model.pawn.arxaiologos;
import Model.pawn.thiseas;

public class PawnTest {

	@Test
	public void archaeologistScoresNormalPoints() {
		assertEquals(1, new arxaiologos().getScoreMultiplier());
	}

	@Test
	public void theseusScoresDoublePoints() {
		assertEquals(2, new thiseas().getScoreMultiplier());
	}

	@Test
	public void theseusCanOnlyDestroyThreeFindings() {
		thiseas t = new thiseas();
		assertTrue(t.canDestroyMore());
		t.registerDestroyedFinding();
		t.registerDestroyedFinding();
		t.registerDestroyedFinding();
		assertFalse(t.canDestroyMore());
		assertEquals(3, t.getDestroyedFindings());
	}

	@Test
	public void newPawnIsNotPlacedYet() {
		arxaiologos a = new arxaiologos();
		assertFalse(a.isPlaced());
		a.setPath(2);
		assertTrue(a.isPlaced());
	}
}
