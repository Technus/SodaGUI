package tec.soda.fileHandleres;

import tec.soda.dataContainers.ByteDataBuilder;
import tec.soda.dataContainers.nQpacket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-03-27.
 */
public class EedFile extends AnyFile {//NVM
    private final byte[] content;
    public final int packetSizeEED;

    public final int size;
    public final TreeMap<Integer,ByteDataBuilder> data=new TreeMap<>();

    public final nQpacket[] commands;

    public EedFile(File f, int packetSizeEED) throws IOException {
        super(f);
        this.packetSizeEED=packetSizeEED;
        System.out.println("|||EED||| "+f.getName()+" "+packetSizeEED);
        content=Files.readAllBytes(file.toPath());

        size=(content[0]&0xFF)|(content[1]<<8);

        for(int pos=16;pos<=content.length-4;){

            int addr=(content[pos]&0xFF)|(content[pos+1]<<8);
            ByteDataBuilder block=new ByteDataBuilder();
            data.put(addr,block);

            block.append(content[pos+2]);

            for(pos+=4;pos<=content.length-4;pos+=4){
                int addrTemp=(content[pos]&0xFF)|(content[pos+1]<<8);
                if(addrTemp!=addr+1) break;
                addr=addrTemp;
                if(block.length()>=packetSizeEED) break;
                block.append(content[pos+2]);
            }

        }

        commands=new nQpacket[data.size()];
        int i=0;
        for(Map.Entry<Integer,ByteDataBuilder> entry:data.entrySet()){
            commands[i++]=new nQpacket(entry.getKey(),entry.getValue());
        }
    }

    public final byte[] getContent() {
        return content.clone();
    }
}
