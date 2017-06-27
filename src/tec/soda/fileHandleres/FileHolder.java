package tec.soda.fileHandleres;

import tec.soda.procedures.Procedure;
import tec.soda.procedures._3_CheckModelName;
import tec.soda.procedures._4_CheckFMVersion;
import tec.soda.procedures._9_ReadOptionCodes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-05-22.
 */
public class FileHolder {
    public final File parentDir;
    public final InfFile infFile;
    public final Map<String, IniFile> iniFiles = new HashMap<>();
    public final EedFile eedFile;
    public final TreeMap<String, TreeMap<Integer, Procedure>> procedures = new TreeMap<>();
    public String model;
    public String version;
    public String codes;

    public FileHolder(InfFile inf) throws IOException {
        System.out.println("---!!!--- File Holder Init: " + inf.getName());
        infFile = inf;

        parentDir = infFile.getFile().getParentFile();

        if (infFile.enableBurnEED) {
            EedFile eed = null;
            try {
                eed = new EedFile(new File(parentDir.getAbsolutePath() + File.separator + infFile.nameOfEED), infFile.packetSizeEED);
            } catch (IOException e) {
                throw new IOException("Invalid EED name specified: " + infFile.nameOfEED);
            } finally {
                eedFile = eed;
            }
        } else eedFile = null;

        File[] fileArr = parentDir.listFiles(File::isFile);
        if (fileArr == null) throw new IOException("Impossible, that file had no parent directory");
        for (File file : fileArr) {
            if (file.getName().endsWith(".ini") && file.getName().startsWith(infFile.fileGroupID)) {
                try {
                    IniFile ini = new IniFile(file, infFile);
                    iniFiles.put(ini.fileID, ini);
                } catch (IOException e) {
                    throw new IOException("Invalid ini construction for: " + file.getName());
                }
            }
        }

        for (IniFile ini : iniFiles.values()) {
            procedures.put(ini.fileID, ini.getProcedures(this));
        }

        for (TreeMap<Integer, Procedure> map : procedures.values()) {
            for (Procedure proc : map.values()) {
                proc.initJumps(procedures);

                if (proc instanceof _3_CheckModelName) {
                    model = ((_3_CheckModelName) proc).model.toString();
                } else if (proc instanceof _4_CheckFMVersion) {
                    version = ((_4_CheckFMVersion) proc).version.toString();
                } else if (proc instanceof _9_ReadOptionCodes) {
                    codes = ((_9_ReadOptionCodes) proc).codes;
                }
            }
        }
        System.out.println("\u001b[34;1m---------->Model: " + model + "\u001b[0m");
        System.out.println("\u001b[34;1m---------->  Ver: " + version + "\u001b[0m");
        System.out.println("\u001b[34;1m---------->Codes: " + codes + "\u001b[0m");
        //Try to add SW loader/CoreSSB comparison
    }
}
