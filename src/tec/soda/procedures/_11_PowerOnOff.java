package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.fileHandleres.FileHolder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _11_PowerOnOff extends Procedure {
    int flag;
    //0 - power off
    //1 - power on
    //2 - ???->off->on
    int offTime;
    // if flag==2 how long the off state should be
    public static final int ID=11;

    public _11_PowerOnOff(){
        super(ID);
    }

    @Override
    public void init2(FileHolder files) {
        flag=Integer.parseInt(param[1]);
        offTime=Integer.parseInt(param[2]);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "--> Flag: "+flag;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "Power Control";
    }

    @Override
    public String getExtraInformation() {
        return "";
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        return new ByteDataBuilder[0];
    }
}
