package tec.soda.frames;

import tec.soda.fileHandleres.AnyFile;
import tec.soda.fileHandleres.FileHolder;
import tec.soda.fileHandleres.InfFile;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeMap;


public class ModelDialog extends JDialog {
    private JPanel contentPane;
    private JButton accept;
    private JButton refresh;
    private JTextField input;
    private JList<AnyFile> infList;

    private Properties config;
    private SodaSelector selector;

    private static TreeMap<String,AnyFile> map;

    public ModelDialog(Properties configuration,SodaSelector select) {
        config=configuration;
        selector=select;
        setContentPane(contentPane);
        setTitle("SodaJ Model Selector");
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getRootPane().setDefaultButton(accept);

        accept.addActionListener(e -> onOK());

        refresh.addActionListener(e -> onRefresh());

        input.addActionListener(e -> onInput());

        infList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() > 1) {
                    // Multi-click detected
                    onOK();
                }
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if(map==null) {
            map = new TreeMap<>();
            onRefresh();
        }else{
            onInput();
        }

        setSize(800,800);
    }

    private void onInput(){
        TreeMap<String, AnyFile> tree = new TreeMap<>(map);
        if(input.getText().length()>0) {
            for (String key : tree.keySet().toArray(new String[0])) {
                if (!key.contains(input.getText())){
                    tree.remove(key);
                }
            }
        }
        infList.setListData(tree.values().toArray(new AnyFile[0]));
        this.update(this.getGraphics());
    }

    private void onOK() {
        if(infList.isSelectionEmpty()) return;
        try {
            selector.infFile=new InfFile(infList.getSelectedValue().getFile());
            selector.fileHolder = new FileHolder(selector.infFile);
            //frame.setVisible(false);
            selector.frame.update(selector.frame.getGraphics());
            config.setProperty("configurationFile", selector.infFile.getPath());
            selector.setConfigSelectedText();
            dispose();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void onRefresh(){
        int i=0;
        map.clear();

        File configure=new File("Configure");//MainDir

        if (configure.isDirectory()){
            File[] p1_arr = configure.listFiles(File::isDirectory);
            if (p1_arr != null) {
                for(File p1:p1_arr){
                    File[] p2_arr = p1.listFiles(File::isDirectory);
                    if(p2_arr!=null){
                        for(File p2:p2_arr){
                            File[] infFiles= p2.listFiles(file -> file.getName().endsWith(".inf"));
                            if(infFiles!=null){
                                for(File inf:infFiles) {
                                    AnyFile any=new AnyFile(inf);
                                    map.put(any.toString(),any);
                                }
                            }

                        }
                    }
                }
            }
        }

        for(;config.containsKey("ConfigurationFolder"+i);i++){
            String path=config.getProperty("ConfigurationFolder"+i);
            if(path.equals("...")) break;
            configure=new File(path);//Redefine
            if (configure.isDirectory()){
                File[] p1_arr = configure.listFiles(File::isDirectory);
                if (p1_arr != null) {
                    for(File p1:p1_arr){
                        File[] p2_arr = p1.listFiles(File::isDirectory);
                        if(p2_arr!=null){
                            for(File p2:p2_arr){
                                File[] infFiles= p2.listFiles(file -> file.getName().endsWith(".inf"));
                                if(infFiles!=null){
                                    for(File inf:infFiles) {
                                        AnyFile any=new AnyFile(inf);
                                        map.put(any.toString(),any);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        config.setProperty("ConfigurationFolder"+i,"...");
        onInput();
    }

    //public static void main(String[] args) {
    //    ModelDialog dialog = new ModelDialog();
    //    dialog.pack();
    //    dialog.setVisible(true);
    //    System.exit(0);
    //}
}
