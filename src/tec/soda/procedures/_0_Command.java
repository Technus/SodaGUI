package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _0_Command extends Procedure {
    public static final int ID=0;
    public ByteDataBuilder command;
    public boolean wasChecksumOK=false;
    public ByteDataBuilder checkSum;

    public _0_Command(){
        super(ID);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public void init2() {
        command=new ByteDataBuilder(cmd,true);
        byte[] bytes=command.toBytes();
        byte sum=0x00;
        for(int i=0;i<bytes.length-1;i++){
            sum^=bytes[i];
        }
        wasChecksumOK=sum==bytes[bytes.length-1];
        checkSum=new ByteDataBuilder(sum);
    }

    @Override
    public String information() {
        return "-->  Cmd: "+command.toHexString(false,false)+
             "\n-->  Cmd: "+command.toString()+
             "\n-->Check: "+wasChecksumOK+" "+checkSum.toHexString(false,false)+" "+checkSum.toString()+(command.length()<8?" \"Check: false\" is OK if this is HEAD!":"")+
             "\n--> Kind: "+cmdKind;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{command};
    }

    @Override
    public String getTypeName() {
        return "Command";
    }
}
