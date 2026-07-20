# Αναζητώντας τα Χαμένα Μινωικά Ανάκτορα

Υλοποίηση του project για το μάθημα ΗΥ252 (Αντικειμενοστρεφής Προγραμματισμός,
Πανεπιστήμιο Κρήτης, χειμερινό εξάμηνο 2024-2025) — Φάση Β.

Παραλλαγή του "Lost Cities" με θέμα τα 4 μινωικά ανάκτορα (Κνωσός, Μάλια,
Φαιστός, Ζάκρος).

## Δομή project

```
minotayros/
├── src/
│   ├── Model/          -> κλάσεις παιχνιδιού (Board, Path, Position, player, Cards, Findings, pawn)
│   ├── Controller/      -> controller.java (κανόνες, ροή γύρου), MusicPlayer, GameTimer, save/load
│   ├── View/            -> Gui.java (Swing, JLayeredPane)
│   ├── Tests/           -> JUnit 4 tests
│   └── project_assets/  -> εικόνες, κάρτες, csv με ιστορικές πληροφορίες, μουσική
├── lib/                 -> junit-4.13.2.jar, hamcrest-core-1.3.jar
├── dist/                -> minotayros.jar (εκτελέσιμο, βλέπε Bonus 1)
└── README.md
```

Το `src` είναι ταυτόχρονα το source root του Eclipse project (εκεί βρίσκονται
τα `.classpath`/`.project`), οπότε αν το ανοίξετε με Eclipse ("Import ->
Existing Projects into Workspace") απλά δείξτε στο `minotayros/src`.

## Εκτέλεση μέσα από το Eclipse

1. Import το `minotayros/src` ως Eclipse project.
2. Run As -> Java Application στο `Controller.controller` (έχει `main`).

Το working directory του Eclipse launcher είναι από default ο φάκελος του
project (δηλαδή `src/`), οπότε τα σχετικά paths προς το `project_assets/`
δουλεύουν χωρίς καμία ρύθμιση.

## Εκτέλεση από τη γραμμή εντολών

```bash
cd minotayros/src
javac -encoding UTF-8 -d ../bin $(find Model Controller View -name "*.java")
java -cp ../bin Controller.controller
```

(Σε Windows PowerShell, αντί για `$(find ...)`, μεταγλωττίστε με
`javac -encoding UTF-8 -d ..\bin (Get-ChildItem -Recurse -Include *.java -Path Model,Controller,View)`.)

**Σημαντικό:** το πρόγραμμα φορτώνει τις εικόνες/κάρτες/μουσική με σχετικά
paths (`project_assets/...`), οπότε πρέπει να το τρέξετε **μέσα από τον
φάκελο `src`** (ή να αντιγράψετε τον φάκελο `project_assets` δίπλα στο σημείο
εκτέλεσης).

## Εκτελέσιμο JAR (Bonus 1)

Υπάρχει ήδη έτοιμο στο `dist/minotayros.jar`. Για να το ξαναφτιάξετε:

```bash
cd minotayros/src
javac -encoding UTF-8 -d ../out $(find Model Controller View -name "*.java")
cd ..
jar cfe dist/minotayros.jar Controller.controller -C out .
```

Εκτέλεση (πάντα μέσα από το `src`, για τους ίδιους λόγους με παραπάνω):

```bash
cd minotayros/src
java -jar ../dist/minotayros.jar
```

## JUnit Tests

Τα tests βρίσκονται στο `src/Tests` και χρησιμοποιούν JUnit 4 (jars στο
`lib/`). Δεν χρειάζονται πραγματικό παράθυρο - το `Tests/FakeGui.java` είναι
ένα test double που δεν ανοίγει πραγματικά dialogs, οπότε τρέχουν headless.

```bash
cd minotayros
javac -encoding UTF-8 -cp "lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" -d out $(find src/Model src/Controller src/View src/Tests -name "*.java")
cd src
java -cp "../out;../lib/junit-4.13.2.jar;../lib/hamcrest-core-1.3.jar;." org.junit.runner.JUnitCore Tests.NumberCardTest Tests.BoardTest Tests.PawnTest Tests.PlayerScoreTest Tests.ControllerFlowTest Tests.SaveLoadTest
```

28 tests, όλα περνάνε.

## Έλεγχοι στο παιχνίδι

- **Δεξί κλικ** σε κάρτα: παίξιμο.
- **Αριστερό κλικ** σε κάρτα: απόρριψη.
- Κλικ πάνω στην εικόνα ανακτόρου (τελευταίο κουτάκι κάθε μονοπατιού):
  ιστορικές πληροφορίες.
- Μενού: New Game / Save Game / Continue Saved Game / Exit Game.

## Σχετικά με τη μουσική

Στην εκφώνηση αναφέρεται ότι δίνονται 2 έτοιμα αρχεία `.wav` (ένα ανά
παίκτη) από το elearn, τα οποία όμως δεν υπήρχαν μέσα στον φάκελο του
project. Έχω βάλει προσωρινά δύο μικρά, δικά μου, συνθετικά κομμάτια
(`project_assets/audio/player1.wav`, `player2.wav`) απλά ώστε να δουλεύει η
λειτουργικότητα από άκρη σε άκρη· αν βρω/πάρω τα κανονικά αρχεία απλά τα
αντικαθιστώ με το ίδιο όνομα.

## Γνωστοί περιορισμοί

- Bonus 2 (Timer): υλοποιημένο με `javax.swing.Timer`, 30" ανά γύρο.
- Bonus 3 (Save/Load): υλοποιημένο με Java serialization σε `savegame.dat`.
- Δεν έγινε ξεχωριστός έλεγχος σε πολλαπλές αναλύσεις οθόνης - το layout
  είναι φτιαγμένο με απόλυτες συντεταγμένες (`JLayeredPane`) σε παράθυρο
  1500x1000.
