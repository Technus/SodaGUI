package tec.soda.dataContainers;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-05-17.
 */
public class AmbilightDataContainer {
    public int boardNum, saveTimeMilliSeconds;

    public int moduleIndex;
    public boolean enable;
    public int RR,RG,RB,GR,GG,GB,BR,BG,BB,factorR,factorG,factorB;
    public int[] data;
    public int[] matrixData=new int[9];
    public int[] factorData=new int[3];

    public AmbilightDataContainer(int boardNum,int saveTimeMilliSeconds,
                                  boolean enable, int moduleIndex, int... data){
        this.boardNum=boardNum;
        this.saveTimeMilliSeconds=saveTimeMilliSeconds;
        this.enable=enable;
        this.moduleIndex=moduleIndex;
        this.data=data;
        matrixData[0]=RR=data[0];matrixData[1]=RG=data[1];matrixData[2]=RB=data[2];
        matrixData[3]=GR=data[3];matrixData[4]=GG=data[4];matrixData[5]=GB=data[5];
        matrixData[6]=BR=data[6];matrixData[7]=BG=data[7];matrixData[8]=BB=data[8];
        factorData[0]=factorR=data[9];
        factorData[1]=factorG=data[10];
        factorData[2]=factorB=data[11];
    }

    @Override
    public AmbilightDataContainer clone(){
        return new AmbilightDataContainer(boardNum,saveTimeMilliSeconds,enable,moduleIndex,data);
    }

    public static TreeMap<Integer,AmbilightDataContainer> getData(int boardNum, int saveTimeMilliSeconds,Map<String,String> info){
        TreeMap<Integer,AmbilightDataContainer> map=new TreeMap<>();
        int i=1;
        if(info==null){
            return null;
        }
        while (info.get("AL_Enable"+i)!=null){
            map.put(i,new AmbilightDataContainer(
                    boardNum,
                    saveTimeMilliSeconds,
                    Integer.parseInt(info.get("AL_Enable"+i))!=0,
                    Integer.parseInt(info.get("ALModule_Index"+i)),
                    Integer.parseInt(info.get("RR"+i)),
                    Integer.parseInt(info.get("RG"+i)),
                    Integer.parseInt(info.get("RB"+i)),
                    Integer.parseInt(info.get("GR"+i)),
                    Integer.parseInt(info.get("GG"+i)),
                    Integer.parseInt(info.get("GB"+i)),
                    Integer.parseInt(info.get("BR"+i)),
                    Integer.parseInt(info.get("BG"+i)),
                    Integer.parseInt(info.get("BB"+i)),
                    Integer.parseInt(info.get("R_Factor"+i)),
                    Integer.parseInt(info.get("G_Factor"+i)),
                    Integer.parseInt(info.get("B_Factor"+i))
            ));
            i++;
        }
        return map;
    }
}
