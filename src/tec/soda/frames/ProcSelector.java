package tec.soda.frames;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.desiredResponseData;
import tec.soda.dataContainers.desiredResponseLength;
import tec.soda.dataContainers.responsePacket;
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
    private JSpinner responseDelay;
    private JSpinner trackingDelay;
    private JButton xButton;
    public JFrame frame;

    public final static int checkDelay=500;
    private final SodaInstance instance;
    private final FileHolder fileHolder;
    private final ArrayList<ByteDataBuilder[]> commands=new ArrayList<>();
    private final ArrayList<ByteDataBuilder[]> responses=new ArrayList<>();
    private final ArrayList<String> commandsInfo =new ArrayList<>();

    private Thread runner;

    public ProcSelector(SodaInstance in, FileHolder fh){
        instance=in;
        fileHolder=fh;
        onRefresh();

        refreshButton.addActionListener(e->onRefresh());
        putButton.addActionListener(e->onPut());
        sendButton.addActionListener(e->onSendMulti());
        xButton.addActionListener(e-> interruptThread());
        processList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() > 1) {
                    // Multi-click detected
                    onSendMulti();
                }
            }
        });
        responseDelay.setValue(2000);
        responseDelay.addChangeListener(e->{
            if((int) responseDelay.getValue()<0){
                responseDelay.setValue(0);
            }else if((int) responseDelay.getValue()>3_600_000){
                responseDelay.setValue(3_600_000);
            }
        });
        trackingDelay.setValue(30000);
        trackingDelay.addChangeListener(e->{
            if((int)trackingDelay.getValue()<0){
                trackingDelay.setValue(0);
            }else if((int)trackingDelay.getValue()>3_600_000){
                trackingDelay.setValue(3_600_000);
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

        responses.clear();

        for(TreeMap<Integer,Procedure> tm:fileHolder.procedures.values()){
            for(Procedure proc:tm.values()){
                ByteDataBuilder[] cmdArr=proc.getCommandsToSend();
                if(cmdArr!=null && cmdArr.length==0)cmdArr=null;
                ByteDataBuilder[] reArr=proc.getResponsesToReceive();
                if(reArr!=null && reArr.length==0)reArr=null;

                if(proc.listAllCommands() && cmdArr!=null) {
                    for(int i=0;i<cmdArr.length;i++){
                        commands.add(new ByteDataBuilder[]{cmdArr[i]});

                        if(reArr!=null) responses.add(new ByteDataBuilder[]{reArr[i]});
                        else responses.add(null);

                        commandsInfo.add(cmdArr[i].toString()+" : "+proc.toString());
                    }
                }else{
                    commands.add(cmdArr);
                    responses.add(reArr);
                    commandsInfo.add((cmdArr==null?"":"MULTI COMMAND : ")+proc.toString());

                    if(cmdArr!=null && reArr!=null && cmdArr.length!=reArr.length) {
                        commands.clear();
                        responses.clear();
                        commandsInfo.clear();
                        processList.setListData(commandsInfo.toArray(new String[0]));
                        processList.updateUI();
                        JOptionPane.showMessageDialog(frame, "Bad length at:\n" + proc.toString() + " " + cmdArr.length + " " + reArr.length);
                        return;
                    }
                }
            }
        }
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

    private void onSendMulti() {
        if (processList.isSelectionEmpty()) return;

        ArrayList<ByteDataBuilder> toSend = new ArrayList<>();
        ArrayList<ByteDataBuilder> toGet = new ArrayList<>();

        for (int i : processList.getSelectedIndices()) {
            ByteDataBuilder[] cmdArr = commands.get(i);
            ByteDataBuilder[] reArr = responses.get(i);
            if (cmdArr != null && reArr != null) {
                for (int j = 0; j < Math.max(cmdArr.length, reArr.length); j++) {
                    toSend.add(cmdArr[j]);
                    toGet.add(reArr[j]);
                }
            } else if (cmdArr != null) {
                for (ByteDataBuilder aCmdArr : cmdArr) {
                    toSend.add(aCmdArr);
                    toGet.add(null);
                }
            } else if (reArr != null) {
                for (ByteDataBuilder aReArr : reArr) {
                    toSend.add(null);
                    toGet.add(aReArr);
                }
            }
        }

        if (toSend.size() == 0) return;

        if (runner != null) runner.interrupt();
        progress.setMaximum(toSend.size());
        progress.setValue(progress.getMaximum());

        runner = new Thread(() -> {
            int searchStart = instance.worker.sdc.rx.length();

            commandIterator:
            for (int i = 0; i < Math.max(toSend.size(), toGet.size()); i++) {
                if (toSend.get(i) != null) instance.worker.sdc.sendSingleCmd(toSend.get(i));
                progress.setValue(progress.getValue() - 1);

                int timeSpent=0,maxWaitTime=(int)((toSend.get(i)==null)?trackingDelay.getValue():responseDelay.getValue());
                while(timeSpent<=maxWaitTime){
                    try {
                        Thread.sleep(checkDelay);
                        timeSpent+=checkDelay;
                    } catch (InterruptedException e) {
                        progress.setValue(progress.getMaximum());
                        return;
                    }
                    if (toGet.get(i) instanceof desiredResponseData) {
                        responsePacket lastResponse = responsePacket.getLastResponse(instance.worker.sdc.rx, searchStart);
                        if (lastResponse != null) {
                            if (lastResponse.toString().equals(toGet.get(i).toString())) {
                                searchStart = lastResponse.endPos;
                                continue commandIterator;
                            } else {
                                if (lastResponse.toString().contains(toGet.get(i).toString())) {
                                    int userInput = retryProcedure.showDialog("Response contains:\n" + toSend.get(i) + " (dat)-> " + toGet.get(i) + " =/= " + lastResponse.toString());
                                    switch (userInput) {
                                        case JOptionPane.YES_OPTION:
                                            i--;
                                            continue commandIterator;
                                        case JOptionPane.NO_OPTION:
                                            continue commandIterator;
                                        default:
                                            return;
                                    }
                                }
                            }
                        }
                    } else if (toGet.get(i) instanceof desiredResponseLength) {
                        responsePacket lastResponse = responsePacket.getLastResponse(instance.worker.sdc.rx, searchStart);
                        if (lastResponse != null && lastResponse.length() == toGet.get(i).length()) {
                            searchStart = lastResponse.endPos;
                            continue commandIterator;
                        }
                    } else if (toGet.get(i) != null) {//Regular byte data
                        int indexOfString = instance.worker.sdc.rx.lastIndexOf(toGet.get(i));
                        if (indexOfString >= searchStart) {
                            searchStart = indexOfString + toGet.get(i).length();
                            continue commandIterator;
                        }
                    }
                }

                /* FINAL CHECK */
                if (toGet.get(i) instanceof desiredResponseData) {
                    responsePacket lastResponse = responsePacket.getLastResponse(instance.worker.sdc.rx, searchStart);
                    if (lastResponse == null) {
                        int userInput = retryProcedure.showDialog("No response:\n" + toSend.get(i) + " (dat)-> " + toGet.get(i));
                        switch (userInput) {
                            case JOptionPane.YES_OPTION:
                                i--;
                                continue commandIterator;
                            case JOptionPane.NO_OPTION:
                                continue commandIterator;
                            default:
                                return;
                        }
                    } else if (!lastResponse.toString().equals(toGet.get(i).toString())) {
                        if (lastResponse.toString().contains(toGet.get(i).toString())) {
                            int userInput = retryProcedure.showDialog("Response contains:\n" + toSend.get(i) + " (dat)-> " + toGet.get(i) + " =/= " + lastResponse.toString());
                            switch (userInput) {
                                case JOptionPane.YES_OPTION:
                                    i--;
                                    continue commandIterator;
                                case JOptionPane.NO_OPTION:
                                    continue commandIterator;
                                default:
                                    return;
                            }
                        } else {
                            int userInput = retryProcedure.showDialog("Invalid response:\n" + toSend.get(i) + " (dat)-> " + toGet.get(i) + " =/= " + lastResponse.toString());
                            switch (userInput) {
                                case JOptionPane.YES_OPTION:
                                    i--;
                                    continue commandIterator;
                                case JOptionPane.NO_OPTION:
                                    continue commandIterator;
                                default:
                                    return;
                            }
                        }
                    } else {
                        searchStart = lastResponse.endPos;
                        //continue commandIterator;
                    }
                } else if (toGet.get(i) instanceof desiredResponseLength) {
                    responsePacket lastResponse = responsePacket.getLastResponse(instance.worker.sdc.rx, searchStart);
                    if (lastResponse == null) {
                        int userInput = retryProcedure.showDialog("No response:\n" + toSend.get(i) + " (len)-> " + toGet.get(i).length());
                        switch (userInput) {
                            case JOptionPane.YES_OPTION:
                                i--;
                                continue commandIterator;
                            case JOptionPane.NO_OPTION:
                                continue commandIterator;
                            default:
                                return;
                        }
                    } else if (lastResponse.length() != toGet.get(i).length()) {
                        int userInput = retryProcedure.showDialog("Invalid response:\n" + toSend.get(i) + " (len)-> " + toGet.get(i).length() + " =/= " + lastResponse.length());
                        switch (userInput) {
                            case JOptionPane.YES_OPTION:
                                i--;
                                continue commandIterator;
                            case JOptionPane.NO_OPTION:
                                continue commandIterator;
                            default:
                                return;
                        }
                    } else {
                        searchStart = lastResponse.endPos;
                        //continue commandIterator;
                    }
                } else if (toGet.get(i) != null) {//Regular byte data
                    int indexOfString = instance.worker.sdc.rx.lastIndexOf(toGet.get(i));
                    if (indexOfString < searchStart) {
                        int userInput = retryProcedure.showDialog("Not found:\n" + toSend.get(i) + " (str)-> " + toGet.get(i));
                        switch (userInput) {
                            case JOptionPane.YES_OPTION:
                                i--;
                                continue commandIterator;
                            case JOptionPane.NO_OPTION:
                                continue commandIterator;
                            default:
                                return;
                        }
                    } else {
                        searchStart = indexOfString + toGet.get(i).length();
                        //continue commandIterator;
                    }
                }

                if (Thread.currentThread().isInterrupted()) {
                    progress.setValue(progress.getMaximum());
                    return;
                }
            }
            progress.setValue(progress.getMaximum());
        });

        runner.start();
    }

    public void interruptThread(){
        if(runner!=null) runner.interrupt();
    }
}
