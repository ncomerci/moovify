package ar.edu.itba.paw.models;

import java.util.Collection;

public class PaginatedCollection<T> {

    private final Collection<T> results;
    private final int pageNumber;
    private final int pageSize;
    private final int totalCount;

    public PaginatedCollection(Collection<T> results, int pageNumber, int pageSize, int totalCount) {
        this.results = results;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public Collection<T> getResults() {
        return results;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getLastPageNumber() {
        return (totalCount - 1) / pageSize;
    }
}
