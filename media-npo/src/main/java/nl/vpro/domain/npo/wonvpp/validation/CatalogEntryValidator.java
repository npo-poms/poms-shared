package nl.vpro.domain.npo.wonvpp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.npo.wonvpp.CatalogEntry;

public class CatalogEntryValidator  implements ConstraintValidator<ValidCatalogEntry, CatalogEntry> {

     public CatalogEntryValidator() {

     }

    @Override
    public boolean isValid(CatalogEntry value, ConstraintValidatorContext context) {
         return switch(value.contentType()) {
             case episode ->
                 value.seasonNumber() == null;
             case season ->
                 value.episodeNumber() == null;
             case series ->
                 value.episodeNumber() == null && value.seasonNumber() == null;
         };
    }
}
