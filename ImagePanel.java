import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class ImagePanel extends JPanel {
    private Image image;
    private int radius = 7;


    public ImagePanel(Image image, int radius) {
        this.radius = radius;
        this.image = image;
    }

    public ImagePanel(Image image) {
        this.image = image;
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int width = getWidth();
        int height = getHeight();

        // Create a rounded rectangle
        Shape roundedRectangle = new RoundRectangle2D.Float(0, 0, width, height, radius, radius);

        // Clip the image to the rounded rectangle
        g2d.setClip(roundedRectangle);
        g2d.drawImage(image, 0, 0, this);
    }
}
