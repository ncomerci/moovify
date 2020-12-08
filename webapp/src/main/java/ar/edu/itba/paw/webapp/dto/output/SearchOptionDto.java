package ar.edu.itba.paw.webapp.dto.output;

import java.util.Collection;

public class SearchOptionDto {

    private String name;
    private Collection<String> options;
    private String defaultValue;

    public SearchOptionDto() {
        //For Jersey Reflection- Do not use
    }

    public SearchOptionDto(String name, Collection<String> options, String defaultValue) {
        this.name = name;
        this.options = options;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getOptions() {
        return options;
    }

    public void setOptions(Collection<String> options) {
        this.options = options;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
