package com.paulpoker.game;

import java.util.Random;

public class Player
{
    public int currentBet = 0;
    public int bank;
    public int[] hand = new int[2];
    public final int identifier = (new Random()).nextInt(10000);
    public boolean isBot = false;
    public boolean isDealer = false;
    public boolean madeFold = false;

    public Player(int bank)
    {
        this.bank = bank;
    }

    public int placeBet(int betValue)
    {
        currentBet = betValue;
        bank -= betValue;
        System.out.println(this + " placed a bet: " + betValue);
        System.out.println(this + " bank: " + bank);

        return currentBet;
    }

    public void raise(int rValue)
    {
        currentBet += rValue;
        bank -= rValue;
        System.out.println(this + " raised to: " + currentBet);
        System.out.println(this + " bank: " + bank);
    }

    public void call(int cValue)
    {
        currentBet += cValue;
    }

    public void fold()
    {
        currentBet = 0;
        this.madeFold = true;
    }

    public void setHand(int val1, int val2)
    {
        hand[0] = val1;
        hand[1] = val2;
    }
}
