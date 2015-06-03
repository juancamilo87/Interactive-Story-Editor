package fi.oulu.interactivestoryeditor.model;

/**
 * Created by JuanCamilo on 6/3/2015.
 */
public class Author {

    private String name;
    private String last_name;
    private String website;
    private String email;

    public Author() {
    }

    public Author(String name, String last_name, String website, String email) {
        this.name = name;
        this.last_name = last_name;
        this.website = website;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
