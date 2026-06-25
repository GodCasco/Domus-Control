import java.io.Serializable;
import java.time.LocalTime;
import java.util.Map;

public class Historico implements Serializable{
    private static final long serialVersionUID = 1L;
    private String tipoDispositivo;
    private String idUtilizador;
    private String idDispositivo;
    private String acao;
    private LocalTime hora;
    private Map<String, String> estados;

    public Historico(String tipoDispositivo, String idUtilizador, String idDispositivo, String acao, LocalTime hora, Map<String, String> estados){
        this.tipoDispositivo = tipoDispositivo;
        this.idUtilizador = idUtilizador;
        this.idDispositivo = idDispositivo;
        this.acao = acao;
        this.hora = hora;
        this.estados = new java.util.HashMap<>(estados);
    }

    public Historico(Historico h){
        this.tipoDispositivo = h.getTipoDispositivo();
        this.idUtilizador = h.getIdUtilizador();
        this.idDispositivo = h.getIdDispositivo();
        this.acao = h.getAcao();
        this.hora = h.getHora();
        this.estados = h.getEstados();
    }

    public String getTipoDispositivo(){
        return this.tipoDispositivo;
    }

    public String getIdUtilizador(){
        return this.idUtilizador;
    }

    public String getIdDispositivo(){
        return this.idDispositivo;
    }

    public String getAcao(){
        return this.acao;
    }

    public LocalTime getHora(){
        return this.hora;
    }

    public Map<String, String> getEstados() {
        return new java.util.HashMap<>(this.estados);
    }

    @Override
    public Historico clone(){
        return new Historico(this);
    }

    @Override
    public String toString(){
        return "Utilizador: " + this.idUtilizador + " | Dispositivo: " + this.idDispositivo +
               " | Acao: " + this.acao + " | Hora: " + this.hora + "\n";
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Historico r = (Historico) o;
        return this.idUtilizador.equals(r.getIdUtilizador()) &&
               this.idDispositivo.equals(r.getIdDispositivo()) &&
               this.acao.equals(r.getAcao()) &&
               this.hora.equals(r.getHora());
    }
}