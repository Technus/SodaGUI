package tec.soda.dataContainers;

/**
 * Created by daniel.peczkowski on 2017-06-14.
 */
public class nQpacket extends ByteDataBuilder {
    public nQpacket(ByteDataBuilder command){
        super();
        append("nQ",false);
        append((byte)(0x80|command.length()));
        append(command);
        byte sum=0x00;
        for(byte b:toBytes()){
            sum^=b;
        }
        append(sum);
    }

    public nQpacket(ByteDataBuilder packetInByteDataBuilder,boolean rebuild) throws Exception{
        super();
        if(packetInByteDataBuilder.subString(0,2).equals("nQ")){
            if(rebuild){
                append("nQ",false);
                append((byte)(0x80|(packetInByteDataBuilder.length()-4)));
                append(packetInByteDataBuilder.subString(3,packetInByteDataBuilder.length()-1),false);
                byte sum=0x00;
                for(byte b:toBytes()){
                    sum^=b;
                }
                append(sum);
            }else{
                append(packetInByteDataBuilder);
            }
        }else{
            throw new Exception("Not a nQ packet");
        }
    }

    //EED BURNING
    public nQpacket(int startPosition, ByteDataBuilder data){
        super();
        append("nQ",false);
        append((byte)(0x80|(data.length()+6)));
        append((byte)0x03,(byte)0xFE,(byte)0xE3);
        append((byte)data.length());
        append((byte)(startPosition>>8));
        append((byte)(startPosition&0xFF));
        append(data);
        byte sum=0x00;
        for(byte b:toBytes()){
            sum^=b;
        }
        append(sum);
    }
}
