
import java.util.Scanner;

public class BlackjackMain {
    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private int buyin;
    private int bet;

    public BlackjackMain() {
        deck = new Deck();
        playerHand = new Hand();
        dealerHand = new Hand();
        buyin = 0;
        bet = 0;
        getBuyin();
    }

    public void getBuyin() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Welcome to Omar's BlackJack game. Please enter your Buy-in Amount: ");
                buyin = Integer.parseInt(scanner.nextLine());

                if (buyin <= 0) {
                    System.out.println("The buy-in amount must be greater than zero. Please try again.");
                } else if (buyin >= 100000) {
                    System.out.println("You're broke. Please enter a realistic amount.");
                } else {
                    System.out.println("Your buy-in amount is $" + buyin + ". Let's start.");
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value for the buy-in amount.");
            }
        }
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        while (buyin > 0) {
            // Betting
            placeBet(scanner);

            // Initial Deal
            playerHand = new Hand();
            dealerHand = new Hand();
            playerHand.addCard(deck.deal());
            playerHand.addCard(deck.deal());
            dealerHand.addCard(deck.deal()); // Visible card
            dealerHand.addCard(deck.deal()); // Hidden card

            // Display Initial Hands
            System.out.println("Your hand: " + playerHand + " (Total: " + playerHand.getTotal() + ")");
            System.out.println("Dealer's visible card: " + dealerHand.getCards().get(0));

            // Player Turn
            if (!playerTurn(scanner)) {
                continue; // Player busted, move to the next round
            }

            // Dealer Turn
            if (!dealerTurn()) {
                continue; // Dealer busted, move to the next round
            }

            // Compare Results
            compareTotals();
        }

        System.out.println("You're out of money. Game over!");
    }

    private void placeBet(Scanner scanner) {
        while (true) {
            try {
                System.out.print("You have $" + buyin + ". Enter your bet: ");
                bet = Integer.parseInt(scanner.nextLine());
                if (bet <= 0 || bet > buyin) {
                    System.out.println("Invalid bet. Enter a bet between 1 and $" + buyin + ".");
                } else {
                    System.out.println("You bet $" + bet + ". Good luck!");
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a numeric value for the bet.");
            }
        }
    }

    private boolean playerTurn(Scanner scanner) {
        boolean canDouble = true;
        boolean canSplit = canSplit();

        while (true) {
            System.out.print("Choose: Hit (h), Stand (s), " + (canDouble ? "Double (d), " : "") + (canSplit ? "Split (sp): " : ": "));
            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("h")) {
                playerHand.addCard(deck.deal());
                System.out.println("You drew: " + playerHand);
                System.out.println("Your total: " + playerHand.getTotal());

                canDouble = false; // Disable doubling after hitting
                canSplit = false;  // Disable splitting after hitting

                if (playerHand.isBust()) {
                    System.out.println("Bust! You lose this hand.");
                    buyin -= bet; // Deduct the bet
                    return false;
                }
            } else if (choice.equals("s")) {
                return true;
            } else if (choice.equals("d") && canDouble) {
                handleDoubleDown();
                return true; // Automatically stand after doubling
            } else if (choice.equals("sp") && canSplit) {
                handleSplit(scanner);
                return true; // Play both hands and move to the next round
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleDoubleDown() {
        buyin -= bet; // Deduct an additional bet from the buy-in
        bet *= 2; // Double the bet for this hand
        System.out.println("You doubled down! Your bet is now $" + bet + ".");

        // Player gets one more card and automatically stands
        playerHand.addCard(deck.deal());
        System.out.println("You drew: " + playerHand);
        System.out.println("Your total: " + playerHand.getTotal());

        if (playerHand.isBust()) {
            System.out.println("Bust! You lose this hand.");
            buyin -= bet; // Deduct the doubled bet
        }
    }

    private boolean canSplit() {
        if (playerHand.getCards().size() == 2) {
            Card firstCard = playerHand.getCards().get(0);
            Card secondCard = playerHand.getCards().get(1);
            return firstCard.getValue() == secondCard.getValue(); // Check if both cards have the same value
        }
        return false;
    }

    private void handleSplit(Scanner scanner) {
        // Create the second hand
        Hand secondHand = new Hand();
        Card splitCard = playerHand.getCards().remove(1); // Remove one card from the player's hand
        secondHand.addCard(splitCard);

        // Place a second bet
        buyin -= bet; // Deduct an additional bet from the buy-in
        System.out.println("You split your hand. Both hands now have a bet of $" + bet + ".");

        // Deal one card to each hand
        playerHand.addCard(deck.deal());
        secondHand.addCard(deck.deal());

        // Play the first hand
        System.out.println("Playing first hand: " + playerHand + " (Total: " + playerHand.getTotal() + ")");
        if (!playerTurn(scanner)) {
            System.out.println("First hand busted.");
        }

        // Play the second hand
        System.out.println("Playing second hand: " + secondHand + " (Total: " + secondHand.getTotal() + ")");
        if (!playerTurn(scanner)) {
            System.out.println("Second hand busted.");
        }

        // Compare totals for both hands against the dealer
        compareSplitTotals(secondHand);
    }

    private boolean dealerTurn() {
        System.out.println("Dealer's hand: " + dealerHand + " (Total: " + dealerHand.getTotal() + ")");
        while (dealerHand.getTotal() < 17) {
            dealerHand.addCard(deck.deal());
            System.out.println("Dealer drew: " + dealerHand);
            System.out.println("Dealer's total: " + dealerHand.getTotal());

            if (dealerHand.isBust()) {
                System.out.println("Dealer busts! You win.");
                buyin += bet * 2; // Win double the bet
                return false;
            }
        }
        return true;
    }

    private void compareSplitTotals(Hand secondHand) {
        System.out.println("Final results:");

        // Compare the first hand
        System.out.println("First hand: " + playerHand + " (Total: " + playerHand.getTotal() + ")");
        if (playerHand.getTotal() > dealerHand.getTotal() && !playerHand.isBust()) {
            System.out.println("First hand wins!");
            buyin += bet * 2; // Win double the bet
        } else if (playerHand.getTotal() < dealerHand.getTotal() || playerHand.isBust()) {
            System.out.println("First hand loses.");
        } else {
            System.out.println("First hand pushes (Draw).");
            buyin += bet; // Return the bet
        }

        // Compare the second hand
        System.out.println("Second hand: " + secondHand + " (Total: " + secondHand.getTotal() + ")");
        if (secondHand.getTotal() > dealerHand.getTotal() && !secondHand.isBust()) {
            System.out.println("Second hand wins!");
            buyin += bet * 2; // Win double the bet
        } else if (secondHand.getTotal() < dealerHand.getTotal() || secondHand.isBust()) {
            System.out.println("Second hand loses.");
        } else {
            System.out.println("Second hand pushes (Draw).");
            buyin += bet; // Return the bet
        }
    }

    private void compareTotals() {
        System.out.println("Final totals: You (" + playerHand.getTotal() + ") vs Dealer (" + dealerHand.getTotal() + ")");
        if (playerHand.getTotal() > dealerHand.getTotal()) {
            System.out.println("You win!");
            buyin += bet * 2; // Win double the bet
        } else if (playerHand.getTotal() < dealerHand.getTotal()) {
            System.out.println("Dealer wins!");
            buyin -= bet; // Lose the bet
        } else {
            System.out.println("Push! It's a draw.");
            buyin += bet; // Return the bet
        }
    }

    public static void main(String[] args) {
        BlackjackMain game = new BlackjackMain();
        game.play();
    }
}
