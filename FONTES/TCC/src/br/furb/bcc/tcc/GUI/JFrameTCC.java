/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JFrameTCC.java
 *
 * Created on 21/09/2010, 22:28:01
 */
package br.furb.bcc.tcc.GUI;

import br.furb.bcc.tcc.video.ProcessEffectLauncher;
import br.furb.bcc.tcc.video.DevicesFinder;
import br.furb.bcc.tcc.util.ImageProcessing;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.*;
import javax.media.protocol.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import br.furb.bcc.tcc.control.Control;
import br.furb.bcc.tcc.control.FlashDeviceRXTX;

/**
 *
 * @author Eduardo
 */
public class JFrameTCC extends javax.swing.JFrame {
    // Paineis para acompanhemento passo a passo

    public final JImagePanel SUBTRACT_PANEL;
    public final JImagePanel NIVEL_PANEL;
    public final JImagePanel REDEYE_PANEL;
    public final JImagePanel DARKEYE_PANEL;
    public final JImagePanel BINARY_PANEL;
    public final JImagePanel ROI_PANEL;
    // Processor de imagem
    private Processor processor;
    //
    private PushBufferDataSource pbds = null;
    // Equipamento de captura
    public CaptureDeviceInfo cdi;
    //
    private Component vc, cc;
    // Cria um novo DefaultListModel que sera utilizado para apresentar as regiões de interesse
    private DefaultListModel listModel = new DefaultListModel();

    /**
     * Receve o painel e a imgem a ser desenhada
     * @param p Painel
     * @param img Imagem
     */
    public void setPanelImage(JImagePanel p, Image img) {
        if (p == null || img == null) {
            return;
        }
        p.removeAll();
        if (img == null) {
            return;
        }
        p.setImage(ImageProcessing.toBufferedImage(img));
    }

    /**
     * Retorna se o modo de detecção esta ativo
     * @return
     */
    public boolean isDetectionEnable() {
        return this.jToggleAtivar.isSelected();
    }

    public void setDetectionMode(boolean bool) {
        this.jToggleAtivar.setSelected(bool);
    }

    /**
     * Adiciona imagem na lista ROI
     * @param e
     * @param rem
     */
    public void setListImage(EyeSVM e, boolean rem) {
        listModel.addElement(e);
        jList1.repaint();
    }

    /** Creates new form JFrameTCC */
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();
        pnlRedEye = new JImagePanel(null);
        plDarkEye = new JImagePanel(null);
        jPanel3 = new javax.swing.JPanel();
        lbStatus = new javax.swing.JLabel();
        pnlVideoFrame = new javax.swing.JPanel();
        plSubtract = new JImagePanel(null);
        plNivel = new JImagePanel(null);
        plBinary = new JImagePanel(null);
        plRoi = new JImagePanel(null);
        jToggleAtivar = new javax.swing.JToggleButton();
        btnAltDebug = new javax.swing.JToggleButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jlstCand = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        jRadioButton1.setText("jRadioButton1");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TCC - Eduardo Henrique Sasse - FURB");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlRedEye.setBorder(javax.swing.BorderFactory.createTitledBorder("Olho Vermelho"));
        pnlRedEye.setPreferredSize(new java.awt.Dimension(320, 240));

        javax.swing.GroupLayout pnlRedEyeLayout = new javax.swing.GroupLayout(pnlRedEye);
        pnlRedEye.setLayout(pnlRedEyeLayout);
        pnlRedEyeLayout.setHorizontalGroup(
            pnlRedEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        pnlRedEyeLayout.setVerticalGroup(
            pnlRedEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 214, Short.MAX_VALUE)
        );

        plDarkEye.setBorder(javax.swing.BorderFactory.createTitledBorder("Olho Escuro"));
        plDarkEye.setPreferredSize(new java.awt.Dimension(320, 240));

        javax.swing.GroupLayout plDarkEyeLayout = new javax.swing.GroupLayout(plDarkEye);
        plDarkEye.setLayout(plDarkEyeLayout);
        plDarkEyeLayout.setHorizontalGroup(
            plDarkEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );
        plDarkEyeLayout.setVerticalGroup(
            plDarkEyeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 214, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbStatus.setText("Status:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 952, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlVideoFrame.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlVideoFrame.setPreferredSize(new java.awt.Dimension(320, 240));

        javax.swing.GroupLayout pnlVideoFrameLayout = new javax.swing.GroupLayout(pnlVideoFrame);
        pnlVideoFrame.setLayout(pnlVideoFrameLayout);
        pnlVideoFrameLayout.setHorizontalGroup(
            pnlVideoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );
        pnlVideoFrameLayout.setVerticalGroup(
            pnlVideoFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 236, Short.MAX_VALUE)
        );

        plSubtract.setBorder(javax.swing.BorderFactory.createTitledBorder("Subtração"));
        plSubtract.setPreferredSize(new java.awt.Dimension(160, 120));

        javax.swing.GroupLayout plSubtractLayout = new javax.swing.GroupLayout(plSubtract);
        plSubtract.setLayout(plSubtractLayout);
        plSubtractLayout.setHorizontalGroup(
            plSubtractLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        plSubtractLayout.setVerticalGroup(
            plSubtractLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );

        plNivel.setBorder(javax.swing.BorderFactory.createTitledBorder("Nivelamento"));
        plNivel.setPreferredSize(new java.awt.Dimension(160, 120));

        javax.swing.GroupLayout plNivelLayout = new javax.swing.GroupLayout(plNivel);
        plNivel.setLayout(plNivelLayout);
        plNivelLayout.setHorizontalGroup(
            plNivelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        plNivelLayout.setVerticalGroup(
            plNivelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );

        plBinary.setBorder(javax.swing.BorderFactory.createTitledBorder("Binarização"));
        plBinary.setPreferredSize(new java.awt.Dimension(160, 120));

        javax.swing.GroupLayout plBinaryLayout = new javax.swing.GroupLayout(plBinary);
        plBinary.setLayout(plBinaryLayout);
        plBinaryLayout.setHorizontalGroup(
            plBinaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        plBinaryLayout.setVerticalGroup(
            plBinaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );

        plRoi.setBorder(javax.swing.BorderFactory.createTitledBorder("ROI"));
        plRoi.setPreferredSize(new java.awt.Dimension(160, 120));

        javax.swing.GroupLayout plRoiLayout = new javax.swing.GroupLayout(plRoi);
        plRoi.setLayout(plRoiLayout);
        plRoiLayout.setHorizontalGroup(
            plRoiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        plRoiLayout.setVerticalGroup(
            plRoiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );

        jToggleAtivar.setFont(new java.awt.Font("Tahoma", 0, 10));
        jToggleAtivar.setText("Iniciar Processos");
        jToggleAtivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleAtivarActionPerformed(evt);
            }
        });

        btnAltDebug.setFont(new java.awt.Font("Tahoma", 0, 10));
        btnAltDebug.setText("Modo Debug");
        btnAltDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAltDebugActionPerformed(evt);
            }
        });

        jToggleButton1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jToggleButton1.setText("Modo EyeMouse");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultado SVM"));

        jList1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlstCand.setViewportView(jList1);

        jButton1.setText("Limpar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Salvar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("svm-scaled");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jlstCand, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jCheckBox1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addGap(173, 173, 173))
            .addComponent(jlstCand, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(plSubtract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(plBinary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(plRoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jToggleAtivar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(2, 2, 2)
                                    .addComponent(btnAltDebug, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jToggleButton1, 0, 0, Short.MAX_VALUE))
                                .addComponent(pnlVideoFrame, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlRedEye, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plDarkEye, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(plDarkEye, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlRedEye, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlVideoFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jToggleAtivar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAltDebug, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(plNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(plSubtract, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(plRoi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(plBinary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAltDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltDebugActionPerformed
        Control.setDebugMode(btnAltDebug.isSelected());
    }//GEN-LAST:event_btnAltDebugActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
         Control.setEyeMouseMode(btnAltDebug.isSelected());
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleAtivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleAtivarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleAtivarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.DESLIGAR_LEDS);
    }//GEN-LAST:event_formWindowClosing

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Control.setSaveMode(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.listModel.clear();
        this.jList1.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        Control.setWithScale(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnAltDebug;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JToggleButton jToggleAtivar;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JScrollPane jlstCand;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JPanel plBinary;
    private javax.swing.JPanel plDarkEye;
    private javax.swing.JPanel plNivel;
    private javax.swing.JPanel plRoi;
    private javax.swing.JPanel plSubtract;
    private javax.swing.JPanel pnlRedEye;
    private javax.swing.JPanel pnlVideoFrame;
    // End of variables declaration//GEN-END:variables

    /**
     * Construtor
     */
    public JFrameTCC() {


        initComponents();
        // Atribui o DefaultListModel à JList
        jList1.setModel(listModel);
        this.jList1.setCellRenderer(new ImageRenderer());

        this.SUBTRACT_PANEL = (JImagePanel) this.plSubtract;
        this.NIVEL_PANEL = (JImagePanel) this.plNivel;
        this.REDEYE_PANEL = (JImagePanel) this.pnlRedEye;
        this.DARKEYE_PANEL = (JImagePanel) plDarkEye;
        this.BINARY_PANEL = (JImagePanel) plBinary;
        this.ROI_PANEL = (JImagePanel) plRoi;

        // Centraliza
        setLocationRelativeTo(null);
        // Exibe
        setVisible(true);

        try {
            // Cursor de espera
            this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Agrupa as portas
            FlashDeviceRXTX.getInstance().ListarPortas();
            final String[] portas = FlashDeviceRXTX.getInstance().ObterPortas();
            if (portas == null || portas[0] == null) {
                JOptionPane.showMessageDialog(this, "Conecte o Dispositivo Flash!", "Não encontrada porta COM", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            if (portas[0] != null && portas[1] == null) {
                Control.setSerialCom(portas[0]);
            } else if (portas[0] != null && portas[1] != null) {
                String lst = "";
                for (String s : portas) {
                    if (s == null) {
                        break;
                    }
                    lst += " " + s + ";\n";
                }
                String serial = JOptionPane.showInputDialog(this, "Informe a porta desejada: \n" + lst, "Seleção porta COM", JOptionPane.QUESTION_MESSAGE);

                if (FlashDeviceRXTX.getInstance().PortaExiste(serial)) {
                    Control.setSerialCom(serial);
                    String ret = FlashDeviceRXTX.getInstance().enviarComando(FlashDeviceRXTX.TESTAR_DISP_FLASH);
                    if (!ret.equals("50")){
                        JOptionPane.showMessageDialog(this, "Dispositivo flash não encontrado na porta serial informada!", "Dispositivo Flash Inválido!", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Porta informada nao e valida!");
                    System.exit(-1);
                }
            }
            // Procura a Camera
            setStatusMessage("Crregando a camera!");
            findCaptureDevice();
            // Cursor Default
            this.getContentPane().setCursor(Cursor.getDefaultCursor());
            setStatusMessage("Ok");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro detectando camera", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Inicia o processo de detecção da camera
     */
    private void findCaptureDevice() {
        DevicesFinder devicesFinder = new DevicesFinder();
        pbds = devicesFinder.findDevices(this);
        if (pbds != null) {
            ProcessEffectLauncher pel = new ProcessEffectLauncher();
            try {
                processor = pel.open(pbds, cdi, this, Control.getModel());
            } catch (NoProcessorException ex) {
                Logger.getLogger(JFrameTCC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JFrameTCC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(JFrameTCC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JFrameTCC.class.getName()).log(Level.SEVERE, null, ex);
            }
            processor.start();
            vc = processor.getVisualComponent();
            vc.setLocation(5, 2);
            vc.setSize(new Dimension(320, 240));
            this.pnlVideoFrame.removeAll();
            this.pnlVideoFrame.add(vc);
            cc = processor.getControlPanelComponent();
            cc.setSize(new Dimension(35, 20));
            cc.setLocation(300, 2);
            this.getContentPane().add(cc);
        }
    }

    public void setStatusMessage(String msg) {
        lbStatus.setText("Status: " + msg);
    }

    private class ImageRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            // for default cell renderer behavior
            Component c = super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);
            // set icon for cell image
            ((JLabel) c).setIcon(new ImageIcon(((EyeSVM) value).getImg()));
            ((JLabel) c).setText(((EyeSVM) value).getMsg());
            return c;
        }
    }
}
