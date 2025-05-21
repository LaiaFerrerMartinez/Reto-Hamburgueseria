
public class Usuario {
    private int id;
    private String username;
    private String email;
    private String typeUser;

    // Nuevo campo para almacenar el token descifrado
    private String apiToken;

    // Constructor principal
    public Usuario(int id, String username, String email, String typeUser) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.typeUser = typeUser;
    }

    // Getters para los campos existentes
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTypeUser() {
        return typeUser;
    }

    // Getter y setter para apiToken
    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
