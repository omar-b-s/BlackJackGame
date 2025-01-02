
#Each Card is Unique
class Card:
    def __init__(self, suit: str, rank: str, value: int):
        self.suit = suit
        self.rank = rank
        self.value = value

    def __str__(self):
        return f"{self.rank} of {self.suit}"
    

