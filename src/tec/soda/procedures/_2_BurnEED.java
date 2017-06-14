package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _2_BurnEED extends Procedure {
    public static final int ID=2;

    public _2_BurnEED(){
        super(ID);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public void init2() {

    }

    @Override
    public String information() {
        return null;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return new ByteDataBuilder[0];
    }

    @Override
    public boolean listAllCommands() {
        return false;
    }

    @Override
    public String getTypeName() {
        return "Burn EED";
    }
}
