package tec.soda.frames;

import jssc.SerialPort;
import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.SerialDataContainer;
import tec.soda.fileHandleres.FileHolder;
import tec.soda.procedures.Procedure;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import static tec.soda.Utility.dtf;

/**
 * Created by daniel.peczkowski on 2017-04-11.
 */
public class SodaInstance {
    public JPanel panel;
    private JButton startStopButton;
    public JTextField commandInput;
    private JTextPane inputData;
    private JTextPane outputData;
    private JLabel statusLabel;
    private JButton txtModeButton;
    private JButton logSaveButton;
    private JLabel modelLabel;
    private JButton resetButton;
    private JPanel statusPane;
    private JSplitPane splitPane;
    private JButton extraButton;
    private JScrollPane inScroll;
    private JScrollPane outScroll;
    private JLabel procTime;
    private JLabel totalTime;

    private final Properties config;
    public final String name;
    public final Worker worker;
    public final FileHolder fileHolder;
    private final ProcSelector cmdPicker;

    private int verbosityLevel=2;

    private Thread updateThread;

    private final ByteDataBuilder backspace=new ByteDataBuilder('\b');


    public SodaInstance(Properties mainConfiguration, SerialPort serialPort, String fieldName, String modelName, FileHolder files) throws IllegalArgumentException {
        fileHolder=files;
        name = fieldName;
        statusLabel.setText(name);
        worker = new Worker(serialPort, fileHolder);
        extraButton.setText(serialPort.getPortName());
        if (modelName != null && modelName.length() > 0) {
            modelLabel.setText(modelName);
        }

        config = mainConfiguration;
        formConfiguration();

        startStopButton.setEnabled(true);

        commandInput.addActionListener(e -> onInput());
        txtModeButton.addActionListener(e -> onTxtModeChange());
        logSaveButton.addActionListener(e -> onSaveLog());
        extraButton.addActionListener(e-> onExtra());
        resetButton.addActionListener(e-> onReset());

        cmdPicker=new ProcSelector(this,fileHolder);

        inputData.setDocument(worker.sdc.info.getDocument());
        outputData.setDocument(worker.sdc.rxtx.getDocument());

        updateThread =new Thread(()->{
            while(!Thread.currentThread().isInterrupted()) {
                inputData.setCaretPosition(inputData.getDocument().getLength());
                outputData.setCaretPosition(outputData.getDocument().getLength());
                //inScroll.getHorizontalScrollBar().setValue(0);
                //outScroll.getHorizontalScrollBar().setValue(0);

                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    return;
                }
            }
        });
        updateThread.start();

        SwingUtilities.invokeLater(this::additionalInitialization);
    }

    public SodaInstance(Properties mainConfiguration, String serialName, String fieldName) {
        modelLabel.setText("Use Selector");
        name = fieldName;
        statusLabel.setText(fieldName);
        extraButton.setText(serialName);

        config = mainConfiguration;
        formConfiguration();

        extraButton.setEnabled(false);
        startStopButton.setEnabled(false);
        logSaveButton.setEnabled(false);
        resetButton.setEnabled(false);
        txtModeButton.setEnabled(false);

        worker = null;
        fileHolder = null;
        cmdPicker=null;

        SwingUtilities.invokeLater(this::additionalInitialization);
    }

    private void formConfiguration() {
        panel.updateUI();
    }

    private void onReset(){
        worker.sdc.clear();
    }

    private void onInput(){
        //send command
        if(worker.serial.isOpened()){
            String in=commandInput.getText();
            System.out.println(commandInput.getText());
            if(in.startsWith("0x")){
                worker.sdc.sendSingleCmd(new ByteDataBuilder(in.substring(2).replace(" ",""), true));
            }else if(in.length()>0){
                worker.sdc.sendSingleCmd(new ByteDataBuilder(in+"\r\n", false));
            }else{
                worker.sdc.send(backspace);
            }
        }
    }

    private void onTxtModeChange() {
        switch (verbosityLevel) {
            case 0:
                verbosityLevel = 1;
                inputData.setDocument(worker.sdc.rxInfoText.getDocument());
                outputData.setDocument(worker.sdc.txInfoText.getDocument());
                txtModeButton.setText("Text");
                break;
            case 1:
                verbosityLevel = 2;
                inputData.setDocument(worker.sdc.info.getDocument());
                outputData.setDocument(worker.sdc.rxtx.getDocument());
                txtModeButton.setText("Info");
                break;
            case 2:
                verbosityLevel = 0;
                inputData.setDocument(worker.sdc.rxText.getDocument());
                outputData.setDocument(worker.sdc.txText.getDocument());
                txtModeButton.setText("Data");
                break;
        }
    }

    private void onExtra(){
        cmdPicker.frame.setVisible(true);
    }

    private void onSaveLog(){
        saveLog(verbosityLevel);
    }

    public void saveLog(int desiredVerbosity){
        if(worker==null)return;
        try {
            StringBuilder temp=new StringBuilder();
            temp.append(modelLabel.getText()).append(" ").append(name).append(" ").append(dtf.format(LocalDateTime.now())).append(".rtf");
            switch (desiredVerbosity){
                case 0:
                    worker.sdc.rxText.exportToRtf("PART1 "+temp.toString());
                    worker.sdc.txText.exportToRtf("PART2 "+temp.toString());
                    break;
                case 1:
                    worker.sdc.rxInfoText.exportToRtf("PART1 "+temp.toString());
                    worker.sdc.txInfoText.exportToRtf("PART2 "+temp.toString());
                    break;
                case 2:
                    worker.sdc.info.exportToRtf("PART1 "+temp.toString());
                    worker.sdc.rxtx.exportToRtf("PART2 "+temp.toString());
                    break;
                case 3:
                    worker.sdc.rxInfoHex.exportToRtf("PART1 "+temp.toString());
                    worker.sdc.txInfoHex.exportToRtf("PART2 "+temp.toString());
                    break;
                case 4:
                    worker.sdc.rxHex.exportToRtf("PART1 "+temp.toString());
                    worker.sdc.txHex.exportToRtf("PART2 "+temp.toString());
                    break;
            }
        }catch (BadLocationException | IOException e){
            e.printStackTrace();
        }
    }

    public void additionalInitialization() {
        splitPane.setDividerLocation(splitPane.getResizeWeight());
    }

    @Override
    public String toString() {
        if (worker != null && worker.serial != null) return name + " " + worker.serial.getPortName() + " " + modelLabel.getText();
        return name;
    }

    public static class Worker {
        private final FileHolder fileHolder;
        private SerialPort serial;
        public SerialDataContainer sdc;
        private Thread thread;
        private final byte UNDEFINED = -1, IDLE = 0, Running = 1, OK = 2, NG = 3;
        private byte state = IDLE;
        private long timeProcess=0,timeTotal=0;

        Worker(SerialPort port, FileHolder file) {
            serial = port;
            fileHolder = file;
            sdc = new SerialDataContainer(serial);
        }


        private static class WorkerThread implements Runnable {
            final Procedure proc;

            WorkerThread(Procedure p) {
                proc = p;
            }

            @Override
            public void run() {
                //do processes
            }
        }

        private void invalidate(){
            if(thread!=null) thread.interrupt();
        }
    }

    public void invalidate(){
        if(cmdPicker!=null) cmdPicker.invalidate();
        if(worker!=null) worker.invalidate();
        if(updateThread !=null) updateThread.interrupt();
    }
}
