public class Lampada extends Dispositivo{

    private double intensidade;
    private int temperaturaCor;

    public Lampada(){
        super();
        this.intensidade = 0;
        this.temperaturaCor = 2700;
    }

    public Lampada(Lampada l){
        super(l);
        this.intensidade = l.getIntensidade();
        this.temperaturaCor = l.getTemperaturaCor();
    }

    public Lampada(String id, String marca, String modelo, int consumo, String estado, double intensidade, int cor){
        super(id, marca, modelo, consumo, estado);
        this.intensidade = intensidade;
        this.temperaturaCor = cor;
    }

    public double getIntensidade(){
        return this.intensidade;
    }

    public void setIntensidade(double i){
        this.intensidade = i;
    }

    public int getTemperaturaCor() {
        return this.temperaturaCor;
    }

    public void setTemperaturaCor(int cor) {
        this.temperaturaCor = cor;
    }

    @Override
    public Lampada clone(){
        return new Lampada(this);
    }

    @Override
    public String toString(){
        return super.toString() + 
               "Intensidade: " + this.intensidade + "\n" +
               "Temperatura Cor: " + this.temperaturaCor + "K\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Lampada l = (Lampada) o;
        return Double.compare(this.intensidade, l.getIntensidade()) == 0 &&
               this.temperaturaCor == l.getTemperaturaCor();
    }
}