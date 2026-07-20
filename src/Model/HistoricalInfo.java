package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads the csv with historical information about the palaces, the rare
 * findings, the frescoes and the Snake Goddess (project_assets/csvFiles).
 * The csv has columns Image;Message;Description, separated by ";".
 */
public class HistoricalInfo {

	private final Map<String, String[]> entries = new HashMap<>(); // key -> {message, description}

	public HistoricalInfo(String csvPath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
			String line = reader.readLine(); // header, skip it
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				String[] parts = line.split(";", 3);
				if (parts.length < 3) {
					continue;
				}
				String key = keyFromImagePath(parts[0].trim());
				entries.put(key, new String[] { parts[1].trim(), parts[2].trim() });
			}
		} catch (IOException e) {
			// missing csv shouldn't stop the game, just means no historical info popups
			System.out.println("Could not load historical info csv: " + e.getMessage());
		}
	}

	/**
	 * Turns "findings/diskos.jpg" into "diskos".
	 */
	private static String keyFromImagePath(String path) {
		String name = path.replace('\\', '/');
		int slash = name.lastIndexOf('/');
		if (slash >= 0) {
			name = name.substring(slash + 1);
		}
		int dot = name.lastIndexOf('.');
		if (dot >= 0) {
			name = name.substring(0, dot);
		}
		return name;
	}

	/**
	 * @param imagePath the image path of the card/finding/palace (as stored in the Model)
	 * @return {message, description}, or null if there's no matching entry.
	 */
	public String[] lookup(String imagePath) {
		return entries.get(keyFromImagePath(imagePath));
	}
}
