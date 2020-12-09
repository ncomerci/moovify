package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.MovieCategory;

import java.util.Collection;
import java.util.stream.Collectors;

public class MovieCategoryDto {

    public static Collection<MovieCategoryDto> mapMovieCategoryToDto(Collection<MovieCategory> movieCategories) {
        return movieCategories.stream().map(MovieCategoryDto::new).collect(Collectors.toList());
    }

    private long id;
    private String name;

    public MovieCategoryDto() {
        // For Jersey - Do not use
    }

    public MovieCategoryDto(MovieCategory movieCategory) {
        id = movieCategory.getId();
        name = movieCategory.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
