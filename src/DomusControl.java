public class DomusControl {
    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        // sistema.carregarDadosTeste();
        Menu menu = new Menu(sistema);
        menu.iniciar();
    }
}