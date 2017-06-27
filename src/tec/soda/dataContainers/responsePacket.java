package tec.soda.dataContainers;

/**
 * Created by daniel.peczkowski on 2017-06-23.
 */
public class responsePacket extends ByteDataBuilder {
    public final byte packetHeaderId;
    public final int endPos;

    public final static String[] headers=new String[]{
            new ByteDataBuilder("C23D",true).toString(),
            new ByteDataBuilder("C738",true).toString(),
            new ByteDataBuilder("CB34",true).toString(),
            new ByteDataBuilder("CC33",true).toString(),
            new ByteDataBuilder("D32C",true).toString(),
            new ByteDataBuilder("D42B",true).toString(),
            new ByteDataBuilder("D827",true).toString(),
            new ByteDataBuilder("DD22",true).toString(),
            new ByteDataBuilder("E31C",true).toString(),
            new ByteDataBuilder("E41B",true).toString(),
            new ByteDataBuilder("E817",true).toString(),
            new ByteDataBuilder("ED12",true).toString(),
            new ByteDataBuilder("F00F",true).toString(),
            new ByteDataBuilder("F50A",true).toString(),
            new ByteDataBuilder("F906",true).toString(),
            new ByteDataBuilder("FE01",true).toString(),
    };

    private responsePacket(ByteDataBuilder input, int startIndex) throws Exception {
        int index=startIndex-1;
        byte tempHeaderId=-1;
        for(byte i=0;i<headers.length;i++){
            int indexTemp=input.lastIndexOf(headers[i]);
            if(indexTemp>index){
                index=indexTemp;
                tempHeaderId=i;
            }
        }
        packetHeaderId=tempHeaderId;
        if(packetHeaderId!=-1){
            try{
                int len=input.charAt(index+2)&0xFF;
                append(input.subString(index+3,index+3+len),false);
                endPos=index+3+len;
            }catch (Exception e){
                throw new Exception();
            }
        }else{
            endPos=-1;
        }
    }

    public static responsePacket getLastResponse(ByteDataBuilder input, int startIndex){
        try {
            responsePacket rP = new responsePacket(input, startIndex);
            if(rP.packetHeaderId!=-1) return rP;
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
