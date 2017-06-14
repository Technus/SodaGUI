package tec.soda;

import com.bulenkov.darcula.DarculaLaf;
import tec.soda.frames.SodaContainer;
import tec.soda.frames.SodaSelector;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by daniel.peczkowski on 2017-04-10.
 */
public class Soda{
    public static final String propFileName="config.xml";

    public static Properties config;

    public static SodaContainer container;
    public static SodaSelector selector;

    public static void main(String[] args) {
        //Create folders
        new File("Log").mkdir();
        new File("Configure").mkdir();

        config=new Properties();
        try{
            config.loadFromXML(new FileInputStream(propFileName));
        }catch (FileNotFoundException e){
            saveProperties();
        }catch (IOException e){
            JOptionPane.showConfirmDialog(null,e.getLocalizedMessage(),"Config Load Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(Soda::saveProperties));

        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        }catch (Exception e){
            e.printStackTrace();
        }

        container=new SodaContainer(config);
        selector=new SodaSelector(config,container);

        container.frame.setVisible(true);
        selector.frame.setVisible(true);
    }

    private static void saveProperties(){
        try{
            Properties temp=new Properties(){
                @Override
                public Set<Object> keySet(){
                    return Collections.unmodifiableSet(new TreeSet<>(super.keySet()));
                }

                @Override
                public synchronized Enumeration<Object> keys() {
                    Comparator<Object> byCaseInsensitiveString = Comparator.comparing(Object::toString,
                            String.CASE_INSENSITIVE_ORDER);

                    Supplier<TreeSet<Object>> supplier = () -> new TreeSet<>(byCaseInsensitiveString);

                    TreeSet<Object> sortedSet = super.keySet().stream()
                            .collect(Collectors.toCollection(supplier));

                    return Collections.enumeration(sortedSet);
                }
            };
            temp.putAll(config);
            temp.storeToXML(new FileOutputStream(propFileName),null);
        }catch (IOException e){
            JOptionPane.showConfirmDialog(selector.frame,e.getLocalizedMessage(),"Config Save Error",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
        }
    }
}
