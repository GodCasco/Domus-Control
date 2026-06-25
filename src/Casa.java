import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

public class Casa implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private Map<String, Divisao> divisoes;
    private List<Automacao> automacoes;
    private List<Escalonamento> escalonamentos;
    private List<Cenario> cenarios;

    public Casa() {
        this.id = "Undefined";
        this.nome = "Undefined";
        this.divisoes = new HashMap<>();
        this.automacoes = new ArrayList<>();
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new ArrayList<>();
    }

    public Casa(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.divisoes = new HashMap<>();
        this.automacoes = new ArrayList<>();
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new ArrayList<>();
    }

    public Casa(Casa c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.divisoes = c.getDivisoes();
        this.automacoes = c.getAutomacoes();
        this.escalonamentos = c.getEscalonamentos();
        this.cenarios = c.getCenarios();
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Map<String, Divisao> getDivisoes() {
        return new HashMap<>(this.divisoes);
    }

    public void setDivisoes(Map<String, Divisao> divisoes) {
        this.divisoes = new HashMap<>(divisoes);
    }

    public List<Automacao> getAutomacoes() {
        return new ArrayList<>(this.automacoes);
    }

    public void addAutomacao(Automacao a) {
        this.automacoes.add(a.clone());
    }

    public List<Escalonamento> getEscalonamentos() {
        return new ArrayList<>(this.escalonamentos);
    }

    public void addEscalonamento(Escalonamento e) {
        this.escalonamentos.add(e.clone());
    }

    public List<Cenario> getCenarios() {
        return new ArrayList<>(this.cenarios);
    }

    public void addCenario(Cenario c) {
        this.cenarios.add(c.clone());
    }

    public void removeCenario(String idCenario) {
        this.cenarios.removeIf(c -> c.getId().equals(idCenario));
    }

    public void removeEscalonamento(String idEscalonamento) {
        this.escalonamentos.removeIf(e -> e.getId().equals(idEscalonamento));
    }

    public void removeAutomacao(String idAutomacao) {
        this.automacoes.removeIf(a -> a.getId().equals(idAutomacao));
    }

    public void addDivisao(Divisao d) {
        this.divisoes.put(d.getNome(), d.clone());
    }

    public void removeDivisao(String nome) {
        this.divisoes.remove(nome);
    }

    public void addDispositivoADivisao(String nomeDivisao, Dispositivo d) {
        if (this.divisoes.containsKey(nomeDivisao)) {
            this.divisoes.get(nomeDivisao).addDispositivo(d);
        }
    }

    public Dispositivo getDispositivo(String nomeDivisao, String idDispositivo) {
        if (this.divisoes.containsKey(nomeDivisao)) {
            for (Dispositivo d : this.divisoes.get(nomeDivisao).getDispositivos()) {
                if (d.getId().equals(idDispositivo)) return d.clone();
            }
        }
        return null;
    }

    public void atualizarDispositivo(String nomeDivisao, Dispositivo dispositivo) {
        if (this.divisoes.containsKey(nomeDivisao)) {
            this.divisoes.get(nomeDivisao).atualizarDispositivo(dispositivo);
        }
    }

    // Devolve todos os dispositivos da casa independentemente da divisão
    public List<Dispositivo> getTodosDispositivos() {
        List<Dispositivo> todos = new ArrayList<>();
        for (Divisao d : this.divisoes.values()) {
            todos.addAll(d.getDispositivos());
        }
        return todos;
    }

    // Encontra um dispositivo pelo id em todas as divisões
    public Dispositivo findDispositivo(String idDispositivo) {
        for (Divisao d : this.divisoes.values()) {
            for (Dispositivo disp : d.getDispositivos()) {
                if (disp.getId().equals(idDispositivo)) return disp.clone();
            }
        }
        return null;
    }

    // Atualiza um dispositivo em qualquer divisão
    public void atualizarDispositivoEmQualquerDivisao(Dispositivo dispositivo) {
        for (Divisao d : this.divisoes.values()) {
            d.atualizarDispositivo(dispositivo);
        }
    }

    @Override
    public Casa clone() { return new Casa(this); }

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Divisoes: " + this.divisoes.toString() + "\n" +
               "Automacoes: " + this.automacoes.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Casa c = (Casa) o;
        return this.id.equals(c.getId()) &&
               this.nome.equals(c.getNome()) &&
               this.divisoes.equals(c.getDivisoes()) &&
               this.automacoes.equals(c.getAutomacoes()) &&
               this.escalonamentos.equals(c.getEscalonamentos()) &&
               this.cenarios.equals(c.getCenarios());
    }
}