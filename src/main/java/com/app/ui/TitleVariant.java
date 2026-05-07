package com.app.ui;

/**
 * CSS variants for the title atom ({@code atoms/title.jte}).
 */
public enum TitleVariant {

    H1("h1"),
    H2("h2"),
    H3("h3"),;

    private final String cssClass;

    TitleVariant(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * @return the CSS class name to apply in the template
     */
    public String cssClass() {
        return cssClass;
    }
}
