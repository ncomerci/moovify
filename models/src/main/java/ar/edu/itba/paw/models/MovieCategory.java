package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "movie_categories")
public class MovieCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_categories_category_id_seq")
    @SequenceGenerator(sequenceName = "movie_categories_category_id_seq", name = "movie_categories_category_id_seq", allocationSize = 1)
    @Column(name = "category_id")
    private long id;

    @Column(nullable = false, unique = true)
    private long tmdbCategoryId;

    @Column(nullable = false, length = 50)
    @Basic(optional = false)
    private String name;

    /*@ManyToMany(fetch = FetchType.LAZY, mappedBy = "movieCategories")
    private Collection<Movie> movies;*/

    public MovieCategory(long id, long tmdbCategoryId, String name) {
        this.id = id;
        this.tmdbCategoryId = tmdbCategoryId;
        this.name = name;
    }

    protected MovieCategory() {
        //Hibernate
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public long getTmdbCategoryId() {
        return tmdbCategoryId;
    }

    /*public Collection<Movie> getMovies() {
        return movies;
    }*/

    @Override
    public String toString() {
        return "MovieCategory{" +
                "id=" + id +
                ", tmdbCategoryId=" + tmdbCategoryId +
                ", name='" + name + '\'' +
                '}';
    }
}