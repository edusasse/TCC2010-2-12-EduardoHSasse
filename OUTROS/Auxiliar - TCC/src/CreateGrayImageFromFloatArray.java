import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import libsvm.svm_model;

public class CreateGrayImageFromFloatArray {
	svm_model model;

	public static void main(String[] args) {
		getImageArray();
		 
		System.out.println("<< fim >>");
	}
	
	static int width = 30;
	static int height = 30; 
	public static void createImage(String line, float[] ibx) {
		// Dimensions of the image.
		float[] imageData = new float[width * height]; // Image data array.
		int count = 0; // Auxiliary counter.

		for (int i = 0; i < ibx.length; i++)
			System.out.println("--> " + ibx[i]);
		for (int w = 0; w < width; w++)
			// Fill the array with a degradé pattern.
			for (int h = 0; h < height; h++) {
				imageData[count] = ibx[count];
				System.out.println(imageData[count]);
				count++;

			}

		// Create a DataBuffer from the values on the image array.
		javax.media.jai.DataBufferFloat dbuffer = new javax.media.jai.DataBufferFloat(
				imageData, width * height);
		// Create a float data sample model.
		SampleModel sampleModel = RasterFactory.createBandedSampleModel(
				DataBuffer.TYPE_FLOAT, width, height, 1);
		// Create a compatible ColorModel.
		ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
		// Create a WritableRaster.
		Raster raster = RasterFactory.createWritableRaster(sampleModel,
				dbuffer, new Point(0, 0));
		// Create a TiledImage using the float SampleModel.
		TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0,
				sampleModel, colorModel);
		// Set the data of the tiled image to be the raster.
		tiledImage.setData(raster);
		// Save the image on a file.
		JAI.create("filestore", tiledImage, "c:\\temp\\z\\" + line + ".tif", "TIFF");
	}

	public static float[] getImageArray() {
		float[] saida = new float[width * height];
		File file = new File("c:\\temp\\teste.txt");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			int line = 0;
			int i = 0;
			while (dis.available() != 0) {

				// this statement reads the line from the file and print it to
				// the console.
				String name = "";
				String l = dis.readLine();
				System.out.println(l);
				String[] buf = l.split(":");
				System.out.println(buf.length);
				for (int j = 0; j < buf.length; j++) {
					if (j == 0) {
						name = buf[j].startsWith("1") ? "N" : "P";
						j++;
					}
					saida[i] = Float.parseFloat(buf[j].split(" ")[0]);
					i++;
				}
				line++;
				createImage(name + "____" + line, saida);
				i = 0;
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < saida.length; i++)
			System.out.println(">>> " + saida[i]);
		return saida;
	}
}