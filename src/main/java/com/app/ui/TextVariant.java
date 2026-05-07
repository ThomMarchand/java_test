package com.app.ui;

/**
 * CSS variants for the text atom ({@code atoms/text.jte}).
 */
public enum TextVariant {

    BODY_REGULAR("body-regular"),
    BODY_MEDIUM("body-medium"),
    CAPTION("caption"),
    LABEL_UPPERCASE("label-uppercase");

    private final String cssClass;

    TextVariant(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * @return the CSS class name to apply in the template
     */
    public String cssClass() {
        return cssClass;
    }
}
