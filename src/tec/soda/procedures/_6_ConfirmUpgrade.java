package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _6_ConfirmUpgrade extends Procedure {
    int maxTime;//Max waiting time
    public static final int ID=6;

    public _6_ConfirmUpgrade(){
        super(ID);
    }

    @Override
    public void init2() {
        maxTime=Integer.parseInt(param[1]);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return null;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "Confirm Upgrade";
    }
}
