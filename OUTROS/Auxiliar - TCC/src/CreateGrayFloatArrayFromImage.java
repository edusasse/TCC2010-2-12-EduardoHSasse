import ij.IJ;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import util.ImageProcessing;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

public class CreateGrayFloatArrayFromImage {
	static StringBuilder sb = new StringBuilder();

	public static void main(String[] args) {
		System.out.println("Iniciando.");
		
		String pos = getDir("Diretorio Imagens Positivas");
		System.out.println(pos);
		int cp = getPositiveFaces(pos);
		
		//String neg = getDir("Diretorio Imagens Negativas");
		//System.out.println(neg);		 
		//int cn = getNegativeFaces(neg);
		
		System.out.println("Salvar arquivo de entrada para SVM");
		String sv = getSave();
		System.out.println(sv);		 
		
		Gravar(sv, sb);
		System.out.println("Positivos: " + cp);
		//System.out.println("Negativos: " + cn);
		//System.out.println("Total....: " + (cp + cn));
	}

	public static String getDir(String dir) {

		JFileChooser chooser = new JFileChooser(); 
		chooser.setDialogTitle(dir);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	
		chooser.setAcceptAllFileFilterUsed(false);
		//    
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else {
			System.out.println("Sem seleção");
			System.exit(-1);
		}
		return null;
	}
	
	public static String getSave() {

		JFileChooser salvar = new JFileChooser(); 
		int ok = salvar.showSaveDialog(null);
		if (ok == JFileChooser.APPROVE_OPTION){
			return salvar.getSelectedFile().getAbsolutePath();
		}
		return null;
	 
	}

	public static int getPositiveFaces(String path) {
		int count = 0;
		for (File f : new File(path).listFiles()) {
			 
			try {
				ImagePlus imp = IJ.openImage(f.getAbsolutePath());
				IJ.run(imp, "8-bit", "");
				// Caso a imegem não seja de 30x30
				if (imp.getImage().getWidth(null) != 30
						|| imp.getImage().getHeight(null) != 30)
					continue;

				int[] grayPixels = null, pixels = null;
				pixels = new int[imp.getImage().getWidth(null)
						* imp.getImage().getHeight(null)];
				grayPixels = new int[imp.getImage().getWidth(null)
						* imp.getImage().getHeight(null)];

				// Extração dos pixels da imagem
				pixels = ImageProcessing.extractPixels(imp.getImage(), 0, 0,
						imp.getImage().getWidth(null), imp.getImage()
								.getHeight(null), pixels);
				// Converte pixels para tons de cinza
				grayPixels = ImageProcessing.toGrayscale(pixels, grayPixels);

				writePixels(true, grayPixels, imp.getImage().getWidth(null));
				count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public static int getNegativeFaces(String path) {
		int count = 0;
		for (File f : new File(path).listFiles()) {
			try {
				ImagePlus imp = IJ.openImage(f.getAbsolutePath());

				// Caso a imegem não seja de 30x30
				if (imp.getImage().getWidth(null) != 30
						|| imp.getImage().getHeight(null) != 30)
					continue;

				int[] grayPixels = null, pixels = null;
				pixels = new int[imp.getImage().getWidth(null)
						* imp.getImage().getHeight(null)];
				grayPixels = new int[imp.getImage().getWidth(null)
						* imp.getImage().getHeight(null)];

				// Extração dos pixels da imagem
				pixels = ImageProcessing.extractPixels(imp.getImage(), 0, 0,
						imp.getImage().getWidth(null), imp.getImage()
								.getHeight(null), pixels);
				// Converte pixels para tons de cinza
				grayPixels = ImageProcessing.toGrayscale(pixels, grayPixels);

				writePixels(false, grayPixels, imp.getImage().getWidth(null));
				count++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public static void Gravar(String local, StringBuilder sb) {

		try {
			// o true significa q o arquivo será constante
			FileWriter x = new FileWriter(local, true);

			x.write(sb.toString()); // armazena o texto no objeto x, que aponta
			// para o arquivo
			x.close(); // cria o arquivo
			JOptionPane.showMessageDialog(null, "Arquivo gravado com sucesso",
					"Concluído", JOptionPane.INFORMATION_MESSAGE);
		}
		// em caso de erro apreenta mensagem abaixo
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Atenção",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public static final void appendImage(boolean isPos, File file)
			throws Exception {
		SeekableStream s = new FileSeekableStream(file);
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, null);
		for (int k = 0; k < dec.getNumPages(); ++k) {
			RenderedImage ri = dec.decodeAsRenderedImage(k);
			Raster ra = ri.getData();
			BufferedImage bi = new BufferedImage(ri.getColorModel(), Raster
					.createWritableRaster(ri.getSampleModel(), ra
							.getDataBuffer(), null), false, new Hashtable());

			sb.append(isPos ? "-1" : "1");
			for (int i = 0; i < ra.getDataBuffer().getSize(); i++)
				sb.append(" " + (i + 1) + ":"
						+ ra.getDataBuffer().getElemFloat(i));
			sb.append("\n");
		}
	}

	private static void writePixels(boolean isPos, int[] grayPixels, int fWidth) {
		// Imagem positiva ou negativa
		sb.append(isPos ? "-1" : "1");
		for (int y = 0; y < 30; y++) {
			for (int x = 0; x < 30; x++) {
				sb.append(" " + ((y * fWidth + x) + 1) + ":"
						+ (grayPixels[y * fWidth + x] / 255d));
			}
		}
		sb.append(" \n");
	}
}