/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.furb.bcc.tcc.video;

import br.furb.bcc.tcc.control.Control;
import br.furb.bcc.tcc.eye.EyeDetectorControl;
import br.furb.bcc.tcc.control.FlashDeviceRXTX;
import java.awt.Image;

/**
 *
 * @author Suporte
 */
public class FrameControl extends Thread {

    private boolean ativo = true;
    private Image cframe = null;
    private Image iiPar;
    public static final int WAIT_FRAME_TIME = 175;
    private EyeDetectorControl edc = null;

    public FrameControl() {
        edc = new EyeDetectorControl();
    }

    @Override
    public void run() {
        while (ativo) {
            // Se o modo de deteccao estiver ativo
            if (Control.getFrame().isDetectionEnable()) {
                // Caso a imagem par seja nula
                if (iiPar == null) {
                    // Verifica se ha algum problema com a imagem
                    if (getCframe() == null) {
                        try {
                            sleep(20);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }
                        continue;
                    }
                    // Armazena quadro atual
                    iiPar = getCframe();
                    // Liga LEDs do eixo Externo
                    FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.LIGAR_LEDS_EIXO_EXTERNO);
                    try {
                        sleep(WAIT_FRAME_TIME);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.getMessage());
                    }
                    // Limpa frame atual
                    setCframe(null);
                } else {
                    // Verifica se ha algum problema com a imagem
                    if (getCframe() == null) {
                        try {
                            sleep(20);
                        } catch (InterruptedException ex) {
                            System.err.println(ex.getMessage());
                        }
                        continue;
                    }
                    // Procedimento de detecção
                    edc.detect(getCframe(), iiPar);
                    // Liga LEDs do eixo Interno
                    FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.LIGAR_LEDS_EIXO_INTERNO);
                    try {
                        Thread.sleep(WAIT_FRAME_TIME);
                    } catch (InterruptedException ex) {
                        System.err.println(ex.getMessage());
                    }
                    // Limpa variavel que armazena o quadro par
                    iiPar = null;
                    setCframe(null);
                }
            }

        }
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setCframe(Image cframe) {
        this.cframe = cframe;
    }

    public Image getCframe() {
        return cframe;
    }
}
