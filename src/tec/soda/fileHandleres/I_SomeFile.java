package tec.soda.fileHandleres;

import java.io.File;

/**
 * Created by daniel.peczkowski on 2017-04-03.
 */
public interface I_SomeFile extends Comparable<I_SomeFile>{
    String getName();

    String getPath();

    File getFile();
}
