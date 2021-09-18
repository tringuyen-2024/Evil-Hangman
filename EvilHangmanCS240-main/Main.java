import hangman.EvilHangmanGame;

import java.io.File;
import java.lang.String;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public void outputGuessedLetters(Set<Character> guessedLetters) {
        StringBuilder sb = new StringBuilder();
        for (char c : guessedLetters) {
            sb.append(c);
            sb.append(" ");
        }
        System.out.println("Used letters: " + sb.toString());
    }

    public static void main(String[] args) throws Exception {
        File dictionary = new File(args[0]);
        String arg1 = args[1];
        boolean isValid1 = true;
        for (int i = 0; i < arg1.length(); i++) {
            if (!Character.isDigit(arg1.charAt(i))) {
                isValid1 = false;
            }
        }
        String arg2 = args[2];
        boolean isValid2 = true;
        for (int i = 0; i < arg1.length(); i++) {
            if (!Character.isDigit(arg2.charAt(i))) {
                isValid2 = false;
            }
        }
        if (isValid1 && isValid2) {
            int wordLength = Integer.parseInt(args[1]);
            int remainingGuesses = Integer.parseInt(args[2]);
            if (wordLength >= 2 && remainingGuesses >= 1) {
                EvilHangmanGame hangmanGame = new EvilHangmanGame();
                hangmanGame.startGame(dictionary, wordLength);
                if (!hangmanGame.getActiveDictionary().isEmpty()) {
                    hangmanGame.setRemainingGuesses(remainingGuesses);
                    Main m = new Main();
                    while (remainingGuesses > 0) {
                        String word = hangmanGame.getActiveKey();
                        if (!word.contains("-")) {
                            break;
                        }
                        System.out.println("You have " + remainingGuesses + " left");
                        Set<Character> guessedLetters = hangmanGame.getGuessedLetters();
                        m.outputGuessedLetters(guessedLetters);
                        System.out.println("Word: " + word);
                        System.out.print("Enter guess: ");
                        Scanner sc = new Scanner(System.in);
                        String input = sc.next();
                        if (input.length() > 1) {
                            System.out.println("Invalid input\n");
                        }
                        else {
                            char guess = input.charAt(0);
                            if (Character.isLetter(guess)) {
                                guess = Character.toLowerCase(guess);
                                try {
                                    hangmanGame.makeGuess(guess);
                                }
                                catch (EvilHangmanGame.GuessAlreadyMadeException ex) {
                                    System.out.println("You already used that letter\n");
                                }
                                remainingGuesses = hangmanGame.getRemainingGuesses();
                            }
                            else {
                                System.out.println("Invalid input\n");
                            }
                        }
                    }
                }
                else {
                    System.out.println("Dictionary is empty.");
                }
            }
            else {
                System.out.println("Invalid word length and/or number of guesses.");
            }
        }
        else {
            System.out.println("Invalid input for word length and/or number of guesses.");
        }
    }
}
