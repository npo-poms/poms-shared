package nl.vpro.berlijn.domain.productmetadata;

public record PridHolder(
    String prid
) {

    /**
     * VPPM-2237, content on kafka is a bit message.
     */
    public static boolean isEmpty(PridHolder pridHolder) {
        return pridHolder == null || pridHolder.prid == null || pridHolder.prid.isEmpty();
    }
}
