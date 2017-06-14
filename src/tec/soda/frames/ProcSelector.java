package tec.soda.frames;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.fileHandleres.FileHolder;
import tec.soda.procedures.Procedure;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-05-30.
 */
public class ProcSelector {
    private JList<String> processList;
    private JButton putButton;
    private JButton refreshButton;
    private JButton sendButton;
    private JPanel panel;
    private JProgressBar progress;
    private JSpinner delay;
    public JFrame frame;

    private final SodaInstance instance;
    private final FileHolder fileHolder;
    private final ArrayList<ByteDataBuilder[]> commands=new ArrayList<>();
    private final ArrayList<String> commandsInfo =new ArrayList<>();

    private Thread runner;

    public ProcSelector(SodaInstance in, FileHolder fh){
        instance=in;
        fileHolder=fh;
        onRefresh();

        refreshButton.addActionListener(e->onRefresh());
        putButton.addActionListener(e->onPut());
        sendButton.addActionListener(e->onSendMulti());
        processList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() > 1) {
                    // Multi-click detected
                    onSendMulti();
                }
            }
        });
        delay.setValue(1000);
        delay.addChangeListener(e->{
            if((int)delay.getValue()<0){
                delay.setValue(0);
            }else if((int)delay.getValue()>3_600_000){
                delay.setValue(3_600_000);
            }
        });

        frame=new JFrame();
        frame.setName("ProcSelector");
        frame.setTitle("SodaJ Procedure Picker "+in.name+" "+in.fileHolder.model);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
    }

    private void onRefresh(){
        commands.clear();
        commandsInfo.clear();
        for(TreeMap<Integer,Procedure> tm:fileHolder.procedures.values()){
            for(Procedure proc:tm.values()){
                ByteDataBuilder[] cmdArr=proc.getCommandsToSend();
                if(cmdArr!=null && cmdArr.length!=0){
                    if(proc.listAllCommands()) {
                        for(ByteDataBuilder bdb:cmdArr){
                            commands.add(new ByteDataBuilder[]{bdb});
                            commandsInfo.add(bdb.toString()+" : "+proc.toString());
                        }

                    }else{
                        commands.add(cmdArr);
                        commandsInfo.add("MULTI COMMAND : "+proc.toString());
                    }
                }else{
                    commands.add(null);
                    commandsInfo.add(proc.toString());
                }
            }
        }
        if(commandsInfo.isEmpty()) return;
        processList.setListData(commandsInfo.toArray(new String[0]));
        processList.updateUI();
    }

    private void onPut(){
        if(processList.isSelectionEmpty())return;

        ByteDataBuilder[] cmdArr=commands.get(processList.getSelectedIndex());
        if(cmdArr==null)return;

        StringBuilder cmd=new StringBuilder("0x");
        for(ByteDataBuilder bdb:cmdArr){
            cmd.append(bdb.toHexString(false,false));
        }

        instance.commandInput.setText(cmd.toString());
    }

    private void onSendMulti(){
        if(processList.isSelectionEmpty()) return;

        ArrayList<ByteDataBuilder> toDo=new ArrayList<>();

        for(int i: processList.getSelectedIndices()){
            ByteDataBuilder[] cmdArr=commands.get(i);
            if(cmdArr==null)continue;
            for(ByteDataBuilder bdb:cmdArr) {
                toDo.add(bdb);
            }
        }

        if(toDo.size()==0) return;

        if(runner!=null)runner.interrupt();
        progress.setMaximum(toDo.size());
        progress.setValue(progress.getMaximum());

        runner=new Thread(() -> {
            for(ByteDataBuilder bdc:toDo) {
                instance.worker.sdc.sendSingleCmd(bdc);
                progress.setValue(progress.getValue()-1);
                try {
                    Thread.sleep((int)delay.getValue());
                } catch (InterruptedException e) {
                    return;
                }
                if(Thread.currentThread().isInterrupted()) return;
            }
            progress.setValue(progress.getMaximum());
        });

        runner.start();
    }

    public void invalidate(){
        if(runner!=null) runner.interrupt();
    }
}
