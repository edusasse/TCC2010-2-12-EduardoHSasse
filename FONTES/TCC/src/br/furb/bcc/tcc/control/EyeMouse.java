/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.furb.bcc.tcc.control;

import br.furb.bcc.tcc.control.Control;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Suporte
 */
public class EyeMouse extends Thread {
    private static EyeMouse em = null;
    private Robot r;
    private int xIni = 0, yIni = 0, x = 0, y = 0;

    public void setMouseXY(int x, int y) {
        this.x = (x * (Control.getScreenWidth()/Control.getEyeAvgWidth())) + xIni;
        this.y = (y * (Control.getScreenHeight()/Control.getEyeAvgHeight())) + yIni;
        if (this.x < 0 || this.y < 0) {
            xIni = MouseInfo.getPointerInfo().getLocation().x;
            yIni = MouseInfo.getPointerInfo().getLocation().y;
            this.x = xIni;
            this.y = yIni;
        }
    }

    /**
     * @param args
     * @throws AWTException
     */
    public EyeMouse() throws AWTException {
        r = new Robot();
        // Posicao atual
        xIni = MouseInfo.getPointerInfo().getLocation().x;
        yIni = MouseInfo.getPointerInfo().getLocation().y;

         
    }

    public static EyeMouse getInstance() {
        if (em == null) {
            try {
                em = new EyeMouse();
                em.start();
            } catch (AWTException ex) {
                Logger.getLogger(EyeMouse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return em;
    }

    public void click(){
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void run() {
        while (true) {
            int xN = 0, yN = 0;
            for (float t = 0.1f; t < 1; t += 0.1f) {
                // Equação paramétrica da reta
                xN = Math.round(xIni + (x - xIni) * t);
                yN = Math.round(yIni + (y - yIni) * t);
                // Se a posição for diferente da anterior
                if (xN != xIni || yN != yIni) {
                    r.mouseMove(xN, yN);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            xIni = xN;
            yIni = yN;
        }

    }
}
