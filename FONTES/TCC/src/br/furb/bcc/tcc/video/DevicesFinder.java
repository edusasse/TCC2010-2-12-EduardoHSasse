package br.furb.bcc.tcc.video;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.format.VideoFormat;
import java.awt.*;
import javax.swing.JOptionPane;
import br.furb.bcc.tcc.GUI.JFrameTCC;
import javax.media.bean.playerbean.MediaPlayer;
import javax.media.control.FrameRateControl;

/* Responsavel pela captura do video e leitura dos frames no formato solicitado */
public class DevicesFinder implements BufferTransferHandler {

    protected int rMask = 0x000000FF;
    protected int gMask = 0x0000FF00;
    protected int bMask = 0x00FF0000;
    public static final Dimension d = new Dimension(320, 240);
    public static final Format[] supportedInputFormats = new Format[]{new VideoFormat(null,d,Format.NOT_SPECIFIED, Format.byteArray, 15.00015f),};
    public DevicesFinder() {
    }

    public PushBufferDataSource findDevices(JFrameTCC frame) {
        autoDetect();
        final String url = "vfw://0";

        if (url.indexOf(":") < 0) {
            System.exit(0);
        }

        MediaLocator ml;


        if ((ml = new MediaLocator(url)) == null) {
            System.err.println("N�o foi poss�vel contruir a m�dia pela URL: "
                    + url);
            System.exit(0);
        }
        
        PushBufferDataSource pbds = null;
        // Preferred capture format parameters
        CaptureDeviceInfo cdi = new CaptureDeviceInfo("vfw://0", ml, supportedInputFormats);

        frame.cdi = cdi;

        try {
            DataSource ds = Manager.createDataSource(ml);
            ds.connect();
            pbds = (PushBufferDataSource) ds;
            pbds.getStreams()[0].setTransferHandler(this);
            pbds.start();
            return pbds;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Não foi possivel conectar a webcam!", "Erro Webcam", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            return null;
        }
    }

    public void autoDetect() {
    }

    public void transferData(PushBufferStream pbs) {
    }
}
