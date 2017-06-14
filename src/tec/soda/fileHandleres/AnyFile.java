package tec.soda.fileHandleres;

import java.io.File;

/**
 * Created by daniel.peczkowski on 2017-04-03.
 */
public class AnyFile implements I_SomeFile {
    protected final File file;
    protected final String identifier;

    public AnyFile(File f){
        file=f;
        identifier=f.getAbsoluteFile().getParentFile().getParentFile().getParentFile().getParentFile().getName() + " | " +
                f.getParentFile().getParentFile().getName() + " | " +
                f.getParentFile().getName() + " | " +
                f.getName();
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public final int compareTo(I_SomeFile someFile) {
        return file.getPath().compareTo(someFile.getPath());
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof I_SomeFile && compareTo((I_SomeFile) o) == 0;
    }

    @Override
    public final int hashCode() {
        return file.getAbsolutePath().hashCode();
    }

    @Override
    public final String getPath() {
        return file.getPath();
    }

    @Override
    public final String getName() {
        return file.getName();
    }

    @Override
    public final File getFile() {
        return file;
    }
}
