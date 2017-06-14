package tec.soda.dataContainers;

import jssc.SerialPort;
import jssc.SerialPortException;
import tec.soda.procedures.Procedure;

/**
 * Created by daniel.peczkowski on 2017-04-13.
 */
public class SerialDataContainer {
    public ByteDataBuilder tx = new ByteDataBuilder(), rx = new ByteDataBuilder();

    private boolean lastTx=false;

    public StyledSodaDocument
            info = new StyledSodaDocument(), rxtx = new StyledSodaDocument(),
            txText = new StyledSodaDocument(), rxText = new StyledSodaDocument(),
            txHex = new StyledSodaDocument(), rxHex = new StyledSodaDocument(),
            txInfoText = new StyledSodaDocument(), rxInfoText = new StyledSodaDocument(),
            txInfoHex = new StyledSodaDocument(), rxInfoHex = new StyledSodaDocument();

    private SerialPort port;

    public SerialDataContainer(SerialPort serial) {
        port = serial;

        if (port != null) {
            //region AddListener
            try {
                port.removeEventListener();
            } catch (SerialPortException e) {
                //e.printStackTrace();
            }
            try {
                port.addEventListener(serialPortEvent -> {
                    if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
                        try {
                            byte[] bytes = port.readBytes(serialPortEvent.getEventValue());
                            appendTextRx(new ByteDataBuilder(bytes));
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                    }
                },SerialPort.MASK_RXCHAR);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            //endregion
        }
    }

    public void send(ByteDataBuilder bds) {
        try {
            port.writeBytes(bds.toBytes());
            appendTextTx(bds);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void send(byte[] bytes) {
        try {
            port.writeBytes(bytes);
            ByteDataBuilder temp=new ByteDataBuilder(bytes);
            appendTextTx(temp);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    //This allows nice new line at end
    public void sendSingleCmd(ByteDataBuilder bds) {
        try {
            port.writeBytes(bds.toBytes());
            appendCmdTx(bds);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void sendSingleCmd(byte[] bytes) {
        try {
            port.writeBytes(bytes);
            ByteDataBuilder temp=new ByteDataBuilder(bytes);
            appendCmdTx(temp);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        info.clear();

        txText.clear();
        txHex.clear();

        txInfoText.clear();
        txInfoHex.clear();

        rxText.clear();
        rxHex.clear();

        rxInfoText.clear();
        rxInfoHex.clear();

        tx.clear();
        rx.clear();
        rxtx.clear();
    }

    private void appendTextRx(ByteDataBuilder bdb){
        rx.append(bdb);
        String temp1=bdb.toMultilineString();
        String temp2=bdb.toHexString(true,false);
        rxText.append(StyledSodaDocument.DATA,temp1);
        rxHex.append(StyledSodaDocument.DATA,temp2);
        rxInfoText.append(StyledSodaDocument.DATA,temp1);
        rxInfoHex.append(StyledSodaDocument.DATA,temp2);
        if(lastTx){
            lastTx=false;
            rxtx.append(StyledSodaDocument.RX,"\n"+temp1+"\n");
        } else {
            rxtx.append(StyledSodaDocument.RX,temp1);
        }
    }

    private void appendCmdTx(ByteDataBuilder bdb){
        tx.append(bdb);
        String temp1="\n"+bdb.toString()+"\n";//Full encoded in E1 page + new line at end
        String temp2="\n"+bdb.toHexString(true,false)+"\n";
        txText.append(StyledSodaDocument.DATA,temp1);
        txHex.append(StyledSodaDocument.DATA,temp2);
        txInfoText.append(StyledSodaDocument.DATA,temp1);
        txInfoHex.append(StyledSodaDocument.DATA,temp2);
        if(lastTx){
            rxtx.append(StyledSodaDocument.TX,temp1);
        } else {
            lastTx=true;
            rxtx.append(StyledSodaDocument.TX,temp1);
        }
    }

    private void appendTextTx(ByteDataBuilder bdb){
        tx.append(bdb);
        String temp1=bdb.toMultilineString();
        String temp2=bdb.toHexString(true,false);
        txText.append(StyledSodaDocument.DATA,temp1);
        txHex.append(StyledSodaDocument.DATA,temp2);
        txInfoText.append(StyledSodaDocument.DATA,temp1);
        txInfoHex.append(StyledSodaDocument.DATA,temp2);
        if(lastTx){
            rxtx.append(StyledSodaDocument.TX,temp1);
        } else {
            lastTx=true;
            rxtx.append(StyledSodaDocument.NG,"\n"+temp1+"\n");
        }
    }

    public void addProcedureHead(Procedure proc) {
        String temp="\n\n"+proc.getProcedureTitle()+"\n";
        info.append(StyledSodaDocument.TITLE,temp);
        rxInfoText.append(StyledSodaDocument.TITLE,temp);
        txInfoText.append(StyledSodaDocument.TITLE,temp);
        rxInfoHex.append(StyledSodaDocument.TITLE,temp);
        txInfoHex.append(StyledSodaDocument.TITLE,temp);

        temp=proc.getProcedureName()+"\n";
        info.append(StyledSodaDocument.INFO,temp);
        rxInfoText.append(StyledSodaDocument.INFO,temp);
        txInfoText.append(StyledSodaDocument.INFO,temp);
        rxInfoHex.append(StyledSodaDocument.INFO,temp);
        txInfoHex.append(StyledSodaDocument.INFO,temp);
    }

    public void addProcedureTail(Procedure proc,int status) {
        String temp="\n"+proc.getProcedureStatusMsg(status)+"\n";
        int style=status==Procedure.NG_STATUS?StyledSodaDocument.NG:StyledSodaDocument.OK;

        info.append(style,temp);
        rxInfoText.append(style,temp);
        txInfoText.append(style,temp);
        rxInfoHex.append(style,temp);
        txInfoHex.append(style,temp);
    }
}
