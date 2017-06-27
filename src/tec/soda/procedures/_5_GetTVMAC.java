package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.desiredResponseLength;
import tec.soda.fileHandleres.FileHolder;

import static tec.soda.Soda.config;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _5_GetTVMAC extends Procedure {
    public static final int ID=5;
    public static ByteDataBuilder CMD;
    static{
        if (config.containsKey("GetTVMAC_CMD")) {
            CMD = new ByteDataBuilder(config.getProperty("GetTVMAC_CMD").replace(" ",""),true);
        } else {
            CMD = new ByteDataBuilder("6E 51 86 01 FE F0 01 01 00 B6".replace(" ", ""), true);
            config.setProperty("GetTVMAC_CMD", "6E 51 86 01 FE F0 01 01 00 B6");
        }
    }

    public _5_GetTVMAC(){
        super(ID);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public void init2(FileHolder files) {

    }

    @Override
    public String information() {
        return "-->  Cmd: "+CMD.toHexString(false,false)+
             "\n-->  Cmd: "+CMD.toString();
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{CMD};
    }

    @Override
    public String getTypeName() {
        return "Read MAC";
    }

    @Override
    public String getExtraInformation() {
        return "";
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        return new ByteDataBuilder[]{
                new desiredResponseLength(6)
        };
    }
}
