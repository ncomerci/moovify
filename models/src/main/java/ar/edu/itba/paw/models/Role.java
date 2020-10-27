package ar.edu.itba.paw.models;

//package ar.edu.itba.paw.models;
//
//public class Role {
//
//    public static final String NOT_VALIDATED_ROLE = "NOT_VALIDATED";
//    public static final String USER_ROLE = "USER";
//    public static final String ADMIN_ROLE = "ADMIN";
//
//    private final long id;
//    private final String role;
//
//    public Role(long id, String role) {
//        this.id = id;
//        this.role = role;
//    }
//
//    // 0 stands for invalid id
//    public Role(String role) {
//        this.id = 0;
//        this.role = role;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    @Override
//    public String toString() {
//        return "Role{" +
//                "id=" + id +
//                ", role='" + role + '\'' +
//                '}';
//    }
//}
public enum Role {

    NOT_VALIDATED,
    USER,
    ADMIN;
}