# DomusControl
Java-based smart home management system. Allows controlling rooms, devices, automations, schedules and scenarios, with support for multiple users with different permission levels.

---

## Features
- Management of **homes** and **rooms**
- Control of **devices** (lights, outlets, blinds, speakers, garage doors)
- **Automation** and **scheduling** system based on conditions and time
- Creation and execution of custom **scenarios**
- Automatic **rules** between devices (e.g. if sensor X → turn on device Y)
- **User** management with access levels (`USER` / `ADMIN`) per home
- **History** of actions per device and user
- Data persistence via **Java serialization**

---

## Project Structure
```
DomusControl/

├── src/
│   ├── DomusControl.java      # Entry point (main)
│   ├── Sistema.java           # Core system logic
│   ├── Menu.java              # User interface (CLI)
│   │
│   ├── Casa.java              # Represents a home
│   ├── Divisao.java           # Room within a home
│   │
│   ├── Dispositivo.java       # Abstract base class for devices
│   ├── Lampada.java           # Device: light bulb (intensity, color temperature)
│   ├── Tomada.java            # Device: power outlet
│   ├── Cortina.java           # Device: blind (opening degree)
│   ├── Coluna.java            # Device: speaker (volume)
│   ├── PortaoGaragem.java     # Device: garage door (opening degree)
│   │
│   ├── Utilizador.java        # System user
│   ├── AdminLevel.java        # Enum: USER / ADMIN
│   │
│   ├── Automacao.java         # Condition-based automation
│   ├── Escalonamento.java     # Time-based automation (extends Automacao)
│   ├── Cenario.java           # Grouped set of actions
│   ├── Regra.java             # Rule linking devices together
│   └── Historico.java         # Action log
└── bin/                       # Compiled files (.class)
```
---

## Build & Run

### Compile
```bash
javac -d bin src/*.java
```

### Run
```bash
java -cp bin DomusControl
```

---

## Authors
Developed as part of an academic project.

| Name          |
|---------------|
| Bruno Coelho  |
| José Gomes    |
| Vasco Machado |
