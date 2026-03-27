package nl.npo.wonvpp.domain.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.npo.wonvpp.domain.CatalogEntry;

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
             case serie ->
                 value.episodeNumber() == null && value.seasonNumber() == null;
         };
    }
}
