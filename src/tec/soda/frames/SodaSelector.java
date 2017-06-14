package tec.soda.frames;

import jssc.SerialPortList;
import tec.soda.fileHandleres.FileHolder;
import tec.soda.fileHandleres.InfFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by daniel.peczkowski on 2017-04-10.
 */
public class SodaSelector {
    private JButton configFolderSelectorButton;
    private JPanel panel;
    private JList<String> serialPorts;
    private JTextPane configSelected;
    private JButton openButton;
    private JButton openAllButton;
    private JButton closeAllButton;
    private JButton configureButton;
    private JList<SodaInstance> availableDUTs;
    public JFrame frame;

    private final Properties config;
    private final SodaContainer container;

    public InfFile infFile;//INF
    public FileHolder fileHolder;

    public SodaSelector(Properties mainConfiguration, SodaContainer sodaContainer){
        config=mainConfiguration;
        container=sodaContainer;
        formConfiguration();

        configFolderSelectorButton.addActionListener(actionEvent -> selectInfFile());
        setPortList();
        setCloseAllButton();
        setOpenButton();
        setOpenAllButton();

        frame =new JFrame("SodaJ Configuration Selector");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(frame.getSize().width,frame.getSize().height));
        //frame.setResizable(false);
    }

    private void formConfiguration(){
        if(config.containsKey("configurationFile")) {
            try {
                infFile = new InfFile(new File(config.getProperty("configurationFile")));
                fileHolder=getFileHolder();
            }catch (IOException e){
                config.remove("configurationFile");
            }finally {
                if(infFile==null) config.remove("configurationFile");
            }
            setConfigSelectedText();
        }
        if(container.panels!=null)
            availableDUTs.setListData(container.panels);
    }

    private void selectInfFile(){
        ModelDialog modelDialog =new ModelDialog(config,this);
        modelDialog.setVisible(true);
    }

    private void setCloseAllButton(){
        closeAllButton.addActionListener(actionEvent -> {
            container.replaceAllWithEmpty();
            frame.update(frame.getGraphics());
        });
    }

    private void setOpenButton(){
        openButton.addActionListener(actionEvent -> {
            if(infFile==null)return;
            for(int index:availableDUTs.getSelectedIndices()){
                if(container.ports[index]!=null) {
                    container.addInstance(
                            new SodaInstance(config, container.ports[index],
                                    "DUT" + (index + 1),
                                    infFile.getFile().getParentFile().getName(),
                                    fileHolder),
                            index);
                }
            }
            frame.update(frame.getGraphics());
        });
    }

    private void setOpenAllButton(){
        openAllButton.addActionListener(actionEvent -> {
            if(infFile==null)return;
            for(int index=0;index<container.panels.length;index++){
                if(container.ports[index]!=null) {
                    container.addInstance(
                            new SodaInstance(config, container.ports[index],
                                    "DUT" + (index + 1),
                                    infFile.getFile().getParentFile().getName(),
                                    fileHolder),
                            index);
                }
            }
            frame.update(frame.getGraphics());
        });
    }

    public void setConfigSelectedText(){
        try {
            configSelected.setText(
                    infFile.getFile().getAbsoluteFile().getParentFile().getParentFile().getParentFile().getParentFile().getName() + "\n" +
                            infFile.getFile().getParentFile().getParentFile().getName() + "\n" +
                            infFile.getFile().getParentFile().getName() + "\n" +
                            infFile.getName()
            );
        }catch (Exception e){
                e.printStackTrace();
        }
    }

    private void setPortList(){
        serialPorts.setListData(SerialPortList.getPortNames());
    }

    private FileHolder getFileHolder(){
        try {
            return new FileHolder(infFile);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
