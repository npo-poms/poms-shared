package nl.vpro.berlijn.domain.productmetadata;

public record Synopsis(
    String longText,
    String mediumText,
    String shortText,
    String brief
) {
}
