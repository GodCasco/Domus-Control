public class Coluna extends Dispositivo{
    private int volume;

    public Coluna(){
        super();
        this.volume = 10;
    }

    public Coluna(String id, String marca, String modelo, int consumo, String estado, int volume){
        super(id, marca, modelo, consumo, estado);
        this.volume = volume;
    }

    public Coluna(Coluna c){
        super(c);
        this.volume = c.getVolume();
    }

    public int getVolume(){
        return this.volume;
    }

    public void setVolume(int v){
        this.volume = v;
    }

    @Override
    public Coluna clone(){
        return new Coluna(this);
    }

    @Override
    public String toString(){
        return super.toString() + "Volume: " + this.volume + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Coluna c = (Coluna) o;
        return this.volume == c.getVolume();
    }
}