public class Tomada extends Dispositivo {

    public Tomada(){
        super();
    }

    public Tomada(String id, String marca, String modelo, int consumo, String estado){
        super(id, marca, modelo, consumo, estado);
    }

    public Tomada(Tomada t){
        super(t);
    }

    @Override
    public Tomada clone(){
        return new Tomada(this);
    }

    @Override
    public String toString(){
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return super.equals(o);
    }
}