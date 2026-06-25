import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

public class Automacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private Utilizador utilizador;
    private String condicao;
    private List<String> acoes;
    private List<Regra> regras;

    public Automacao() {
        this.id = "Undefined";
        this.nome = "Undefined";
        this.utilizador = new Utilizador();
        this.condicao = "Undefined";
        this.acoes = new ArrayList<>();
        this.regras = new ArrayList<>();
    }

    public Automacao(String id, String nome, Utilizador utilizador, String condicao) {
        this.id = id;
        this.nome = nome;
        this.utilizador = utilizador.clone();
        this.condicao = condicao;
        this.acoes = new ArrayList<>();
        this.regras = new ArrayList<>();
    }

    public Automacao(Automacao a) {
        this.id = a.getId();
        this.nome = a.getNome();
        this.utilizador = a.getUtilizador();
        this.condicao = a.getCondicao();
        this.acoes = a.getAcoes();
        this.regras = a.getRegras();
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Utilizador getUtilizador() { return this.utilizador.clone(); }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador.clone(); }
    public String getCondicao() { return this.condicao; }
    public void setCondicao(String condicao) { this.condicao = condicao; }

    public List<String> getAcoes() { return new ArrayList<>(this.acoes); }
    public void addAcao(String acao) { this.acoes.add(acao); }
    public void removeAcao(String acao) { this.acoes.remove(acao); }

    public List<Regra> getRegras() { return new ArrayList<>(this.regras); }

    public void adicionarRegra(Regra r) { this.regras.add(r); }

    public Regra getRegra(String idRegra) {
        for (Regra r : this.regras) {
            if (r.getId().equals(idRegra)) return r;
        }
        return null;
    }

    public void removerRegra(String idRegra) {
        this.regras.removeIf(r -> r.getId().equals(idRegra));
    }

    public void removerTodasRegras() { this.regras.clear(); }

    @Override
    public Automacao clone() { return new Automacao(this); }

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Utilizador: " + this.getUtilizador().getUsername() + "\n" +
               "Condicao: " + this.condicao + "\n" +
               "Acoes: " + this.acoes.toString() + "\n" +
               "Regras: " + this.regras.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Automacao a = (Automacao) o;
        return this.id.equals(a.getId()) &&
               this.nome.equals(a.getNome()) &&
               this.utilizador.equals(a.getUtilizador()) &&
               this.condicao.equals(a.getCondicao()) &&
               this.acoes.equals(a.getAcoes()) &&
               this.regras.equals(a.getRegras());
    }
}