# DomusControl

Sistema desenvolvido em Java para gestão inteligente de dispositivos numa casa. Permite controlar divisões, dispositivos, automações, escalonamentos e cenários, com suporte a múltiplos utilizadores com diferentes níveis de permissão.

---

## Funcionalidades

- Gestão de **casas** e **divisões**
- Controlo de **dispositivos** (lâmpadas, tomadas, cortinas, colunas, portões de garagem)
- Sistema de **automações** e **escalonamentos** baseados em condições e horários
- Criação e execução de **cenários** personalizados
- **Regras** automáticas entre dispositivos (ex: se sensor X → ligar dispositivo Y)
- Gestão de **utilizadores** com níveis de acesso (`USER` / `ADMIN`) por casa
- **Histórico** de ações por dispositivo e utilizador
- Persistência de dados via **serialização Java**

---

## Estrutura do Projeto

```
DomusControl/
├── src/
│   ├── DomusControl.java      # Ponto de entrada (main)
│   ├── Sistema.java           # Lógica central do sistema
│   ├── Menu.java              # Interface de utilizador (CLI)
│   │
│   ├── Casa.java              # Representa uma casa
│   ├── Divisao.java           # Divisão dentro de uma casa
│   │
│   ├── Dispositivo.java       # Classe abstrata base dos dispositivos
│   ├── Lampada.java           # Dispositivo: lâmpada (intensidade, temperatura de cor)
│   ├── Tomada.java            # Dispositivo: tomada
│   ├── Cortina.java           # Dispositivo: cortina (grau de abertura)
│   ├── Coluna.java            # Dispositivo: coluna de som (volume)
│   ├── PortaoGaragem.java     # Dispositivo: portão de garagem (abertura)
│   │
│   ├── Utilizador.java        # Utilizador do sistema
│   ├── AdminLevel.java        # Enum: USER / ADMIN
│   │
│   ├── Automacao.java         # Automação baseada em condições
│   ├── Escalonamento.java     # Automação com horário (estende Automacao)
│   ├── Cenario.java           # Conjunto de ações agrupadas
│   ├── Regra.java             # Regra de ligação entre dispositivos
│   └── Historico.java         # Registo de ações
└── bin/                       # Ficheiros compilados (.class)
```

---

## Compilar e Executar

### Compilar

```bash
javac -d bin src/*.java
```

### Executar

```bash
java -cp bin DomusControl
```

---

## Autores

Trabalho desenvolvido no âmbito de projeto académico.

| Número | Nome |
|--------|------|
| a110375 | Bruno Coelho |
| a110367 | José Gomes |
| a109949 | Vasco Machado |
