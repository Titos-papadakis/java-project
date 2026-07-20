package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Model.Cards.numbercard;

public class NumberCardTest {

	@Test
	public void higherOrEqualValueIsValidContinuation() {
		numbercard c = new numbercard(5, "dummy.jpg", "knossos");
		assertTrue(c.isValidContinuation(5)); // ties are allowed
		assertTrue(c.isValidContinuation(3));
	}

	@Test
	public void lowerValueIsNotValidContinuation() {
		numbercard c = new numbercard(3, "dummy.jpg", "knossos");
		assertFalse(c.isValidContinuation(4));
	}

	@Test
	public void anyValueIsValidToStartAPath() {
		numbercard c = new numbercard(1, "dummy.jpg", "knossos");
		assertTrue(c.isValidContinuation(0)); // 0 = no card played on that path yet
	}

	@Test
	public void canPlayRespectsTheOneToTenRange() {
		assertTrue(new numbercard(1, "dummy.jpg", "knossos").canPlay());
		assertTrue(new numbercard(10, "dummy.jpg", "knossos").canPlay());
	}
}
