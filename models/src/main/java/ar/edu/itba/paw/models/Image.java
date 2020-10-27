package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_image_id_seq")
    @SequenceGenerator(sequenceName = "images_image_id_seq", name = "images_image_id_seq", allocationSize = 1)
    private Long id;

    @Lob
    @Column(name = "image", columnDefinition = "BLOB") //TODO puede haber error usando BLOB aca
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    @Column(name = "security_tag", nullable = false)
    private String tag;

    public Long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getTag() {
        return tag;
    }

    protected Image() {
    }

    public Image(byte[] data, String tag) {
        this.data = data;
        this.tag = tag;
    }

    public Image(Long id, byte[] data, String tag) {
        this.id = id;
        this.data = data;
        this.tag = tag;
    }
}
