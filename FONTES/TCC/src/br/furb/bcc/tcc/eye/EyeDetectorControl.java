package br.furb.bcc.tcc.eye;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.MaximumFinder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import br.furb.bcc.tcc.GUI.EyeSVM;
import br.furb.bcc.tcc.control.EyeMouse;

import br.furb.bcc.tcc.control.Control;
import java.awt.Toolkit;

public class EyeDetectorControl {

    private EyeDetector mfd = null;
    private Control con = null;
    private int cxy = 0;
    private int[][] xy;
    private int lastPosX = -1, lastPosY = -1;
    private boolean foundEyeLastFrame;
    final ImageCalculator ic = new ImageCalculator();
    final MaximumFinder mf = new MaximumFinder();

    /**
     * Construtor
     */
    public EyeDetectorControl() {
        this.mfd = new EyeDetector();
    }

    public void detect(Image iiImpar, Image iiPar) {
        final ImagePlus ipPar, ipImpar, ipSubtractedImg, ipNivel;
        int[][] eyecand;

        { // IMAGEM IMPAR - Efeito olhos vermelhos
            ipPar = new ImagePlus("par", iiPar);
            // Padrão utilizado
            IJ.run(ipPar, "8-bit", "");
            
            // Retira informacoes da imagem
            final Image imgPar = ipPar.getImage();
            this.mfd.setfHeight(imgPar.getHeight(null));
            this.mfd.setfWidth(imgPar.getWidth(null));
            if (Control.isSaveMode()) {
                IJ.saveAs(ipPar, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_par.tiff");
            }
            if (Control.isDebugMode()) {
                con.getFrame().setPanelImage(con.getFrame().REDEYE_PANEL, imgPar);
            }
            // subtraacao fundo
            IJ.run(ipPar, "Subtract Background...", "rolling=50 light disable");
        }

        { // IMAGEM IMPAR - Olho escuro
            ipImpar = new ImagePlus("impar", iiImpar);
            // Padrão utilizado
            IJ.run(ipImpar, "8-bit", "");
            
            // Retira informacoes da imagem
            final Image imgImpar = ipImpar.getImage();
            this.mfd.setfHeight(imgImpar.getHeight(null));
            this.mfd.setfWidth(imgImpar.getWidth(null));
            if (Control.isSaveMode()) {
                IJ.saveAs(ipImpar, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_impar.tiff");
            }
            if (Control.isDebugMode()) {
                con.getFrame().setPanelImage(con.getFrame().DARKEYE_PANEL, imgImpar);
            }

            // subtraacao fundo
            IJ.run(ipImpar, "Subtract Background...", "rolling=50 light disable");
        }


        { // PROCESSO DE SUBTRACÃO
            // Subtrai as imagens
            ipSubtractedImg = ic.run("Subtract create 32-bit", ipImpar, ipPar);
            IJ.run(ipSubtractedImg, "32-bit", "");
            IJ.run(ipSubtractedImg,"Erode","");
            if (Control.isDebugMode()) {
                this.con.getFrame().setPanelImage(con.getFrame().SUBTRACT_PANEL, ipSubtractedImg.getImage());
            }
            if (Control.isSaveMode()) {
                IJ.saveAs(ipImpar, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_subtracted.tiff");
            }
        }

        { // NIVELAMENTO POR HISTOGRAMA
            // Divide histograma de forma a resaltar as areas mais claras
            final int h = (int) Math.round((ipSubtractedImg.getProcessor().getMax() + ipSubtractedImg.getProcessor().getMin()) / 2);
            ipSubtractedImg.getProcessor().setMinAndMax(h - 1, h);
            ipNivel = new ImagePlus("nivel", ipSubtractedImg.getProcessor());
            if (Control.isDebugMode()) {
                this.con.getFrame().setPanelImage(con.getFrame().NIVEL_PANEL, ipNivel.getImage());
            }
            if (Control.isSaveMode()) {
                IJ.saveAs(ipNivel, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_nivel.tiff");
            }
        }


        { // BINARIZACAO            
            // Torna binario
            IJ.run(ipNivel, "8-bit", "");
            IJ.runPlugIn(ipNivel, "ij.plugin.Thresholder", "Make Binary");
            if (Control.isDebugMode()) {
                this.con.getFrame().setPanelImage(con.getFrame().BINARY_PANEL, ipNivel.getImage());
            }
            if (Control.isSaveMode()) {
                IJ.saveAs(ipNivel, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_binarizacao.tiff");
            }
        }


        { // Análise da região encontrada no quadro anterior
            if (lastPosX > 0 && lastPosY > 0) {
                double res = Double.MAX_VALUE;
                for (int g = 4; g < 7; g++) {
                    // IMAGEM IMPAR
                    ImagePlus ipIdLast = new ImagePlus("par", iiImpar);
                    // Retira informacoes da imagem
                    float ind = (float) Math.pow(0.833333f, g);
                    IJ.run(ipIdLast, "Size...", "width=" + (ipIdLast.getWidth() * ind) + " height=" + (ipIdLast.getHeight() * ind) + " constrain interpolation=Bilinear");

                    int x = Math.round(lastPosX * ind);
                    int y = Math.round(lastPosY * ind);

                    if (x < 15) {
                        x = x + (x - 15);
                    }
                    if (y < 15) {
                        y = y + (y - 15);
                    }

                    ipIdLast.setRoi(x - 15, y - 15, 30, 30);
                    IJ.run(ipIdLast, "Crop", "");
                    // Padrão utilizado
                    IJ.run(ipIdLast, "32-bit", "");
                    IJ.run(ipIdLast, "8-bit", "");
                    final double tres = mfd.classifyImage(ipIdLast.getImage());

                    if (tres < res) {
                        res = tres;
                    }

                }
                if (res < 0) {
                    foundEyeLastFrame = true;
                }
            }
        }


        { // ROI
            // Inverte as cores para procurar os maximos
            IJ.run(ipNivel, "Invert", "");
            // Procura os maximos
            IJ.run(ipNivel, "Find Maxima...", "noise=10 output=List exclude");
            // Obtem resultados
            ImagePlus ipROI = new ImagePlus("ROI", ipNivel.getImage());
            final ResultsTable rt = ResultsTable.getResultsTable();
            rt.show("Results");
            eyecand = new int[rt.getCounter()][2];
            xy = new int[rt.getCounter()][2];
            // Não executa o busca se o numero de resultados for maior que o maxiimo definido
            if (rt.getCounter() > Control.getMaxResults()) {
                Control.getFrame().setStatusMessage(rt.getCounter() + " resultados. Maximo permitido: " + Control.getMaxResults());
                return;
            }
            if (Control.isSaveMode()) {
                IJ.saveAs(ipROI, "Tiff", "c:\\temp\\" + System.currentTimeMillis() + "_roi.tiff");
            }

            // Para cada ponto encontrado é definida a bound box
            if (Control.isDebugMode()) {
                for (int i = 0; i < rt.getCounter(); i++) {
                    eyecand[i][0] = (int) rt.getValue(0, i);
                    eyecand[i][1] = (int) rt.getValue(1, i);
                    Image iROIs = desenhaRoi(ipROI.getImage(), Color.RED, eyecand[i][0] - 15, eyecand[i][1] - 15, 30, 30);
                    ipROI = new ImagePlus("ROIs", iROIs);
                    con.getFrame().setPanelImage(con.getFrame().ROI_PANEL, ipROI.getImage());
                }
            }
        }


        { // LOCALIZACAO DO OLHO
            for (int i = 0; i < eyecand.length; i++) {
                double res = Double.MAX_VALUE;
                for (int g = 4; g < 6; g++) {
                    // IMAGEM IMPAR
                    ImagePlus ipId = new ImagePlus("par", iiImpar);
                    // Reduz a escala da imagem, para uma escala proxima a da base de treinamento
                    float ind = (float) Math.pow(0.833333f, g);
                    IJ.run(ipId, "Size...", "width=" + (ipId.getWidth() * ind) + " height=" + (ipId.getHeight() * ind) + " constrain interpolation=Bilinear");
                    // Escala a posição
                    int x = Math.round(eyecand[i][0] * ind);
                    int y = Math.round(eyecand[i][1] * ind);
                    // Centraliza no caso da imagem passar da borda
                    if (x < 15) {
                        x = x + (x - 15);
                    }
                    if (y < 15) {
                        y = y + (y - 15);
                    }
                    // Define a região de interesse
                    ipId.setRoi(x - 15, y - 15, 30, 30);
                    // Retira a area definida com o comando anterior
                    IJ.run(ipId, "Crop", "");
                    // Padrão utilizado na base de treinamento
                    IJ.run(ipId, "32-bit", "");
                    IJ.run(ipId, "8-bit", "");
                    // repassa a imagem para a SVM
                    double tres = mfd.classifyImage(ipId.getImage());

                    if (tres < res) {
                        res = tres;
                    }
                    // Aramazena os resultados positivos
                    if (res < 0) {
                        xy[cxy][0] = eyecand[i][0];
                        xy[cxy][1] = eyecand[i][1];


                    }
                    if (Control.isDebugMode()) {
                        this.con.getFrame().setListImage(new EyeSVM(ipId.getImage(), (i + ": [x:" + x + ",y:" + y + "] - Result: " + res + " Itr.: " + g)), false);
                    }
                    if (res < 0) {
                        break;
                    }
                }
                cxy++;
            }
            // Busca pelo olho no menor eixo X
            int fx = Integer.MAX_VALUE, fy = 0;
            for (int j = 0; j < xy.length; j++) {
                if (xy[j][0] < fx && xy[j][0] > 0) {
                    fx = xy[j][0];
                    fy = xy[j][1];


                }

            }

            {
                // Caso a região na qual se encontrou um olho no quadro anterior,
                // não tenha sido marcada como ROI no momento posterior. E a
                // análise desta região reconheu o olho, é entendido um click.
                if (foundEyeLastFrame) {
                    // Verifica o quão distante a nova região esta da anterior
                    float pctx = 100f * ((float) (lastPosX - fx) / (float) fx);
                    if (pctx < 0) {
                        pctx *= -1;
                    }
                    if (pctx > Control.getPctClick() && Control.isEyeMouseMode()) {
                        Control.getFrame().setStatusMessage("Click simples!");
                        EyeMouse.getInstance().click();
                        // Emite beep
                        Toolkit.getDefaultToolkit().beep();
                        // Não efetua o movimento caso tenha ocorrido.
                        return;
                    }

                    // Zera contador
                    cxy = 0;
                }
            }

            // Caso as posições não tenham sido iniciadas
            if (lastPosX < 0 && lastPosY < 0) {
                lastPosX = fx;
                lastPosY = fy;
            }
            // Para desenho da região do olho
            Control.setXY(fx, fy);
            lastPosX = fx;
            lastPosY = fy;
            if (Control.isEyeMouseMode()) {
                EyeMouse.getInstance().setMouseXY(fx - lastPosX, fy - lastPosY);
               
            }
             cxy = 0;
        }


    }

    private Image desenhaRoi(Image img, Color c, int x, int y, int w, int h) {
        BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.drawImage(img, 0, 0, img.getWidth(null),
                img.getHeight(null), null);
        g.setColor(c);
        g.drawRect(x, y, w, h);

        return new ImageIcon(buffer).getImage();
    }
}
