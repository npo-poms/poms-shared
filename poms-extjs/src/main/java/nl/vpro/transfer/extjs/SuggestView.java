/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs;

public class SuggestView {

    private String text;

    private SuggestView() {
    }

    public SuggestView(String text) {
        this.text = text;
    }

    public static SuggestView create(String fullTitle) {
        SuggestView simpleSuggest = new SuggestView(fullTitle);
        return simpleSuggest;
    }

    public String getText() {
        return text;
    }
}
