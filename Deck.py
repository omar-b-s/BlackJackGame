import random


class Deck:
    def __init__(self):
        suits = ["Hearts", "Diamonds", "Clubs", "Spades"]
        ranks = {
            "2": 2, "3": 3, "4": 4, "5": 5, "6": 6, "7": 7,
            "8": 8, "9": 9, "10": 10, "Jack": 10, "Queen": 10,
            "King": 10, "Ace": 1  # Ace starts as 1
        }
        self.cards = [Card(suit, rank, value) for suit in suits for rank, value in ranks.items()]

    def shuffle(self):
        random.shuffle(self.cards)

    def deal(self):
        """Deal two cards to the player and one card to the dealer."""
        if len(self.cards) < 3:
            raise ValueError("Not enough cards in the deck to deal!")
        
        # Deal two cards to the player
        player_cards = [self.cards.pop(), self.cards.pop()]
        
        # Deal one card to the dealer
        dealer_card = self.cards.pop()

        # Display the dealer's card
        print(f"Dealer's card: {dealer_card}")

        # Return both player and dealer cards
        return player_cards, dealer_card

    def addcard(self, card):
        """Add a card back to the deck only if it's not already present."""
        if card not in self.cards:
            self.cards.append(card)

    def force_shuffle(self):
        if len(self.cards) > 40:
            print("Force shuffling the deck...")
            random.shuffle(self.cards)

    