package nl.npo.wonvpp.domain;

public record CreditsType(
    PersonType person,
    FunctionType function,
    String role
    ) {
}
