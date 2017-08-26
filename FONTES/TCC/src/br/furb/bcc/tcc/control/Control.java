/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.furb.bcc.tcc.control;

import br.furb.bcc.tcc.GUI.JFrameTCC;
import br.furb.bcc.tcc.svm.MySvmScale;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

/**
 *
 * @author Suporte
 */
public class Control {
    private static svm_node nodes[];
    // Modelo de entrada SVM
    private static svm_model model = null;
    // Arquivo de entrada do modelo SVM
    private static File svmFile = null;
    // Arquivo com a escala para svm
    private static File svmParScaleFile = null;
    // Porta serial do dispositivo flash
    private static String serialCom = null;
    // Janela do programa
    private static JFrameTCC frame;
    // Janela do programa
    private static int x= 0, y= 0, maxResults = 6;
    // Pct click
    private static int pctClick = 35;
    // Debug mode
    private static boolean debugMode = false;
    private static boolean eyeMouseMode = false;
    // SCALE
    private static boolean withScale = false;
    // Eye
    private static int eyeAvgWidth = 13;
    private static int eyeAvgHeight = 13;
    private static int screenWidth, screenHeight;

    private static boolean saveMode = false;
    /**
     * Inicializa a apliicação
     * @param args Caminho do modelo SVM
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Nenhum modelo SVM foi informado!");
        }

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        
        svmFile = new File(args[0]);
        svmParScaleFile = new File(args[1]);



        frame = new JFrameTCC();
        // Carega o modelo SVM
        frame.setStatusMessage("Carregando o modelo SVM!");
        try {
            loadSvmModel();
        } catch (IOException ex) {
            frame.setStatusMessage("Erro ao carregar o modelo SVM!");
        }
        frame.setStatusMessage("Ok");
        
    }

    public static void setSaveMode(boolean saveMode) {
        Control.saveMode = saveMode;
    }

    public static boolean isWithScale() {
        return withScale;
    }

    public static void setWithScale(boolean withScale) {
        Control.withScale = withScale;
    }


    public static boolean isSaveMode() {
        return saveMode;
    }

    public static void setPctClick(int pctClick) {
        Control.pctClick = pctClick;
    }

    public static int getPctClick() {
        return pctClick;
    }


    
    public static void setDebugMode(boolean debugMode) {
      Control.debugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setEyeMouseMode(boolean eyeMouseMode) {
      Control.eyeMouseMode = debugMode;
    }

    public static boolean isEyeMouseMode() {
        return eyeMouseMode;
    }
    

    public static void setXY(int x,int y) {
        Control.x = x;
        Control.y = y;
    }

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static int getMaxResults() {
        return maxResults;
    }

    public static void setMaxResults(int maxResults) {
        Control.maxResults = maxResults;
    }

    public static int getEyeAvgHeight() {
        return eyeAvgHeight;
    }

    public static int getEyeAvgWidth() {
        return eyeAvgWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }


    /**
     * Carrega o modelo SVM
     * @param svmFile Arquivo do modelo SVM
     * @throws IOException
     */
    private static void loadSvmModel() throws IOException {
        try {
            model = svm.svm_load_model(svmFile.getAbsolutePath());
            MySvmScale.loadParamters(svmParScaleFile);
        } catch (IOException e) {
            throw e;
        }
    }

    public static svm_model getModel() {
        return model;
    }

    public static JFrameTCC getFrame() {
        return frame;
    }

    public static void setSerialCom(String serialCom) {
        Control.serialCom = serialCom;
    }

    public static String getSerialCom() {
        return serialCom;
    }

    


}
