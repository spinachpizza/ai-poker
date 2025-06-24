import java.util.Random;

public class Deck {

    private int index = 0;

    private String[] deck = {
        "ac","ah","as","ad","2c","2h","2s","2d","3c","3h","3s","3d",
        "4c","4h","4s","4d","5c","5h","5s","5d","6c","6h","6s","6d",
        "7c","7h","7s","7d","8c","8h","8s","8d","9c","9h","9s","9d",
        "0c","0h","0s","0d","jc","jh","js","jd","qc","qh","qs","qd",
        "kc","kh","ks","kd"
    };


    public Deck() {
        reset(); 
    }


    public final void reset() {
        this.index = 0;
        shuffleDeck();
    }


    //Fisher-Yates Shuffle
    private final void shuffleDeck() {
        Random rand = new Random();
        for(int i=deck.length-1; i>0; i--) {
            int j = rand.nextInt(i+1);
            String temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
    }


    public final String getCard() {
        if(this.index < deck.length) {
            String card = deck[this.index];
            this.index++;
            return card;  
        } else {
            return null; //This case should never happen
        }
    }
}
