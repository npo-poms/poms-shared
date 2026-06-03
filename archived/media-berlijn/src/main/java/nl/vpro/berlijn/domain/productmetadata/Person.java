package nl.vpro.berlijn.domain.productmetadata;

public record Person(
    String givenName,
    String familyName,
    Long id // TODO Thesaurus?
)  {
}
