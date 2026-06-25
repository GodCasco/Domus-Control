import java.io.Serializable;

public abstract class Dispositivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String marca;
    private String modelo;
    private int consumoHora;
    private String estado;
    private int numAtivacoes;
    private long tempoLigado;
    private long instanteLigado;

    public Dispositivo() {
        this.id = "Undefined";
        this.marca = "Undefined";
        this.modelo = "Undefined";
        this.consumoHora = 0;
        this.estado = "Undefined";
        this.numAtivacoes = 0;
        this.tempoLigado = 0;
        this.instanteLigado = 0;
    }

    public Dispositivo(Dispositivo d) {
        this.id = d.getId();
        this.marca = d.getMarca();
        this.modelo = d.getModelo();
        this.consumoHora = d.getConsumoHora();
        this.estado = d.getEstado();
        this.numAtivacoes = d.getNumAtivacoes();
        this.tempoLigado = d.getTempoLigado();
        this.instanteLigado = d.getInstanteLigado();
    }

    public Dispositivo(String id, String marca, String modelo, int consumoHora, String estado) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoHora = consumoHora;
        this.estado = estado;
        this.numAtivacoes = 0;
        this.tempoLigado = 0;
        this.instanteLigado = 0;
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getMarca() { return this.marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return this.modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getConsumoHora() { return this.consumoHora; }
    public void setConsumoHora(int consumoHora) { this.consumoHora = consumoHora; }
    public String getEstado() { return this.estado; }
    public int getNumAtivacoes() { return this.numAtivacoes; }
    public long getTempoLigado() { return this.tempoLigado; }
    public long getInstanteLigado() { return this.instanteLigado; }

    public void ligar() {
        if (!this.estado.equals("Ligado")) {
            this.estado = "Ligado";
            this.numAtivacoes++;
            this.instanteLigado = System.currentTimeMillis();
        }
    }

    public void desligar() {
        if (this.estado.equals("Ligado")) {
            this.tempoLigado += System.currentTimeMillis() - this.instanteLigado;
            this.instanteLigado = 0;
        }
        this.estado = "Desligado";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getTempoLigadoSegundos() {
        if (this.instanteLigado > 0) {
            return (this.tempoLigado + (System.currentTimeMillis() - this.instanteLigado)) / 1000;
        }
        return this.tempoLigado / 1000;
    }

    @Override
    public abstract Dispositivo clone();

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Marca: " + this.marca + "\n" +
               "Modelo: " + this.modelo + "\n" +
               "Consumo por hora (Wh): " + this.consumoHora + "\n" +
               "Estado: " + this.estado + "\n" +
               "Nº Ativações: " + this.numAtivacoes + "\n" +
               "Tempo ligado (s): " + this.getTempoLigadoSegundos() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (this.getClass() != o.getClass())) return false;
        Dispositivo d = (Dispositivo) o;
        return this.id.equals(d.getId()) &&
               this.marca.equals(d.getMarca()) &&
               this.modelo.equals(d.getModelo()) &&
               this.consumoHora == d.getConsumoHora() &&
               this.estado.equals(d.getEstado());
    }
}