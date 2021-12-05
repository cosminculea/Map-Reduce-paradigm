package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedInput {
    private final String fileName;
    private final Map<Integer, Integer> appearances;
    private final List<String> longestWords;

    public MappedInput(String fileName) {
        this.fileName = fileName;
        this.appearances = new HashMap<>();
        this.longestWords = new ArrayList<>();
    }

    /**
     *  Depending on the length of the word given, increment the entry in the appearances map
     * with that length if it already exists, or create a new entry with key 1 if it does not exist.
     *  If the word has a length greater than what exists in longestWords list, update the list with
     * the new word.
     * @param word the word processed
     */

    public void addAppearance(String word) {
        if (word.equals("")) {
            return;
        }

        if (appearances.containsKey(word.length())) {
            appearances.put(word.length(), appearances.get(word.length()) + 1);
        } else {
            appearances.put(word.length(), 1);
        }

        if (longestWords.isEmpty()) {
            longestWords.add(word);
        } else if (longestWords.get(0).length() == word.length()) {
            longestWords.add(word);
        } else if (longestWords.get(0).length() < word.length()) {
            longestWords.clear();
            longestWords.add(word);
        }
    }

    /**
     *  Function needed especially on the reduce step for combining more mappedInput into one.
     *  The function adds all number of appearances and all longest words from the mapped input
     *  given into the current object.
     * @param mappedInput the mapped input object which is added to the current object
     */

    public void combineMappedInputs(MappedInput mappedInput) {
        for (Integer length : mappedInput.getAppearances().keySet()) {
            if (appearances.containsKey(length)) {
                appearances.put(length, appearances.get(length) +
                        mappedInput.getAppearances().get(length));
            } else {
                appearances.put(length, mappedInput.getAppearances().get(length));
            }
        }

        longestWords.addAll(mappedInput.getLongestWords());
    }

    @Override
    public String toString() {
        return "MappedInput{" +
                "fileName='" + fileName + '\'' +
                ", appearances=" + appearances +
                ", longestWords=" + longestWords +
                '}';
    }

    public String getFileName() {
        return fileName;
    }

    public Map<Integer, Integer> getAppearances() {
        return appearances;
    }


    public List<String> getLongestWords() {
        return longestWords;
    }
}
