package ar.edu.itba.paw.webapp.dto.output;

import java.util.Collection;

public class SearchOptionDto {

    private String name;
    private Collection<String> options;

    public SearchOptionDto() {
        //For Jersey Reflection- Do not use
    }

    public SearchOptionDto(String name, Collection<String> options) {
        this.name = name;
        this.options = options;
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
}
