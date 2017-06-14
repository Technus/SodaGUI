package tec.soda.dataContainers;

import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by daniel.peczkowski on 2017-06-05.
 */
public class StyledSodaDocument {
    public final static int NG=0,OK=1,DATA=2,INFO=3,TITLE=4,RX=5,TX=6;

    private final StyledDocument document;
    private Style ok,ng,data,info,title,rx,tx;
    private final Style[] styles;

    public StyledSodaDocument(){
        document=new DefaultStyledDocument();
        rx=document.addStyle("rx",null);
        tx=document.addStyle("tx",null);
        ok=document.addStyle("ok",null);
        ng=document.addStyle("ng",null);
        data=document.addStyle("data",null);
        info=document.addStyle("info",null);
        title=document.addStyle("title",null);

        StyleConstants.setForeground(rx,new Color(170, 255, 0));
        StyleConstants.setForeground(tx,new Color(255, 170, 0));
        StyleConstants.setForeground(ok,new Color(0, 255, 42));
        StyleConstants.setForeground(ng,new Color(255, 0, 0));
        StyleConstants.setForeground(data,new Color(255, 255, 0));
        StyleConstants.setForeground(info,new Color(0, 128, 255));
        StyleConstants.setForeground(title,new Color(0, 255, 255));

        StyleConstants.setBold(ok,true);
        StyleConstants.setBold(ng,true);
        StyleConstants.setItalic(info,true);
        StyleConstants.setBold(title,true);

        StyleConstants.setFontFamily(rx,"ConsolasUART");
        StyleConstants.setFontFamily(tx,"ConsolasUART");
        StyleConstants.setFontFamily(ok,"ConsolasUART");
        StyleConstants.setFontFamily(ng,"ConsolasUART");
        StyleConstants.setFontFamily(data,"ConsolasUART");
        StyleConstants.setFontFamily(info,"ConsolasUART");
        StyleConstants.setFontFamily(title,"ConsolasUART");

        StyleConstants.setFontSize(rx,14);
        StyleConstants.setFontSize(tx,14);
        StyleConstants.setFontSize(ok,18);
        StyleConstants.setFontSize(ng,18);
        StyleConstants.setFontSize(data,14);
        StyleConstants.setFontSize(info,18);
        StyleConstants.setFontSize(title,20);

        StyleConstants.setAlignment(rx,StyleConstants.ALIGN_RIGHT);
        StyleConstants.setAlignment(tx,StyleConstants.ALIGN_RIGHT);
        StyleConstants.setAlignment(ok,StyleConstants.ALIGN_RIGHT);
        StyleConstants.setAlignment(ng,StyleConstants.ALIGN_RIGHT);
        StyleConstants.setAlignment(data,StyleConstants.ALIGN_CENTER);
        StyleConstants.setAlignment(info,StyleConstants.ALIGN_LEFT);
        StyleConstants.setAlignment(title,StyleConstants.ALIGN_CENTER);

        styles=new Style[]{ng,ok,data,info,title,rx,tx};
    }

    public StyledDocument getDocument(){
        return document;
    }

    public void append(int style,String s){
        try {
            document.insertString(document.getLength(), s, styles[style]);
        }catch (BadLocationException e){
            e.printStackTrace();
        }
    }

    public void clear(){
        try {
            document.remove(0,document.getLength());
        }catch (BadLocationException e){
            e.printStackTrace();
        }
    }

    public void exportToRtf(String name) throws IOException, BadLocationException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RTFEditorKit kit = new RTFEditorKit();
        kit.write(baos, document, document.getStartPosition().getOffset(), document.getLength());
        baos.close();
        final File file = new File("Log"+File.separator+name);
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(baos.toByteArray());
        fos.close();
    }
}
