
# Continuously prompt the user until valid input is received
from deck_module import Deck
from hand_module import Hand


class BlackjackMain:
    def __init__(self):
        self.deck = Deck()
        self.player_hand = Hand()
        self.dealer_hand = Hand()
        self.buyin = 0
        self.bet = 0

        # Initialize buy-in
        self.get_buyin()

    def get_buyin(self):
        """Prompt the player to enter a valid buy-in amount."""
        while True:
            try:
                # Get user input for the buy-in amount
                buyin = int(input("Welcome to Omar's BlackJack game. Please enter your Buy-in Amount: "))

                # Validate the input
                if buyin <= 0:
                    print("The buy-in amount must be greater than zero. Please try again.")
                elif buyin >= 100000:
                    print("You're broke. Please enter a realistic amount.")
                else:
                    print(f"Your buy-in amount is ${buyin}. Let's start.")
                    self.buyin = buyin  # Set the buy-in amount
                    break  # Exit the loop on valid input
            except ValueError:
                print("Invalid input! Please enter a numeric value for the buy-in amount.")

    def play(self):
        # Example for betting
        self.bet = int(input(f"You have ${self.buyin}. Enter your bet: "))
        while self.bet <= 0 or self.bet > self.buyin:
            print(f"Invalid bet. Enter a bet between 1 and ${self.buyin}.")
            self.bet = int(input("Enter your bet: "))
        print(f"You bet ${self.bet}. Good luck!")

        # Initial deal
        self.player_hand = Hand()
        self.dealer_hand = Hand()

        self.player_hand.add_card(self.deck.deal())
        self.player_hand.add_card(self.deck.deal())
        self.dealer_hand.add_card(self.deck.deal())  # Visible card
        self.dealer_hand.add_card(self.deck.deal())  # Hidden card

        # Display initial hands
        print(f"Your hand: {self.player_hand} (Total: {self.player_hand.total})")
        print(f"Dealer's visible card: {self.dealer_hand.cards[0]}")

        # Player turn
        if not self.player_turn():
            return  # Player busted

        # Dealer turn
        if not self.dealer_turn():
            return  # Dealer busted

        # Compare results
        self.compare_totals()

    # Remaining methods (player_turn, dealer_turn, compare_totals) remain unchanged


if __name__ == "__main__":
    game = BlackjackMain()
    game.play()

