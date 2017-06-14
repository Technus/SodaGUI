package tec.soda.fileHandleres;

import tec.soda.procedures.Procedure;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by daniel.peczkowski on 2017-03-27.
 */
public class IniFile extends DataFile {//Processes etc.
    public final InfFile inf;
    public final String fileID,fileGroupID;
    public final int declaredProcedureCount,realProcedureCount;

    public IniFile(File f, InfFile inf) throws IOException {
        super(f);
        this.inf=inf;
        for (String section : getSections()) {
            if (section.startsWith("Procedure")) {
                if (section.length() == 10) {
                    _entries.put("Procedure0" + section.substring(9), _entries.get(section));
                    _entries.remove(section);
                }
            }
        }

        realProcedureCount=procCount();
        declaredProcedureCount=getInt("TotalProcedure","Count",0);

        String temp = getName().substring(0,getName().length()-4);
        String[] tempArr=temp.split("_");
        if (tempArr.length>0) {
            fileID = tempArr[tempArr.length-1];
            fileGroupID=temp.split(fileID)[0];
            System.out.println("===INI=== " + fileGroupID + " " + fileID + " " + file.getName()+" "+declaredProcedureCount+" "+realProcedureCount);
        } else {
            fileID = null;
            fileGroupID=null;
            System.out.println("===INI=== NULL NULL " + file.getName()+" "+declaredProcedureCount+" "+realProcedureCount);
        }

    }

    private int procCount(){
        int count=0;
        for(String section:getSections()){
            if(section.startsWith("Procedure")) count++;
        }
        return count;
    }

    public TreeMap<Integer,Procedure> getProcedures(){
        TreeMap<Integer,Procedure> p=new TreeMap<>();
        for(String section:getSections()){
            if(!section.startsWith("Procedure")) continue;
            Procedure proc=Procedure.get(getInt(section,"Func",-1));
            if(proc==null)continue;
            int ID = Integer.parseInt(section.substring(9));
            if (ID < 0) continue;
            proc.init(inf,fileID,ID,
                    getString(section,"Name","Unnamed"),
                    getString(section,"Sort","1"),
                    getString(section,"CMD","0"),//STRING
                    getString(section,"CMDKind","0"),//STRING
                    getString(section,"WaitAck","0"),
                    getString(section,"DelayMS","0"),
                    getString(section,"Para0",null),//NEVER USED, just for convenience
                    getString(section,"Para1","0").trim(),
                    getString(section,"Para2","0").trim(),
                    getString(section,"Para3","0").trim(),
                    getString(section,"Para4","0").trim(),
                    getString(section,"Para5","0").trim(),
                    getString(section,"Para6","0").trim(),
                    getString(section,"Para7","0").trim(),
                    getString(section,"Para8","0").trim(),
                    getString(section,"Para9","0").trim());
            System.out.println(proc);
            System.out.println(proc.information());
            p.put(ID,proc);
        }
        return p;
    }
}
