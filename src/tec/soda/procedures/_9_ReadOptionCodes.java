package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

import static tec.soda.Soda.config;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _9_ReadOptionCodes extends Procedure {
    String command;//Look for
    public String codes;
    public static final int ID=9;
    public static ByteDataBuilder CMD;
    public ByteDataBuilder data;
    static{
        if (config.containsKey("ReadOptionCodes_CMD")) {
            CMD = new ByteDataBuilder(config.getProperty("ReadOptionCodes_CMD").replace(" ",""),true);
        } else {
            CMD = new ByteDataBuilder("6E 51 86 01 FE F0 12 06 00 A2".replace(" ", ""), true);
            config.setProperty("ReadOptionCodes_CMD", "6E 51 86 01 FE F0 12 06 00 A2");
        }
    }

    public _9_ReadOptionCodes(){
        super(ID);
    }

    @Override
    public void init2() {
        command=param[1];
        codes =param[2];
        String[] codePieces=codes.split(" ");
        if(codePieces.length!=8){
            codePieces=new String[8];
            for(int i=0;i<codePieces.length;i++){
                codePieces[i]=codes.substring(i*5,(i+1)*5);
            }
        }
        data=new ByteDataBuilder();
        for(int i=0;i<codePieces.length;i++){
            int piece=Integer.parseInt(codePieces[i]);
            data.append((byte)(piece&0xff));
            data.append((byte)((piece>>>8)&0xff));
        }
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->  Cmd: "+CMD.toHexString(false,false)+
             "\n-->  Cmd: "+CMD.toString()+
             "\n-->CodeS: "+codes+
             "\n-->CodeB: "+data.toHexString(false,false)+
             "\n-->CodeB: "+data.toString();
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{CMD};
    }

    @Override
    public String toString() {
        return fileID+"/"+procedureID +" Type: "+ID+" Name: "+name+" Codes: "+data.toString();
    }

    @Override
    public String getTypeName() {
        return "Read Option Codes";
    }
}
