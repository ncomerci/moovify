package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class SearchUsersForm {

    @NotNull
    private String query;

    private String role;

    private String sortCriteria;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }
}
