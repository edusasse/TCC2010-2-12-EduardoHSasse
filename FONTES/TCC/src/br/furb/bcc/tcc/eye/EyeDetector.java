package br.furb.bcc.tcc.eye;

import br.furb.bcc.tcc.control.Control;
import br.furb.bcc.tcc.svm.MySvmScale;
import java.awt.Image;


import br.furb.bcc.tcc.util.ImageProcessing;
import java.io.IOException;
import libsvm.svm;
import libsvm.svm_node;

public class EyeDetector {

    private int fWidth = 30, fHeight = 30;
    public int[] grayPixels, pixels;
    int faces;

    /**
     * Construtor
     * @param model
     */
    public EyeDetector() {
        pixels = new int[fWidth * fHeight];
        grayPixels = new int[fWidth * fHeight];

    }

    public void setfWidth(int fWidth) {
        this.fWidth = fWidth;
    }

    public void setfHeight(int fHeight) {
        this.fHeight = fHeight;
    }

    /**
     *
     * @param eyeImg
     * @param x
     * @param y
     * @return
     */
    public double classifyImage(Image eyeImg) {
        pixels = new int[fWidth * fHeight];
        grayPixels = new int[fWidth * fHeight];
        // transfere os pixels para uma array unidimensional
        pixels = ImageProcessing.extractPixels(eyeImg, 0, 0, fWidth, fHeight, pixels);
        // faz a conversão para grayscale e transfere os pixels para uma array unidimensional
        grayPixels = ImageProcessing.toGrayscale(pixels, grayPixels);
        // Carrega o array template que sera utilizado no processo de classificação
        svm_node[] template = createSVMNodeTemplate();
        // Prepara entrada para o algoritmo que escala os dados
        if (Control.isWithScale()) {
            String toScale = "0 ";
            for (int i = 0; i < 900; i++) {
                toScale += ((i + 1) + ":" + template[i].value + " ");
            }
            try {
                template = MySvmScale.scale(toScale);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Realiza o processo de classificação
        double result = classify(template);

        return result;
    }

    /**
     *
     * @param xCenter
     * @param yCenter
     * @return
     */
    private svm_node[] createSVMNodeTemplate() {
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
        return template;
    }

    /**
     *
     * @param template
     * @return
     */
    private double classify(svm_node[] template) {
        double cResults;
        double[] decValues = new double[1];

        svm.svm_predict_values(Control.getModel(), template, decValues);
        cResults = decValues[0] * Control.getModel().label[0];

        return cResults;
    }
}
