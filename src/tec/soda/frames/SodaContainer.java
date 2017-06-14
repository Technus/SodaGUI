package tec.soda.frames;

import jssc.SerialPort;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * Created by daniel.peczkowski on 2017-04-12.
 */
public class SodaContainer {
    public JFrame frame;
    public JPanel panel;
    public final SodaInstance[] panels;
    private final SodaInstance[] blanks;

    private final Properties config;
    private int rows, cols;
    public String[] portNames;
    public SerialPort[] ports;

    public SodaContainer(Properties mainConfiguration) {
        config = mainConfiguration;
        formConfiguration();

        panels = new SodaInstance[rows * cols];
        blanks = new SodaInstance[rows * cols];

        panel = new JPanel(new GridLayout(rows, cols, 2, 2));

        frame = new JFrame("SodaJ Instance Container");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (int i = 0; i < blanks.length; i++) {
            panels[i] = blanks[i] = new SodaInstance(config, portNames[i], "DUT" + (i + 1));
            panel.add(panels[i].panel, i);
        }

        frame.setContentPane(panel);
        frame.pack();
        frame.setMinimumSize(new Dimension(frame.getSize().width, frame.getSize().height + 200));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        SwingUtilities.invokeLater(this::additionalInitialization);
    }

    private void additionalInitialization(){
        for(SodaInstance si:panels)
            si.additionalInitialization();
        frame.update(frame.getGraphics());
    }

    public void replaceAllWithEmpty() {
        panel.removeAll();
        for (int index = 0; index < panels.length; index++) {
            panels[index].invalidate();//old one
            panels[index] = blanks[index];
            panel.add(panels[index].panel, index);
        }
        frame.setContentPane(panel);
        frame.update(frame.getGraphics());
    }

    private void formConfiguration() {
        int temp;

        if (config.containsKey("containerRowsCount") &&
                (temp = Integer.parseInt(config.getProperty("containerRowsCount"))) > 0) {
            rows = temp;
        } else {
            rows = 2;
            config.setProperty("containerRowsCount", Integer.toString(rows));
        }

        if (config.containsKey("containerColumnsCount") &&
                (temp = Integer.parseInt(config.getProperty("containerColumnsCount"))) > 0) {
            cols = temp;
        } else {
            cols = 3;
            config.setProperty("containerColumnsCount", Integer.toString(cols));
        }

        portNames = new String[rows * cols];
        ports=new SerialPort[rows * cols];

        for (int i = 0; i < rows * cols; i++) {
            if (config.containsKey("instanceCom" + i)) {
                portNames[i] = config.getProperty("instanceCom" + i);
                boolean test=false;
                for (String name:SerialPortList.getPortNames()){
                    test|=name.equals(portNames[i]);
                }
                if(test) {
                    try {
                        ports[i]=new SerialPort(portNames[i]);
                        ports[i].openPort();
                        System.out.println("Port "+ports[i].getPortName()+(ports[i].isOpened()?" Open":" Closed"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    portNames[i] = "COM?";
                    config.setProperty("instanceCom" + i, portNames[i]);
                }

            } else {
                portNames[i] = "COM?";
                config.setProperty("instanceCom" + i, portNames[i]);
            }
        }
    }

    public void addInstance(SodaInstance soda, int index) {
        if (index >= panels.length) return;
        panels[index].invalidate();//old one
        panels[index] = soda;
        panel.remove(index);
        panel.add(panels[index].panel, index);
        frame.setContentPane(panel);
        frame.update(frame.getGraphics());
    }
}
