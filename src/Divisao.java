import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Divisao implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private List<Dispositivo> dispositivos;

    public Divisao() {
        this.nome = "Undefined";
        this.dispositivos = new ArrayList<>();
    }

    public Divisao(String nome) {
        this.nome = nome;
        this.dispositivos = new ArrayList<>();
    }

    public Divisao(Divisao d) {
        this.nome = d.getNome();
        this.dispositivos = d.getDispositivos();
    }

    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Dispositivo> getDispositivos() {
        return new ArrayList<>(this.dispositivos);
    }

    public void setDispositivos(List<Dispositivo> dispositivos) {
        this.dispositivos = new ArrayList<>(dispositivos);
    }

    public void addDispositivo(Dispositivo d) {
        this.dispositivos.add(d.clone());
    }

    public void removeDispositivo(String id) {
        this.dispositivos.removeIf(d -> d.getId().equals(id));
    }

    // Atualiza diretamente na lista interna sem criar cópias intermédias
    public void atualizarDispositivo(Dispositivo dispositivo) {
        for (int i = 0; i < this.dispositivos.size(); i++) {
            if (this.dispositivos.get(i).getId().equals(dispositivo.getId())) {
                this.dispositivos.set(i, dispositivo.clone());
                return;
            }
        }
    }

    @Override
    public Divisao clone() { return new Divisao(this); }

    @Override
    public String toString() {
        return "Nome: " + this.nome + "\n" +
               "Dispositivos: " + this.dispositivos.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Divisao d = (Divisao) o;
        return this.nome.equals(d.getNome()) &&
               this.dispositivos.equals(d.getDispositivos());
    }
}