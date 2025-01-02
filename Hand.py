class hand_module:
    def __init__(self):
        self.cards = []
        self.total = 0
        self.aces = 0

    def add_card(self, card):
        self.cards.append(card)
        if card.rank == "Ace":
            self.aces += 1
        self.calculate_total()

    def calculate_total(self):
        self.total = sum(card.value for card in self.cards)
        while self.total > 21 and self.aces > 0:
            self.total -= 10  # Convert one Ace from 11 to 1
            self.aces -= 1

    def is_bust(self):
        return self.total > 21

    def __str__(self):
        return ", ".join(str(card) for card in self.cards)
