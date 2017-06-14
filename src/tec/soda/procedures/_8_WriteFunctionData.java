package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _8_WriteFunctionData extends Procedure {
    ByteDataBuilder command=new ByteDataBuilder();
    String data;
    public static final int ID=8;

    public _8_WriteFunctionData(){
        super(ID);
    }

    @Override
    public void init2() {
        command.append(param[1],true);//hex command
        data = param[2];
        command.append((char)(data.length())).append(data,false);
        byte sum=0x00;
        for(byte b:command.toBytes()){
            sum^=b;
        }
        command.append(sum);
        //USEFUL later for board connection check
        //if(inf.enableCommandHead){
        //    command=new ByteDataBuilder(inf.commandHead,true).append(command);
        //}
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->  Cmd: "+command.toHexString(false,false)+
             "\n-->  Cmd: "+command.toString()+
             "\n--> Data: "+new ByteDataBuilder(data,false).toHexString(false,false)+
             "\n--> Data: "+data;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{command};
    }

    @Override
    public String getTypeName() {
        return "Write Function Data";
    }
}
