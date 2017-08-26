package br.furb.bcc.tcc.video;

import br.furb.bcc.tcc.control.Control;
import java.awt.Dimension;
import java.awt.Image;

import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

import br.furb.bcc.tcc.control.FlashDeviceRXTX;
import ij.ImagePlus;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.media.util.ImageToBuffer;
import javax.swing.ImageIcon;

/* Esta classe recebe o stream e dispara os processamentos de detecção */
public class ProcessEffect implements Effect {

    private ImageToBuffer itb;
    private Format[] formats = DevicesFinder.supportedInputFormats;
    private byte outData[];
    private BufferToImage bti;
    private Image image = null, iiPar = null;
    //
    private Format inputFormat;
    private Format outputFormat;
    private FrameControl frameControl;

    /**
     * 
     * @param frame
     * @param cdi
     * @param model
     */
    public ProcessEffect(CaptureDeviceInfo cdi) {
        this.itb = new ImageToBuffer();
        this.formats = cdi.getFormats();
        this.outData = new byte[320 * 240 * 3];
        this.frameControl = new FrameControl();
    }
    boolean first = true;

    /**
     * Processa o codec
     * @param inBuffer
     * @param outBuffer
     * @return
     */
    public int process(Buffer inBuffer, Buffer outBuffer) {
        if (first) {
            first = false;
            // Cria a imagem do buffer
            bti = new BufferToImage((VideoFormat) inBuffer.getFormat());
            // Inicia sequencia de acionamento
            FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.DESLIGAR_LEDS);
            FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.LIGAR_LEDS_EIXO_INTERNO);
            try {
                Thread.sleep(frameControl.WAIT_FRAME_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            this.frameControl.start();
        }

        

        // Transforma buffer em imagem
        final Image ipq = bti.createImage(inBuffer);
        // passa imagem para controladora
        frameControl.setCframe(ipq);
        // Quadro
        if (Control.getX() != 0 || Control.getY() != 0) {
            image = desenhaRoi(ipq, Color.RED, Control.getX() - 15, Control.getY() - 15, 30, 30);
            image = desenhaCentro(image, Color.RED, Control.getX(), Control.getY());
        } else {
            image = ipq;
        }
        // cria buffer a partir da imgem alterada
        final Buffer b = itb.createBuffer(image, ((VideoFormat) inBuffer.getFormat()).getFrameRate());

        //Copy the input attributes to the output
        outBuffer.setLength(b.getLength());
        outBuffer.setOffset(b.getOffset());
        //Copy the input attributes to the output
        outBuffer.setFormat(b.getFormat());
        outBuffer.setData(b.getData());
        
        return BUFFER_PROCESSED_OK;
    }

    /**
     * Formatos suportados
     * @return
     */
    public Format[] getSupportedInputFormats() {
        return formats;
    }

    /**
     * Formatos suportados de saida
     * @param input
     * @return
     */
    public Format[] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return formats;
        }
        if (matches(input, formats) != null) {
            return new Format[]{formats[0].intersects(input)};
        } else {
            return new Format[0];
        }
    }

    /**
     * Atribui formato de entrada
     * @param input
     * @return
     */
    public Format setInputFormat(Format input) {
        inputFormat = input;
        return input;
    }

    /**
     * Atribui formato de saida
     * @param output
     * @return
     */
    public Format setOutputFormat(Format output) {
        if (output == null || matches(output, formats) == null) {
            return null;
        }

        RGBFormat incoming = (RGBFormat) output;
        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = 30.000f;
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null) {
            return null;
        }
        if (maxDataLength < size.width * size.height * 3) {
            maxDataLength = size.width * size.height * 3;
        }
        if (lineStride < size.width * 3) {
            lineStride = size.width * 3;
        }
        if (flipped != Format.FALSE) {
            flipped = Format.FALSE;
        }

        outputFormat = formats[0].intersects(new RGBFormat(size,
                maxDataLength,
                null,
                frameRate,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED,
                lineStride,
                Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED));

        return outputFormat;
    }

    /**
     * Controles
     * @param controlType
     * @return
     */
    @Override
    public Object getControl(String controlType) {
        return null;
    }

    /**
     * Controles
     * @param controlType
     * @return
     */
    @Override
    public Object[] getControls() {
        return null;
    }

    /**
     * Encontra formato
     * @param in
     * @param outs
     * @return
     */
    private Format matches(Format in, Format outs[]) {
        for (int i = 0; i < outs.length; i++) {
            if (in.matches(outs[i])) {
                return outs[i];
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "TCC";
    }

    @Override
    public void open() throws ResourceUnavailableException {
    }

    @Override
    public void close() {
    }

    @Override
    public void reset() {
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

    private Image desenhaCentro(Image img, Color c, int x, int y) {
        BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.drawImage(img, 0, 0, img.getWidth(null),
                img.getHeight(null), null);
        g.setColor(c);
        g.drawRect(x, y, 5, 5);

        return new ImageIcon(buffer).getImage();
    }
}
