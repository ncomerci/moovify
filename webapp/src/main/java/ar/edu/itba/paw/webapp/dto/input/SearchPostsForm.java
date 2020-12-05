package ar.edu.itba.paw.webapp.dto.input;

public class SearchPostsForm {

    private String query;
    private String sortCriteria;
    private String postCategory;
    private String postAge;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostAge() {
        return postAge;
    }

    public void setPostAge(String postAge) {
        this.postAge = postAge;
    }
}
