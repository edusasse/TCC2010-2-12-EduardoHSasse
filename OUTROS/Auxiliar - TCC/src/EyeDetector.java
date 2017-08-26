 

import java.awt.Image;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import util.ImageProcessing;

public class EyeDetector {

    private int fWidth = 30, fHeight = 30;

    public int[] grayPixels, pixels;
    int faces;
    svm_model model;
    svm_node nodes[];

    public EyeDetector(svm_model model) {
        pixels = new int[fWidth * fHeight];
        grayPixels = new int[fWidth * fHeight];
        this.model = model;
       
    }

    public void setfWidth(int fWidth) {
        this.fWidth = fWidth;
    }

    public void setfHeight(int fHeight) {
        this.fHeight = fHeight;
    }

    public double classifyImage(Image eyeImg, int x, int y) {


        pixels = new int[fWidth * fHeight];
        grayPixels = new int[fWidth * fHeight];

        // Initializing
        pixels = ImageProcessing.extractPixels(eyeImg, 0, 0, fWidth, fHeight, pixels);
        grayPixels = ImageProcessing.toGrayscale(pixels, grayPixels);

        // Load templates to the array in order to classify them
        svm_node[] template = createSVMNodeTemplate(15, 15);
        String toScale =  "0 ";
        for (int i =0; i < 900; i++){
        	toScale +=((i+1)+":"+template[i].value+" ");
        }
        
        svm_node[] template2 = null;
        try {
			 template2 = MySvmScale.scale(toScale);
		} catch (IOException e) {
			e.printStackTrace();
		}
		         
        // Classiffying templates
        double result = classify(template);
        //System.out.println("Resultado 1: " + result);
        
        //double result2 = classify(template2);
        //System.out.println("Resultado 2: " + result2);
        return result;

    }

    // ////////////////////////////////////////
    private svm_node[] createSVMNodeTemplate(int xCenter, int yCenter) {

        svm_node node;
        svm_node template[] = new svm_node[900];

        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 30; x++) {
                node = new svm_node();
                node.index = y * 30 + x + 1;
                node.value = grayPixels[y * fWidth + x] / 255d;
                template[y * 30 + x] = node;

            }
        }

        int count = 1;
        for (svm_node s : template) {
            System.out.print(s.index + ":" + s.value + " ");
            count++;
        }
        System.out.print("\n");
        return template;
    }
    // ///////////////////////////////////////////

    private double classify(svm_node[] template) {
        double cResults;
        double[] decValues = new double[1];

        svm.svm_predict_values(model, template, decValues);
        cResults = decValues[0] * model.label[0];

        return cResults;
    }
}
