package br.furb.bcc.tcc.rxtx;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RXTXCom implements SerialPortEventListener , Runnable{
    public String readData;
    public int nodeBytes;
    private int baudrate;
    private int timeout;
    private CommPortIdentifier cp;
    private SerialPort serialPort;
    private OutputStream outputDataStream ;
    private InputStream inputStream;
    private Thread thRead;
    private boolean idPort;
    private boolean stPort;
    private boolean writeMode;
    private String serialPortName;
    private int novoDado;


    public RXTXCom(String p, int b, int t) {
        this.serialPortName = p;
        this.baudrate = b;
        this.timeout = t;
        
    }
    /**
     * Buffer to hold the reading
     */
    private byte[] readBuffer = new byte[1];
    private InputStream inStream;

    public void setWriteMode() {
        writeMode = true;
    }

    public void setWriteMode(boolean b) {
        writeMode = b;
    }

    private void getCommPortIdentifier() {
        try {
            cp = CommPortIdentifier.getPortIdentifier(serialPortName);
            if (cp == null) {
                System.err.println("Erro na porta");
                idPort = false;
                return;
            }
            idPort = true;
        } catch (Exception e) {
            System.err.println("Erro obtendo ID da porta: " + e);
            idPort = false;
            return;
        }
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public void setSerialPortOpen() {
        try {
            // Obtem identificador da porta serial através do nome
            cp = CommPortIdentifier.getPortIdentifier(serialPortName);
            // Obtem objeto da porta serial
            serialPort = (SerialPort) cp.open(getSerialPortName(), timeout);
            inStream = serialPort.getInputStream();
            stPort = true;
            // configurar parâmetros
            serialPort.setSerialPortParams(baudrate,
                    serialPort.DATABITS_8,
                    serialPort.STOPBITS_1,
                    serialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

        } catch (Exception e) {
            stPort = false;
            System.err.println("Erro abrindo comunicação: " + e);
            System.exit(1);
        }
    }

    boolean first = true;
    public int sendData(String msg) {
         setWriteMode();
            try {
                outputDataStream  = serialPort.getOutputStream();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            try {
                outputDataStream .write(msg.getBytes());
                outputDataStream .flush();

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            setWriteMode(false);
            if (first){
                LerDados();
                first = false;
            }
        try {
            Thread.sleep(4);
        } catch (InterruptedException ex) {
            Logger.getLogger(RXTXCom.class.getName()).log(Level.SEVERE, null, ex);
        }
           return novoDado;
    }


    public void serialEvent(SerialPortEvent ev) {
        StringBuffer bufferLeitura = new StringBuffer();
        int novoDado = 0;

        switch (ev.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                // Novo algoritmo de leitura.
                while (novoDado != -1) {
                    try {
                        novoDado = inputStream.read();
                        if (novoDado == -1) {
                            break;
                        }
                        if ('\r' == (char) novoDado) {
                            bufferLeitura.append('\n');
                        } else {
                            bufferLeitura.append((char) novoDado);
                            
                            this.novoDado = novoDado;
                        }
                    } catch (IOException ioe) {
                        System.err.println("Erro de leitura serial: " + ioe);
                    }
                }
                break;
        }
    }

    public void closeComm() {
        try {
            serialPort.close();
        } catch (Exception e) {
            System.err.println("Erro fechando porta: " + e);
        }
    }

    public int obterBaudrate() {
        return baudrate;
    }

    private Thread threadLeitura;
    public void LerDados() {
        
		if (!writeMode) {
			try {
				inputStream = serialPort.getInputStream();
			} catch (Exception e) {
				System.out.println("Erro de stream: " + e);
				System.exit(1);
			}
			try {
				serialPort.addEventListener((SerialPortEventListener) this);
			} catch (Exception e) {
				System.out.println("Erro de listener: " + e);
				System.exit(1);
			}
			serialPort.notifyOnDataAvailable(true);
			try {
				threadLeitura = new Thread((Runnable) this);
				threadLeitura.start();
				run();
			} catch (Exception e) {
				System.out.println("Erro de Thred: " + e);
			}
		}
	}


	public void run() {
		try {
			Thread.sleep(3);
		} catch (Exception e) {
			System.out.println("Erro de Thred: " + e);
		}
	}
}
