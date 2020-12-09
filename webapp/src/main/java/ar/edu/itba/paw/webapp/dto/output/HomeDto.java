package ar.edu.itba.paw.webapp.dto.output;

public class HomeDto {

    private String hello;

    public HomeDto() {
        hello = "Hello :)";
    }

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }
}
