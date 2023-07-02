package core;

public class Faculty {

    private String name;
    private String password;
    private String privilege;

    public Faculty() {

    }

    public Faculty(String name, String password, String privilege) {
        this.name = name;
        this.password = password;
        this.privilege = privilege;
    }

    //getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPrivilege() {
        return privilege;
    }
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
    
}
