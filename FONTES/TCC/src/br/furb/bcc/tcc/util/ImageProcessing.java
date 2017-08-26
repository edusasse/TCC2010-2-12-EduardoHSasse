package br.furb.bcc.tcc.util;

import java.awt.image.*;
import java.awt.*;
import javax.swing.ImageIcon;

/**
 * Classe auxiliar para processamento de imagens
 */
public class ImageProcessing {
	public ImageProcessing() {}

        /**
         * Realiza a extração para um array de inteiros
         * @param image
         * @param sX x inicial
         * @param sY y inicial
         * @param width largura
         * @param height altura
         * @param pixels array
         * @return
         */
        public static int[] extractPixels(Image image, int sX, int sY, int width,
			int height, int pixels[]) {
		PixelGrabber pg = new PixelGrabber(image, sX, sY, width, height,
				pixels, 0, width);
		try {
			pg.grabPixels(); // the PixelGrabber class grabs the pixels from the
								// image and puts them in the desired array.
		} catch (Exception ex) {
		}
		return pixels;
	}

        /**
         * Converte pixels para tons de cinza
         * @param pixels
         * @param grayPixels array
         * @return
         */
	public static int[] toGrayscale(int pixels[], int grayPixels[]) {
		float[] hsb = new float[3]; // array that will contain the
									// hew,saturation,and brightness values of
		float brightness; // the image pixels.
		int rgb, red, green, blue;
		for (int i = 0; i < pixels.length; i++) // extract the red,green,blue
		// bands of the image
		{ // and convert them to the hsb color model.
			red = (pixels[i] >> 16) & 0xFF;
			green = (pixels[i] >> 8) & 0xFF;
			blue = pixels[i] & 0xFF;
			Color.RGBtoHSB(red, green, blue, hsb);
			brightness = hsb[2];
			rgb = Color.HSBtoRGB(0, 0, brightness); // set the hew and the
													// saturation to zero to get
													// the representitive gray
													// color.
			grayPixels[i] = rgb & 0xFF; // the red = green = blue after the
										// conversion so we're storing one of
										// the bands.
		}
		return grayPixels;
	}

    /**
     * Converte um Image para um BufferedImage
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = false;

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

}
