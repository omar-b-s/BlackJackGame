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

            if (checkBlackjack()) {
                continue; // Move to the next round if there's a Blackjack
            }

            // Player Turn
            if (!playerTurn(scanner)) {
                System.out.println("Dealer wins the round.");
                continue; // Skip dealer's turn if player busted
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
                    buyin -= bet;
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
            // Build and display available options
            StringBuilder options = new StringBuilder("Choose: Hit (h), Stand (s)");
            if (canDouble) options.append(", Double (d)");
            if (canSplit) options.append(", Split (sp)");
            options.append(": ");
            System.out.print(options);

            // Get player's choice
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
                case "h": // Hit
                    playerHand.addCard(deck.deal());
                    System.out.println("You drew: " + playerHand);
                    System.out.println("Your total: " + playerHand.getTotal());
                    canDouble = false;
                    canSplit = false;
                    if (playerHand.isBust()) {
                        System.out.println("Bust! You lose this hand.");
                        return false;
                    }
                    break;

                case "s": // Stand
                    return true;

                case "d": // Double
                    if (canDouble) {
                        handleDoubleDown();
                        return !playerHand.isBust(); // Return false if the player busted after doubling
                    } else {
                        System.out.println("Double is not available now. Try another option.");
                    }
                    break;

                case "sp": // Split
                    if (canSplit) {
                        handleSplit(scanner);
                        return true;
                    } else {
                        System.out.println("Split is not available now. Try another option.");
                    }
                    break;

                default: // Invalid input
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void handleDoubleDown() {
        buyin -= bet;
        bet *= 2;
        System.out.println("You doubled down! Your bet is now $" + bet + ".");
        playerHand.addCard(deck.deal());
        System.out.println("You drew: " + playerHand);
        System.out.println("Your total: " + playerHand.getTotal());

        if (playerHand.isBust()) {
            System.out.println("Bust! You lose this hand.");
        }
    }

    private boolean canSplit() {
        if (playerHand.getCards().size() == 2) {
            Card firstCard = playerHand.getCards().get(0);
            Card secondCard = playerHand.getCards().get(1);
            return firstCard.getValue() == secondCard.getValue();
        }
        return false;
    }

    private void handleSplit(Scanner scanner) {
        Hand secondHand = new Hand();
        Card splitCard = playerHand.getCards().remove(1);
        secondHand.addCard(splitCard);
        buyin -= bet;
        System.out.println("You split your hand. Both hands now have a bet of $" + bet + ".");
        playerHand.addCard(deck.deal());
        secondHand.addCard(deck.deal());

        System.out.println("Playing first hand: " + playerHand + " (Total: " + playerHand.getTotal() + ")");
        if (!playerTurn(scanner)) System.out.println("First hand busted.");

        System.out.println("Playing second hand: " + secondHand + " (Total: " + secondHand.getTotal() + ")");
        if (!playerTurn(scanner)) System.out.println("Second hand busted.");

        compareSplitTotals(playerHand, secondHand);
    }

    private boolean dealerTurn() {
        System.out.println("Dealer's hand: " + dealerHand + " (Total: " + dealerHand.getTotal() + ")");
        while (dealerHand.getTotal() < 17) {
            dealerHand.addCard(deck.deal());
            System.out.println("Dealer drew: " + dealerHand);
            System.out.println("Dealer's total: " + dealerHand.getTotal());
            if (dealerHand.isBust()) {
                System.out.println("Dealer busts! You win.");
                buyin += bet * 2;
                return false;
            }
        }
        return true;
    }

    private void compareSplitTotals(Hand firstHand, Hand secondHand) {
        System.out.println("Final results:");
        System.out.println("First hand: " + firstHand + " (Total: " + firstHand.getTotal() + ")");
        if (firstHand.getTotal() > dealerHand.getTotal() && !firstHand.isBust()) {
            System.out.println("First hand wins!");
            buyin += bet * 2;
        } else if (firstHand.getTotal() == dealerHand.getTotal() && !firstHand.isBust()) {
            System.out.println("First hand pushes.");
            buyin += bet;
        } else {
            System.out.println("First hand loses.");
        }

        System.out.println("Second hand: " + secondHand + " (Total: " + secondHand.getTotal() + ")");
        if (secondHand.getTotal() > dealerHand.getTotal() && !secondHand.isBust()) {
            System.out.println("Second hand wins!");
            buyin += bet * 2;
        } else if (secondHand.getTotal() == dealerHand.getTotal() && !secondHand.isBust()) {
            System.out.println("Second hand pushes.");
            buyin += bet;
        } else {
            System.out.println("Second hand loses.");
        }
    }

    private void compareTotals() {
        System.out.println("Final totals: You (" + playerHand.getTotal() + ") vs Dealer (" + dealerHand.getTotal() + ")");
        if (playerHand.getTotal() > dealerHand.getTotal()) {
            System.out.println("You win!");
            buyin += bet * 2;
        } else if (playerHand.getTotal() == dealerHand.getTotal()) {
            System.out.println("Push! It's a draw.");
            buyin += bet;
        } else {
            System.out.println("Dealer wins!");
        }
    }

    private boolean checkBlackjack() {
        if (playerHand.getTotal() == 21 && playerHand.getCards().size() == 2) {
            System.out.println("Blackjack! You win.");
            int payout = (int) (bet * 1.5);
            buyin += bet + payout;
            System.out.println("You receive $" + payout + " as your payout. Total funds: $" + buyin);
            return true;
        }
        if (dealerHand.getTotal() == 21 && dealerHand.getCards().size() == 2) {
            System.out.println("Dealer has Blackjack! You lose.");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        BlackjackMain game = new BlackjackMain();
        game.play();
    }
}
