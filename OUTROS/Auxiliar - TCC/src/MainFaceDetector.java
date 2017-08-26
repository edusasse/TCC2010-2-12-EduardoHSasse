

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import libsvm.svm;
import libsvm.svm_model;

public class MainFaceDetector extends JFrame {
	static svm_model model;

	public static void main(String[] args) {
		//getImageArray();
		try {
		    //model = svm.svm_load_model("eye.txt");
		    model = svm.svm_load_model("c:\\temp\\model.txt");
			System.out.println("nr_class -->" + model.nr_class);
			
			int[] start = new int[model.nr_class];
			for (int i = 1; i < model.nr_class; i++){
				start[i] = start[i - 1] + model.nSV[i - 1];
				System.out.println("model.nSV[i - 1] -->" + model.nSV[i - 1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//transform();
		//transformNegatives();
		new MainFaceDetector();
		System.out.println("<< fim >>");
	}
	
	// janela do programa
	public MainFaceDetector() {
		super("Carregar Imagem");
		
		// container onde serão adicionados todos componentes
		Container container = getContentPane();
		//EyeDetector mfd = new EyeDetector(model);
		FaceDetector mfd = new FaceDetector(model);
		// carrega a imagem passando o nome da mesma
		
		ImageIcon face = new ImageIcon("face2.jpg");
		
		//x.createImage(1111, mfd.grayPixels);
		int altura = face.getImage().getHeight(null);
		int largura = face.getImage().getWidth(null);
		//mfd.setfHeight(altura);
		//mfd.setfWidth(largura);

		mfd.detectAllFaces(face.getImage());
				
		// adiciona a imagem em um label
		JLabel label = new JLabel(new ImageIcon(mfd.getRetimg()));
		// adiciona a altura e largura em outro label
		JLabel label2 = new JLabel("Altura: " + altura + "      Largura: "	+ largura);

		// cria o JPanel para adicionar os labels
		JPanel panel = new JPanel();
		panel.add(label, BorderLayout.NORTH);
		panel.add(label2, BorderLayout.SOUTH);

		// adiciona o panel no container
		container.add(panel, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
 

}
