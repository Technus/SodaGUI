package tec.soda.fileHandleres;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by daniel.peczkowski on 2017-03-27.
 */
public class EedFile extends AnyFile {//NVM
    private final byte[] content;
    public final int packetSizeEED;

    public EedFile(File f, int packetSizeEED) throws IOException {
        super(f);
        this.packetSizeEED=packetSizeEED;
        System.out.println("|||EED||| "+f.getName()+" "+packetSizeEED);
        content=Files.readAllBytes(file.toPath());
    }

    public final byte[] getContent() {
        return content.clone();
    }
}
