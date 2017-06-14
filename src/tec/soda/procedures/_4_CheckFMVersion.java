package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

import static tec.soda.Soda.config;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _4_CheckFMVersion extends Procedure {
    public String version;//Look for
    public static final int ID=4;
    public static ByteDataBuilder CMD;
    static{
        if (config.containsKey("CheckFMVersion_CMD")) {
            CMD = new ByteDataBuilder(config.getProperty("CheckFMVersion_CMD").replace(" ",""),true);
        } else {
            CMD = new ByteDataBuilder("6E 51 86 01 FE E4 13 00 00 B1".replace(" ", ""), true);
            config.setProperty("CheckFMVersion_CMD", "6E 51 86 01 FE E4 13 00 00 B1");
        }
    }

    public _4_CheckFMVersion(){
        super(ID);
    }

    @Override
    public void init2() {
        version = param[1];
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->  Cmd: "+CMD.toHexString(false,false)+
             "\n-->  Cmd: "+CMD.toString()+
             "\n-->  Ver: "+version;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{CMD};
    }

    @Override
    public String toString() {
        return fileID+"/"+procedureID +" Type: "+ID+" Name: "+name+" Ver: "+version;
    }

    @Override
    public String getTypeName() {
        return "Check Firmware";
    }
}
