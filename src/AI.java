import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class AI extends Player {

    
    public AI(int id, String name, int balance, Deck deck, Game game, GUI gui) {
        super(id, name, balance, deck, game, gui);
    }

    @Override
    public final void startTurn() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> makeChoice());
        pause.play();
    }

    private void makeChoice() {

        double foldChance = 0.5;
        double callChance = 1.0;
        double raiseChance = 0.5;

        int playersLeft = game.getPlayersLeft();

        //Change probabilities for players left
        if(playersLeft <= 2) {
            foldChance = foldChance * 0.1;
        } else if(playersLeft == 3) {
            foldChance = foldChance * 0.75;
        }

        //Change probabilities for round num
        int roundNum = game.getCurrentRound();
        if(roundNum == 0 || roundNum == 3) {
            foldChance = foldChance * 0.9;
        }
        if(roundNum == 0) {
            raiseChance = raiseChance * 0.5;
        }

        int cardScore = getScore();

        //Change probabilities for card value
        if(roundNum == 0) {

            if(cardScore >= 210) {
                raiseChance = raiseChance * 2.5;
                foldChance = foldChance * 0.25;
            } else if(cardScore >= 200) {
                raiseChance = raiseChance * 2;
                foldChance = foldChance * 0.6;
            } else if(cardScore >= 110) {
                raiseChance = raiseChance * 1.5;
            } else {
                raiseChance = raiseChance * 0.4;
                foldChance = foldChance * 2.5;
            }

        } else {

            if(cardScore < 110) {
                raiseChance = raiseChance * 0.1;
                foldChance = foldChance * 4;
            } else if(cardScore < 200) {
                raiseChance = raiseChance * 0.25;
                foldChance = foldChance * 2.5;
            } else if(cardScore < 210) {
                raiseChance = raiseChance * 0.95;
            } else if(cardScore < 300) {
                raiseChance = raiseChance * 1.75;
                foldChance = foldChance * 0.25;
            } else if(cardScore < 400) {
                raiseChance = raiseChance * 2.5;
                foldChance = foldChance * 0.1;
            } else if(cardScore < 500) {
                raiseChance = raiseChance * 3;
                foldChance = foldChance * 0.05;
            } else {
                raiseChance = raiseChance * 3.5;
                foldChance = foldChance * 0.01;
            }
        }


        if(roundNum > 0) {
            int tableCardScore = getTableScore();

            //If table cards are the best cards then thats not good
            if((tableCardScore / 100) * 100 == (cardScore / 100) * 100) {
                foldChance = foldChance * 1.2;
                raiseChance = raiseChance * 0.4;
            }
        }


        int bet = game.getCurrentBet();


        //Change probabilities for the current bet amount
        if(bet == 0) {
            foldChance = 0.1;
        } else if(bet <= 10) {
            foldChance = foldChance * 0.75;
        } else if(bet <= 50) {
            foldChance = foldChance * 0.9;
        } else if(bet <= 100) {
            foldChance = foldChance * 2.25;
        } else {
            foldChance = foldChance * 3;
        }



        //Stop people continually raising (might not work too well)
        if(raised) {
            raiseChance = raiseChance * 0.25;
        }
        //If someone else has raised then less likely to raise
        if(game.hasRaised()) {
            raiseChance = raiseChance * 0.4;
        }

        DecimalFormat df = new DecimalFormat("#.###");

        System.out.println("Fold: " + df.format(foldChance) + ", Call: " + df.format(callChance) + ", Raise: " + df.format(raiseChance));


        Map<String, Double> choices = new HashMap<>();
        choices.put("fold", foldChance);
        choices.put("call", callChance);
        choices.put("raise", raiseChance);

        String choice = weightedRandomChoice(choices);
        //System.out.println(choice);


        switch(choice) {
            case "fold":
                fold();
                break;
            case "call":
                call();
                break;
            case "raise":
                raise(game.getCurrentBet() + getRaiseAmount());
                break;
        }
    }
    

    private int getRaiseAmount() {

        double lowChance = 1.5;
        double midChance = 1.0;
        double highChance = 0.5;


        int cardScore = getScore();

        if(cardScore < 110) {
            lowChance = lowChance * 3.5;
            highChance = highChance * 0.25;
        } else if(cardScore < 200) {
            lowChance = lowChance * 2;
            highChance = highChance * 0.5;
        } else if(cardScore < 210) {
            lowChance = lowChance * 1.2;
            highChance = highChance * 0.8;
        } else if(cardScore < 300) {
            lowChance = lowChance * 0.8;
            midChance = midChance * 1.2;
        } else if(cardScore < 400) {
            lowChance = lowChance * 0.5;
            midChance = midChance * 1.8;
            highChance = highChance * 1.2;
        } else {
            lowChance = lowChance * 0.2;
            midChance = midChance * 1.2;
            highChance = highChance * 2;
        }



        int tableCardScore = getTableScore();

        //If table cards are the best cards then thats not good
        if((tableCardScore / 100) * 100 == (cardScore / 100) * 100) {
            lowChance = lowChance * 2.5;
            highChance = highChance * 0.5;
        }


        //Change probabilities for the current personal pot
        if(myPot > 50 && myPot < 100) {
            lowChance = lowChance * 1.8;
        } else if(myPot >= 100) {
            lowChance = lowChance * 2.2;
            highChance = highChance * 0.75;
        }



        Map<String, Double> choices = new HashMap<>();
        choices.put("low", lowChance);
        choices.put("mid", midChance);
        choices.put("high", highChance);

        String choice = weightedRandomChoice(choices);

        int min = 0;
        int max = 10;
        switch(choice) {
            case "low":
                min = 5;
                max = 15;
                break;
            case "mid":
                min = 12;
                max = 30;
                break;
            case "high":
                min = 22;
                max = 50;
                break;
        }

        int randomNum = ThreadLocalRandom.current().nextInt(min, max);
        return randomNum;
    }


    private <T> T weightedRandomChoice(Map<T, Double> weights) {
        // Build the cumulative weight map
        NavigableMap<Double, T> map = new TreeMap<>();
        double total = 0;

        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            if (entry.getValue() <= 0) continue;
            total += entry.getValue();
            map.put(total, entry.getKey());
        }

        // Generate random number in [0, total)
        double r = new Random().nextDouble() * total;

        // Find the closest key greater than r
        return map.higherEntry(r).getValue();
    }
}
