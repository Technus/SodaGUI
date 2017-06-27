package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.fileHandleres.FileHolder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _1_StringTracking extends Procedure {
    public ByteDataBuilder lookFor;
    public int lookForMiliSeconds;
    public int behaviourFlag;
    //1 - matched string will be stopped and shown fail
    //0 - matched string go to next and show ok
    public static final int ID=1;

    public _1_StringTracking(){
        super(ID);
    }

    @Override
    public void init2(FileHolder files) {
        lookFor = new ByteDataBuilder(param[1],false);
        lookForMiliSeconds =Integer.parseInt(param[2]);
        behaviourFlag =Integer.parseInt(param[3]);//default 0
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->Track: " + lookFor.toString()+
             "\n--> Flag: " + behaviourFlag;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return null;
    }

    @Override
    public String getTypeName() {
        return "String Tracking";
    }

    @Override
    public String getExtraInformation() {
        return "Track: "+lookFor.toString();
    }

    @Override
    public ByteDataBuilder[] getResponsesToReceive() {
        return new ByteDataBuilder[]{
                new ByteDataBuilder(lookFor)
        };
    }
}
