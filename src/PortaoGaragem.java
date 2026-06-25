public class PortaoGaragem extends Dispositivo {
    private int abertura;

    public PortaoGaragem(){
        super();
        this.abertura = 0;
        this.setEstado("Fechado");
    }

    public PortaoGaragem(String id, String marca, String modelo, int consumo, String estado, int abertura){
        super(id, marca, modelo, consumo, estado);
        this.setAbertura(abertura); 
    }

    public PortaoGaragem(PortaoGaragem p){
        super(p);
        this.abertura = p.getAbertura();
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

    public void fecharPortao(){
        this.setAbertura(0);
    }

    public void abrirPortao(){
        this.setAbertura(100);
    }

    @Override
    public PortaoGaragem clone(){
        return new PortaoGaragem(this);
    }

    @Override
    public String toString(){
        return super.toString() + 
               "Abertura do Portão: " + this.abertura + "%\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PortaoGaragem p = (PortaoGaragem) o;
        return this.abertura == p.getAbertura();
    }
}