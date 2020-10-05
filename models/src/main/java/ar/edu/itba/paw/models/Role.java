package ar.edu.itba.paw.models;

public class Role {

    public static final String NOT_VALIDATED_ROLE = "NOT_VALIDATED";
    public static final String USER_ROLE = "USER";

    private final long id;
    private final String role;

    public Role(long id, String role) {
        this.id = id;
        this.role = role;
    }

    // 0 stands for invalid id
    public Role(String role) {
        this.id = 0;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }
}
