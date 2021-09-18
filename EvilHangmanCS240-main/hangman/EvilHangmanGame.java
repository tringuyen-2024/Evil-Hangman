package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;
import java.lang.String;
import java.util.TreeMap;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame {

    private Set<String> activeDictionary;
    private Set<Character> guessedLetters;
    private Map<String, Set<String>> partitionMap;
    private int wordLength;
    private String activeKey;
    private int remainingGuesses;


    public EvilHangmanGame() {
        this.activeDictionary = new HashSet<String>();
        this.guessedLetters = new TreeSet<Character>();
        this.partitionMap = new TreeMap<String, Set<String>>();
        this.wordLength = 0;
        this.activeKey = "";
        this.remainingGuesses = 0;
    }

    @SuppressWarnings("serial")
    public static class GuessAlreadyMadeException extends Exception {
        public GuessAlreadyMadeException() {
            super();
        }
    }

    public Set<String> getActiveDictionary() {
        return activeDictionary;
    }

    public void setRemainingGuesses(int remainingGuesses) {
        this.remainingGuesses = remainingGuesses;
    }

    public void decrementRemainingGuesses() {
        this.remainingGuesses--;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public void combineKeys(String newKey) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            if (activeKey.charAt(i) != '-') {
                stringBuilder.append(activeKey.charAt(i));
            }
            else if (newKey.charAt(i) != '-') {
                stringBuilder.append(newKey.charAt(i));
            }
            else {
                stringBuilder.append('-');
            }
        }
        setActiveKey(stringBuilder.toString());
    }

    public void setActiveKey(String newKey) {
        this.activeKey = newKey;
    }

    public int getRemainingGuesses() {
        return this.remainingGuesses;
    }

    public String getActiveKey() { return this.activeKey; }

    public Set<Character> getGuessedLetters() {
        return this.guessedLetters;
    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength) {
        setWordLength(wordLength);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordLength; i++) {
            sb.append("-");
        }
        setActiveKey(sb.toString());
        try {
            Scanner in = new Scanner(dictionary);
            if (!in.hasNext()) {
                System.out.println("Empty dictionary file.");
            }
            else {
                while (in.hasNext()) {
                    String s = in.next();
                    if (s.length() == wordLength) {
                        activeDictionary.add(s);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if (guessedLetters.contains(guess)) {
            throw new GuessAlreadyMadeException();
        }
        else {
            guessedLetters.add(guess);
            for (String word : activeDictionary) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < this.wordLength; i++) {
                    if (word.charAt(i) == guess) {
                        stringBuilder.append(guess);
                    }
                    else {
                        stringBuilder.append("-");
                    }
                }
                String key = stringBuilder.toString();
                if (partitionMap.containsKey(key)) {
                    partitionMap.get(key).add(word);
                }
                else {
                    Set<String> partition = new HashSet<String>();
                    partition.add(word);
                    partitionMap.put(key, partition);
                }
            }
            String key = activeKey;
            Set<String> largestSet = new HashSet<>();
            int appearances = 0;
            for (Map.Entry<String, Set<String>> entry : partitionMap.entrySet()) { // choose the set with the most words
                if (entry.getValue().size() >= largestSet.size()) {
                    if (entry.getValue().size() == largestSet.size()) { // entrySet size == largestSet size
                        // How many times does the letter appear in each partition?
                        LinkedList<Integer> largestSetIndices = new LinkedList<>();
                        LinkedList<Integer> entrySetIndices = new LinkedList<>();
                        int index = key.indexOf(guess);
                        while (index >= 0) {
                            largestSetIndices.addLast(index);
                            index = key.indexOf(guess, index + 1);
                        }
                        index = entry.getKey().indexOf(guess);
                        while (index >= 0) {
                            entrySetIndices.addLast(index);
                            index = entry.getKey().indexOf(guess, index + 1);
                        }
                        int matchingLetters = largestSetIndices.size();
                        int matchingLetters2 = entrySetIndices.size();
                        if (largestSet.isEmpty()) { // the largestSet is empty
                            if (!entry.getValue().isEmpty()) {
                                largestSet = entry.getValue();
                                key = entry.getKey();
                                appearances = matchingLetters2;
                            }
                            else {
                                appearances = matchingLetters;
                            }
                        }
                        else { // the largestSet is not empty
                            if (matchingLetters > 0) { // the largestSet does have words with the guessed letter
                                if (matchingLetters2 > 0) { // the entrySet does have words with the guessed letter
                                    if (matchingLetters2 < matchingLetters) {
                                        largestSet = entry.getValue();
                                        key = entry.getKey();
                                        appearances = matchingLetters2;
                                    }
                                    else {
                                        if (matchingLetters == matchingLetters2) {
                                            while (!largestSetIndices.isEmpty() && !entrySetIndices.isEmpty()) {
                                                int firstIndex = largestSetIndices.removeLast();
                                                int secondIndex = entrySetIndices.removeLast();
                                                if (secondIndex > firstIndex) {
                                                    largestSet = entry.getValue();
                                                    key = entry.getKey();
                                                    appearances = matchingLetters2;
                                                    largestSetIndices.clear();
                                                    entrySetIndices.clear();
                                                }
                                                else if (firstIndex > secondIndex){
                                                    appearances = matchingLetters;
                                                    largestSetIndices.clear();
                                                    entrySetIndices.clear();
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    largestSet = entry.getValue();
                                    key = entry.getKey();
                                    appearances = matchingLetters2;
                                }
                            }
                            else {
                                appearances = matchingLetters;
                            }
                        }
                    }
                    else { // entrySet is bigger
                        key = entry.getKey();
                        largestSet = entry.getValue();
                        LinkedList<Integer> largestSetIndices = new LinkedList<>();
                        int index = key.indexOf(guess);
                        while (index >= 0) {
                            largestSetIndices.addLast(index);
                            index = key.indexOf(guess, index + 1);
                        }
                        appearances = largestSetIndices.size();
                    }
                }
                else { // largestSet is bigger
                    LinkedList<Integer> largestSetIndices = new LinkedList<>();
                    int index = key.indexOf(guess);
                    while (index >= 0) {
                        largestSetIndices.addLast(index);
                        index = key.indexOf(guess, index + 1);
                    }
                    appearances = largestSetIndices.size();
                }
            }
            activeDictionary = largestSet;
            combineKeys(key);
            boolean found = false;
            if (appearances > 0) {
                found = true;
            }
            if (found == true) {
                if (getActiveKey().contains("-")) {
                    if (appearances > 1) {
                        System.out.println("Yes, there are " + appearances + " " + guess + "\n");
                    }
                    else {
                        System.out.println("Yes, there is " + appearances + " " + guess + "\n");
                    }
                }
                else {
                    System.out.println("You win!");
                    for (String s : activeDictionary) {
                        System.out.println("The word was: " + s);
                        break;
                    }
                }
            }
            else {
                decrementRemainingGuesses();
                if (remainingGuesses > 0) {
                    System.out.println("Sorry, there are no " + guess + "'s\n");
                }
                else {
                    System.out.println("You lose!");
                    for (String s : activeDictionary) {
                        System.out.println("The word was: " + s);
                        break;
                    }
                }
            }
        }
        partitionMap.clear();
        return activeDictionary;
    }
}
