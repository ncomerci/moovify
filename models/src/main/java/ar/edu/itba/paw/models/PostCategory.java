package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_category")
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_category_category_id_seq")
    @SequenceGenerator(sequenceName = "post_category_category_id_seq", name = "post_category_category_id_seq", allocationSize = 1)
    @Column(name = "category_id")
    private long id;

    @Column(name = "creation_date", nullable = false)
    @Basic(optional = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, unique = true)
    @Basic(optional = false)
    private String name;

    public PostCategory(long id, LocalDateTime creationDate, String name) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
    }

    protected PostCategory() {
        //Hibernate
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PostCategory{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", name='" + name + '\'' +
                '}';
    }
}
