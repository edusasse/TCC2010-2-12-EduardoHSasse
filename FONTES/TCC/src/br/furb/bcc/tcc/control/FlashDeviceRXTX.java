package br.furb.bcc.tcc.control;

import br.furb.bcc.tcc.rxtx.RXTXCom;
import gnu.io.CommPortIdentifier;
import java.util.Enumeration;

public class FlashDeviceRXTX {

    private static FlashDeviceRXTX sin = null;
    protected String[] portas;
    protected Enumeration listaDePortas;
    private static RXTXCom rxtxctrl = null;
    public static String LIGAR_LEDS_EIXO_EXTERNO = "O";
    public static String LIGAR_LEDS_EIXO_INTERNO = "X";
    public static String TESTAR_DISP_FLASH = "?";
    public static String DESLIGAR_LEDS = "F";
    private static String serialIniciada = "";

    private FlashDeviceRXTX() {
        listaDePortas = CommPortIdentifier.getPortIdentifiers();
    }

    public static FlashDeviceRXTX getInstance() {
        if (sin == null) {
            sin = new FlashDeviceRXTX();
        }
        // Inicia a porta escolhida
        if (Control.getSerialCom() != null) {
            if (!serialIniciada.equals(Control.getSerialCom())) {
                rxtxctrl = new RXTXCom(Control.getSerialCom(), 9600, 1000);
                rxtxctrl.setWriteMode();
                rxtxctrl.setSerialPortOpen();
                serialIniciada = Control.getSerialCom();

            }
        }
        return sin;
    }

    public String[] ObterPortas() {
        return portas;
    }

    public void ListarPortas() {
        int i = 0;
        portas = new String[10];
        listaDePortas = CommPortIdentifier.getPortIdentifiers();
        while (listaDePortas.hasMoreElements()) {
            CommPortIdentifier ips = (CommPortIdentifier) listaDePortas.nextElement();
            portas[i] = ips.getName();
            i++;
        }
    }

    public boolean PortaExiste(String COMp) {
        String temp;
        boolean e = false;
        listaDePortas = CommPortIdentifier.getPortIdentifiers();
        while (listaDePortas.hasMoreElements()) {
            CommPortIdentifier ips = (CommPortIdentifier) listaDePortas.nextElement();
            temp = ips.getName();
            if (temp.equals(COMp) == true) {
                e = true;
            }
        }
        return e;
    }

    /**
     * @param args
     */
    public String enviarComando(String cmd) {
        // Controle de tempo da leitura aberta na serial
       int d = rxtxctrl.sendData(cmd);
       return ""+d;
    }

    public static void main(String args[]) throws InterruptedException {
        Control.setSerialCom("COM15");
        //while (true) {
            
               FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.TESTAR_DISP_FLASH);
               FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.LIGAR_LEDS_EIXO_EXTERNO);
               System.exit(0);
          
    }
}
