package nl.vpro.domain.media.bind;


import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.domain.media.UsedLanguage;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class UsedLanguageAdapter extends XmlAdapter<UsedLanguageWrapper, UsedLanguage> {


    @Override
    public UsedLanguage unmarshal(UsedLanguageWrapper v) {
        return v.getUsedLanguage();

    }

    @Override
    public UsedLanguageWrapper marshal(UsedLanguage v) {
        return new UsedLanguageWrapper(v);
    }


}
