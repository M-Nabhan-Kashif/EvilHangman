/*  Student information for assignment:
 *
 *  On my honor, Mohammad Nabhan Kashif, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name: Mohammad Nabhan Kashif
 *  email address: mohammadnkashif@utexas.edu
 *  UTEID: mnk665
 *  Section 5 digit ID: 52055
 *  Grader name:Pranav Chandupatla
 *  Number of slip days used on this assignment: 1
 */

import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 */
public class HangmanManager {

    // instance variables / fields
    ArrayList<String> allWords;
    ArrayList<String> currentWords;
    ArrayList<String> guesses;
    ArrayList<Pattern> allPatterns;
    String currKey;
    TreeMap<String, ArrayList<String>> treeMap;
    int numTries;
    int length;
    boolean debug;
    HangmanDifficulty diffLevel;

    // Global Variables
    private static final int MOD_MEDIUM = 4;
    private static final int MOD_EASY = 2;
    private static final char WILD_CARD = '-';


    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        if (words == null || words.size() == 0) {
            throw new IllegalArgumentException("The words set cannot be null or empty");
        }
        allWords = new ArrayList<>();
        currentWords = new ArrayList<>();
        guesses = new ArrayList<>();
        allPatterns = new ArrayList<>();
        treeMap = new TreeMap<>();

        allWords.addAll(words);
        debug = debugOn;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        if (words == null || words.size() == 0) {
            throw new IllegalArgumentException("The words set cannot be null or empty");
        }
        allWords = new ArrayList<>();
        currentWords = new ArrayList<>();
        guesses = new ArrayList<>();
        allPatterns = new ArrayList<>();
        treeMap = new TreeMap<>();

        allWords.addAll(words);
        debug = false;
    }

    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length) {
        int numWords = 0;
        for (String curr: allWords) {
            if (curr.length() == length) {
                numWords++;
            }
        }
        return numWords;
    }


    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * @param wordLen the length of the word to pick this time.
     * numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     * player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        numTries = numGuesses;
        length = wordLen;
        diffLevel = diff;
        currentWords.clear();
        guesses.clear();
        for (String curr: allWords) {
            if (curr.length() == wordLen) {
                currentWords.add(curr);
            }
        }
        createKey (wordLen);
    }

    /**
     * Creates an initial current key so the program can properly run
     * @param wordLen the length of the key we need
     */
    public void createKey(int wordLen) {
        StringBuilder str = new StringBuilder();
        for (int j = 0; j < wordLen; j++) {
            str.append(WILD_CARD);
        }
        currKey = str.toString();
    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     *  Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return currentWords.size();
    }


    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return numTries;
    }


    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade() {
        return guesses.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        return guesses.contains(guess + "");
    }


    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character 
     * for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return currKey;
    }


    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
        if (alreadyGuessed(guess)) {
            throw new IllegalArgumentException("This guess has already been made.");
        }
        guesses.add(guess + "");
        Collections.sort(guesses);
        // Clears treemap and then creates new map based on new word list
        treeMap.clear();
        createMap();
        // Clears and then creates allPatterns ArrayList containing Pattern objects
        allPatterns.clear();
        for (Map.Entry<String, ArrayList<String>> entry : treeMap.entrySet()) {
            allPatterns.add(new Pattern(entry.getKey(), entry.getValue()));
        }
        Collections.sort(allPatterns);
        // Updates list of names based on
        implementDifficulty();
        TreeMap<String, Integer> returnMap = new TreeMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : treeMap.entrySet()) {
            returnMap.put(entry.getKey(), entry.getValue().size());
        }
        if (debug) {
            runDebug();
        }
        return returnMap;
    }

    /**
     * Updates currentWords list based on diffLevel, which is the difficulty selected by user.
     * Updates to the hardest word pattern each time, except for second hardest every MOD_MEDIUM
     * time for medium level and every MOD_EASY time for easy level.
     */
    private void implementDifficulty() {
        int hardest = allPatterns.size() - 1;
        int better = allPatterns.size() - 2;
        if (allPatterns.size() < 2) {
            better = hardest;
        }
        String old = currKey;
        if (diffLevel == HangmanDifficulty.HARD) {
            currentWords = allPatterns.get(hardest).matches;
            currKey = allPatterns.get(hardest).currentPattern;
        }
        else if (diffLevel == HangmanDifficulty.MEDIUM && guesses.size() % MOD_MEDIUM != 0) {
            currentWords = allPatterns.get(hardest).matches;
            currKey = allPatterns.get(hardest).currentPattern;
        }
        else if (diffLevel == HangmanDifficulty.MEDIUM) {
            currentWords = allPatterns.get(better).matches;
            currKey = allPatterns.get(better).currentPattern;
        }
        else if (diffLevel == HangmanDifficulty.EASY && guesses.size() % MOD_EASY == 1) {
            currentWords = allPatterns.get(hardest).matches;
            currKey = allPatterns.get(hardest).currentPattern;
        }
        else if (diffLevel == HangmanDifficulty.EASY) {
            currentWords = allPatterns.get(better).matches;
            currKey = allPatterns.get(better).currentPattern;
        }

        if (old.equals(currKey)) {
            numTries--;
        }
    }

    /**
     * Creates an updated map that contains all patterns as well as stores a list of words matching
     * the given pattern.
     */
    private void createMap() {
        for (int i = 0; i < currentWords.size(); i++) {
            String key = modifyKey(i);
            if (!treeMap.containsKey(key)) {
                ArrayList<String> con = new ArrayList<>();
                con.add(currentWords.get(i));
                treeMap.put(key, con);
            }
            else {
                ArrayList<String> con = new ArrayList<>(treeMap.get(key));
                con.add(currentWords.get(i));
                treeMap.replace(key, con);
            }
        }
    }

    /**
     * Creates a key for the word at a given index in currentWords list based on presence
     * of user's guess in given word.
     * @return key that shows where in the word the user's guessed letter appears.
     */
    private String modifyKey(int index) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < currentWords.get(index).length(); i++) {
            if (guesses.contains(currentWords.get(index).charAt(i) + "")) {
                str.append(currentWords.get(index).charAt(i));
            }
            else {
                str.append(WILD_CARD);
            }
        }
        return str.toString();
    }

    /**
     * Runs some debugging on program if debug boolean is true.
     * Prints out which difficulty level will/should be used as well as the pattern and family.
     */
    private void runDebug() {
        String debug = "";
        if (diffLevel == HangmanDifficulty.HARD) {
            debug = "\nDEBUGGING: Picking hardest list.";
        }
        else if (diffLevel == HangmanDifficulty.MEDIUM && guesses.size() % 4 != 0) {
            debug = "\nDEBUGGING: Picking hardest list.";
        }
        else if (diffLevel == HangmanDifficulty.MEDIUM) {
            debug = "\nDEBUGGING: Difficulty second hardest pattern and list.";
        }
        else if (diffLevel == HangmanDifficulty.EASY && guesses.size() % 2 == 1) {
            debug = "\nDEBUGGING: Picking hardest list.";
        }
        else if (diffLevel == HangmanDifficulty.EASY) {
            debug = "\nDEBUGGING: Difficulty second hardest pattern and list.";
        }
        if (allPatterns.size() < 2) {
            System.out.println("\nDEBUGGING: Should pick second hardest pattern this turn, " +
                    "but only one pattern available.");
            debug = "\nDEBUGGING: Picking  list.";
        }

        System.out.println(debug);
        System.out.println("DEBUGGING: New pattern is: " + currKey + ". New family has " +
                currentWords.size() + " words.\n");

    }


    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        if (numWordsCurrent() <= 0) {
            throw new IllegalArgumentException("numWordsCurrent must be greater than zero");
        }
        if (currKey.contains(WILD_CARD + "")) {
            if (currentWords.size() == 1) {
                return currentWords.get(0);
            }
            Random r = new Random();
            return currentWords.get(r.nextInt(currentWords.size()));
        }
        return currKey;
    }

    /**
     * Pattern subclass created to help with storing patterns and list of matching words.
     * Allows usage of comparable interface for easier sorting of patterns so correct difficulty
     * can be selected.
     */
    private static class Pattern implements Comparable<Pattern> {

        // Instance variables/fields
        String currentPattern;
        ArrayList<String> matches;

        /**
         * Create a new Pattern object from the provided pattern and set of words and pattern.
         * pre: none
         * @param key The pattern/key connected with the list of words
         * @param words The list of words
         */
        public Pattern (String key, ArrayList<String> words) {
            currentPattern = key;
            matches = words;
        }

        /**
         * Counts the number of matches ot the given pattern as stored in the matches List.
         * @return The count of names matching this pattern.
         */
        public int size() {
            return matches.size();
        }

        /**
         * Overrides compareTo from Comparable to function for the pattern object.
         * First sorts by family that has the maximum elements.
         * If there is a tie, sorts by number of characters revealed.
         * If there is a tie again, sorts based on which pattern is smaller lexicographically
         * @return an int value allowing Comparable to get a proper comparison of two Pattern
         * objects.
         */
        @Override
        public int compareTo(Pattern o) {
            if (size() - o.size() != 0) {
                return size() - o.size();
            }
            if (countDashes() != o.countDashes()) {
                return countDashes() - o.countDashes();
            }
            return o.currentPattern.compareTo(currentPattern);
        }

        /**
         * Counts the number of dashes in a word from the list of words matching the pattern.
         * @return Number of dashes in given string.
         */
        private int countDashes() {
            int count = 0;
            for (int i = 0; i < currentPattern.length(); i++) {
                if (currentPattern.charAt(i) == WILD_CARD) {
                    count++;
                }
            }
            return count;
        }
    }
}