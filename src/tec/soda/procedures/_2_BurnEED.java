package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.desiredResponseLength;
import tec.soda.fileHandleres.EedFile;
import tec.soda.fileHandleres.FileHolder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _2_BurnEED extends Procedure {
    public static final int ID = 2;
    public EedFile eed;

    public _2_BurnEED() {
        super(ID);
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public void init2(FileHolder files) {
        eed = files.eedFile;
    }

    @Override
    public String information() {
        return null;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return eed != null ? eed.commands : new ByteDataBuilder[0];
    }

    @Override
    public boolean listAllCommands() {
        return eed != null && eed.commands.length <= 16;
    }

    @Override
    public String getTypeName() {
        return "Burn EED";
    }

    @Override
    public String getExtraInformation() {
        if(listAllCommands()) return "";
        return "Commands: "+eed.commands.length;
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        if(eed!=null){
            ByteDataBuilder[] arr=new ByteDataBuilder[eed.commands.length];
            for(int i=0;i<arr.length;i++){
                arr[i]=new desiredResponseLength(0);
            }
            return arr;
        } else return new ByteDataBuilder[0];
    }
}
