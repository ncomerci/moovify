package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    public static final String TABLE_NAME = "images";

    public static final String DEFAULT_TYPE = "image/jpeg";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_image_id_seq")
    @SequenceGenerator(sequenceName = "images_image_id_seq", name = "images_image_id_seq", allocationSize = 1)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image", nullable = false, length = 10500000)
    @Basic(fetch = FetchType.LAZY, optional = false)
    private byte[] data;

    @Column(name = "type", nullable = true)
    @Basic(optional = true)
    private String type;

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getType() {

        if(type == null)
            return DEFAULT_TYPE;

        return type;
    }

    protected Image() {
        // Hibernate
    }

    public Image(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }

    public Image(Long id, byte[] data, String type) {
        this(data, type);
        this.id = id;
    }
}
