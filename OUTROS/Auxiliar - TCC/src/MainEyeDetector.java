import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.MaximumFinder;

import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import libsvm.svm;
import libsvm.svm_model;

public class MainEyeDetector extends JFrame {
	static svm_model model;

	public static void main(String[] args) throws IOException {
		MySvmScale.loadParamters(new File("d:\\tcc\\svm\\eyes.paramters"));
		long svmFin = 0;
		try {
			long svmIni = new Date().getTime();
			model = svm.svm_load_model("d:\\tcc\\svm\\eyes.scaled.txt.model");
			System.out.println("nr_class -->" + model.nr_class);
			svmFin = new Date().getTime();
			System.out.println("SVM: " + (svmFin - svmIni) / 1000 + " (seg)");
			
			int[] start = new int[model.nr_class];
			for (int i = 1; i < model.nr_class; i++) {
				start[i] = start[i - 1] + model.nSV[i - 1];
				System.out.println("model.nSV[i - 1] -->" + model.nSV[i - 1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//new MainEyeDetector("d:\\eyeNew\\tst", 0);
		new MainEyeDetector("d:\\test\\positivo3\\",0);
		System.out.println("<< fim >>");
		long svmFinTot = new Date().getTime();
		System.out
				.println("Processamento: " + (svmFinTot - svmFin) + " (mili)");

	}

	// janela do programa
	public MainEyeDetector() {
		super("Carregar Imagem");
		// container onde serão adicionados todos componentes
		Container container = getContentPane();
		EyeDetector mfd = new EyeDetector(model);
		while (true) {
			// carrega a imagem passando o nome da mesma
			System.out.println("INI");
			JFileChooser jf = new JFileChooser("./");
			jf.showOpenDialog(null);
			if (jf.getSelectedFile() == null) {
				break;
			}
			ImageIcon face = new ImageIcon(jf.getSelectedFile()
					.getAbsolutePath());
			ImagePlus ip_even_image = new ImagePlus("", face.getImage());
			IJ.run(ip_even_image, "8-bit", "");
			Image img = ip_even_image.getImage();

			int altura = img.getHeight(null);
			int largura = img.getWidth(null);
			mfd.setfHeight(altura);
			mfd.setfWidth(largura);

			JFileChooser jf2 = new JFileChooser("./");
			jf2.showOpenDialog(null);
			ImageIcon face2 = new ImageIcon(jf2.getSelectedFile()
					.getAbsolutePath());
			ImagePlus ip_odd_image = new ImagePlus("", face2.getImage());
			IJ.run(ip_odd_image, "8-bit", "");
			Image i2 = ip_odd_image.getImage();

			// /

			final ImageCalculator ic = new ImageCalculator();
			ImagePlus ip3;

			// Inverte as imagens na horizontal
			IJ.run(ip_even_image, "Flip Horizontally", "");
			IJ.run(ip_odd_image, "Flip Horizontally", "");

			// Subtrai as imagens
			ip3 = ic.run("Subtract create 32-bit", ip_even_image, ip_odd_image);
			IJ.run(ip3, "8-bit", "");

			// Nivel do Histograma
			final int h = (int) Math.round(ip3.getProcessor().getMax() / 2);
			ip3.getProcessor().setMinAndMax(h - 1, h);

			// Inverte as corres
			IJ.run(ip3, "Invert", "");

			// Torna binario
			IJ.runPlugIn(ip3, "ij.plugin.Thresholder", "Make Binary");

			// Inverte as cores para procurar os maximos
			IJ.run(ip3, "Invert", "");

			// Procura os maximos
			final MaximumFinder mf = new MaximumFinder();
			IJ.run(ip3, "Find Maxima...", "noise=10 output=List exclude");

			final ResultsTable rt = ResultsTable.getResultsTable();
			rt.show("Results");
			int[][] eyecand = new int[rt.getCounter()][2];
			for (int i = 0; i < rt.getCounter(); i++) {
				eyecand[i][0] = (int) rt.getValue(0, i);
				eyecand[i][1] = (int) rt.getValue(1, i);
			}
			for (int i = 0; i < eyecand.length; i++) {
				System.out.println(eyecand[i][0] + "," + eyecand[i][1]);
				ip_odd_image.setTitle("1");
				float ind = 0.83333f;
				IJ.run(ip_odd_image, "Size...", "width="
						+ (ip_odd_image.getWidth() * ind) + " height="
						+ (ip_odd_image.getHeight() * ind)
						+ " constrain interpolation=Bilinear");

				mfd.classifyImage(ip_odd_image.getImage(), Math
						.round(eyecand[i][0] * ind), Math.round(eyecand[i][1]
						* ind));
			}

			System.out.println("FIM");
		}
	}

	// janela do programa
	public MainEyeDetector(String x) {
		super("Carregar Imagem");
		System.out.println("x");
		// container onde serão adicionados todos componentes
		Container container = getContentPane();
		EyeDetector mfd = new EyeDetector(model);
		while (true) {
			// carrega a imagem passando o nome da mesma
			System.out.println("INI");
			JFileChooser jf = new JFileChooser("./");
			jf.showOpenDialog(null);
			if (jf.getSelectedFile() == null) {
				break;
			}
			// ImageIcon face = new
			// ImageIcon(jf.getSelectedFile().getAbsolutePath());
			ImagePlus ip = new ImagePlus(jf.getSelectedFile().getAbsolutePath());
			IJ.run(ip, "32-bit", "");
			IJ.run(ip, "8-bit", "");
			Image img = ip.getImage();

			int altura = img.getHeight(null);
			int largura = img.getWidth(null);
			mfd.setfHeight(altura);
			mfd.setfWidth(largura);

			mfd.classifyImage(ip.getImage(), 15, 15);
		}

		System.out.println("FIM");
	}

	public MainEyeDetector(String path, int x) {
		EyeDetector mfd = new EyeDetector(model);
		int pos = 0, neg = 0;
		for (File f : new File(path).listFiles()) {

			ImagePlus ip = new ImagePlus(f.getAbsolutePath());
			//IJ.run(ip, "32-bit", "");
			//IJ.run(ip, "8-bit", "");
			Image img = ip.getImage();

			int altura = img.getHeight(null);
			int largura = img.getWidth(null);
			mfd.setfHeight(altura);
			mfd.setfWidth(largura);

			double res = mfd.classifyImage(ip.getImage(), 15, 15);
			if (res > 0){
				
				pos++;
			} else {
				neg++;
				
			}
			System.out.println("RESULTADO: " + res);
		}
		System.out.println("Positivo: " + pos);
		System.out.println("Negativo: " + neg);
	}
}
