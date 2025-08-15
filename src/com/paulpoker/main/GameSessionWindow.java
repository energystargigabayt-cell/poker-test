package com.paulpoker.main;

import com.paulpoker.game.Bot;
import com.paulpoker.game.Player;
import com.paulpoker.game.PokerParty;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GameSessionWindow extends JFrame {
    private final PokerParty party;

    GameSessionWindow(PokerParty party)
    {
        this.party = party;

        try {
            setIconImage(ImageIO.read(new File(getClass().getResource("../media/favicon.jpg").getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(new PokerGamePane(this));

        {
            System.out.println("Player banks values: " + party.PLAYER_BANKS);
            System.out.println("Minimum bet value: " + party.MIN_BET);
        }

        setTitle("Paul POKER!!!");
        setVisible(true);
        setSize(1040, 585);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    static class PokerGamePane extends JPanel
    {
        private GameSessionWindow window;
        private final JButton button = new JButton("Repaint hands!");
        private Image pokerTable, smallStack, card1, card2, card3, card4, chipsStack1, chipsStack2, chipsStack3, street[] = new Image[5];
        private int x = 40, y = 40;
        private PokerStage currentStage = PokerStage.Preflop;
        private Player[] currentQueue, playersList;
        private int currentOrder = 0;
        private int currentBank = 0;
        private int minBet;
        private Player client;
        private Timer initTrade;
        private BettingWindow test = new BettingWindow(window, "Input your bet!");

        enum PokerStage { Preflop, Flop, Thorn, River, FINAL }

        public PokerGamePane(GameSessionWindow window)
        {
            this.window = window;
            currentQueue = window.party.partyQueue;

            currentQueue[0] = new Player(window.party.PLAYER_BANKS);
            currentQueue[1] = new Bot(window.party.PLAYER_BANKS);

            client = currentQueue[0];

            minBet = window.party.MIN_BET;

            test.setVisible(false);

            setLayout(new FlowLayout());

            initGame();
        }

        void initGame()
        {
            window.party.shuffleDeck();

            currentOrder = (new Random()).nextInt(2);

            if(client == currentQueue[currentOrder])
            {
                client.isDealer = true;
            }

            initTrade = new Timer(2000, e ->
            {
                minBet = Math.max(currentQueue[0].currentBet, currentQueue[1].currentBet);

                currentOrder++;

                if(currentOrder > 1)
                    currentOrder = 0;

                System.out.println("Current stage: " + currentStage);
                System.out.println("Is player turn: " + (currentQueue[currentOrder] == client));
                System.out.println("Order: " + currentOrder);
                System.out.println("Player: " + currentQueue[currentOrder]);

                if(currentQueue[currentOrder] == client)
                {
                    test.setBank(currentQueue[currentOrder].bank);
                    test.setSlider();
                    test.setVisible(true);
                    System.out.println("You're running out of money!");
//                    initTrade.setDelay(10000);
                    initTrade.stop();
                    currentQueue[currentOrder].placeBet(test.getBet());
                } else
                    currentQueue[currentOrder].placeBet(minBet + 50);
//                currentQueue[currentOrder].placeBet(minBet + (new Random()).nextInt(currentQueue[currentOrder].bank - minBet));

                updateImages();
                render();

                if(currentQueue[0].currentBet == currentQueue[1].currentBet)
                {
                    resetBets();

                    switch (currentStage) {
                        case Preflop -> currentStage = PokerStage.Flop;
                        case Flop -> currentStage = PokerStage.Thorn;
                        case Thorn -> currentStage = PokerStage.River;
                        case River -> {
                            currentStage = PokerStage.FINAL;
                            endGame();
                            test.dispose();
                            return;
                        }
                    }
                }

                if(currentQueue[0].madeFold || currentQueue[1].madeFold)
                {
                    endGame();
                    test.dispose();
                    return;
                }

                if(currentQueue[0].bank == 0 && currentQueue[1].bank == 0)
                {
                    currentStage = PokerStage.FINAL;
                    endGame();
                    test.dispose();
                    return;
                }
//                initTrade.setDelay(2000);
                initTrade.start();
            });

            initPrefBets();
            initPreflopStage();

            updateImages();

            initPreflopStage();

//            endGame();
        }

        void initPreflopStage()
        {
            currentStage = PokerStage.Preflop;
            initTradingStage();
            render();
        }

        void initTradingStage()
        {
            initTrade.start();
        }

        void initPrefBets()
        {
            currentQueue[currentOrder].placeBet(minBet / 2);
            currentQueue[(++currentOrder > 1) ? (currentOrder = 0) : currentOrder].placeBet(minBet);
        }

        void endGame()
        {
            updateImages();
            render();

            Object[] options = {"Yes! Sure...", "No! I'm out."};

            initTrade.stop();

            int n = JOptionPane.showOptionDialog(window, "Would u like to play once more?", "Tricky question =)",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if(n == 0)
            {
                restartGame();
            }
            else
            {
                window.dispose();
            }
            System.out.println("Game ended!");
        }

        void restartGame()
        {
            currentBank = 0;
            currentStage = PokerStage.Preflop;
            updateImages();
            initGame();
        }

        void resetBets()
        {
            for(Player player : currentQueue)
            {
                currentBank += player.currentBet;
                player.currentBet = 0;
            }
        }

        void render()
        {
            repaint();
        }

        void updateImages()
        {
            try
            {
                pokerTable = ImageIO.read(new File(getClass().getResource("../media/poker-table.png").getPath()));

                card1 = ImageIO.read(new File(getClass().getResource("../media/cards/" + window.party.deck[0] + ".png").getPath()));
                card2 = ImageIO.read(new File(getClass().getResource("../media/cards/" + window.party.deck[1] + ".png").getPath()));

                if(currentStage != PokerStage.FINAL)
                {
                    card3 = ImageIO.read(new File(getClass().getResource("../media/cards/card_back.png").getPath()));
                    card4 = ImageIO.read(new File(getClass().getResource("../media/cards/card_back.png").getPath()));
                }
                else
                {
                    card3 = ImageIO.read(new File(getClass().getResource("../media/cards/" + window.party.deck[2] + ".png").getPath()));
                    card4 = ImageIO.read(new File(getClass().getResource("../media/cards/" + window.party.deck[3] + ".png").getPath()));
                }

                chipsStack1 = ImageIO.read(new File(getClass().getResource("../media/small-stack.png").getPath()));
                chipsStack2 = ImageIO.read(new File(getClass().getResource("../media/small-stack.png").getPath()));
                chipsStack3 = ImageIO.read(new File(getClass().getResource("../media/big-stack.png").getPath()));

                for (int i = 0; i < street.length; i++)
                {
                    street[i] = ImageIO.read(new File(getClass().getResource("../media/cards/" + window.party.deck[i + window.party.MAX_PLAYERS * 2] + ".png").getPath()));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D graph = (Graphics2D) g;

            graph.drawImage(pokerTable, 0, 20, this.getWidth(), this.getHeight(), null);
            graph.drawImage(smallStack, x, y, 100, 100, null);
            graph.drawImage(card1, 425, 370, 70, 100, null);
            graph.drawImage(card2, 525, 370, 70, 100, null);
            graph.drawImage(card3, 425, 45, 70, 100, null);
            graph.drawImage(card4, 525, 45, 70, 100, null);

            graph.drawImage(chipsStack1, 275, 105, 50, 50, null);
            graph.drawImage(chipsStack2, 625, 375, 50, 50, null);
            graph.drawImage(chipsStack3, 130, 178, 120, 100, null);

            graph.setColor(new Color(255, 216, 8));
            graph.drawString("Bank: " + currentQueue[0].bank, 695, 405);
            graph.drawString("Bank: " + currentQueue[1].bank, 345, 135);
            graph.drawString("Current Bank: " + currentBank, 135, 288);
            graph.drawString("Bet: " + currentQueue[1].currentBet, 695, 135);
            graph.drawString("Bet: " + currentQueue[0].currentBet, 345, 405);

            if(!currentStage.equals(PokerStage.Preflop))
            {
                for (int i = 0; i < 3; i++) {
                    Image el = street[i];
                    graph.drawImage(el, 295 + i * 90, 208, 70, 100, null);
                }
            }

//            if(currentStage.equals(PokerStage.Thorn) || currentStage.equals(PokerStage.River) || currentStage.equals(PokerStage.FINAL))
//                graph.drawImage(street[3], 295 + 3 * 90, 208, 70, 100, null);

            switch (currentStage)
            {
                case Thorn, River, FINAL -> graph.drawImage(street[3], 295 + 3 * 90, 208, 70, 100, null);
            }

            switch (currentStage)
            {
                case River, FINAL -> graph.drawImage(street[4], 295 + 4 * 90, 208, 70, 100, null);
            }

//            if(currentStage.equals(PokerStage.River) || currentStage.equals(PokerStage.FINAL))
//                graph.drawImage(street[4], 295 + 4 * 90, 208, 70, 100, null);

        }

        class BettingWindow extends JDialog
        {
            private int bank = 0;
            private int bet;
            private JSlider betSlider = new JSlider(SwingConstants.HORIZONTAL);
            private JLabel bankValue = new JLabel();
            private JLabel betValue = new JLabel("Current bet: ");
            private JButton button = new JButton("Place bet!");
            private JButton fold = new JButton("Fold!");
            private JButton allIn = new JButton("All-in!");

            public BettingWindow(Frame owner, String title) {
                super(owner, title, true);

                settings();

                add(betSlider);
                add(bankValue);
                add(betValue);
                add(button);
                add(fold);
                add(allIn);

                setLayout(new FlowLayout());
                setSize(new Dimension(250, 250));
                setResizable(false);
            }

            public void setBank(int bank)
            {
                this.bank = bank;
                bankValue.setText("Current bank: " + this.bank);
            }

            private void setSlider()
            {
                betSlider.setMaximum(this.bank);

                betSlider.setMinimum(minBet);
                betSlider.setValue(minBet);
                betSlider.setMajorTickSpacing(20);
            }

            private void settings()
            {
                button.addActionListener(e ->
                {
                    initTrade.start();
                    setVisible(false);
                });

                betSlider.addChangeListener(e ->
                {
                    this.bet = ((JSlider) e.getSource()).getValue();
                    bankValue.setText("Current bank: " + (this.bank - ((JSlider) e.getSource()).getValue()));
                    betValue.setText("Current bet: " + ((JSlider) e.getSource()).getValue());
                });

                fold.addActionListener(e ->
                {
                    bet = 0;
                    client.fold();
                    setVisible(false);
                });

                allIn.addActionListener(e ->
                {
                    betSlider.setValue(this.bank);
                });
            }

            public int getBet()
            {
                return this.bet;
            }
        }
    }
}
