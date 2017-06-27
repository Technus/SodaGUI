package tec.soda.fileHandleres;

import tec.soda.dataContainers.AmbilightDataContainer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-03-27.
 */
public class InfFile extends DataFile {//Header
    public final int serialSpeed, packetSizeEED, controlFlowKind,
            waitAfterCommandHeadMilliSeconds, waitPowerOnSeconds, waitFileDetectSeconds,
            waitUpgradeSeconds, waitRebootSeconds, waitCheckVersionSeconds,
            ambilightBoardNum, ambilightSaveTimeMilliSeconds;
    public final String commandHead, nameOfEED, nameOfTest, fileGroupID;
    public final boolean enableBurnEED, enableCommandHead, enableBarcodeScan, enableSaveLog, enableMarkMAC;
    private final TreeMap<Integer,AmbilightDataContainer> ambilightDataContainerMap;

    public InfFile(File f) throws IOException {
        super(f);
        serialSpeed = getInt("RS232", "BaudRate_TV1", 115200);

        enableBarcodeScan = getInt("Miscellaneous", "Barcode_Enable", 0) != 0;

        if (enableBurnEED = getInt("EED", "Enable", 0) != 0) {
            nameOfEED = getString("EED", "FileName", null);
            packetSizeEED = getInt("Miscellaneous", "CmdEEDPacketSize", 0);
        } else {
            nameOfEED = null;
            packetSizeEED = -1;
        }

        if (enableCommandHead = getInt("FactoryMode", "Head_Enable", 0) != 0) {//For board connection check???
            commandHead = getString("FactoryMode", "CommandHead", null);
            waitAfterCommandHeadMilliSeconds = getInt("FactoryMode", "DelayAfterHead", 1000);
        } else {
            commandHead = null;
            waitAfterCommandHeadMilliSeconds = -1;
        }

        enableSaveLog=getInt("Miscellaneous", "SavelogEnable", 0)!=0;
        enableMarkMAC=getInt("Miscellaneous", "MarkMacEnable", 0)!=0;

        nameOfTest = getString("Miscellaneous", "TestCaseName", null);
        String[] partsOfName=nameOfTest.split("_");
        String groupID="";
        for(int i=0;i<partsOfName.length-1;i++){
            groupID+=partsOfName[i]+"_";
        }
        fileGroupID=groupID;

        waitPowerOnSeconds = getInt("Timeout_Setting", "WaitPowerOn", 300);
        waitFileDetectSeconds = getInt("Timeout_Setting", "FileDetect", 300);
        waitUpgradeSeconds = getInt("Timeout_Setting", "WaitUpgrade", 300);
        waitRebootSeconds = getInt("Timeout_Setting", "Reboot", 300);
        waitCheckVersionSeconds = getInt("Timeout_Setting", "WaitChkVer", 300);

        controlFlowKind = getInt("ControlFlow", "Kind", 1);

        ambilightBoardNum = getInt("AL_Setting", "AL_BoardNum", -1);
        ambilightSaveTimeMilliSeconds = getInt("AL_Setting", "AL_SaveTime", 1000);

        ambilightDataContainerMap = AmbilightDataContainer.getData(
                ambilightBoardNum,
                ambilightSaveTimeMilliSeconds,
                getSectionContents("AL_Setting"));

        System.out.println(">>>INF<<< "+f.getName()+" "+fileGroupID+" "+nameOfTest+" "+nameOfEED);
    }

    public TreeMap<Integer,AmbilightDataContainer> getAmbilightDataContainerMapClone(){
        TreeMap<Integer,AmbilightDataContainer> newMap = new TreeMap<>();
        for(Map.Entry<Integer,AmbilightDataContainer> entry:ambilightDataContainerMap.entrySet()){
            newMap.put(entry.getKey(),entry.getValue().clone());
        }
        return newMap;
    }
}
