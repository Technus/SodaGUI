package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public class _1_StringTracking extends Procedure {
    public String lookForString;
    public int lookForMiliSeconds;
    public int behaviourFlag;
    //1 - matched string will be stopped and shown fail
    //0 - matched string go to next and show ok
    public static final int ID=1;

    public _1_StringTracking(){
        super(ID);
    }

    @Override
    public void init2() {
        lookForString = param[1];
        lookForMiliSeconds =Integer.parseInt(param[2]);
        behaviourFlag =Integer.parseInt(param[3]);//default 0
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public String information() {
        return "-->Track: " + lookForString+
             "\n--> Flag: " + behaviourFlag;
    }

    @Override
    public ByteDataBuilder[] getCommandsToSend() {
        return null;
    }

    @Override
    public String toString() {
        return fileID+"/"+procedureID +" Type: "+ID+" Name: "+name+" Track: "+lookForString;
    }

    @Override
    public String getTypeName() {
        return "String Tracking";
    }
}
