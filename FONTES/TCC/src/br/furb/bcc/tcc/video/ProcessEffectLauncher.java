package br.furb.bcc.tcc.video;

import java.io.IOException;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.format.*;
import javax.swing.*;
import javax.media.protocol.*;
import libsvm.svm_model;
import br.furb.bcc.tcc.GUI.JFrameTCC;

/**
 * Utilizado como um localizador de mídia. Cria e usa um Processor para se
 * comportar como um player para a reprodução de mídia. Durante o estado
 * configurado, exitem dois meios de passagem dos codecs: PreAccessCodec e
 * PostAccessCodec que são definidos no video track. Estes codecs são usados
 * para obter acesso aos frames individualmente da mídia de vídeo.
 */
public class ProcessEffectLauncher implements ControllerListener {

    private Processor processor;
    private final Object waitSync = new Object();
    private boolean stateTransitionOK = true;

    public ProcessEffectLauncher() {
    }

    public Processor open(DataSource ds, CaptureDeviceInfo cdi, JFrameTCC frame, svm_model model) throws NoProcessorException, IOException, InterruptedException, Exception {
        // Iniciando o processor
        processor = Manager.createProcessor(ds);
        processor.addControllerListener(this);

        // Coloca o Processor no estado configuracao
        processor.configure();
        if (!waitForState(processor.Configured)) {
            JOptionPane.showMessageDialog(frame, "Falha ao configurar o Processor", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // usar o player
        processor.setContentDescriptor(null);

        // Obtém os controles de track.
        TrackControl tc[] = processor.getTrackControls();

        if (tc == null) {
            throw new Exception("Falha ao obter o controle de track do Processor.");
        }

        // Procura pelo controle de track para o video track.
        TrackControl videoTrack = null;
        for (int i = 0; i < tc.length; i++) {
            if (tc[i].getFormat() instanceof VideoFormat) {
                {
                    videoTrack = tc[i];
                    break;
                }
            }
        }

        if (videoTrack == null) {
            JOptionPane.showMessageDialog(frame, "O input da mídia não contém um video track.", null, JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // Instancia e define o acesso ao frame para o caminho do fluxo de dados.
        try {
            String format = videoTrack.getFormat().toString();
            Codec codec[] = {new ProcessEffect(cdi)};
            videoTrack.setCodecChain(codec);
        } catch (UnsupportedPlugInException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Efeitos do Processor", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        // Executa o processor
        processor.prefetch();
        if (!waitForState(processor.Prefetched)) {
            JOptionPane.showMessageDialog(frame, "Falha ao executar o processor", "Processor", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //Start the processor.
        processor.start();

        return processor;
    }

    /**
     * Bloqueia o processor até que seja atingido o estado solicitado
     * @param state
     * @return boolean Retorna <i>false</i> caso falhe durante o processamento.
     */
    boolean waitForState(int state) throws InterruptedException {
        synchronized (waitSync) {
            while (processor.getState() != state && stateTransitionOK) {
                waitSync.wait();
            }
        }
        return stateTransitionOK;
    }

    /**
     *
     */
    public void controllerUpdate(ControllerEvent evt) {

        if (evt instanceof ConfigureCompleteEvent || evt instanceof RealizeCompleteEvent || evt instanceof PrefetchCompleteEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        } else if (evt instanceof EndOfMediaEvent) {
            processor.close();
            System.exit(0);
        }
    }
}
