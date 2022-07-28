package dolniak;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CrazyEights {
    private Deck deck = new Deck();
    private Hand myHand = new Hand();
    private Hand computerHand = new Hand();
    private Card discard;
    private ArrayList<Card> discardPile = new ArrayList<Card>();
    private Random rand = new Random();
    private char activeSuit = ' ';
    private int countHearts = 0;
    private int countDiamonds = 0;
    private int countClubs = 0;
    private int countSpades = 0;
    public void print () {
        System.out.println();
    }
    public void print (String msg) {
        System.out.println(msg);
    }

    //private MyWindow promptForChar = new MyWindow();

    public CrazyEights () {
        //deal 7 cards to each of us
        for (int i = 0; i < 7; i++) {
            Card card1 = deal();
            myHand.add(card1);
            Card card2 = deal();
            computerHand.add(card2);
        }

        discard = deal();

        if (discard.getRank() == '8') {
            activeSuit = discard.getSuit();
        } else {
            activeSuit = ' ';
        }

        int turn = rand.nextInt(2);
        if (turn == 1) {
            print("Computer goes first.");
            playComputerCard();
        } else {
            print("You go first.");
        }

        boolean done = false;
        while (!done) {
            playMyCard();
            if (myHand.size() == 0) {
                done = true;
            } else {
                playComputerCard();
                if (computerHand.size() == 0) {
                    done = true;
                }
            }
        }
        print();
        // who played all their cards
        if (myHand.size() == 0) {
            print("Congratulations! You won! The computer still had " + computerHand.size() + " cards.");
        } else {
            print("Sorry, you lost. You still had " + myHand.size() + " cards.");
            print("My Hand: " + myHand);
            print("Discard: " + discard);
        }
    }

    private Card deal() {
        if (deck.size() == 0) {
            deck.reuse(discardPile);
            deck.shuffle();
            discardPile.clear();
            print();
            print("Reshuffled the discard pile.");
        }

        Card card = deck.deal();
        return card;
    }

    public void showStatus() {
        print();
        print("Computer has " + computerHand.size() + " cards.");
        print("My Hand: " + myHand);
        print("Discard: " + discard);
        if (discard.getRank() == '8') {
            print("Suit is " + activeSuit);
        }
    }

    private void discardMyCard(Card myCard) {
        myHand.remove(myCard);
        discardPile.add(discard);
        discard = myCard;
        if (myCard.getRank() == '8') {
            activeSuit = promptForSuit();
        }
    }

    public boolean isValidPlay(String rankSuit) {
        boolean validPlay = true;
        Card card = new Card(rankSuit);
        if (!card.isValid()) {
            print(rankSuit + " is not valid card");
            validPlay = false;
        }

        else if (!myHand.contains(card)) {
            print(rankSuit + " is not in your hand");
            validPlay = false;
        }
        // 8s are always valid
        else if (card.getRank() != '8') {
            // is the discard an 8
            if (discard.getRank() == '8') {
                if (card.getSuit() != activeSuit) {
                    print(rankSuit + " cannot be played on " + discard + " because the suit wass set to " + activeSuit);
                    validPlay = false;
                }
            }
            // if the discard is not 8
            // does the discard match the rank or suit
            else if ((card.getSuit() != discard.getSuit())
                && (card.getRank() != discard.getRank())) {
                System.out.println(rankSuit + " cannot be played on " + discard + ".");
                validPlay = false;
            }
        }

        return validPlay;
    }

    private void playMyCard() {
        showStatus();
        boolean validPlay = false;
        // repeat until a valid play has been entered
        while (!validPlay) {
            Scanner input = new Scanner(System.in);

            System.out.println("Which card do you want to play (or D to draw)?");
            String rankSuit = input.nextLine();
            rankSuit = rankSuit.toUpperCase();

            if (rankSuit.equals("D")) {
                drawMyCard();
                validPlay = true;
            }

            // if valid play  the card
            else if (isValidPlay(rankSuit)) {
                Card selectedCard = new Card(rankSuit);
                discardMyCard(selectedCard);
                validPlay = true;
            }
        }
    }

    public void drawMyCard() {
        Card drewCard = deal();
        System.out.println("");
        System.out.println("You draw " + drewCard);
        myHand.add(drewCard);

        if (isValidPlay(drewCard.toString())) {
            System.out.println("You played " + drewCard);
            discardMyCard(drewCard);
        }
    }
    //page 21
    private void discardComputerCard(Card computerCard) {
        computerHand.remove(computerCard);
        discardPile.add(discard);
        discard = computerCard;
        if (discard.getRank() == '8') {
            int highestCount = countHearts;
            activeSuit = 'H';
            if (countDiamonds > highestCount) {
                highestCount = countDiamonds;
                activeSuit = 'D';
            }
            if (countClubs > highestCount) {
                highestCount = countClubs;
                activeSuit = 'C';
            }
            if (countSpades > highestCount) {
                highestCount = countSpades;
                activeSuit = 'S';
            }
        }
    }

    private void playComputerCard() {
        System.out.println("Computer hand: " + computerHand);
        ArrayList<Card> playableCards = new ArrayList<Card>();
        ArrayList<Card> eights = new ArrayList<Card>();
        countHearts = 0;
        countDiamonds = 0;
        countClubs = 0;
        countSpades = 0;

        // count eights and number of each suit
        for (int i = 0; i < computerHand.size(); i++) {
            Card card = computerHand.cardAt(i);
            // if it's an eight, save it
            if (card.getRank() == '8') {
                eights.add(card);
            }
            // otherwise, count the number of each suit
            else {
                switch(card.getSuit()) {
                    case 'H':
                        countHearts++;
                        break;
                    case 'D':
                        countDiamonds++;
                        break;
                    case 'C':
                        countClubs++;
                        break;
                    case 'S':
                        countSpades++;
                        break;
                }
            }
        }

        // make a list of playable cards
        for (int i = 0; i < computerHand.size(); i++) {
            Card card = computerHand.cardAt(i);

            // if discard is an 8 all cards of active suit are playable
            if(discard.getRank() == '8') {
                if (card.getSuit() == activeSuit) {
                    playableCards.add(card);
                }
            }
            //else if discard is not an 8
            // only cards of same suit or rank are playable
            if (card.getSuit() == (discard.getSuit())
            || card.getRank() == (discard.getRank())) {
                playableCards.add(card);
            }
        }

        // pick a random playable card
        int numberOfPlayableCards = playableCards.size();
        if (numberOfPlayableCards > 0) {
            int pick = rand.nextInt(numberOfPlayableCards);
            Card playedCard = playableCards.get(pick);
            discardComputerCard(playedCard);
        }

        // otherwise, if have an eight, play an eight
        else if (eights.size() > 0) {
            Card playCard = eights.get(0);
            discardComputerCard(playCard);
        }

        // if nothing could play, draw a card
        else {
            Card drewCard = deal();
            computerHand.add(drewCard);
            System.out.println("");
            System.out.println("Computer draw a card.");

            // if it plays, play it
            if (drewCard.getSuit() == discard.getSuit() ||
            drewCard.getRank() == discard.getRank()) {
                discardComputerCard(drewCard);
            }
        }
    }
    private char promptForSuit() {
        char suit = ' ';
        boolean validSuit = false;
        while (!validSuit) {
            suit = promptForChar("Change the suit to H, D, C, or S?");
            suit = Character.toUpperCase(suit);
            if (Card.isValidSuit(suit)) {
                validSuit = true;
            }
        }
        return suit;
    }

    /**/
    protected char promptForChar(String prompt) {
        char c = ' ';
        boolean valid = false;
        while (!valid) {
            Scanner input = new Scanner(System.in);
            //print(prompt);
            System.out.println(prompt);
            //String s = input();
            String s = input.nextLine();
            if(s.length() == 1) {
                c = s.charAt(0);
                valid = true;
            } else {
                c = promptForChar(prompt);
            }
        }
        return c;
    }


    public static void main(String[] args) {
        new CrazyEights();
    }
}
