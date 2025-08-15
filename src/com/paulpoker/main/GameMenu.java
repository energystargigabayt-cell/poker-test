package com.paulpoker.main;

import com.paulpoker.game.PokerParty;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameMenu extends JFrame
{
    GameMenu()
    {
        add(new MenuPane());

        try {
            setIconImage(ImageIO.read(new File(getClass().getResource("../media/favicon.jpg").getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Paul POKER!!!");
        setVisible(true);
        setSize(1040, 585);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    class MenuPane extends JPanel
    {
        private JButton startButton = new JButton("Start game!");
        private JButton optionsButton = new JButton("Options...");
        private JButton quitButton = new JButton("Quit game!");
        private int betValue, bankValue;
        private Image backgroundImage;

        {
            try {
                backgroundImage = ImageIO.read(new File(getClass().getResource("../media/poker-table.png").getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MenuPane()
        {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            startButton.setMaximumSize(new Dimension(340, 50));
            optionsButton.setMaximumSize(new Dimension(340, 50));
            quitButton.setMaximumSize(new Dimension(340, 50));

            startButton.setAlignmentX(CENTER_ALIGNMENT);
            optionsButton.setAlignmentX(CENTER_ALIGNMENT);
            quitButton.setAlignmentX(CENTER_ALIGNMENT);

            startButton.addActionListener(e ->
            {
                new GameSessionWindow(new PokerParty(betValue, bankValue));
                GameMenu.this.dispose();
            });

            optionsButton.addActionListener(e ->
            {
                new OptionsDialog(GameMenu.this, "Poker options...", true);
            });

            quitButton.addActionListener(e ->
            {
                dispose();
            });

            add(Box.createRigidArea(new Dimension(0, 140)));
            add(startButton);
            add(Box.createRigidArea(new Dimension(0, 40)));
            add(optionsButton);
            add(Box.createRigidArea(new Dimension(0, 40)));
            add(quitButton);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graph = (Graphics2D) g;

            graph.drawImage(backgroundImage, 0, 20, this.getWidth(), this.getHeight(), null);
        }

        class OptionsDialog extends JDialog
        {
            private final JButton confirm = new JButton("Confirm settings!");
            private final JLabel betText = new JLabel("Minimum bet: ");
            private final JTextField bet = new JTextField("50", 10);
            private final JLabel bankText = new JLabel("Player bank size: ");
            private final JTextField bank = new JTextField("1000", 7);
            private final JPanel betPanel = new JPanel(new FlowLayout());
            private final JPanel bankPanel = new JPanel(new FlowLayout());

            public OptionsDialog(Frame owner, String title, boolean modal)
            {
                super(owner, title, modal);

                setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

                dialogSettings();

                setPreferredSize(new Dimension(400, 300));
                setLocationRelativeTo(owner);

                add(Box.createRigidArea(new Dimension(350, 60)));
                add(betPanel);
                add(bankPanel);
                add(Box.createRigidArea(new Dimension(350, 20)));
                add(confirm);

                pack();

                try {
                    setIconImage(ImageIO.read(new File(getClass().getResource("../media/small-stack.png").getPath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setVisible(true);
                setResizable(false);
            }

            private void dialogSettings()
            {
                betPanel.add(betText);
                betPanel.add(bet);

                betPanel.setMaximumSize(new Dimension(350, 60));
                betPanel.setAlignmentX(CENTER_ALIGNMENT);

                bankPanel.add(bankText);
                bankPanel.add(bank);

                bankPanel.setMaximumSize(new Dimension(350, 60));
                bankPanel.setAlignmentX(CENTER_ALIGNMENT);

                confirm.setAlignmentX(CENTER_ALIGNMENT);
                confirm.addActionListener(e ->
                {
                    betValue = Integer.parseInt(bet.getText());
                    bankValue = Integer.parseInt(bank.getText());
                    dispose();
                });
            }
        }
    }
}
