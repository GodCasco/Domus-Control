import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Cenario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private Utilizador utilizador;
    private List<String> acoes;
    private List<Regra> regras;

    public Cenario() {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = "Undefined";
        this.utilizador = new Utilizador();
        this.acoes = new ArrayList<>();
        this.regras = new ArrayList<>();
    }

    public Cenario(String id, String nome, Utilizador utilizador) {
        this.id = id;
        this.nome = nome;
        this.utilizador = utilizador.clone();
        this.acoes = new ArrayList<>();
        this.regras = new ArrayList<>();
    }

    public Cenario(Cenario c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.utilizador = c.getUtilizador();
        this.acoes = c.getAcoes();
        this.regras = c.getRegras();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Utilizador getUtilizador() {
        return this.utilizador.clone();
    }

    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador.clone();
    }

    public List<String> getAcoes() {
        return new ArrayList<>(this.acoes);
    }

    public void addAcao(String acao) {
        this.acoes.add(acao);
    }

    public void removeAcao(String acao) {
        this.acoes.remove(acao);
    }

    public List<Regra> getRegras() {
        return new ArrayList<>(this.regras);
    }

    public void addRegra(Regra r) {
        this.regras.add(r);
    }

    public void removeRegra(String idRegra) {
        this.regras.removeIf(r -> r.getId().equals(idRegra));
    }

    @Override
    public Cenario clone() {
        return new Cenario(this);
    }

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Utilizador: " + this.getUtilizador().getUsername() + "\n" +
               "Acoes: " + this.acoes.toString() + "\n" +
               "Regras: " + this.regras.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Cenario c = (Cenario) o;
        return this.id.equals(c.getId()) &&
               this.nome.equals(c.getNome()) &&
               this.utilizador.equals(c.getUtilizador()) &&
               this.acoes.equals(c.getAcoes()) &&
               this.regras.equals(c.getRegras());
    }
}