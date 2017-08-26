import ij.IJ;
import ij.ImagePlus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import libsvm.svm;
import libsvm.svm_model;

public class MainExtratorParaSVM {
	private static boolean debug = false;
	private static svm_model model;
	private static EyeDetectorParaSVM mfd;

	public static void main(String[] args) {
		try {

			model = svm.svm_load_model("./ModelVisage.txt");
			int[] start = new int[model.nr_class];
			for (int i = 1; i < model.nr_class; i++) {
				start[i] = start[i - 1] + model.nSV[i - 1];
			}
			mfd = new EyeDetectorParaSVM(model);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Iniciando extracao de positivos:");
		final String pathDest = "d:\\test\\positivo3\\";
		transformPositivo(pathDest);
		System.out.println("Finalizada extracao de positivos.\n");

		System.out.println("Iniciando extracao de negativos:");
		final String pathDestNeg = "d:\\test\\eyeNeg\\";
		//transformNegatives(705,pathDestNeg);
		System.out.println("Finalizada extracao de negativos.\n");

	}

	public static void transformPositivo(String pathDest) {
		int count = 0;
		int count2 = 0;
		for (File f : new File("D:/test/").listFiles()) {
			final ImagePlus ip = IJ.openImage(f.getAbsolutePath());
			IJ.run(ip, "Size...", "width=" + (ip.getWidth() * 0.35f) + " height=" + (ip.getHeight() * 0.35f) + " constrain interpolation=Bilinear");
			final boolean res = crop(ip, pathDest, f.getName());
			if (res)
				count++;
			count2++;

		}
		System.out.println("Positivos: Total " + count2
				+ " - Onde Encontrou Faces: " + count + " DIF: "
				+ (count2 - count));
	}

	public static boolean crop(ImagePlus face, String pathDest, String name) {
		// Metodo responsável pela detecção da face e posição aproximada da
		// pupila
		mfd.detectAllFaces(face.getImage());
		// Contem a posição obtida da pupila
		final int[] facexy = mfd.getFace();
		if (facexy == null)
			return false;

		// Imagens para recorte da imagem do olho
		ImagePlus ipREye = new ImagePlus("Right Eye_", face.getImage());
		IJ.run(ipREye, "8-bit", "");

		
		ImagePlus ipLEye = new ImagePlus("Left Eye", face.getImage());		 
		IJ.run(ipLEye, "8-bit", "");

		if (debug) {
			ipREye = desenhaRoi(ipREye.getImage(), Color.RED, facexy[0] - 2,
					facexy[1] - 2, 4, 4);
			ipLEye = desenhaRoi(ipLEye.getImage(), Color.RED, facexy[2] - 2,
					facexy[3] - 2, 4, 4);
		}

		// Marca a ROI na imagem do olho direito. Tendo como centro a pupila do
		// olho		
		ipREye.setRoi(facexy[0] - 15, facexy[1] - 15, 30, 30);
		// Marca a ROI na imagem do olho esquero. Tendo como centro a pupila do
		// olho
		ipLEye.setRoi(facexy[2] - 15, facexy[3] - 15, 30, 30);

		// Função Crop do ImageJ recorta a ROI
		IJ.run(ipREye, "Crop", "");
		IJ.run(ipREye, "8-bit", "");
		IJ.run(ipLEye, "Crop", "");
		IJ.run(ipREye, "8-bit", "");

		// Salva a imagem obtida
		if (ipREye.getWidth() == 30 && ipREye.getHeight() == 30) {
			IJ.saveAs(ipREye, "Tiff", pathDest + "dirEye_" + name);
		}
		if (ipLEye.getWidth() == 30 && ipLEye.getHeight() == 30) {			 
			IJ.saveAs(ipLEye, "Tiff", pathDest + "esqEye_" + name);
		}
		return true;
	}

	private static ImagePlus desenhaRoi(Image img, Color c, int x, int y,
			int w, int h) {
		BufferedImage buffer = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.createGraphics();
		g.drawImage(img, 0, 0, img.getWidth(null), img.getHeight(null), null);
		g.setColor(c);
		g.drawRect(x, y, w, h);

		return new ImagePlus("", new ImageIcon(buffer).getImage());
	}

	public static void transformNegatives(int maxCount, String path) {
		int count = 0;
		for (File f : new File("D:/test/").listFiles()) {
			ImagePlus ip = IJ.openImage(f.getAbsolutePath());
			IJ.run(ip, "Size...", "width=" + (ip.getWidth() * 0.3f) + " height=" + (ip.getHeight() * 0.3f) + " constrain interpolation=Bilinear");
			ImagePlus ip1 = new ImagePlus("1", ip.getImage());
			ip1.setRoi(0, 0, 30, 30);
			
			ImagePlus ip2 = new ImagePlus("1", ip.getImage());
			ip2.setRoi(30, 0, 30, 30);
			
			ImagePlus ip3 = new ImagePlus("1", ip.getImage());
			ip3.setRoi(55, 109, 30, 30);
			
			ImagePlus ip4 = new ImagePlus("1", ip.getImage());
			ip4.setRoi(60, 0, 30, 30);
			
			ImagePlus ip5 = new ImagePlus("1", ip.getImage());
			ip5.setRoi(49, 67, 30, 30);
			
			ImagePlus ip6 = new ImagePlus("1", ip.getImage());
			ip6.setRoi(20, 60, 30, 30);
			
			IJ.run(ip1, "Crop", "");
			IJ.run(ip1, "8-bit", "");
			
			IJ.run(ip2, "Crop", "");
			IJ.run(ip2, "8-bit", "");
			
			IJ.run(ip3, "Crop", "");
			IJ.run(ip3, "8-bit", "");
			
			IJ.run(ip4, "Crop", "");
			IJ.run(ip4, "8-bit", "");
			
			IJ.run(ip5, "Crop", "");
			IJ.run(ip5, "8-bit", "");
			
			IJ.run(ip6, "Crop", "");
			IJ.run(ip6, "8-bit", "");

			IJ.saveAs(ip1, "Tiff", path + "0x0x30x30_"+ f.getName());
			++count ;
			if (count >= maxCount)
				return;
			
			IJ.saveAs(ip2, "Tiff", path + "30x0x30x30_"+ f.getName());
			++count ;
			if (count >= maxCount)
				return;
			
			IJ.saveAs(ip3, "Tiff", path + "0x60x30x30_"+ f.getName());
			++count ;
			if (count >= maxCount)
				return;
			
			IJ.saveAs(ip4, "Tiff", path + "60x0x30x30_"+ f.getName());
			++count ;
			if (count >= maxCount)
				return;
			
			IJ.saveAs(ip5, "Tiff", path + "49x67x30x30_"+ f.getName());
			++count ;
			if (count >= maxCount)
				return;
			
			IJ.saveAs(ip6, "Tiff", path + "20x60x30x30_"+ f.getName());			 
			++count ;
			if (count >= maxCount)
				return;

		}
	}
 
}
