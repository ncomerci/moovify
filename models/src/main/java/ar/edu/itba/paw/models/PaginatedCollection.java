package ar.edu.itba.paw.models;

import java.util.Collection;

public class PaginatedCollection<T> {

    private final Collection<T> results;
    private final int pageNumber;
    private final int pageSize;
    private final long totalCount;

    public PaginatedCollection(Collection<T> results, int pageNumber, int pageSize, long totalCount) {
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

    public long getTotalCount() {
        return totalCount;
    }

    public long getLastPageNumber() {
        return (totalCount - 1) / pageSize;
    }

    @Override
    public String toString() {
        return "PaginatedCollection{" +
                "results=" + results +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                '}';
    }
}
