package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.PostCategory;

import java.util.Collection;
import java.util.stream.Collectors;

public class PostCategoryDto {

    public static Collection<PostCategoryDto> mapPostCategoryToDto(Collection<PostCategory> postCategories) {
        return postCategories.stream().map(PostCategoryDto::new).collect(Collectors.toList());
    }

    private long id;
    private String name;

    public PostCategoryDto() {
        // For Jersey - Do not use
    }

    public PostCategoryDto(PostCategory postCategory) {
        id = postCategory.getId();
        name = postCategory.getName();
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
