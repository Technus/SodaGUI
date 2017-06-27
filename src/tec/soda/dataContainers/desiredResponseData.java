package tec.soda.dataContainers;

/**
 * Created by daniel.peczkowski on 2017-06-23.
 */
public class desiredResponseData extends ByteDataBuilder {
    public desiredResponseData(ByteDataBuilder bdb){
        append(bdb);
    }
}
