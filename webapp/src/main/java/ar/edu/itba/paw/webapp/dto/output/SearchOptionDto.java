package ar.edu.itba.paw.webapp.dto.output;

import java.util.Collection;

public class SearchOptionDto {

    private String name;
    private Collection<String> values;

    public SearchOptionDto() {
        //For Jersey Reflection- Do not use
    }

    public SearchOptionDto(String name, Collection<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<String> getValues() {
        return values;
    }

    public void setValues(Collection<String> values) {
        this.values = values;
    }

}
