package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class SearchMoviesForm {
    @NotNull
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
