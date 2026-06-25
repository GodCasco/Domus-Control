import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String username;
    private String password;
    private Set<String> idsCasas;
    private Map<String, AdminLevel> permissoesPorCasa;

    public Utilizador() {
        this.id = "Undefined";
        this.username = "Undefined";
        this.password = "Undefined";
        this.idsCasas = new HashSet<>();
        this.permissoesPorCasa = new HashMap<>();
    }

    public Utilizador(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.idsCasas = new HashSet<>();
        this.permissoesPorCasa = new HashMap<>();
    }

    public Utilizador(Utilizador u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.idsCasas = u.getCasas();
        this.permissoesPorCasa = u.getPermissoesPorCasa();
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getCasas() { return new HashSet<>(this.idsCasas); }
    public Map<String, AdminLevel> getPermissoesPorCasa() { return new HashMap<>(this.permissoesPorCasa); }

    public void addCasa(String casa, AdminLevel a) {
        this.idsCasas.add(casa);
        this.permissoesPorCasa.put(casa, a);
    }

    public void removeCasa(String casa) {
        this.idsCasas.remove(casa);
        this.permissoesPorCasa.remove(casa);
    }

    public void removeAllCasas() {
        this.idsCasas.clear();
        this.permissoesPorCasa.clear();
    }

    public void setAdmin(String casa) {
        if (this.idsCasas.contains(casa))
            this.permissoesPorCasa.put(casa, AdminLevel.ADMIN);
    }

    public void setUser(String casa) {
        if (this.idsCasas.contains(casa))
            this.permissoesPorCasa.put(casa, AdminLevel.USER);
    }

    public boolean isAdmin(String casa) {
        return this.permissoesPorCasa.getOrDefault(casa, AdminLevel.USER) == AdminLevel.ADMIN;
    }

    public boolean temCasa(String casa) { return this.idsCasas.contains(casa); }

    @Override
    public Utilizador clone() { return new Utilizador(this); }

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Username: " + this.username + "\n" +
               "idsCasas: " + this.idsCasas.toString() + "\n" +
               "Permissoes: " + this.permissoesPorCasa.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Utilizador u = (Utilizador) o;
        return this.id.equals(u.getId()) &&
               this.username.equals(u.getUsername()) &&
               this.password.equals(u.getPassword()) &&
               this.idsCasas.equals(u.getCasas()) &&
               this.permissoesPorCasa.equals(u.getPermissoesPorCasa());
    }
}