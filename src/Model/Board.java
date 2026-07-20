package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Cards.Cards;
import Model.Cards.minotayros;
import Model.Cards.mitos;
import Model.Cards.numbercard;
import Model.Findings.Finding;
import Model.Findings.Fresco;
import Model.Findings.RareFinding;
import Model.Findings.SnakeGoddess;

/**
 * The game board. Holds the 4 palace paths, the draw pile and the discard
 * pile.
 *
 * Images (paths/findings/cards) are loaded from the project_assets folder,
 * which must be reachable from the working directory the program runs in
 * (see README).
 */
public class Board implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int PATH_LENGTH = 9;
	public static final int CHECKPOINT_INDEX = 6; // 7th step (1-indexed) = index 6
	public static final int[] POINTS = { -20, -15, -10, 5, 10, 15, 30, 35, 50 };
	public static final int[] FINDING_SLOTS = { 1, 3, 5, 7, 8 }; // positions 2,4,6,8,9 (1-indexed)

	public static final String[] PALACES = { "knossos", "malia", "phaistos", "zakros" };

	private static final String ASSETS = "project_assets/";

	private Path[] paths;
	private List<Cards> deck;
	private List<Cards> discardPile;

	public Board() {
		paths = new Path[PALACES.length];
		discardPile = new ArrayList<>();
		buildPaths();
		buildDeck();
	}

	// ---------------------------------------------------------------
	// Paths + findings
	// ---------------------------------------------------------------

	private void buildPaths() {
		for (int p = 0; p < PALACES.length; p++) {
			paths[p] = new Path(p, PALACES[p], buildPositions());
		}
		placeFindings();
	}

	private Position[] buildPositions() {
		Position[] positions = new Position[PATH_LENGTH];
		for (int i = 0; i < PATH_LENGTH; i++) {
			if (isFindingSlot(i)) {
				positions[i] = new FindingPosition(i, POINTS[i], null); // the actual finding is assigned later, in placeFindings()
			} else {
				positions[i] = new SimplePosition(i, POINTS[i]);
			}
		}
		return positions;
	}

	private static boolean isFindingSlot(int index) {
		for (int slot : FINDING_SLOTS) {
			if (slot == index) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Randomly places the 20 findings into the excavation slots: each of the
	 * 4 rare findings goes into a random slot INSIDE its own palace path, the
	 * remaining 16 (10 statues + 6 frescoes) are shuffled and handed out to
	 * whichever slots are left, across all 4 paths.
	 */
	private void placeFindings() {
		RareFinding[] rareFindings = {
				new RareFinding("Δίσκος της Φαιστού", "Ο Δίσκος της Φαιστού είναι ένα από τα γνωστότερα μυστήρια της αρχαιολογίας.", ASSETS + "images/findings/diskos.jpg", "phaistos", 35),
				new RareFinding("Δαχτυλίδι του Μίνωα", "Ένα από τα μεγαλύτερα και σπανιότερα χρυσά σφραγιδικά στον κόσμο.", ASSETS + "images/findings/ring.jpg", "knossos", 25),
				new RareFinding("Κόσμημα Μαλίων", "Το χρυσό κόσμημα των μελισσών από τον Χρυσόλακκο των Μαλίων.", ASSETS + "images/findings/kosmima.jpg", "malia", 25),
				new RareFinding("Ρυτό Ζάκρου", "Τελετουργικό αγγείο από το θησαυροφυλάκιο του ανακτόρου της Ζάκρου.", ASSETS + "images/findings/ruto.jpg", "zakros", 25),
		};

		for (RareFinding rf : rareFindings) {
			int pathIndex = indexOfPalace(rf.getPalace());
			int slot = pickFreeSlot(pathIndex); // random empty slot, inside its own path
			setFinding(pathIndex, slot, rf);
		}

		List<Finding> rest = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			rest.add(new SnakeGoddess("Θεά των Φιδιών", "Αγαλματίδιο που παρουσιάζει γυναίκα να κρατάει φίδια, χαρακτηριστικό εύρημα των μινωικών ανασκαφών.", ASSETS + "images/findings/snakes.jpg"));
		}
		rest.add(new Fresco("Οι γαλάζιες κυρίες", "Μινωίτισσες με όμορφα φορέματα και κοσμήματα, σύμφωνα με τη μόδα της εποχής.", ASSETS + "images/findings/fresco1_20.jpg", 20));
		rest.add(new Fresco("Τα ταυροκαθάψια", "Επικίνδυνο αγώνισμα με άλμα πάνω στη ράχη ταύρου, σύνηθες στα μινωικά χρόνια.", ASSETS + "images/findings/fresco2_20.jpg", 20));
		rest.add(new Fresco("Τα δελφίνια", "Δελφίνια που κολυμπούν ανάμεσα σε ψάρια, από το μέγαρο της βασίλισσας.", ASSETS + "images/findings/fresco3_15.jpg", 15));
		rest.add(new Fresco("Ο πρίγκιπας με τα κρίνα", "Ανδρική μορφή με μινωικό περίζωμα και κάλυμμα κεφαλής διακοσμημένο με κρίνα.", ASSETS + "images/findings/fresco4_20.jpg", 20));
		rest.add(new Fresco("Πομπή νέων", "Νέοι σε θρησκευτική πομπή που φέρουν δώρα για τη θεότητα.", ASSETS + "images/findings/fresco5_15.jpg", 15));
		rest.add(new Fresco("Η Παριζιάνα", "Γυναικεία μορφή σε προφίλ, από τις πιο γνωστές τοιχογραφίες της Κνωσού.", ASSETS + "images/findings/fresco6_15.jpg", 15));

		Collections.shuffle(rest);

		int findingIdx = 0;
		for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
			for (int slot : FINDING_SLOTS) {
				FindingPosition fp = (FindingPosition) paths[pathIndex].getPosition(slot);
				if (fp.getFinding() == null && findingIdx < rest.size()) {
					setFinding(pathIndex, slot, rest.get(findingIdx));
					findingIdx++;
				}
			}
		}
	}

	private int pickFreeSlot(int pathIndex) {
		List<Integer> free = new ArrayList<>();
		for (int slot : FINDING_SLOTS) {
			if (((FindingPosition) paths[pathIndex].getPosition(slot)).getFinding() == null) {
				free.add(slot);
			}
		}
		return free.get((int) (Math.random() * free.size()));
	}

	private void setFinding(int pathIndex, int slot, Finding finding) {
		paths[pathIndex].getPositions()[slot] = new FindingPosition(slot, POINTS[slot], finding);
	}

	private static int indexOfPalace(String palace) {
		for (int i = 0; i < PALACES.length; i++) {
			if (PALACES[i].equals(palace)) {
				return i;
			}
		}
		throw new IllegalArgumentException("Unknown palace: " + palace);
	}

	public Path getPath(int index) {
		return paths[index];
	}

	public Path getPath(String palace) {
		return paths[indexOfPalace(palace)];
	}

	public Path[] getPaths() {
		return paths;
	}

	// ---------------------------------------------------------------
	// Deck (100 cards)
	// ---------------------------------------------------------------

	private void buildDeck() {
		deck = new ArrayList<>();
		for (String palace : PALACES) {
			for (int v = 1; v <= 10; v++) {
				deck.add(new numbercard(v, ASSETS + "images/cards/" + palace + v + ".jpg", palace));
				deck.add(new numbercard(v, ASSETS + "images/cards/" + palace + v + ".jpg", palace));
			}
			for (int i = 0; i < 3; i++) {
				deck.add(new mitos(-1, ASSETS + "images/cards/" + palace + "Ari.jpg", palace));
			}
			for (int i = 0; i < 2; i++) {
				deck.add(new minotayros(-1, ASSETS + "images/cards/" + capitalize(palace) + "Min.jpg", palace));
			}
		}
		Collections.shuffle(deck);
	}

	private static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public Cards drawCard() {
		if (deck.isEmpty()) {
			return null;
		}
		return deck.remove(deck.size() - 1);
	}

	public void discard(Cards card) {
		discardPile.add(card);
	}

	public boolean isDeckEmpty() {
		return deck.isEmpty();
	}

	public int getDeckSize() {
		return deck.size();
	}

	public List<Cards> getDeck() {
		return deck;
	}

	public List<Cards> getDiscardPile() {
		return discardPile;
	}
}
