import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Menu é a camada de interação com o utilizador.
// Tem uma referência ao Sistema para invocar as operações.
// Todo o input/output é feito aqui, e só aqui (cf. Ficha 1 - metodologia POO).
public class Menu {
    private static final int INTERVALO_VERIFICACAO_SEGUNDOS = 5;
    private Sistema sistema;
    private Scanner scanner;
    private Utilizador utilizadorAtual;
    private java.util.Timer timerVerificacoes;

    public Menu(Sistema sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
        this.utilizadorAtual = null;
    }

    private int lerInt(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Introduza um numero inteiro.");
            }
        }
    }

    private double lerDouble(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Introduza um numero.");
            }
        }
    }

    private int lerIntIntervalo(String mensagem, int min, int max) {
        while (true) {
            int valor = lerInt(mensagem);
            if (valor >= min && valor <= max) return valor;
            System.out.println("Valor invalido. Introduza um valor entre " + min + " e " + max + ".");
        }
    }

    private double lerDoubleIntervalo(String mensagem, double min, double max) {
        while (true) {
            double valor = lerDouble(mensagem);
            if (valor >= min && valor <= max) return valor;
            System.out.println("Valor invalido. Introduza um valor entre " + min + " e " + max + ".");
        }
    }

    private String lerString(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("Valor invalido. O campo nao pode estar vazio.");
        }
    }

    private void pausar() {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }

    private void iniciarVerificacaoAutomatica() {
        if (this.timerVerificacoes != null) return;
        this.timerVerificacoes = new java.util.Timer(true);
        long periodoMs = INTERVALO_VERIFICACAO_SEGUNDOS * 1000L;
        this.timerVerificacoes.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                try {
                    List<String> ids = new ArrayList<>(sistema.listarCasas().keySet());
                    for (String idCasa : ids) {
                        sistema.verificarAutomacoes(idCasa);
                        sistema.verificarEscalonamentos(idCasa);
                    }
                } catch (Exception e) {
                    // Evita parar a thread de verificacao por erro pontual
                }
            }
        }, periodoMs, periodoMs);
    }

    private Map<String, Casa> listarCasasVisiveis() {
        Map<String, Casa> casas = this.sistema.listarCasas();
        if (this.utilizadorAtual == null) return casas;
        Utilizador u = this.sistema.getUtilizadores().get(this.utilizadorAtual.getId());
        if (u == null) return new java.util.HashMap<>();
        casas.entrySet().removeIf(entry -> !u.temCasa(entry.getKey()));
        return casas;
    }

    private String escolherCasa() {
        Map<String, Casa> casas = listarCasasVisiveis();
        if (casas.isEmpty()) {
            System.out.println("Nao existem casas disponiveis para este utilizador.");
            pausar();
            return null;
        }
        List<String> ids = new ArrayList<>(casas.keySet());
        System.out.println("\n=== Casas disponiveis ===");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println((i + 1) + ". " + casas.get(ids.get(i)).getNome());
        }
        int opcao = lerIntIntervalo("Escolha a casa: ", 1, ids.size());
        return ids.get(opcao - 1);
    }

    private String escolherDivisao(String idCasa) {
        Map<String, Divisao> divisoes = this.sistema.listarDivisoes(idCasa);
        if (divisoes.isEmpty()) {
            System.out.println("Nao existem divisoes nesta casa.");
            pausar();
            return null;
        }
        List<String> nomes = new ArrayList<>(divisoes.keySet());
        System.out.println("\n=== Divisoes disponiveis ===");
        for (int i = 0; i < nomes.size(); i++) {
            System.out.println((i + 1) + ". " + nomes.get(i));
        }
        int opcao = lerIntIntervalo("Escolha a divisao: ", 1, nomes.size());
        return nomes.get(opcao - 1);
    }

    private String escolherUtilizador() {
        Map<String, Utilizador> utilizadores = this.sistema.getUtilizadores();
        if (utilizadores.isEmpty()) {
            System.out.println("Nao existem utilizadores registados.");
            pausar();
            return null;
        }
        List<String> ids = new ArrayList<>(utilizadores.keySet());
        System.out.println("\n=== Utilizadores disponiveis ===");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println((i + 1) + ". " + utilizadores.get(ids.get(i)).getUsername());
        }
        int opcao = lerIntIntervalo("Escolha o utilizador: ", 1, ids.size());
        return ids.get(opcao - 1);
    }

    private void login() {
        System.out.println("\n=== DomusControl ===");
        System.out.println("1. Login");
        System.out.println("2. Criar conta");
        System.out.println("3. Carregar estado");
        System.out.println("0. Sair");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        switch (opcao) {
            case 1:
                String username = lerString("Username: ");
                String password = lerString("Password: ");
                Utilizador u = this.sistema.login(username, password);
                if (u == null) {
                    System.out.println("Username ou password incorretos.");
                    pausar();
                } else {
                    this.utilizadorAtual = u;
                    System.out.println("Bem-vindo, " + u.getUsername() + "!");
                    pausar();
                }
                break;
            case 2:
                String novoUsername = lerString("Username: ");
                String novaPassword = lerString("Password: ");
                if (this.sistema.novoUtilizador(novoUsername, novaPassword)) {
                    System.out.println("Conta criada com sucesso! Faca login para continuar.");
                } else {
                    System.out.println("Username ja existe. Escolha outro nome.");
                }
                pausar();
                break;
            case 3:
                carregarEstado();
                break;
            case 0:
                System.out.println("Ate logo!");
                System.exit(0);
                break;
        }
    }

    public void iniciar() {
        while (this.utilizadorAtual == null) {
            login();
        }
        iniciarVerificacaoAutomatica();
        int opcao = -1;
        do {
            System.out.println("\n=== DomusControl === [" + this.utilizadorAtual.getUsername() + "]");
            System.out.println("1. Gerir Utilizadores (super user)");
            System.out.println("2. Gerir Casas");
            System.out.println("3. Gerir Dispositivos");
            System.out.println("4. Atuar sobre Dispositivos");
            System.out.println("5. Gerir Automacoes");
            System.out.println("6. Gerir Escalonamentos");
            System.out.println("7. Gerir Cenarios");
            System.out.println("8. Guardar Estado");
            System.out.println("9. Carregar Estado");
            System.out.println("10. Estatisticas");
            System.out.println("11. Logout");
            System.out.println("0. Sair");
            opcao = lerIntIntervalo("Opcao: ", 0, 11);
            switch (opcao) {
                case 1: menuUtilizadores(); break;
                case 2: menuCasas(); break;
                case 3: menuDispositivos(); break;
                case 4: menuAtuarDispositivos(); break;
                case 5: menuAutomacoes(); break;
                case 6: menuEscalonamentos(); break;
                case 7: menuCenarios(); break;
                case 8: guardarEstado(); break;
                case 9: carregarEstado(); break;
                case 10: menuEstatisticas(); break;
                case 11:
                    this.utilizadorAtual = null;
                    System.out.println("Logout efetuado.");
                    pausar();
                    while (this.utilizadorAtual == null) login();
                    break;
                case 0: System.out.println("Ate logo!"); break;
            }
        } while (opcao != 0);
    }

    private void menuUtilizadores() {
        System.out.println("\n=== Gerir Utilizadores (super user) ===");
        System.out.println("1. Criar Utilizador");
        System.out.println("2. Listar Utilizadores");
        System.out.println("3. Ver detalhes de um Utilizador");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        switch (opcao) {
            case 1:
                String username = lerString("Username: ");
                String pass = lerString("Password: ");
                if (this.sistema.novoUtilizador(username, pass)) {
                    System.out.println("Utilizador criado com sucesso!");
                } else {
                    System.out.println("Username ja existe. Escolha outro nome.");
                }
                pausar();
                break;
            case 2:
                this.sistema.getUtilizadores().forEach((id, u) ->
                    System.out.println("Username: " + u.getUsername()));
                pausar();
                break;
            case 3:
                String idU = escolherUtilizador();
                if (idU == null) break;
                Utilizador u = this.sistema.getUtilizadores().get(idU);
                System.out.println("\n=== Detalhes do Utilizador ===");
                System.out.println("Username: " + u.getUsername());
                System.out.println("Casas e permissoes:");
                u.getPermissoesPorCasa().forEach((idCasa, nivel) ->
                    System.out.println("  Casa: " + idCasa + " | Permissao: " + nivel));
                pausar();
                break;
            case 0: break;
        }
    }

    private void menuCasas() {
        System.out.println("\n=== Gerir Casas ===");
        System.out.println("1. Criar Casa");
        System.out.println("2. Adicionar Divisao a Casa");
        System.out.println("3. Adicionar Utilizador a Casa");
        System.out.println("4. Listar Casas");
        System.out.println("5. Listar Divisoes de uma Casa");
        System.out.println("6. Ver detalhes de uma Casa");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 6);
        switch (opcao) {
            case 1:
                String nomeCasa = lerString("Nome da casa: ");
                this.sistema.novaCasa(nomeCasa, this.utilizadorAtual.getId());
                System.out.println("Casa criada com sucesso!");
                pausar();
                break;
            case 2:
                String idCasa = escolherCasa();
                if (idCasa == null) break;
                if (!verificarAdminCasa(idCasa)) break;
                String nomeDivisao = lerString("Nome da divisao: ");
                this.sistema.addDivisaoACasa(idCasa, new Divisao(nomeDivisao));
                System.out.println("Divisao adicionada com sucesso!");
                pausar();
                break;
            case 3:
                adicionarUtilizadorACasa();
                break;
            case 4:
                listarCasasVisiveis().forEach((id, c) ->
                    System.out.println("Nome: " + c.getNome()));
                pausar();
                break;
            case 5:
                String idCasaDiv = escolherCasa();
                if (idCasaDiv == null) break;
                this.sistema.listarDivisoes(idCasaDiv).forEach((nome, d) ->
                    System.out.println("Divisao: " + nome + " | Dispositivos: " + d.getDispositivos().size()));
                pausar();
                break;
            case 6:
                String idCasaDet = escolherCasa();
                if (idCasaDet == null) break;
                Casa c = this.sistema.listarCasas().get(idCasaDet);
                System.out.println("\n=== Detalhes da Casa ===");
                System.out.println("Nome: " + c.getNome());
                System.out.println("Divisoes:");
                c.getDivisoes().forEach((nome, d) ->
                    System.out.println("  " + nome + " | Dispositivos: " + d.getDispositivos().size()));
                pausar();
                break;
            case 0: break;
        }
    }

    private void menuDispositivos() {
        System.out.println("\n=== Gerir Dispositivos ===");
        System.out.println("1. Adicionar Lampada (admin)");
        System.out.println("2. Adicionar Coluna (admin)");
        System.out.println("3. Adicionar Cortina (admin)");
        System.out.println("4. Adicionar Portao de Garagem (admin)");
        System.out.println("5. Adicionar Tomada (admin)");
        System.out.println("6. Listar Dispositivos de uma Divisao");
        System.out.println("7. Ver detalhes de um Dispositivo");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 7);
        switch (opcao) {
            case 1: adicionarDispositivo("lampada"); break;
            case 2: adicionarDispositivo("coluna"); break;
            case 3: adicionarDispositivo("cortina"); break;
            case 4: adicionarDispositivo("portao"); break;
            case 5: adicionarDispositivo("tomada"); break;
            case 6: listarDispositivos(); break;
            case 7: verDetalhesDispositivo(); break;
            case 0: break;
        }
    }

    private boolean verificarAdminCasa(String idCasa) {
        Utilizador u = this.sistema.getUtilizadores().get(this.utilizadorAtual.getId());
        if (u == null || !u.isAdmin(idCasa)) {
            System.out.println("Sem permissao. Apenas administradores podem realizar esta operacao.");
            pausar();
            return false;
        }
        return true;
    }

    private void adicionarUtilizadorACasa() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        if (!verificarAdminCasa(idCasa)) return;
        String username = lerString("Username do utilizador: ");
        String idUtilizador = this.sistema.getIdUtilizadorPorUsername(username);
        if (idUtilizador == null) {
            System.out.println("Utilizador nao encontrado.");
            pausar();
            return;
        }
        Utilizador u = this.sistema.getUtilizadores().get(idUtilizador);
        if (u != null && u.temCasa(idCasa)) {
            System.out.println("Utilizador ja pertence a esta casa.");
            pausar();
            return;
        }
        this.sistema.associarCasa(idUtilizador, idCasa, AdminLevel.USER);
        System.out.println("Utilizador adicionado com sucesso!");
        pausar();
    }

    private void adicionarDispositivo(String tipo) {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        if (!verificarAdminCasa(idCasa)) return;
        String nomeDivisao = escolherDivisao(idCasa);
        if (nomeDivisao == null) return;
        switch (tipo) {
            case "lampada": adicionarLampada(idCasa, nomeDivisao); break;
            case "coluna": adicionarColuna(idCasa, nomeDivisao); break;
            case "cortina": adicionarCortina(idCasa, nomeDivisao); break;
            case "portao": adicionarPortao(idCasa, nomeDivisao); break;
            case "tomada": adicionarTomada(idCasa, nomeDivisao); break;
        }
    }

    private void verDetalhesDispositivo() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String nomeDivisao = escolherDivisao(idCasa);
        if (nomeDivisao == null) return;
        List<Dispositivo> dispositivos = this.sistema.listarDivisoes(idCasa).get(nomeDivisao).getDispositivos();
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta divisao.");
            pausar();
            return;
        }
        System.out.println("\n=== Dispositivos disponiveis ===");
        for (int i = 0; i < dispositivos.size(); i++) {
            System.out.println((i + 1) + ". " + dispositivos.get(i).getMarca() + " " + dispositivos.get(i).getModelo());
        }
        int opcao = lerIntIntervalo("Escolha o dispositivo: ", 1, dispositivos.size());
        System.out.println("\n=== Detalhes do Dispositivo ===");
        System.out.println(dispositivos.get(opcao - 1).toString());
        pausar();
    }

    private void adicionarLampada(String idCasa, String nomeDivisao) {
        String id = java.util.UUID.randomUUID().toString();
        String marca = lerString("Marca: ");
        String modelo = lerString("Modelo: ");
        int consumo = lerIntIntervalo("Consumo (Wh): ", 0, Integer.MAX_VALUE);
        double intensidade = lerDoubleIntervalo("Intensidade (0-100): ", 0, 100);
        int cor = lerIntIntervalo("Temperatura cor (2700-4000): ", 2700, 4000);
        this.sistema.addDispositivoADivisao(idCasa, nomeDivisao, new Lampada(id, marca, modelo, consumo, "Desligado", intensidade, cor));
        System.out.println("Lampada adicionada com sucesso!");
        pausar();
    }

    private void adicionarColuna(String idCasa, String nomeDivisao) {
        String id = java.util.UUID.randomUUID().toString();
        String marca = lerString("Marca: ");
        String modelo = lerString("Modelo: ");
        int consumo = lerIntIntervalo("Consumo (Wh): ", 0, Integer.MAX_VALUE);
        int volume = lerIntIntervalo("Volume (0-100): ", 0, 100);
        this.sistema.addDispositivoADivisao(idCasa, nomeDivisao, new Coluna(id, marca, modelo, consumo, "Desligado", volume));
        System.out.println("Coluna adicionada com sucesso!");
        pausar();
    }

    private void adicionarCortina(String idCasa, String nomeDivisao) {
        String id = java.util.UUID.randomUUID().toString();
        String marca = lerString("Marca: ");
        String modelo = lerString("Modelo: ");
        int consumo = lerIntIntervalo("Consumo (Wh): ", 0, Integer.MAX_VALUE);
        int abertura = lerIntIntervalo("Abertura (0-100): ", 0, 100);
        this.sistema.addDispositivoADivisao(idCasa, nomeDivisao, new Cortina(id, marca, modelo, consumo, "Fechado", abertura));
        System.out.println("Cortina adicionada com sucesso!");
        pausar();
    }

    private void adicionarPortao(String idCasa, String nomeDivisao) {
        String id = java.util.UUID.randomUUID().toString();
        String marca = lerString("Marca: ");
        String modelo = lerString("Modelo: ");
        int consumo = lerIntIntervalo("Consumo (Wh): ", 0, Integer.MAX_VALUE);
        int abertura = lerIntIntervalo("Abertura (0-100): ", 0, 100);
        this.sistema.addDispositivoADivisao(idCasa, nomeDivisao, new PortaoGaragem(id, marca, modelo, consumo, "Fechado", abertura));
        System.out.println("Portao adicionado com sucesso!");
        pausar();
    }

    private void adicionarTomada(String idCasa, String nomeDivisao) {
        String id = java.util.UUID.randomUUID().toString();
        String marca = lerString("Marca: ");
        String modelo = lerString("Modelo: ");
        int consumo = lerIntIntervalo("Consumo (Wh): ", 0, Integer.MAX_VALUE);
        this.sistema.addDispositivoADivisao(idCasa, nomeDivisao, new Tomada(id, marca, modelo, consumo, "Desligado"));
        System.out.println("Tomada adicionada com sucesso!");
        pausar();
    }

    private void listarDispositivos() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String nomeDivisao = escolherDivisao(idCasa);
        if (nomeDivisao == null) return;
        List<Dispositivo> dispositivos = this.sistema.listarDivisoes(idCasa).get(nomeDivisao).getDispositivos();
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta divisao.");
        } else {
            dispositivos.forEach(d ->
                System.out.println("Id: " + d.getId() + " | " + d.getMarca() + " " + d.getModelo() + " | Estado: " + d.getEstado()));
        }
        pausar();
    }

    private void menuAtuarDispositivos() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String nomeDivisao = escolherDivisao(idCasa);
        if (nomeDivisao == null) return;
        List<Dispositivo> dispositivos = this.sistema.listarDivisoes(idCasa).get(nomeDivisao).getDispositivos();
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta divisao.");
            pausar();
            return;
        }
        System.out.println("\n=== Dispositivos disponiveis ===");
        for (int i = 0; i < dispositivos.size(); i++) {
            Dispositivo d = dispositivos.get(i);
            System.out.println((i + 1) + ". [" + d.getId() + "] " + d.getMarca() + " " + d.getModelo() + " | Estado: " + d.getEstado());
        }
        int opcao = lerIntIntervalo("Escolha o dispositivo: ", 1, dispositivos.size());
        Dispositivo d = dispositivos.get(opcao - 1);
        if (d instanceof Lampada) atuarLampada(idCasa, nomeDivisao, (Lampada) d);
        else if (d instanceof Coluna) atuarColuna(idCasa, nomeDivisao, (Coluna) d);
        else if (d instanceof Cortina) atuarCortina(idCasa, nomeDivisao, (Cortina) d);
        else if (d instanceof PortaoGaragem) atuarPortao(idCasa, nomeDivisao, (PortaoGaragem) d);
        else if (d instanceof Tomada) atuarTomada(idCasa, nomeDivisao, (Tomada) d);
    }

    private void atuarLampada(String idCasa, String nomeDivisao, Lampada l) {
        System.out.println("\n=== Atuar sobre Lampada ===");
        System.out.println("Estado atual: " + l.getEstado());
        System.out.println("1. Ligar");
        System.out.println("2. Desligar");
        System.out.println("3. Alterar intensidade");
        System.out.println("4. Alterar temperatura de cor");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 4);
        String acaoRealizada = "";

        switch (opcao) {
            case 1: l.ligar();
                acaoRealizada = "ligar";
                break;
            case 2: l.desligar();
                acaoRealizada = "desligar";
                break;
            case 3:
                double intensidade = lerDoubleIntervalo("Nova intensidade (0-100): ", 0, 100);
                l.setIntensidade(intensidade);
                break;
            case 4:
                int cor = lerIntIntervalo("Nova temperatura de cor (2700-4000): ", 2700, 4000);
                l.setTemperaturaCor(cor);
                break;
            case 0: return;
        }
        this.sistema.atualizarDispositivo(idCasa, nomeDivisao, l);
        if (!acaoRealizada.isEmpty()) {
            this.sistema.registarHistorico("Lampada", this.utilizadorAtual.getId(), l.getId(), acaoRealizada, this.sistema.estadosDivisao(this.sistema.listarDivisoes(idCasa).get(nomeDivisao)));
        }
        verificarESugerirEscalonamento(idCasa, l.getId(), acaoRealizada);
        verificarESugerirAutomacao(idCasa, l.getId(), acaoRealizada);
        System.out.println("Dispositivo atualizado com sucesso!");
        pausar();
    }

    private void atuarColuna(String idCasa, String nomeDivisao, Coluna c) {
        System.out.println("\n=== Atuar sobre Coluna ===");
        System.out.println("Estado atual: " + c.getEstado() + " | Volume: " + c.getVolume());
        System.out.println("1. Ligar");
        System.out.println("2. Desligar");
        System.out.println("3. Alterar volume");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        String acaoRealizada = "";

        switch (opcao) {
            case 1: c.ligar();
                acaoRealizada = "ligar";
                break;
            case 2: c.desligar(); 
                acaoRealizada = "desligar";
                break;
            case 3:
                int volume = lerIntIntervalo("Novo volume (0-100): ", 0, 100);
                c.setVolume(volume);
                break;
            case 0: return;
        }
        this.sistema.atualizarDispositivo(idCasa, nomeDivisao, c);
        if (!acaoRealizada.isEmpty()) {
            this.sistema.registarHistorico("Coluna", this.utilizadorAtual.getId(), c.getId(), acaoRealizada, this.sistema.estadosDivisao(this.sistema.listarDivisoes(idCasa).get(nomeDivisao)));
        }
        verificarESugerirEscalonamento(idCasa, c.getId(), acaoRealizada);
        verificarESugerirAutomacao(idCasa, c.getId(), acaoRealizada);
        System.out.println("Dispositivo atualizado com sucesso!");

        pausar();
    }

    private void atuarCortina(String idCasa, String nomeDivisao, Cortina c) {
        System.out.println("\n=== Atuar sobre Cortina ===");
        System.out.println("Estado atual: " + c.getEstado() + " | Abertura: " + c.getAbertura() + "%");
        System.out.println("1. Abrir totalmente");
        System.out.println("2. Fechar totalmente");
        System.out.println("3. Definir grau de abertura");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        String acaoRealizada = "";

        switch (opcao) {
            case 1: c.abrirCortina();
                acaoRealizada = "abrirCortina";
                break;
            case 2: c.fecharCortina();
                acaoRealizada = "fecharCortina";
                break;
            case 3:
                int abertura = lerIntIntervalo("Grau de abertura (0-100): ", 0, 100);
                c.setAbertura(abertura);
                break;
            case 0: return;
        }
        this.sistema.atualizarDispositivo(idCasa, nomeDivisao, c);
        if (!acaoRealizada.isEmpty()) {
            this.sistema.registarHistorico("Cortina", this.utilizadorAtual.getId(), c.getId(), acaoRealizada, this.sistema.estadosDivisao(this.sistema.listarDivisoes(idCasa).get(nomeDivisao)));
        }
        verificarESugerirEscalonamento(idCasa, c.getId(), acaoRealizada);
        verificarESugerirAutomacao(idCasa, c.getId(), acaoRealizada);
        System.out.println("Dispositivo atualizado com sucesso!");
        pausar();
    }

    private void atuarPortao(String idCasa, String nomeDivisao, PortaoGaragem p) {
        System.out.println("\n=== Atuar sobre Portao de Garagem ===");
        System.out.println("Estado atual: " + p.getEstado() + " | Abertura: " + p.getAbertura() + "%");
        System.out.println("1. Abrir totalmente");
        System.out.println("2. Fechar totalmente");
        System.out.println("3. Definir grau de abertura");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        String acaoRealizada = "";

        switch (opcao) {
            case 1: p.abrirPortao();
                acaoRealizada = "abrirPortao";
                break;
            case 2: p.fecharPortao();
                acaoRealizada = "fecharPortao";
                break;
            case 3:
                int abertura = lerIntIntervalo("Grau de abertura (0-100): ", 0, 100);
                p.setAbertura(abertura);
                break;
            case 0: return;
        }
        this.sistema.atualizarDispositivo(idCasa, nomeDivisao, p);
        if (!acaoRealizada.isEmpty()) {
            this.sistema.registarHistorico("Portao", this.utilizadorAtual.getId(), p.getId(), acaoRealizada, this.sistema.estadosDivisao(this.sistema.listarDivisoes(idCasa).get(nomeDivisao)));
        }
        verificarESugerirEscalonamento(idCasa, p.getId(), acaoRealizada);
        verificarESugerirAutomacao(idCasa, p.getId(), acaoRealizada);
        System.out.println("Dispositivo atualizado com sucesso!");
        pausar();
    }

    private void atuarTomada(String idCasa, String nomeDivisao, Tomada t) {
        System.out.println("\n=== Atuar sobre Tomada ===");
        System.out.println("Estado atual: " + t.getEstado());
        System.out.println("1. Ligar");
        System.out.println("2. Desligar");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 2);
        String acaoRealizada = "";

        switch (opcao) {
            case 1: t.ligar();
                acaoRealizada = "ligar";
                break;
            case 2: t.desligar();
                acaoRealizada = "desligar";
                break;
            case 0: return;
        }
        this.sistema.atualizarDispositivo(idCasa, nomeDivisao, t);
        if (!acaoRealizada.isEmpty()) {
            this.sistema.registarHistorico("Tomada", this.utilizadorAtual.getId(), t.getId(), acaoRealizada, this.sistema.estadosDivisao(this.sistema.listarDivisoes(idCasa).get(nomeDivisao)));
        }
        verificarESugerirEscalonamento(idCasa, t.getId(), acaoRealizada);
        verificarESugerirAutomacao(idCasa, t.getId(), acaoRealizada);
        System.out.println("Dispositivo atualizado com sucesso!");
        pausar();
    }

    private void menuAutomacoes() {
        System.out.println("\n=== Gerir Automacoes ===");
        System.out.println("1. Criar Automacao");
        System.out.println("2. Listar Automacoes");
        System.out.println("3. Apagar Automacao");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        switch (opcao) {
            case 1: criarAutomacao(); break;
            case 2: listarAutomacoes(); break;
            case 3: apagarAutomacao(); break;
            case 0: break;
        }
    }

    private void preencherDispositivosCasa(String idCasa, List<String> divisoes, List<Dispositivo> dispositivos) {
        divisoes.clear();
        dispositivos.clear();
        this.sistema.listarDivisoes(idCasa).forEach((nomeDivisao, d) -> {
            for (Dispositivo disp : d.getDispositivos()) {
                divisoes.add(nomeDivisao);
                dispositivos.add(disp);
            }
        });
    }

    private void mostrarDispositivosAutomacao(List<String> divisoes, List<Dispositivo> dispositivos) {
        for (int i = 0; i < dispositivos.size(); i++) {
            Dispositivo d = dispositivos.get(i);
            System.out.println((i + 1) + ". [" + divisoes.get(i) + "] " + d.getClass().getSimpleName() +
                " | " + d.getMarca() + " " + d.getModelo() + " (id: " + d.getId() + ")");
        }
    }

    private List<String> atributosCondicao(Dispositivo d) {
        List<String> attrs = new ArrayList<>();
        attrs.add("estado");
        if (d instanceof Lampada) {
            attrs.add("intensidade");
            attrs.add("temperaturaCor");
        }
        if (d instanceof Coluna) {
            attrs.add("volume");
        }
        if (d instanceof Cortina || d instanceof PortaoGaragem) {
            attrs.add("abertura");
        }
        return attrs;
    }

    private List<String> atributosAcao(Dispositivo d) {
        List<String> attrs = new ArrayList<>();
        if (d instanceof Lampada) {
            attrs.add("estado");
            attrs.add("intensidade");
            attrs.add("temperaturaCor");
        } else if (d instanceof Coluna) {
            attrs.add("estado");
            attrs.add("volume");
        } else if (d instanceof Tomada) {
            attrs.add("estado");
        } else if (d instanceof Cortina || d instanceof PortaoGaragem) {
            attrs.add("abertura");
        }
        return attrs;
    }

    private String labelAtributo(String atributo) {
        switch (atributo) {
            case "estado": return "estado";
            case "volume": return "volume";
            case "abertura": return "abertura";
            case "intensidade": return "intensidade";
            case "temperaturaCor": return "temperatura de cor";
            default: return atributo;
        }
    }

    private String escolherEstadoCondicao(Dispositivo d) {
        List<String> estados = new ArrayList<>();
        if (d instanceof Cortina || d instanceof PortaoGaragem) {
            estados.add("Fechado");
            estados.add("Entreaberto");
            estados.add("Aberto");
        } else {
            estados.add("Ligado");
            estados.add("Desligado");
        }
        System.out.println("Estados possiveis:");
        for (int i = 0; i < estados.size(); i++) {
            System.out.println((i + 1) + ". " + estados.get(i));
        }
        int opcao = lerIntIntervalo("Escolha: ", 1, estados.size());
        return estados.get(opcao - 1);
    }

    private String escolherEstadoAcao() {
        List<String> estados = new ArrayList<>();
        estados.add("Ligado");
        estados.add("Desligado");
        System.out.println("Estado desejado:");
        for (int i = 0; i < estados.size(); i++) {
            System.out.println((i + 1) + ". " + estados.get(i));
        }
        int opcao = lerIntIntervalo("Escolha: ", 1, estados.size());
        return estados.get(opcao - 1);
    }

    private Object lerValorAtributo(String atributo, String mensagem) {
        switch (atributo) {
            case "volume":
                return lerIntIntervalo(mensagem + " (0-100): ", 0, 100);
            case "abertura":
                return lerIntIntervalo(mensagem + " (0-100): ", 0, 100);
            case "intensidade":
                return lerDoubleIntervalo(mensagem + " (0-100): ", 0, 100);
            case "temperaturaCor":
                return lerIntIntervalo(mensagem + " (2700-4000): ", 2700, 4000);
            default:
                return lerString(mensagem + ": ");
        }
    }

    private void criarAutomacao() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String id = java.util.UUID.randomUUID().toString();
        String nome = lerString("Nome da automacao: ");
        String condicao = lerString("Descricao da condicao: ");
        Automacao a = new Automacao(id, nome, this.utilizadorAtual, condicao);
        List<String> divisoes = new ArrayList<>();
        List<Dispositivo> dispositivos = new ArrayList<>();
        preencherDispositivosCasa(idCasa, divisoes, dispositivos);
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Dispositivos disponiveis ===");
        mostrarDispositivosAutomacao(divisoes, dispositivos);
        System.out.println("\n--- Condicao (dispositivo de origem) ---");
        int idxOrigem = lerIntIntervalo("Escolha o dispositivo de origem: ", 1, dispositivos.size()) - 1;
        Dispositivo origem = dispositivos.get(idxOrigem);
        String idOrigem = origem.getId();
        List<String> atributos = atributosCondicao(origem);
        System.out.println("Atributo a verificar:");
        for (int i = 0; i < atributos.size(); i++) {
            System.out.println((i + 1) + ". " + labelAtributo(atributos.get(i)));
        }
        int idxAtributo = lerIntIntervalo("Escolha: ", 1, atributos.size()) - 1;
        String atributo = atributos.get(idxAtributo);
        String operador = "==";
        if (!atributo.equals("estado")) {
            System.out.println("Operador: 1. >   2. <   3. ==");
            int op = lerIntIntervalo("Escolha: ", 1, 3);
            operador = op == 1 ? ">" : op == 2 ? "<" : "==";
        }
        Object valorRef = atributo.equals("estado")
            ? escolherEstadoCondicao(origem)
            : lerValorAtributo(atributo, "Valor de referencia");
        System.out.println("\n--- Acao (dispositivo de destino) ---");
        mostrarDispositivosAutomacao(divisoes, dispositivos);
        int idxDestino = lerIntIntervalo("Escolha o dispositivo de destino: ", 1, dispositivos.size()) - 1;
        Dispositivo destino = dispositivos.get(idxDestino);
        String idDestino = destino.getId();
        List<String> atributosAcao = atributosAcao(destino);
        System.out.println("Atributo a alterar:");
        for (int i = 0; i < atributosAcao.size(); i++) {
            System.out.println((i + 1) + ". " + labelAtributo(atributosAcao.get(i)));
        }
        int idxAcao = lerIntIntervalo("Escolha: ", 1, atributosAcao.size()) - 1;
        String atributoAcao = atributosAcao.get(idxAcao);
        String metodo = "";
        Object valorAcao = null;
        if (atributoAcao.equals("estado")) {
            String estado = escolherEstadoAcao();
            metodo = estado.equals("Ligado") ? "ligar" : "desligar";
        } else if (atributoAcao.equals("volume")) {
            metodo = "setVolume";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("abertura")) {
            metodo = "setAbertura";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("intensidade")) {
            metodo = "setIntensidade";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("temperaturaCor")) {
            metodo = "setTemperaturaCor";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        }
        Regra regra = new Regra(nome, idOrigem, atributo, operador, valorRef, idDestino, metodo, valorAcao);
        a.adicionarRegra(regra);
        this.sistema.addAutomacaoACasa(idCasa, a);
        System.out.println("Automacao criada com sucesso!");
        pausar();
    }

    private void listarAutomacoes() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Automacao> automacoes = this.sistema.listarAutomacoes(idCasa);
        if (automacoes.isEmpty()) {
            System.out.println("Nao existem automacoes para esta casa.");
        } else {
            for (Automacao a : automacoes) {
                System.out.println("Id: " + a.getId() + " | Nome: " + a.getNome() + " | Condicao: " + a.getCondicao());
            }
        }
        pausar();
    }

    private void verificarAutomacoes() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        System.out.println(this.sistema.verificarAutomacoes(idCasa));
        pausar();
    }

    private void apagarAutomacao() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Automacao> automacoes = this.sistema.listarAutomacoes(idCasa);
        if (automacoes.isEmpty()) {
            System.out.println("Nao existem automacoes para esta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Automacoes disponiveis ===");
        for (int i = 0; i < automacoes.size(); i++) {
            Automacao a = automacoes.get(i);
            System.out.println((i + 1) + ". " + a.getNome() + " | Condicao: " + a.getCondicao());
        }
        int opcao = lerIntIntervalo("Escolha a automacao a apagar: ", 1, automacoes.size());
        Automacao a = automacoes.get(opcao - 1);
        this.sistema.removerAutomacao(idCasa, a.getId());
        System.out.println("Automacao apagada com sucesso!");
        pausar();
    }

    private void menuEscalonamentos() {
        System.out.println("\n=== Gerir Escalonamentos ===");
        System.out.println("1. Criar Escalonamento");
        System.out.println("2. Listar Escalonamentos");
        System.out.println("3. Apagar Escalonamento");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 3);
        switch (opcao) {
            case 1: criarEscalonamento(); break;
            case 2: listarEscalonamentos(); break;
            case 3: apagarEscalonamento(); break;
            case 0: break;
        }
    }

    private void criarEscalonamento() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String id = java.util.UUID.randomUUID().toString();
        String nome = lerString("Nome do escalonamento: ");
        System.out.print("Hora de inicio (HH:mm): ");
        String inicioStr = scanner.nextLine().trim();
        System.out.print("Hora de fim (HH:mm): ");
        String fimStr = scanner.nextLine().trim();
        java.time.LocalTime horaInicio = java.time.LocalTime.parse(inicioStr);
        java.time.LocalTime horaFim = java.time.LocalTime.parse(fimStr);
        String condicao = lerString("Condicao: ");
        Escalonamento e = new Escalonamento(id, nome, this.utilizadorAtual, condicao, horaInicio, horaFim);
        List<String> divisoes = new ArrayList<>();
        List<Dispositivo> dispositivos = new ArrayList<>();
        preencherDispositivosCasa(idCasa, divisoes, dispositivos);
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Dispositivos disponiveis ===");
        mostrarDispositivosAutomacao(divisoes, dispositivos);
        int idxDestino = lerIntIntervalo("Escolha o dispositivo a atuar: ", 1, dispositivos.size()) - 1;
        Dispositivo destino = dispositivos.get(idxDestino);
        String idDestino = destino.getId();
        List<String> atributosAcao = atributosAcao(destino);
        System.out.println("Atributo a alterar:");
        for (int i = 0; i < atributosAcao.size(); i++) {
            System.out.println((i + 1) + ". " + labelAtributo(atributosAcao.get(i)));
        }
        int idxAcao = lerIntIntervalo("Escolha: ", 1, atributosAcao.size()) - 1;
        String atributoAcao = atributosAcao.get(idxAcao);
        String metodo = "";
        Object valorAcao = null;
        if (atributoAcao.equals("estado")) {
            String estado = escolherEstadoAcao();
            metodo = estado.equals("Ligado") ? "ligar" : "desligar";
        } else if (atributoAcao.equals("volume")) {
            metodo = "setVolume";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("abertura")) {
            metodo = "setAbertura";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("intensidade")) {
            metodo = "setIntensidade";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        } else if (atributoAcao.equals("temperaturaCor")) {
            metodo = "setTemperaturaCor";
            valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
        }
        Regra regra = new Regra(nome, "N/A", "N/A", "N/A", "N/A", idDestino, metodo, valorAcao);
        e.adicionarRegra(regra);
        this.sistema.addEscalonamentoACasa(idCasa, e);
        System.out.println("Escalonamento criado com sucesso!");
        pausar();
    }

    private void listarEscalonamentos() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Escalonamento> escalonamentos = this.sistema.listarEscalonamentos(idCasa);
        if (escalonamentos.isEmpty()) {
            System.out.println("Nao existem escalonamentos para esta casa.");
        } else {
            for (Escalonamento e : escalonamentos) {
                System.out.println("Id: " + e.getId() + " | Nome: " + e.getNome() + " | Condicao: " + e.getCondicao() +
                    " | Inicio: " + e.getHoraInicio() + " | Fim: " + e.getHoraFim());
            }
        }
        pausar();
    }

    private void verificarEscalonamentos() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        System.out.println(this.sistema.verificarEscalonamentos(idCasa));
        pausar();
    }

    private void apagarEscalonamento() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Escalonamento> escalonamentos = this.sistema.listarEscalonamentos(idCasa);
        if (escalonamentos.isEmpty()) {
            System.out.println("Nao existem escalonamentos para esta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Escalonamentos disponiveis ===");
        for (int i = 0; i < escalonamentos.size(); i++) {
            Escalonamento e = escalonamentos.get(i);
            System.out.println((i + 1) + ". " + e.getNome() + " | Inicio: " + e.getHoraInicio() + " | Fim: " + e.getHoraFim());
        }
        int opcao = lerIntIntervalo("Escolha o escalonamento a apagar: ", 1, escalonamentos.size());
        Escalonamento e = escalonamentos.get(opcao - 1);
        this.sistema.removerEscalonamento(idCasa, e.getId());
        System.out.println("Escalonamento apagado com sucesso!");
        pausar();
    }

    private void menuCenarios() {
        System.out.println("\n=== Gerir Cenarios ===");
        System.out.println("1. Criar Cenario");
        System.out.println("2. Listar Cenarios");
        System.out.println("3. Ativar Cenario");
        System.out.println("4. Apagar Cenario");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 4);
        switch (opcao) {
            case 1: criarCenario(); break;
            case 2: listarCenarios(); break;
            case 3: ativarCenario(); break;
            case 4: apagarCenario(); break;
            case 0: break;
        }
    }

    private void criarCenario() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        String id = java.util.UUID.randomUUID().toString();
        String nome = lerString("Nome do cenario: ");
        Cenario c = new Cenario(id, nome, this.utilizadorAtual);
        List<String> divisoes = new ArrayList<>();
        List<Dispositivo> dispositivos = new ArrayList<>();
        preencherDispositivosCasa(idCasa, divisoes, dispositivos);
        if (dispositivos.isEmpty()) {
            System.out.println("Nao existem dispositivos nesta casa.");
            pausar();
            return;
        }
        String continuar = "s";
        while (continuar.equals("s")) {
            System.out.println("\n=== Dispositivos disponiveis ===");
            mostrarDispositivosAutomacao(divisoes, dispositivos);
            int idxDestino = lerIntIntervalo("Escolha o dispositivo a atuar: ", 1, dispositivos.size()) - 1;
            Dispositivo destino = dispositivos.get(idxDestino);
            String idDestino = destino.getId();
            List<String> atributosAcao = atributosAcao(destino);
            System.out.println("Atributo a alterar:");
            for (int i = 0; i < atributosAcao.size(); i++) {
                System.out.println((i + 1) + ". " + labelAtributo(atributosAcao.get(i)));
            }
            int idxAcao = lerIntIntervalo("Escolha: ", 1, atributosAcao.size()) - 1;
            String atributoAcao = atributosAcao.get(idxAcao);
            String metodo = "";
            Object valorAcao = null;
            if (atributoAcao.equals("estado")) {
                String estado = escolherEstadoAcao();
                metodo = estado.equals("Ligado") ? "ligar" : "desligar";
            } else if (atributoAcao.equals("volume")) {
                metodo = "setVolume";
                valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
            } else if (atributoAcao.equals("abertura")) {
                metodo = "setAbertura";
                valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
            } else if (atributoAcao.equals("intensidade")) {
                metodo = "setIntensidade";
                valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
            } else if (atributoAcao.equals("temperaturaCor")) {
                metodo = "setTemperaturaCor";
                valorAcao = lerValorAtributo(atributoAcao, "Novo valor");
            }
            c.addRegra(new Regra(nome, "N/A", "N/A", "N/A", "N/A", idDestino, metodo, valorAcao));
            System.out.print("Adicionar mais uma acao? (s/n): ");
            continuar = scanner.nextLine().trim().toLowerCase();
        }
        this.sistema.addCenarioACasa(idCasa, c);
        System.out.println("Cenario criado com sucesso!");
        pausar();
    }

    private void listarCenarios() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Cenario> cenarios = this.sistema.listarCenarios(idCasa);
        if (cenarios.isEmpty()) {
            System.out.println("Nao existem cenarios para esta casa.");
        } else {
            for (Cenario c : cenarios) {
                System.out.println("Id: " + c.getId() + " | Nome: " + c.getNome() + " | Acoes: " + c.getRegras().size());
            }
        }
        pausar();
    }

    private void ativarCenario() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Cenario> cenarios = this.sistema.listarCenarios(idCasa);
        if (cenarios.isEmpty()) {
            System.out.println("Nao existem cenarios para esta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Cenarios disponiveis ===");
        for (int i = 0; i < cenarios.size(); i++) {
            System.out.println((i + 1) + ". " + cenarios.get(i).getNome());
        }
        int opcao = lerIntIntervalo("Escolha o cenario: ", 1, cenarios.size());
        String idCenario = cenarios.get(opcao - 1).getId();
        System.out.println(this.sistema.ativarCenario(idCasa, idCenario));
        pausar();
    }

    private void apagarCenario() {
        String idCasa = escolherCasa();
        if (idCasa == null) return;
        List<Cenario> cenarios = this.sistema.listarCenarios(idCasa);
        if (cenarios.isEmpty()) {
            System.out.println("Nao existem cenarios para esta casa.");
            pausar();
            return;
        }
        System.out.println("\n=== Cenarios disponiveis ===");
        for (int i = 0; i < cenarios.size(); i++) {
            Cenario c = cenarios.get(i);
            System.out.println((i + 1) + ". " + c.getNome() + " | Acoes: " + c.getRegras().size());
        }
        int opcao = lerIntIntervalo("Escolha o cenario a apagar: ", 1, cenarios.size());
        Cenario c = cenarios.get(opcao - 1);
        this.sistema.removerCenario(idCasa, c.getId());
        System.out.println("Cenario apagado com sucesso!");
        pausar();
    }

    private void menuEstatisticas() {
        System.out.println("\n=== Estatisticas ===");
        System.out.println("1. Casa que mais consome");
        System.out.println("2. Top 3 dispositivos por ativacoes");
        System.out.println("3. Top 3 dispositivos por tempo ligado");
        System.out.println("4. Top 3 divisoes com mais dispositivos");
        System.out.println("5. Total de dispositivos ligados numa casa");
        System.out.println("6. Listar todos os dispositivos de uma casa");
        System.out.println("7. Consumo total de uma casa");
        System.out.println("0. Voltar");
        int opcao = lerIntIntervalo("Opcao: ", 0, 7);
        switch (opcao) {
            case 1:
                System.out.println(this.sistema.casaQueMaisConsome());
                pausar();
                break;
            case 2:
                String idCasa2 = escolherCasa();
                if (idCasa2 == null) break;
                System.out.println(this.sistema.top3DispositivosPorAtivacoes(idCasa2));
                pausar();
                break;
            case 3:
                String idCasa3 = escolherCasa();
                if (idCasa3 == null) break;
                System.out.println(this.sistema.top3DispositivosPorTempo(idCasa3));
                pausar();
                break;
            case 4:
                System.out.println(this.sistema.top3DivisoesPorDispositivos());
                pausar();
                break;
            case 5:
                String idCasa5 = escolherCasa();
                if (idCasa5 == null) break;
                System.out.println("Dispositivos ligados: " + this.sistema.totalDispositivosLigados(idCasa5));
                pausar();
                break;
            case 6:
                String idCasa6 = escolherCasa();
                if (idCasa6 == null) break;
                System.out.println(this.sistema.listarTodosDispositivos(idCasa6));
                pausar();
                break;
            case 7:
                String idCasa7 = escolherCasa();
                if (idCasa7 == null) break;
                System.out.println(this.sistema.consumoTotalCasa(idCasa7));
                pausar();
                break;
            case 0: break;
        }
    }

    private void guardarEstado() {
        String nomeFicheiro = lerString("Nome do ficheiro: ");
        this.sistema.guardarEstado(this.sistema, nomeFicheiro);
        pausar();
    }

    private void carregarEstado() {
        java.io.File pasta = new java.io.File(".");
        java.io.File[] ficheiros = pasta.listFiles((dir, nome) -> !nome.endsWith(".java") && !nome.endsWith(".class") && !nome.endsWith(".jar") && !nome.endsWith(".md") && !nome.startsWith(".") && new java.io.File(nome).isFile());
        if (ficheiros == null || ficheiros.length == 0) {
            System.out.println("Nao existem estados guardados.");
            pausar();
            return;
        }
        System.out.println("\n=== Estados disponiveis ===");
        for (int i = 0; i < ficheiros.length; i++) {
            System.out.println((i + 1) + ". " + ficheiros[i].getName());
        }
        int opcao = lerIntIntervalo("Escolha o estado: ", 1, ficheiros.length);
        Sistema s = (Sistema) this.sistema.carregarEstado(ficheiros[opcao - 1].getName());
        if (s != null) {
            this.sistema = s;
            System.out.println("Estado carregado com sucesso!");
        }
        pausar();
    }

    private void verificarESugerirEscalonamento(String idCasa, String idDispositivo, String acao){
        Escalonamento sugestao = this.sistema.verificarSugestaoEscalonamento(
            this.utilizadorAtual.getId(), idDispositivo, acao);
        if (sugestao != null){
            System.out.println("\nSugestao de escalonamento: " + sugestao.getNome());
            System.out.println("Hora: " + sugestao.getHoraInicio() + " - " + sugestao.getHoraFim());
            System.out.print("Aceitar? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (resposta.equals("s")) {
                this.sistema.addEscalonamentoACasa(idCasa, sugestao);
                System.out.println("Escalonamento adicionado!");
            }
        }
    }

    private void verificarESugerirAutomacao(String idCasa, String idDispositivo, String acao){
        Automacao sugestao = this.sistema.verificarSugestaoAutomacao(
            this.utilizadorAtual.getId(), idDispositivo, acao);
        if (sugestao != null) {
            System.out.println("\nSugestao de automacao: " + sugestao.getNome());
            System.out.println("Condicao: " + sugestao.getCondicao());
            System.out.print("Aceitar? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (resposta.equals("s")){
                this.sistema.addAutomacaoACasa(idCasa, sugestao);
                System.out.println("Automacao adicionada!");
            }
        }
    }
}