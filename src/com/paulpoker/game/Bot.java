package com.paulpoker.game;

public class Bot extends Player
{
    public Bot(int bank)
    {
        super(bank);
        super.isBot = true;
    }
}
