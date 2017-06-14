package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _7_CheckFunctionVersion extends Procedure {
    ByteDataBuilder command=new ByteDataBuilder();
    public boolean wasChecksumOK=false;
    public ByteDataBuilder checkSum;
    ByteDataBuilder data;
    String func;
    public static final int ID=7;

    public _7_CheckFunctionVersion(){
        super(ID);
    }

    @Override
    public void init2() {
        command.append(param[1],true);//hex command
        func =param[2];
        data =new ByteDataBuilder(func,false);

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
             "\n-->DataS: "+ func+
             "\n-->DataB: "+ data.toHexString(false,false)+
             "\n-->DataB: "+ data.toString();
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{command};
    }

    @Override
    public String toString() {
        return fileID+"/"+procedureID +" Type: "+ID+" Name: "+name+" Data: "+ data.toString();
    }

    @Override
    public String getTypeName() {
        return "Check Function Version";
    }
}
