package com.paulpoker.game;

import java.util.*;

public class PokerParty
{
    public int MAX_PLAYERS = 2;
    public int MIN_BET = 0;
    public int PLAYER_BANKS = 0;
    public Player[] partyQueue = new Player[MAX_PLAYERS];
    public int[] deck =
    {
            12, 13, 14, 15, 16, 17, 18, 19, 110, 111, 112, 113, 114,
            22, 23, 24, 25, 26, 27, 28, 29, 210, 211, 212, 213, 214,
            32, 33, 34, 35, 36, 37, 38, 39, 310, 311, 312 ,313, 314,
            42, 43, 44, 45, 46, 47, 48, 49, 410, 411, 412, 413, 414
    };

    public int[] street = new int[5];

    public PokerParty(int minBetValue, int playerBanksValue)
    {
        this.MIN_BET = minBetValue;
        this.PLAYER_BANKS = playerBanksValue;
    }

    public void shuffleDeck()
    {
        Random random = new Random();

        for(int i = deck.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);

            int temp = deck[index];
            deck[index] = deck[i];
            deck[i] = temp;
        }
    }

    public void setStreet(int ... vals)
    {
        System.arraycopy(vals, 0, street, 0, street.length);
    }
}
