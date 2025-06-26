import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.border.EmptyBorder;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean playerXTurn = true;
    private JLabel statusLabel;
    private Random random = new Random();
    private JButton restartButton;
    private LinkedList<String> recentResults = new LinkedList<>();
    private JLabel resultsLabel;
    private boolean gameOver = false;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(450, 550);
        setLayout(new BorderLayout(10, 10));

        statusLabel = new JLabel("Player X's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(statusLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 10, 10));
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        restartButton.addActionListener(e -> resetBoard());
        add(restartButton, BorderLayout.SOUTH);

        resultsLabel = new JLabel("<html>Recent Results:<br>None</html>", SwingConstants.CENTER);
        resultsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultsLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(resultsLabel, BorderLayout.WEST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        JButton clickedButton = (JButton) e.getSource();

        if (!clickedButton.getText().equals("")) {
            return;
        }

        clickedButton.setText("X");
        playSound("click.wav");

        if (checkForWinner("X")) return;

        statusLabel.setText("AI's turn");

        SwingUtilities.invokeLater(() -> {
            aiMove();
            if (!gameOver) {
                statusLabel.setText("Player X's turn");
            }
        });
    }

    private void aiMove() {
        if (gameOver) return;

        while (true) {
            int i = random.nextInt(3);
            int j = random.nextInt(3);
            if (buttons[i][j].getText().equals("")) {
                buttons[i][j].setText("O");
                playSound("click.wav");
                checkForWinner("O");
                break;
            }
        }
    }

    private boolean checkForWinner(String currentPlayer) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i][0], buttons[i][1], buttons[i][2], currentPlayer) ||
                checkLine(buttons[0][i], buttons[1][i], buttons[2][i], currentPlayer)) {
                return true;
            }
        }

        // Check diagonals
        if (checkLine(buttons[0][0], buttons[1][1], buttons[2][2], currentPlayer) ||
            checkLine(buttons[0][2], buttons[1][1], buttons[2][0], currentPlayer)) {
            return true;
        }

        // Check for draw
        boolean isDraw = true;
        for (int i = 0; i < 3 && isDraw; i++) {
            for (int j = 0; j < 3 && isDraw; j++) {
                if (buttons[i][j].getText().equals("")) {
                    isDraw = false;
                }
            }
        }

        if (isDraw) {
            JOptionPane.showMessageDialog(this, "It's a Draw!");
            addResult("Draw");
            gameOver = true;
            return true;
        }

        return false;
    }

    private boolean checkLine(JButton b1, JButton b2, JButton b3, String currentPlayer) {
        if (!b1.getText().equals("") &&
            b1.getText().equals(b2.getText()) &&
            b1.getText().equals(b3.getText())) {

            b1.setBackground(Color.GREEN);
            b2.setBackground(Color.GREEN);
            b3.setBackground(Color.GREEN);

            JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " wins!");
            addResult("Player " + currentPlayer + " wins");
            gameOver = true;
            return true;
        }
        return false;
    }

    private void addResult(String result) {
        if (recentResults.size() == 3) {
            recentResults.removeFirst();
        }
        recentResults.add(result);
        updateResultsLabel();
    }

    private void updateResultsLabel() {
        StringBuilder resultsText = new StringBuilder("<html>Recent Results:<br>");
        for (String result : recentResults) {
            resultsText.append(result).append("<br>");
        }
        resultsText.append("</html>");
        resultsLabel.setText(resultsText.toString());
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.WHITE);
            }
        }
        playerXTurn = true;
        gameOver = false;
        statusLabel.setText("Player X's turn");
    }

    private void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            if (!file.exists()) return;

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToe::new);
    }
}
