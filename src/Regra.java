import java.io.Serializable;

public class Regra implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private String dispositivoOrigemId;
    private String atributoParaVerificar;
    private String operador;
    private Object valorReferencia;
    private String dispositivoDestinoId;
    private String metodoParaExecutar;
    private Object valorAcao;

    public Regra() {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = "undefined";
        this.dispositivoOrigemId = "undefined";
        this.atributoParaVerificar = "undefined";
        this.operador = "undefined";
        this.valorReferencia = null;
        this.dispositivoDestinoId = "undefined";
        this.metodoParaExecutar = "undefined";
        this.valorAcao = null;
    }

    public Regra(String nome, String dispositivoOrigemId, String atributoParaVerificar, String operador,
            Object valorReferencia, String dispositivoDestinoId, String metodoParaExecutar, Object valorAcao) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.dispositivoOrigemId = dispositivoOrigemId;
        this.atributoParaVerificar = atributoParaVerificar;
        this.operador = operador;
        this.valorReferencia = valorReferencia;
        this.dispositivoDestinoId = dispositivoDestinoId;
        this.metodoParaExecutar = metodoParaExecutar;
        this.valorAcao = valorAcao;
    }

    public Regra(Regra r) {
        this.id = r.getId();
        this.nome = r.getNome();
        this.dispositivoOrigemId = r.getDispositivoOrigemId();
        this.atributoParaVerificar = r.getAtributoParaVerificar();
        this.operador = r.getOperador();
        this.valorReferencia = r.getValorReferencia();
        this.dispositivoDestinoId = r.getDispositivoDestinoId();
        this.metodoParaExecutar = r.getMetodoParaExecutar();
        this.valorAcao = r.getValorAcao();
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

    public String getDispositivoOrigemId() {
        return this.dispositivoOrigemId;
    }

    public void setDispositivoOrigemId(String dispositivoOrigemId) {
        this.dispositivoOrigemId = dispositivoOrigemId;
    }

    public String getAtributoParaVerificar() {
        return this.atributoParaVerificar;
    }

    public void setAtributoParaVerificar(String atributoParaVerificar) {
        this.atributoParaVerificar = atributoParaVerificar;
    }

    public String getOperador() {
        return this.operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Object getValorReferencia() {
        return this.valorReferencia;
    }

    public void setValorReferencia(Object valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public String getDispositivoDestinoId() {
        return this.dispositivoDestinoId;
    }

    public void setDispositivoDestinoId(String dispositivoDestinoId) {
        this.dispositivoDestinoId = dispositivoDestinoId;
    }

    public String getMetodoParaExecutar() {
        return this.metodoParaExecutar;
    }

    public void setMetodoParaExecutar(String metodoParaExecutar) {
        this.metodoParaExecutar = metodoParaExecutar;
    }

    public Object getValorAcao() {
        return this.valorAcao;
    }

    public void setValorAcao(Object valorAcao) {
        this.valorAcao = valorAcao;
    }

    @Override
    public Regra clone() {
        return new Regra(this);
    }

    @Override
    public String toString() {
        return "Id: " + this.id + "\n" +
               "Nome: " + this.nome + "\n" +
               "Dispositivo Origem: " + this.dispositivoOrigemId + "\n" +
               "Atributo: " + this.atributoParaVerificar + "\n" +
               "Operador: " + this.operador + "\n" +
               "Valor Referencia: " + this.valorReferencia + "\n" +
               "Dispositivo Destino: " + this.dispositivoDestinoId + "\n" +
               "Metodo: " + this.metodoParaExecutar + "\n" +
               "Valor Acao: " + this.valorAcao + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Regra r = (Regra) o;
        return this.id.equals(r.getId()) &&
               this.nome.equals(r.getNome()) &&
               this.dispositivoOrigemId.equals(r.getDispositivoOrigemId()) &&
               this.atributoParaVerificar.equals(r.getAtributoParaVerificar()) &&
               this.operador.equals(r.getOperador()) &&
               this.dispositivoDestinoId.equals(r.getDispositivoDestinoId()) &&
               this.metodoParaExecutar.equals(r.getMetodoParaExecutar());
    }
}