/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.furb.bcc.tcc.GUI;

import java.awt.Image;

/**
 *
 * @author Eduardo
 */
public class EyeSVM {
    private Image img;
    private String msg;

    public EyeSVM(Image img, String msg) {
        this.img = img;
        this.msg = msg;
    }

    
    /**
     * @return the img
     */
    public Image getImg() {
        return img;
    }

    /**
     * @param img the img to set
     */
    public void setImg(Image img) {
        this.img = img;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    
}
