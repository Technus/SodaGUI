package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _10_WriteALData extends Procedure {
    public static final int ID=10;

    public _10_WriteALData(){
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
    public String getTypeName() {
        return "Write Ambilight Data";
    }
}
