import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    int cornerRadius;
    Color backgroundColor;

    public RoundedButton(String label, int radius, Color backgroundColor) {
        super(label);

        cornerRadius = radius;
        setBackground(backgroundColor);

        // Make the button transparent
        setContentAreaFilled(false);
        setFocusPainted(false);
        //setOpaque(false);

        setBorderPainted(false);

        // Modify the default font, if desired
        setFont(new Font("Arial", Font.BOLD, 16));
        setForeground(new Color(30,55,30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }

        // Draw a rounded rectangle in the background of the button
        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getBackground());
        // Draw the border of the button
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
    }
}