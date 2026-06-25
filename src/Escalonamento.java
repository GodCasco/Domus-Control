import java.time.LocalTime;

public class Escalonamento extends Automacao {
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Escalonamento() {
        super();
        this.horaInicio = LocalTime.of(0, 0);
        this.horaFim = LocalTime.of(0, 0);
    }

    public Escalonamento(String id, String nome, Utilizador utilizador, String condicao, LocalTime horaInicio, LocalTime horaFim) {
        super(id, nome, utilizador, condicao);
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public Escalonamento(Escalonamento e) {
        super(e);
        this.horaInicio = e.getHoraInicio();
        this.horaFim = e.getHoraFim();
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return this.horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    @Override
    public Escalonamento clone() {
        return new Escalonamento(this);
    }

    @Override
    public String toString() {
        return super.toString() +
               "Hora Inicio: " + this.horaInicio.toString() + "\n" +
               "Hora Fim: " + this.horaFim.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Escalonamento e = (Escalonamento) o;
        return this.horaInicio.equals(e.getHoraInicio()) &&
               this.horaFim.equals(e.getHoraFim());
    }
}