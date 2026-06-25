public class Cortina extends Dispositivo{
    private int abertura;

    public Cortina(){
        super();
        this.abertura = 0;
    }

    public Cortina(String id, String marca, String modelo, int consumo, String estado, int abertura){
        super(id, marca, modelo, consumo, estado);
        setAbertura(abertura);
    }

    public Cortina(Cortina c){
        super(c);
        this.abertura = c.getAbertura();
    }

    public int getAbertura(){
        return this.abertura;
    }

    public void setAbertura(int a){
        if (a < 0) a = 0;
        if (a > 100) a = 100;

        this.abertura = a;

        if (this.abertura == 0){
            this.setEstado("Fechado");
        }

        else if (this.abertura == 100){
            this.setEstado("Aberto");
        }

        else{
            this.setEstado("Entreaberto");
        }
    }

    public void fecharCortina(){
        this.abertura = 0;
        this.setEstado("Fechado");
    }

    public void abrirCortina(){
        this.abertura = 100;
        this.setEstado("Aberto");
    }

    @Override
    public Cortina clone() {
        return new Cortina(this);
    }

    @Override
    public String toString() {
        return super.toString() + 
               "Grau de Abertura: " + this.abertura + "%\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cortina c = (Cortina) o;
        return this.abertura == c.getAbertura();
    }
}