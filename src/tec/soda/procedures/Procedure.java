package tec.soda.procedures;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.fileHandleres.InfFile;

import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-03-29.
 */
public abstract class Procedure implements Comparable<Procedure> {
    public String name,cmd,cmdKind, msgNG, msgOK, fileID, jumpToFile;
    public int waitAck, delayMS, sort, procedureID;
    public boolean jumpOnNG;

    public String[] param;

    public InfFile inf;
    public Procedure jumpNG,jumpOK;

    public final int ID;

    protected Procedure(int ID){
        this.ID=ID;
    }

    public final void init(InfFile inf,String fileID, int number,String name,String sort,String cmd,String cmdKind,String waitAck,String delayMS,String... para) {
        this.fileID = fileID;
        this.procedureID = number;
        this.name = name;
        this.cmd = cmd;//sometimes as hex
        this.cmdKind = cmdKind;//sometimes TV
        this.waitAck = Integer.parseInt(waitAck);
        this.delayMS = Integer.parseInt(delayMS);
        this.sort = Integer.parseInt(sort);
        this.param = para;
        this.msgNG = param[4];
        this.msgOK = param[5];
        this.jumpOnNG = Integer.parseInt(param[6])!=0;
        this.jumpToFile = param[7];
        this.inf=inf;
        init2();
    }

    public abstract void init2();

    public void initJumps(TreeMap<String,TreeMap<Integer,Procedure>> tree){
        try {
            jumpOK = tree.get(fileID).get(procedureID + 1);
        }catch (Exception e){
            e.printStackTrace();
            jumpOK=null;
        }
        if(jumpOnNG){//jump on fail?
            try {
                jumpNG = tree.get(jumpToFile).get(1);//Jump to file specified in para 7
            }catch (Exception e){
                e.printStackTrace();
                jumpNG = null;
            }
        }else{
            jumpNG=null;
        }
    }

    public Procedure jump(boolean pass){
        return pass?jumpOK:jumpNG;
    }

    public abstract boolean run();

    //@Override
    //public String toString() {
    //    return "\u001b[34;1mProcedure: "+fileID+"/"+ procedureID+"\u001b[0m"
    //        +"\nType: "+ID+" Name: \u001b[36m"+name+"\u001b[0m";
    //}

    @Override
    public String toString() {
        return fileID+"/"+procedureID +" Type: "+getTypeName()+" Name: "+name;
    }

    public abstract String information();

    public static Procedure get(int ID){
        switch(ID){
            case 0:return new _0_Command();
            case 1:return new _1_StringTracking();
            case 2:return new _2_BurnEED();
            case 3:return new _3_CheckModelName();
            case 4:return new _4_CheckFMVersion();
            case 5:return new _5_GetTVMAC();
            case 6:return new _6_ConfirmUpgrade();
            case 7:return new _7_CheckFunctionVersion();
            case 8:return new _8_WriteFunctionData();
            case 9:return new _9_ReadOptionCodes();
            case 10:return new _10_WriteALData();
            case 11:return new _11_PowerOnOff();
            case 12:return new _12_CheckACK();
            default:return null;
        }
    }

    public abstract ByteDataBuilder[] getCommandsToSend();

    public boolean listAllCommands(){
        return true;
    }

    @Override
    public int compareTo(Procedure procedure) {
        int temp=fileID.compareTo(procedure.fileID);
        if(temp!=0) return temp;
        return procedureID -procedure.procedureID;
    }

    public static final int UNDEFINED_STATUS=-1,NG_STATUS=0,OK_STATUS=1;

    public final String getProcedureTitle(){
        return "Procedure: "+fileID+"/"+procedureID +" Type: "+getTypeName();
    }

    public final String getProcedureName(){
        return name;
    }

    public final String getProcedureStatusMsg(int state){
        switch (state){
            case NG_STATUS: return msgNG;
            case OK_STATUS: return msgOK;
            default: return "Undefined";
        }
    }

    public abstract String getTypeName();
}
