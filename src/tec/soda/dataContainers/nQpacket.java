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

    public nQpacket(int startPostion, byte[] data){
        super();
        append("nQ",false);
        append((byte)(0x80|data.length()));
    }
}
