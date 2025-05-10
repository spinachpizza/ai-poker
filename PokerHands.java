public class PokerHands {

    public static int highestValue;
    public static int first, last;



    //Creates a new array and counts how many times each number occurs
    public static int[] countArray(String[] cardArray) {

        int[] numbers = new int[14];
        for(int i=0; i<cardArray.length; i++) {

            //Convert element at i to int and add to count for each num
            int num = convert(cardArray[i].charAt(0));
            numbers[num-1]++;
        }
        return numbers;
    }


    public static int getMaxValue(String[] cardArray)
    {
        int maxValue = 0;
        for(int i=0; i<cardArray.length; i++)
        {
            int cardNum = convert(cardArray[i].charAt(0));
            if(cardNum > maxValue)
            {
                maxValue = cardNum;
            }
        }
        return maxValue;
    }


    //Converts char value to int value
    public static int convert(char c) {

        if(c == 'j') {return 11;}
        else if(c == 'q') {return 12;}
        else if(c == 'k') {return 13;}
        else if(c == 'a') {return 14;} 
        else if(c == '0') {return 10;} 
        else {return Integer.parseInt(String.valueOf(c));}
    }


    //Converts int to name
    public static String getCardName(int n)
    {
        if(n == 14) {return "ACE";}
        else if(n==13) {return "KING";}
        else if(n==12) {return "QUEEN";}
        else if(n==11) {return "JACK";}
        else {return String.valueOf(n).toUpperCase();}
    }



    //Returns true if theres at least 1 pair
    public static boolean checkPair(String[] cardArray) {

        int[] numberArray = countArray(cardArray);

        for(int i=numberArray.length-1; i>=0; i--) {

            if(numberArray[i] == 2) {
                highestValue = i + 1;
                return true;
            }
        }
        return false;
    }

    //Returns true if theres a set of 3 cards
    public static boolean checkThree(String[] cardArray) {

        int[] numberArray = countArray(cardArray);

        for(int i=numberArray.length-1; i>=0; i--) {

            if(numberArray[i] == 3) {
                highestValue = i + 1;
                return true;
            }
        }
        return false;
    }


    //Returns true if theres a set of 4 cards
    public static boolean checkFour(String[] cardArray) {

        int[] numberArray = countArray(cardArray);

        for(int i=numberArray.length-1; i>=0; i--) {

            if(numberArray[i] == 4) {
                highestValue = i + 1;
                return true;
            }
        }
        return false;
    }


    //Returns true if theres 2 pairs
    public static boolean checkTwoPair(String[] cardArray) {

        int highestValue = 0;
        //Counts how many pairs
        int count = 0;

        int[] numberArray = countArray(cardArray);

        for(int i=numberArray.length-1; i>=0; i--) {

            if(numberArray[i] == 2) {
                highestValue = highestValue + i + 1;
                count ++;
            }
            if(count >= 2) {
                return true;
            }
        }
        return false;
    }


    //Returns true if theres a full house
    public static boolean checkFullHouse(String[] cardArray) {

        int highestValue = 0;

        boolean three = false;
        boolean pair = false;

        int[] numberArray = countArray(cardArray);

        //Checks if theres a set of 3 if not doesnt execute code to save time
        if(checkThree(cardArray) == false) {
            return false;
        } else {

            for(int i=numberArray.length-1; i>=0; i--) {

                //Checks for set of 3
                if(numberArray[i] == 3) {
                    highestValue = highestValue + (3* (i + 1));
                    three = true;

                //Checks for pair
                } else if(numberArray[i] == 2) {
                    highestValue = highestValue + i + 1;
                    pair = true;
                }
            }

            if(three == true && pair == true) {
                return true;
            } else {
                return false;
            }
        }
    }



    //Returns true if the cards contain a flush
    public static boolean checkFlush(String[] cardArray) {

        char suit = ' ';
        //Iterates through array and counts each suit
        int[] suits = new int[4];
        for(int i=0; i<cardArray.length; i++) {
            suit = cardArray[i].charAt(1);
            if(suit == 'c') { suits[0]++;}
            else if(suit == 'h') { suits[1]++;}
            else if(suit == 's') { suits[2]++;}
            else if(suit == 'd') { suits[3]++;}
        }

        //Iterates through array to see if theres a flush
        for(int i=0; i<4; i++) {
            if(suits[i] >= 5) {

                //Find highest value
                for(int j=0; j< cardArray.length; j++) {
            
                    int cardToNum = convert(cardArray[j].charAt(0));
                    
                    if(i == 0) { suit = 'c';}
                    else if(i == 1) { suit = 'h';}
                    else if(i == 2) { suit = 's';}
                    else if(i == 3) { suit = 'd';}

                    if(cardArray[j].charAt(1) == suit && cardToNum > highestValue) {
                        highestValue = cardToNum;
                    }
                }
                return true;
            }
        }
        return false;
    }


    //Returns true if the cards contain a straight
    public static boolean checkStraight(String[] cardArray) {

        int[] cardCount = countArray(cardArray);
        int count = 0; //Count how many in a row
        for(int i=0; i<cardCount.length; i++)
        {
            if(cardCount[i] != 0)
            {
                count ++;

            } else {
                count = 0;
            }

            if(count >= 5)
            {
                return true;
            }
        }
        return false;
    }



    //Produces new array containing the 5 cards wanted from a straight
    public static String[] keepBestFive(String[] cardArray) {

        int first = 0;

        int count = 1; //Count how many in a row
        for(int i=0; i<cardArray.length-1; i++)
        {
            int num1 = convert(cardArray[i].charAt(0));
            int num2 = convert(cardArray[i+1].charAt(0));

            //If next card number is a consecutive number
            if(num1 + 1 == num2)
            {
                count ++;
            } else {
                count = 1;
            }


            //Keep track of first index
            if(count >= 5)
            {
                //Build new array with those 5 cards
                String[] newArray = new String[5];
                for(int j=0; j<5; i++)
                {
                    newArray[j] = cardArray[first+j];
                }
                return newArray;

            } else if(count == 2)
            {
                first = i;
            }
        }

        return cardArray;
    }



    public static String[] sortArray(String[] cardArray) {

        for(int i=0; i<cardArray.length - 1; i++) {
            for(int j=0; j<cardArray.length - 1; j++) {
                
                int charToInt = convert(cardArray[j].charAt(0));
                int charToInt2 = convert(cardArray[j+1].charAt(0));

                if(charToInt > charToInt2) {
                    String temp = cardArray[j];
                    cardArray[j] = cardArray[j+1];
                    cardArray[j+1] = temp;
                }
            }
        }

        return cardArray;
    }



    //Returns value of cards in terms of poker hands
    public static int getCardValue(String[] cards) {
        

        int value = 0;

        //Keeps just the card numbers and sorts it
        String[] newCardArray = sortArray(cards);
        

        //Check for royal flush
        if(checkStraight(newCardArray) == true && highestValue == 14 && checkFlush(keepBestFive(newCardArray)) == true) {
            value = 1000;

        //Check for straight flush
        } else if(checkStraight(newCardArray) == true && checkFlush(keepBestFive(newCardArray)) == true) {
            value = 900 + highestValue;

        //Checks for four of a kind
        } else if(checkFour(newCardArray) == true) {
            value = 800 + highestValue;

        //Checks for full house
        } else if(checkFullHouse(newCardArray) == true) {
            value = 700 + highestValue;

        //Checks for flush
        } else if(checkFlush(newCardArray) == true) {
            value = 600 + highestValue;

        //Checks for straight
        } else if(checkStraight(newCardArray) == true) {
            value = 500 + highestValue;

        //Checks for three of a kind
        } else if(checkThree(newCardArray) == true) {
            value = 400 + highestValue;

        //Checks for two pairs
        } else if(checkTwoPair(newCardArray) == true) {
            value = 300 + highestValue;
        
        //Checks for pair
        } else if(checkPair(newCardArray) == true) {
            value = 200 + highestValue;

        //Else gives value of highst card
        } else {
            value = 100 + convert(newCardArray[newCardArray.length - 1].charAt(0));
        }
        
        return value;
    } 



    public static String valueToName(int value) {

        if(value >= 100 && value < 200) {
            return getCardName(value-100) + " HIGH";

        } else if(value >= 200 && value < 300) {
            return "PAIR OF " + getCardName(value-200) + "S";

        } else if(value >= 300 && value < 400) {
            return "TWO PAIR";

        } else if(value >= 400 && value < 500) {
            return "THREE OF A KIND";

        } else if(value >= 500 && value < 600) {
            return getCardName(value - 500) + " HIGH STRAIGHT";

        } else if(value >= 600 && value < 700) {
            return "FLUSH";

        } else if(value >= 700 && value < 800) {
            return "FULL HOUSE";
             
        } else if(value >= 800 && value < 900) {
            return "FOUR OF A KIND";

        } else if(value >= 900 && value < 1000) {
            return getCardName(value - 900) + " HIGH STRAIGHT FLUSH";

        } else if(value >= 1000) {
            return "ROYAL FLUSH";
        }
        return null;
    }



}
