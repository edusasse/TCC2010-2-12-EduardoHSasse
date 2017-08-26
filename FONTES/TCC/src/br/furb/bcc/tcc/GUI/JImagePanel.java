/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.furb.bcc.tcc.GUI;

import ij.IJ;
import ij.ImagePlus;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A panel that contains a background image. The background image is
 * automatically sized to fit in the panel.
 */
public class JImagePanel extends JPanel {

    private BufferedImage image = null;
    private FillType fillType = FillType.RESIZE;

    /**
     * Creates a new panel with the given background image.
     *
     * @param img The background image.
     */
    public JImagePanel(BufferedImage img) {
        setImage(img);
    }

    /**
     * Creates a new panel with the given background image.
     *
     * @param img The background image.
     * @throws IOException, if the image file is not found.
     */
    /**
     * Creates a new panel with the given background image.
     *
     * @param img The background image.
     * @throws IOException, if the image file is not found.
     */
    /**
     * Changes the image panel image.
     *
     * @param img The new image to set.
     */
    public final void setImage(BufferedImage img) {
        if (img == null) {
            return;
        }

        this.image = img;
        invalidate();
        validate();
        repaint();
    }

    /**
     * Returns the image associated with this image panel.
     *
     * @return The associated image.
     */
    public BufferedImage getImage() {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        fillType.drawImage(this, g2d, image);
        g2d.dispose();
    }

    /**
     * Returns the way this image fills itself.
     *
     * @return The fill type.
     */
    public FillType getFillType() {
        return fillType;
    }

    /**
     * Changes the fill type.
     *
     * @param fillType The new fill type
     * @throws IllegalArgumentException If the fill type is null.
     */
    public void setFillType(FillType fillType) {
        if (fillType == null) {
            throw new IllegalArgumentException("Invalid fill type!");
        }

        this.fillType = fillType;
        invalidate();
    }

    public static enum FillType {

        /**
         * Make the image size equal to the panel size, by resizing it.
         */
        RESIZE {

            @Override
            public void drawImage(JPanel panel, Graphics2D g2d,
                    BufferedImage image) {
                if (image == null)
                    return;
                ImagePlus imp = new ImagePlus("", image);
                IJ.run(imp, "Size...", "width=" + panel.getWidth() + " height=" + panel.getHeight() + " constrain interpolation=Bilinear");
                g2d.drawImage(imp.getImage(), 0, 0, panel.getWidth(), panel.getHeight(), null);
            }
        },
        /**
         * Centers the image on the panel.
         */
        CENTER {

            @Override
            public void drawImage(JPanel panel, Graphics2D g2d,
                    BufferedImage image) {
                int left = (panel.getWidth() - image.getWidth()) / 2;
                int top = (panel.getHeight() - image.getHeight()) / 2;
                g2d.drawImage(image, left, top, null);
            }
        },
        /**
         * Makes several copies of the image in the panel, putting them side by
         * side.
         */
        SIDE_BY_SIDE {

            @Override
            public void drawImage(JPanel panel, Graphics2D g2d,
                    BufferedImage image) {
                 if (image == null)
                    return;
                Paint p = new TexturePaint(image, new Rectangle2D.Float(0, 0,
                        image.getWidth(), image.getHeight()));
                g2d.setPaint(p);
                g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            }
        };

        public abstract void drawImage(JPanel panel, Graphics2D g2d,
                BufferedImage image);
    }
}
