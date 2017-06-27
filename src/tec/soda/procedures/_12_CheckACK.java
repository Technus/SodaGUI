package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.desiredResponseData;
import tec.soda.fileHandleres.FileHolder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _12_CheckACK extends Procedure {
    ByteDataBuilder command=new ByteDataBuilder();
    String dataStr;
    ByteDataBuilder data;
    public boolean wasChecksumOK=false;
    public ByteDataBuilder checkSum;
    public static final int ID = 12;

    public _12_CheckACK() {
        super(ID);
    }

    @Override
    public void init2(FileHolder files) {
        command.append(param[1],true);//hex command
        dataStr = param[2];
        if(dataStr.equals("0")) dataStr = null;
        if(dataStr!=null){
            data=new ByteDataBuilder(dataStr.replace(" ",""),true);
        }else {
            data=new ByteDataBuilder();
        }

        byte[] bytes=command.toBytes();
        byte sum=0x00;
        for(int i=0;i<bytes.length-1;i++){
            sum^=bytes[i];
        }
        wasChecksumOK=sum==bytes[bytes.length-1];
        checkSum=new ByteDataBuilder(sum);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->  Cmd: "+command.toHexString(false,false)+
             "\n-->  Cmd: "+command.toString()+
             "\n-->Check: "+wasChecksumOK+" "+checkSum.toHexString(false,false)+" "+checkSum.toString()+(command.length()<8?" Check: false is OK if this is HEAD!":"")+
             "\n-->DataS: "+dataStr+
             "\n-->DataB: "+data.toHexString(false,false)+
             "\n-->DataB: "+data.toString();
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{command};
    }

    @Override
    public String getTypeName() {
        return "Check Data";
    }

    @Override
    public String getExtraInformation() {
        return "Ack: "+(data.length()>0?data.toString():dataStr);
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        return new ByteDataBuilder[]{
                new desiredResponseData(data)
        };
    }
}
