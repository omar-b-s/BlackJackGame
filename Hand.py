import java.util.ArrayList;
import java.util.List;

class Hand {
    private List<Card> cards;
    private int total;
    private int aces;

    public Hand() {
        cards = new ArrayList<>();
        total = 0;
        aces = 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        if (card.getRank().equals("Ace")) {
            aces++;
        }
        calculateTotal();
    }

    private void calculateTotal() {
        total = 0;
        for (Card card : cards) {
            total += card.getValue();
        }
        while (total > 21 && aces > 0) {
            total -= 10; // Convert one Ace from 11 to 1
            aces--;
        }
    }

    public boolean isBust() {
        return total > 21;
    }

    public int getTotal() {
        return total;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        StringBuilder handString = new StringBuilder();
        for (Card card : cards) {
            handString.append(card).append(", ");
        }
        return handString.substring(0, handString.length() - 2); // Remove trailing comma and space
    }
}
