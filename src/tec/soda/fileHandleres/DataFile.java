package tec.soda.fileHandleres;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daniel.peczkowski on 2017-03-27.
 */
public class DataFile extends AnyFile {//Header
    private final static Pattern  _section  = Pattern.compile( "\\s*\\[([^]]*)\\]\\s*" );
    private final static Pattern  _keyValue = Pattern.compile( "\\s*([^=]*)=(.*)" );
    protected final Map< String, Map< String, String >>  _entries  = new TreeMap<>();

    protected DataFile(File f) throws IOException {
        super(f);
        try( BufferedReader br = new BufferedReader( new FileReader( file.getPath() ))) {
            String line;
            String section = null;
            while(( line = br.readLine()) != null ) {
                Matcher m = _section.matcher( line );
                if( m.matches()) {
                    section = m.group( 1 ).trim();
                }
                else if( section != null ) {
                    m = _keyValue.matcher( line );
                    if( m.matches()) {
                        String key   = m.group( 1 ).trim();
                        if(key.contains(";"))continue;          //COMMENTARY DETECTION NEEDS REWORK
                        String value = m.group( 2 ).trim();
                        Map< String, String > kv = _entries.get( section );
                        if( kv == null ) {
                            _entries.put( section, kv = new TreeMap<>());
                        }
                        kv.put( key, value );
                    }
                }
            }
        }
    }

    public final String getString( String section, String key, String defaultvalue ) {
        Map< String, String > kv = _entries.get( section );
        if( kv == null || kv.get( key ) == null ) {
            return defaultvalue;
        }
        return kv.get( key );
    }

    public final int getInt( String section, String key, int defaultvalue ) {
        Map< String, String > kv = _entries.get( section );
        if( kv == null || kv.get( key ) == null ) {
            return defaultvalue;
        }
        return Integer.parseInt( kv.get( key ));
    }

    public final float getFloat( String section, String key, float defaultvalue ) {
        Map< String, String > kv = _entries.get( section );
        if( kv == null || kv.get( key )==null ) {
            return defaultvalue;
        }
        return Float.parseFloat( kv.get( key ));
    }

    public final double getDouble( String section, String key, double defaultvalue ) {
        Map< String, String > kv = _entries.get( section );
        if( kv == null || kv.get( key )==null ) {
            return defaultvalue;
        }
        return Double.parseDouble( kv.get( key ));
    }

    public final String[] getSections(){
        return _entries.keySet().toArray(new String[0]);
    }

    public final Map<String,String> getSectionContents(String section){
        return _entries.get(section);
    }

    public final void print(){
        for(String section:this.getSections()) {
            System.out.println();
            for (String key : this.getSectionContents(section).keySet()) {
                System.out.format("%-20s%-30s%-35s\n", section, key, this.getString(section, key, "NULL"));
                //println(section+" "+key+" "+f.getString(section,key,"NULL"));
            }
        }
    }


}
