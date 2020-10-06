package ar.edu.itba.paw.models;

public class Like {

    private long user_id;
    private int value;

    public Like(long user_id, int value) {
        this.user_id = user_id;
        this.value = value;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
