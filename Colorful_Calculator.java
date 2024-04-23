import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Colorful_Calculator extends JFrame implements ActionListener {
    private JTextField displayField;
    private String currentInput;

    public Colorful_Calculator() {
        super("Colorful Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 550); 

        // Display field
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setBackground(Color.WHITE);
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        add(displayField, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        buttonPanel.setBackground(Color.BLACK);
        add(buttonPanel, BorderLayout.CENTER);

        // Button labels and colors
        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "C", "0", ".", "+",
            "√", "<-", "=","CE"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        // Set initial state
        currentInput = "";
        displayField.setText("0");
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "C":
                currentInput = "";
                displayField.setText("0");
                break;
            case "CE":
            currentInput = "";
            displayField.setText("");
                break;
            case "<-":
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    if (currentInput.isEmpty())
                        displayField.setText("0");
                    else
                        displayField.setText(currentInput);
                }
                break;
            case "=":
                try {
                    double result = evaluateExpression(currentInput);
                    displayField.setText(String.format("%.2f", result));
                    currentInput = String.valueOf(result);
                } catch (ArithmeticException ex) {
                    displayField.setText("Error");
                    currentInput = "";
                }
                break;
            default:
                currentInput += command;
                displayField.setText(currentInput);
                break;
        }
    }

    private double evaluateExpression(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean isDigitChar(int c) {
                return c >= '0' && c <= '9';
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (ch == '+') {
                        nextChar();
                        x += parseTerm();
                    } else if (ch == '-') {
                        nextChar();
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (ch == '*') {
                        nextChar();
                        x *= parseFactor();
                    } else if (ch == '/') {
                        nextChar();
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("Division by zero");
                        x /= divisor;
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (ch == '(') {
                    nextChar();
                    double x = parseExpression();
                    if (ch != ')') throw new RuntimeException("Expected ')'");
                    nextChar();
                    return x;
                }
                if (ch == '-') {
                    nextChar();
                    return -parseFactor();
                }
                double x = 0;
                while (Character.isDigit(ch) || ch == '.') {
                    x = x * 10 + (ch - '0');
                    nextChar();
                }
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Colorful_Calculator::new);
    }
}
