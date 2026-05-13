import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {

    public ButtonPanel(DrawPanel drawPanel){
        int x = 20;
        int y = 255;
        setLayout(new GridLayout(1,4));

        JButton rotateLeft = createButton("Left 45", x, y);
        JButton rotateRight = createButton("Right 45", x, y);
        JButton reset = createButton("Reset", x, y);
        JButton quiz = createButton("Quiz Mode", x, y);

        rotateLeft.addActionListener(e -> {
            drawPanel.rotateY(-45);
        });

        rotateRight.addActionListener(e -> {
            drawPanel.rotateY(45);
        });

        reset.addActionListener(e -> {
            drawPanel.resetView();
        });

        quiz.addActionListener(e -> {
            new QuizMode(drawPanel).start();
        });

        add(rotateLeft);
        add(rotateRight);
        add(reset);
        add(quiz);
    }

    public JButton createButton(String text, int background, int foreground) {
        JButton button = new JButton(text);
        button.setBackground(new Color(background, background, background));
        button.setForeground(new Color(foreground, foreground, foreground));
        button.setOpaque(true);
        return button;
    }
}
