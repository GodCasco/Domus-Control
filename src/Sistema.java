import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sistema implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Utilizador> utilizadores;
    private Map<String, Casa> casas;
    private List<Historico> historico;

    public Sistema() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.historico = new ArrayList<>();
    }

    public Sistema(Map<String, Utilizador> utilizadores, Map<String, Casa> casas, List<Historico> historico) {
        this.utilizadores = new HashMap<>(utilizadores);
        this.casas = new HashMap<>(casas);
        this.historico = new ArrayList<>(historico);
    }

    public Sistema(Sistema s) {
        this.utilizadores = s.getUtilizadores();
        this.casas = s.getCasas();
        this.historico = s.getHistorico();
    }

    public Map<String, Utilizador> getUtilizadores() {
        return new HashMap<>(this.utilizadores);
    }

    public void setUtilizadores(Map<String, Utilizador> utilizadores) {
        this.utilizadores = new HashMap<>(utilizadores);
    }

    public Map<String, Casa> getCasas() {
        return new HashMap<>(this.casas);
    }

    public void setCasas(Map<String, Casa> casas) {
        this.casas = new HashMap<>(casas);
    }

    public List<Historico> getHistorico() {
        return new ArrayList<>(this.historico);
    }

    public void setHistorico(List<Historico> historico) {
        this.historico = new ArrayList<>(historico);
    }

    public Utilizador login(String username, String password) {
        for (Utilizador u : this.utilizadores.values()) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) return u.clone();
        }
        return null;
    }

    public boolean isAdmin(Utilizador u, String idCasa) {
        return u.isAdmin(idCasa);
    }

    public boolean usernameExiste(String username) {
        for (Utilizador u : this.utilizadores.values()) {
            if (u.getUsername().equals(username)) return true;
        }
        return false;
    }

    public String getIdUtilizadorPorUsername(String username) {
        for (Map.Entry<String, Utilizador> entry : this.utilizadores.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) return entry.getKey();
        }
        return null;
    }

    public boolean novoUtilizador(String username, String password) {
        if (usernameExiste(username)) return false;
        String id = java.util.UUID.randomUUID().toString();
        this.utilizadores.put(id, new Utilizador(id, username, password));
        return true;
    }

    public void novaCasa(String nome, String admin) {
        String id = java.util.UUID.randomUUID().toString();
        this.casas.put(id, new Casa(id, nome));
        this.associarCasa(admin, id, AdminLevel.ADMIN);
    }

    public void associarCasa(String utilizador, String casa, AdminLevel a) {
        this.utilizadores.get(utilizador).addCasa(casa, a);
    }

    public void addDivisaoACasa(String idCasa, Divisao divisao) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).addDivisao(divisao);
        }
    }

    public void addDispositivoADivisao(String idCasa, String nomeDivisao, Dispositivo d) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).addDispositivoADivisao(nomeDivisao, d);
        }
    }

    public Map<String, Casa> listarCasas() {
        return new HashMap<>(this.casas);
    }

    public Map<String, Divisao> listarDivisoes(String idCasa) {
        if (this.casas.containsKey(idCasa)) {
            return this.casas.get(idCasa).getDivisoes();
        }
        return new HashMap<>();
    }

    public Dispositivo getDispositivo(String idCasa, String nomeDivisao, String idDispositivo) {
        if (this.casas.containsKey(idCasa)) {
            return this.casas.get(idCasa).getDispositivo(nomeDivisao, idDispositivo);
        }
        return null;
    }

    public void addAutomacaoACasa(String idCasa, Automacao a) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).addAutomacao(a);
        }
    }

    public void removerAutomacao(String idCasa, String idAutomacao) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).removeAutomacao(idAutomacao);
        }
    }

    public List<Automacao> listarAutomacoes(String idCasa) {
        if (this.casas.containsKey(idCasa)) {
            return this.casas.get(idCasa).getAutomacoes();
        }
        return new ArrayList<>();
    }

    public String verificarAutomacoes(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa nao encontrada.";
        Casa casa = this.casas.get(idCasa);
        List<Automacao> automacoes = casa.getAutomacoes();
        if (automacoes.isEmpty()) return "Nao existem automacoes para esta casa.";
        StringBuilder sb = new StringBuilder();
        for (Automacao a : automacoes) {
            for (Regra r : a.getRegras()) {
                Dispositivo origem = casa.findDispositivo(r.getDispositivoOrigemId());
                if (origem == null) continue;
                boolean condicaoVerdadeira = verificarCondicao(origem, r);
                if (condicaoVerdadeira) {
                    Dispositivo destino = casa.findDispositivo(r.getDispositivoDestinoId());
                    if (destino == null) continue;
                    executarAcao(destino, r);
                    casa.atualizarDispositivoEmQualquerDivisao(destino);
                    sb.append("Automacao '" + a.getNome() + "' ativada: " + r.getMetodoParaExecutar() + " em " + destino.getMarca() + " " + destino.getModelo() + "\n");
                }
            }
        }
        if (sb.length() == 0) return "Nenhuma condicao verificada.";
        return sb.toString();
    }

    private boolean verificarCondicao(Dispositivo d, Regra r) {
        String atributo = r.getAtributoParaVerificar();
        String operador = r.getOperador();
        Object valorRef = r.getValorReferencia();
        if (atributo.equals("estado")) {
            return d.getEstado().equals(valorRef.toString());
        }
        if (atributo.equals("intensidade") && d instanceof Lampada) {
            double intensidade = ((Lampada) d).getIntensidade();
            double ref = Double.parseDouble(valorRef.toString());
            if (operador.equals(">")) return intensidade > ref;
            if (operador.equals("<")) return intensidade < ref;
            if (operador.equals("==")) return intensidade == ref;
        }
        if (atributo.equals("temperaturaCor") && d instanceof Lampada) {
            int temperatura = ((Lampada) d).getTemperaturaCor();
            int ref = Integer.parseInt(valorRef.toString());
            if (operador.equals(">")) return temperatura > ref;
            if (operador.equals("<")) return temperatura < ref;
            if (operador.equals("==")) return temperatura == ref;
        }
        if (atributo.equals("volume") && d instanceof Coluna) {
            int volume = ((Coluna) d).getVolume();
            int ref = Integer.parseInt(valorRef.toString());
            if (operador.equals(">")) return volume > ref;
            if (operador.equals("<")) return volume < ref;
            if (operador.equals("==")) return volume == ref;
        }
        if (atributo.equals("abertura") && (d instanceof Cortina || d instanceof PortaoGaragem)) {
            int abertura = (d instanceof Cortina) ? ((Cortina) d).getAbertura() : ((PortaoGaragem) d).getAbertura();
            int ref = Integer.parseInt(valorRef.toString());
            if (operador.equals(">")) return abertura > ref;
            if (operador.equals("<")) return abertura < ref;
            if (operador.equals("==")) return abertura == ref;
        }
        return false;
    }

    private void executarAcao(Dispositivo d, Regra r) {
        String metodo = r.getMetodoParaExecutar();
        Object valorAcao = r.getValorAcao();
        if (metodo.equals("ligar")) d.ligar();
        else if (metodo.equals("desligar")) d.desligar();
        else if (metodo.equals("setIntensidade") && d instanceof Lampada) {
            ((Lampada) d).setIntensidade(Double.parseDouble(valorAcao.toString()));
        }
        else if (metodo.equals("setTemperaturaCor") && d instanceof Lampada) {
            ((Lampada) d).setTemperaturaCor(Integer.parseInt(valorAcao.toString()));
        }
        else if (metodo.equals("setVolume") && d instanceof Coluna) {
            ((Coluna) d).setVolume(Integer.parseInt(valorAcao.toString()));
        }
        else if (metodo.equals("setAbertura") && d instanceof Cortina) {
            ((Cortina) d).setAbertura(Integer.parseInt(valorAcao.toString()));
        }
        else if (metodo.equals("setAbertura") && d instanceof PortaoGaragem) {
            ((PortaoGaragem) d).setAbertura(Integer.parseInt(valorAcao.toString()));
        }
        else if (metodo.equals("abrirCortina") && d instanceof Cortina) {
            ((Cortina) d).abrirCortina();
        }
        else if (metodo.equals("fecharCortina") && d instanceof Cortina) {
            ((Cortina) d).fecharCortina();
        }
        else if (metodo.equals("abrirPortao") && d instanceof PortaoGaragem) {
            ((PortaoGaragem) d).abrirPortao();
        }
        else if (metodo.equals("fecharPortao") && d instanceof PortaoGaragem) {
            ((PortaoGaragem) d).fecharPortao();
        }
    }

    public void atualizarDispositivo(String idCasa, String nomeDivisao, Dispositivo dispositivo) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).atualizarDispositivo(nomeDivisao, dispositivo);
        }
    }

    // Consulta extra 1 — total de dispositivos ligados numa casa
    public int totalDispositivosLigados(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return 0;
        int total = 0;
        for (Divisao d : this.casas.get(idCasa).getDivisoes().values()) {
            for (Dispositivo disp : d.getDispositivos()) {
                if (disp.getEstado().equals("Ligado")) total++;
            }
        }
        return total;
    }

    // Consulta extra 2 — todos os dispositivos de uma casa independentemente da divisão
    public String listarTodosDispositivos(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa não encontrada.";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Divisao> entry : this.casas.get(idCasa).getDivisoes().entrySet()) {
            for (Dispositivo d : entry.getValue().getDispositivos()) {
                sb.append("Divisao: " + entry.getKey() + " | " + d.getMarca() + " " + d.getModelo() + " | Estado: " + d.getEstado() + "\n");
            }
        }
        if (sb.length() == 0) return "Nenhum dispositivo encontrado.";
        return sb.toString();
    }

    // Consulta extra 3 — consumo total de uma casa específica
    public String consumoTotalCasa(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa não encontrada.";
        int total = 0;
        for (Divisao d : this.casas.get(idCasa).getDivisoes().values()) {
            for (Dispositivo disp : d.getDispositivos()) {
                if (disp.getEstado().equals("Ligado")) {
                    total += disp.getConsumoHora();
                }
            }
        }
        return "Casa: " + this.casas.get(idCasa).getNome() + " | Consumo total: " + total + " Wh";
    }

    public void addEscalonamentoACasa(String idCasa, Escalonamento e) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).addEscalonamento(e);
        }
    }

    public void removerEscalonamento(String idCasa, String idEscalonamento) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).removeEscalonamento(idEscalonamento);
        }
    }

    public List<Escalonamento> listarEscalonamentos(String idCasa) {
        if (this.casas.containsKey(idCasa)) {
            return this.casas.get(idCasa).getEscalonamentos();
        }
        return new ArrayList<>();
    }

    public String verificarEscalonamentos(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa nao encontrada.";
        Casa casa = this.casas.get(idCasa);
        List<Escalonamento> escalonamentos = casa.getEscalonamentos();
        if (escalonamentos.isEmpty()) return "Nao existem escalonamentos para esta casa.";
        java.time.LocalTime agora = java.time.LocalTime.now();
        StringBuilder sb = new StringBuilder();
        for (Escalonamento e : escalonamentos) {
            if (!agora.isBefore(e.getHoraInicio()) && !agora.isAfter(e.getHoraFim())) {
                for (Regra r : e.getRegras()) {
                    Dispositivo destino = casa.findDispositivo(r.getDispositivoDestinoId());
                    if (destino == null) continue;
                    executarAcao(destino, r);
                    casa.atualizarDispositivoEmQualquerDivisao(destino);
                    sb.append("Escalonamento '" + e.getNome() + "' ativado: " + r.getMetodoParaExecutar() + " em " + destino.getMarca() + " " + destino.getModelo() + "\n");
                }
            }
        }
        if (sb.length() == 0) return "Nenhum escalonamento ativo neste momento.";
        return sb.toString();
    }

    public void addCenarioACasa(String idCasa, Cenario c) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).addCenario(c);
        }
    }

    public void removerCenario(String idCasa, String idCenario) {
        if (this.casas.containsKey(idCasa)) {
            this.casas.get(idCasa).removeCenario(idCenario);
        }
    }

    public List<Cenario> listarCenarios(String idCasa) {
        if (this.casas.containsKey(idCasa)) {
            return this.casas.get(idCasa).getCenarios();
        }
        return new ArrayList<>();
    }

    public String ativarCenario(String idCasa, String idCenario) {
        if (!this.casas.containsKey(idCasa)) return "Casa nao encontrada.";
        Casa casa = this.casas.get(idCasa);
        for (Cenario c : casa.getCenarios()) {
            if (c.getId().equals(idCenario)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cenario '" + c.getNome() + "' ativado:\n");
                for (Regra r : c.getRegras()) {
                    Dispositivo destino = casa.findDispositivo(r.getDispositivoDestinoId());
                    if (destino == null) continue;
                    executarAcao(destino, r);
                    casa.atualizarDispositivoEmQualquerDivisao(destino);
                    sb.append("  " + r.getMetodoParaExecutar() + " em " + destino.getMarca() + " " + destino.getModelo() + "\n");
                }
                return sb.toString();
            }
        }
        return "Cenario nao encontrado.";
    }

    public String casaQueMaisConsome() {
        String idCasaMax = null;
        int consumoMax = 0;
        for (Map.Entry<String, Casa> entry : this.casas.entrySet()) {
            int consumoCasa = 0;
            for (Divisao d : entry.getValue().getDivisoes().values()) {
                for (Dispositivo disp : d.getDispositivos()) {
                    if (disp.getEstado().equals("Ligado")) {
                        consumoCasa += disp.getConsumoHora();
                    }
                }
            }
            if (consumoCasa > consumoMax) {
                consumoMax = consumoCasa;
                idCasaMax = entry.getKey();
            }
        }
        if (idCasaMax == null) return "Nenhuma casa tem dispositivos ligados.";
        return "Casa: " + this.casas.get(idCasaMax).getNome() + " | Consumo: " + consumoMax + " Wh";
    }

    public String top3DispositivosPorAtivacoes(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa não encontrada.";
        List<Dispositivo> todos = new ArrayList<>();
        for (Divisao d : this.casas.get(idCasa).getDivisoes().values()) {
            todos.addAll(d.getDispositivos());
        }
        todos.sort((a, b) -> b.getNumAtivacoes() - a.getNumAtivacoes());
        StringBuilder sb = new StringBuilder();
        int limite = Math.min(3, todos.size());
        for (int i = 0; i < limite; i++) {
            Dispositivo d = todos.get(i);
            sb.append((i + 1) + ". " + d.getMarca() + " " + d.getModelo() + " | Ativacoes: " + d.getNumAtivacoes() + "\n");
        }
        return sb.toString();
    }

    public String top3DispositivosPorTempo(String idCasa) {
        if (!this.casas.containsKey(idCasa)) return "Casa não encontrada.";
        List<Dispositivo> todos = new ArrayList<>();
        for (Divisao d : this.casas.get(idCasa).getDivisoes().values()) {
            todos.addAll(d.getDispositivos());
        }
        todos.sort((a, b) -> (int)(b.getTempoLigado() - a.getTempoLigado()));
        StringBuilder sb = new StringBuilder();
        int limite = Math.min(3, todos.size());
        for (int i = 0; i < limite; i++) {
            Dispositivo d = todos.get(i);
            sb.append((i + 1) + ". " + d.getMarca() + " " + d.getModelo() + " | Tempo ligado: " + d.getTempoLigadoSegundos() + "s\n");
        }
        return sb.toString();
    }

    public String top3DivisoesPorDispositivos() {
        List<String[]> lista = new ArrayList<>();
        for (Map.Entry<String, Casa> entryCasa : this.casas.entrySet()) {
            for (Map.Entry<String, Divisao> entryDiv : entryCasa.getValue().getDivisoes().entrySet()) {
                lista.add(new String[]{
                    entryCasa.getValue().getNome(),
                    entryDiv.getKey(),
                    String.valueOf(entryDiv.getValue().getDispositivos().size())
                });
            }
        }
        lista.sort((a, b) -> Integer.parseInt(b[2]) - Integer.parseInt(a[2]));
        StringBuilder sb = new StringBuilder();
        int limite = Math.min(3, lista.size());
        for (int i = 0; i < limite; i++) {
            sb.append((i + 1) + ". Casa: " + lista.get(i)[0] + " | Divisao: " + lista.get(i)[1] + " | Dispositivos: " + lista.get(i)[2] + "\n");
        }
        return sb.toString();
    }

    public void guardarEstado(Object sistema, String nomeFicheiro) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeFicheiro))) {
            oos.writeObject(sistema);
            System.out.println("Estado gravado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object carregarEstado(String nomeFicheiro) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeFicheiro))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ficheiro não encontrado ou erro na leitura.");
            return null;
        }
    }

    // public void carregarDadosTeste() {
    //     novoUtilizador("jose", "123");
    //     novoUtilizador("joao", "321");

    //     String idJose = getIdUtilizadorPorUsername("jose");
    //     String idJoao = getIdUtilizadorPorUsername("joao");
    //     if (idJose == null) return;

    //     novaCasa("Casa do Jose", idJose);

    //     String idCasa = null;
    //     for (Map.Entry<String, Casa> entry : this.casas.entrySet()) {
    //         if (entry.getValue().getNome().equals("Casa do Jose")) idCasa = entry.getKey();
    //     }

    //     if (idJoao != null && idCasa != null) {
    //         associarCasa(idJoao, idCasa, AdminLevel.USER);
    //     }

    //     addDivisaoACasa(idCasa, new Divisao("Sala"));
    //     addDivisaoACasa(idCasa, new Divisao("Quarto"));
    //     addDivisaoACasa(idCasa, new Divisao("Garagem"));

    //     addDispositivoADivisao(idCasa, "Sala",
    //         new Lampada("lampada1", "Philips", "Hue E27", 10, "Desligado", 80, 3000));
    //     addDispositivoADivisao(idCasa, "Sala",
    //         new Coluna("coluna1", "JBL", "go", 15, "Desligado", 30));
    //     addDispositivoADivisao(idCasa, "Sala",
    //         new Cortina("cortina1", "IKEA", "Teste", 5, "Fechado", 0));

    //     addDispositivoADivisao(idCasa, "Quarto",
    //         new Lampada("lampada2", "Philips", "Hue E27", 10, "Desligado", 70, 2900));
    //     addDispositivoADivisao(idCasa, "Quarto",
    //         new Cortina("cortina2", "IKEA", "Teste", 4, "Fechado", 0));

    //     addDispositivoADivisao(idCasa, "Garagem",
    //         new PortaoGaragem("portao1", "LEROY MERLIN", "Teste", 50, "Fechado", 0));
    //     addDispositivoADivisao(idCasa, "Garagem",
    //         new Tomada("tomada1", "LEROY MERLIN", "16A", 3, "Desligado"));

    //     Utilizador uJose = this.utilizadores.get(idJose);

    //     Cenario sairDeCasa = new Cenario("cenario1", "Sair de Casa", uJose);
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "lampada1", "desligar", null));
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "lampada2", "desligar", null));
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "coluna1",  "desligar", null));
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "cortina1", "fecharCortina", null));
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "cortina2", "fecharCortina", null));
    //     sairDeCasa.addRegra(new Regra("Sair", "N/A", "N/A", "N/A", "N/A", "portao1",  "fecharPortao", null));
    //     addCenarioACasa(idCasa, sairDeCasa);

    //     Cenario jantarAmigos = new Cenario("cenario2", "Jantar com Amigos", uJose);
    //     jantarAmigos.addRegra(new Regra("Jantar", "N/A", "N/A", "N/A", "N/A", "lampada1", "ligar", null));
    //     jantarAmigos.addRegra(new Regra("Jantar", "N/A", "N/A", "N/A", "N/A", "coluna1",  "ligar", null));
    //     jantarAmigos.addRegra(new Regra("Jantar", "N/A", "N/A", "N/A", "N/A", "coluna1",  "setVolume", "70"));
    //     jantarAmigos.addRegra(new Regra("Jantar", "N/A", "N/A", "N/A", "N/A", "cortina1", "fecharCortina", null));
    //     addCenarioACasa(idCasa, jantarAmigos);

    //     Cenario deitar = new Cenario("cenario3", "Deitar", uJose);
    //     deitar.addRegra(new Regra("Deitar", "N/A", "N/A", "N/A", "N/A", "lampada1", "desligar", null));
    //     deitar.addRegra(new Regra("Deitar", "N/A", "N/A", "N/A", "N/A", "coluna1",  "desligar", null));
    //     deitar.addRegra(new Regra("Deitar", "N/A", "N/A", "N/A", "N/A", "cortina1", "fecharCortina", null));
    //     deitar.addRegra(new Regra("Deitar", "N/A", "N/A", "N/A", "N/A", "lampada2", "ligar", null));
    //     deitar.addRegra(new Regra("Deitar", "N/A", "N/A", "N/A", "N/A", "cortina2", "fecharCortina", null));
    //     addCenarioACasa(idCasa, deitar);

    //     Cenario acordar = new Cenario("cenario4", "Acordar", uJose);
    //     acordar.addRegra(new Regra("Acordar", "N/A", "N/A", "N/A", "N/A", "cortina1", "abrirCortina", null));
    //     acordar.addRegra(new Regra("Acordar", "N/A", "N/A", "N/A", "N/A", "cortina2", "abrirCortina", null));
    //     acordar.addRegra(new Regra("Acordar", "N/A", "N/A", "N/A", "N/A", "lampada2", "ligar", null));
    //     addCenarioACasa(idCasa, acordar);

    //     Automacao autoColuna = new Automacao("auto1", "Fechar cortinas se coluna alta",
    //         uJose, "Se volume da coluna > 80, fechar cortinas");
    //     autoColuna.adicionarRegra(new Regra("AutoCortina",
    //         "coluna1", "volume", ">", "80", "cortina1", "fecharCortina", null));
    //     addAutomacaoACasa(idCasa, autoColuna);

    //     Automacao autoPortao = new Automacao("auto2", "Ligar tomada se portao abrir",
    //         uJose, "Se portao aberto, ligar tomada da garagem");
    //     autoPortao.adicionarRegra(new Regra("AutoPortao",
    //         "portao1", "abertura", ">", "0", "tomada1", "ligar", null));
    //     addAutomacaoACasa(idCasa, autoPortao);

    //     Escalonamento manha = new Escalonamento("escalonamento1", "Rotina Manha", uJose,
    //         "Abrir cortinas de manha",
    //         java.time.LocalTime.of(7, 0), java.time.LocalTime.of(9, 0));
    //     manha.adicionarRegra(new Regra("Manha", "N/A", "N/A", "N/A", "N/A", "cortina1", "abrirCortina", null));
    //     manha.adicionarRegra(new Regra("Manha", "N/A", "N/A", "N/A", "N/A", "cortina2", "abrirCortina", null));
    //     addEscalonamentoACasa(idCasa, manha);

    //     Escalonamento noite = new Escalonamento("escalonamento2", "Rotina Noite", uJose,
    //         "Fechar cortinas a noite",
    //         java.time.LocalTime.of(21, 0), java.time.LocalTime.of(23, 59));
    //     noite.adicionarRegra(new Regra("Noite", "N/A", "N/A", "N/A", "N/A", "cortina1", "fecharCortina", null));
    //     noite.adicionarRegra(new Regra("Noite", "N/A", "N/A", "N/A", "N/A", "cortina2", "fecharCortina", null));
    //     addEscalonamentoACasa(idCasa, noite);

    //     Escalonamento musica = new Escalonamento("escalonamento", "Musica Tarde", uJose,
    //         "Ligar musica de tarde",
    //         java.time.LocalTime.of(15, 0), java.time.LocalTime.of(18, 0));
    //     musica.adicionarRegra(new Regra("Musica", "N/A", "N/A", "N/A", "N/A", "coluna1", "ligar", null));
    //     musica.adicionarRegra(new Regra("Musica", "N/A", "N/A", "N/A", "N/A", "coluna1", "setVolume", "40"));
    //     addEscalonamentoACasa(idCasa, musica);

    //     Escalonamento seguranca = new Escalonamento("escalonamento4", "Seguranca Noturna", uJose,
    //         "Fechar portao durante a noite",
    //         java.time.LocalTime.of(0, 0), java.time.LocalTime.of(6, 0));
    //     seguranca.adicionarRegra(new Regra("Seguranca", "N/A", "N/A", "N/A", "N/A", "portao1", "fecharPortao", null));
    //     addEscalonamentoACasa(idCasa, seguranca);
    // }

    public void registarHistorico(String tipoDispositivo, String idUtilizador, String idDispositivo, String acao, Map<String, String> estados) {
        this.historico.add(new Historico(tipoDispositivo, idUtilizador, idDispositivo, acao, java.time.LocalTime.now(), estados));
    }

    public Escalonamento verificarSugestaoEscalonamento(String idUtilizador, String idDispositivo, String acao){
        int n = 0;
        int totalMinutos = 0;

        for(Historico h : this.historico){
            if(h.getIdUtilizador().equals(idUtilizador) &&
               h.getIdDispositivo().equals(idDispositivo) &&
               h.getAcao().equals(acao) &&
               Math.abs(h.getHora().until(java.time.LocalTime.now(), java.time.temporal.ChronoUnit.MINUTES)) <= 15){
                    n++;
                    totalMinutos += h.getHora().getHour() * 60 + h.getHora().getMinute();
               }
        }

        if(n >= 3){
            int mediaMinutos = totalMinutos / n;
            java.time.LocalTime horaInicio = java.time.LocalTime.of(mediaMinutos / 60, mediaMinutos % 60);
            java.time.LocalTime horaFim = horaInicio.plusMinutes(30);
            Utilizador u = this.utilizadores.get(idUtilizador);
            Escalonamento e = new Escalonamento(
                java.util.UUID.randomUUID().toString(),
                "Sugestao: " + acao + " em " + idDispositivo,
                u,
                "Sugestao automatica gerada pelo sistema",
                horaInicio,
                horaFim
            );
            e.adicionarRegra(new Regra("Sugestao", "N/A", "N/A", "N/A", "N/A", idDispositivo, acao, null));
            return e;
        }

        return null;
    }

    public Automacao verificarSugestaoAutomacao(String idUtilizador, String idDispositivo, String acao){
        Map<String, Integer> contadores = new HashMap<>();
        Map<String, String> estadosContexto = new HashMap<>();

        for (Historico h : this.historico){
            if (!h.getIdUtilizador().equals(idUtilizador) ||
                !h.getIdDispositivo().equals(idDispositivo) ||
                !h.getAcao().equals(acao)) continue;

            for (Map.Entry<String, String> entry : h.getEstados().entrySet()){
                String idContexto = entry.getKey();
                if (idContexto.equals(idDispositivo)) continue;

                for (Casa c : this.casas.values()){
                    Dispositivo dispAtual = c.findDispositivo(idContexto);
                    if (dispAtual != null && dispAtual.getEstado().equals(entry.getValue())){
                        contadores.put(idContexto, contadores.getOrDefault(idContexto, 0) + 1);
                        estadosContexto.put(idContexto, entry.getValue());
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : contadores.entrySet()){
            if (entry.getValue() >= 2){
                String idContextoEncontrado = entry.getKey();
                String estadoContextoEncontrado = estadosContexto.get(idContextoEncontrado);
                Utilizador u = this.utilizadores.get(idUtilizador);
                Automacao a = new Automacao(
                    java.util.UUID.randomUUID().toString(),
                    "Sugestao: " + acao + " em " + idDispositivo,
                    u,
                    "Se " + idContextoEncontrado + " esta " + estadoContextoEncontrado
                );
                a.adicionarRegra(new Regra("Sugestao", idContextoEncontrado, "estado", "==",
                    estadoContextoEncontrado, idDispositivo, acao, null));
                return a;
            }
        }

        return null;
    }

    public Map<String, String> estadosDivisao(Divisao d){
        List<Dispositivo> dispositivos = d.getDispositivos();
        Map<String, String> estados = new HashMap<>();
        
        for(Dispositivo dispositivo : dispositivos){
            estados.put(dispositivo.getId(), dispositivo.getEstado());
        }

        return estados;
    }

    @Override
    public Sistema clone() { return new Sistema(this); }

    @Override
    public String toString() {
        return "Utilizadores: " + this.utilizadores.toString() + "\n" +
               "Casas: " + this.casas.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Sistema s = (Sistema) o;
        return this.utilizadores.equals(s.getUtilizadores()) &&
               this.casas.equals(s.getCasas());
    }
}