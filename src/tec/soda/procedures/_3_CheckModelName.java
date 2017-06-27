package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.desiredResponseData;
import tec.soda.fileHandleres.FileHolder;

import static tec.soda.Soda.config;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _3_CheckModelName extends Procedure {
    public ByteDataBuilder model;//Look for
    public static final int ID = 3;
    public static ByteDataBuilder CMD;
    static{
        if (config.containsKey("CheckModelName_CMD")) {
            CMD = new ByteDataBuilder(config.getProperty("CheckModelName_CMD").replace(" ",""),true);
        } else {
            CMD = new ByteDataBuilder("6E 51 86 01 FE F0 18 04 00 AA".replace(" ", ""), true);
            config.setProperty("CheckModelName_CMD", "6E 51 86 01 FE F0 18 04 00 AA");
        }
    }

    public _3_CheckModelName() {
        super(ID);
    }

    @Override
    public void init2(FileHolder files) {
        model = new ByteDataBuilder(param[1],false);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->  Cmd: " + CMD.toHexString(false, false) +
             "\n-->  Cmd: " + CMD.toString() +
             "\n-->Model: " + model.toString();
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[]{CMD};
    }

    @Override
    public String getTypeName() {
        return "Check Model";
    }

    @Override
    public String getExtraInformation() {
        return "Model: "+model.toString();
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        return new ByteDataBuilder[]{
                new desiredResponseData(model)
        };
    }
}
