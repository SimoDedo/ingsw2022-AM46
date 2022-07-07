# Eriantys
### Final project for the Software Engineering course at PoliMi AY 2022, curated by group AM46
![Eriantys banner](https://user-images.githubusercontent.com/71946484/168785428-4267d78b-f1a8-48ee-86a3-55e4e34056af.png "Eriantys title banner")

Online implementation of the tabletop game [Eriantys](https://craniointernational.com/products/eriantys/) (Italian version [here](https://www.craniocreations.it/prodotto/eriantys/)), produced by [Cranio Creations](https://craniointernational.com/) (Italian website [here](https://www.craniocreations.it/)). Written in Java with the help of various tools.

The project was awarded a score of **30 cum Laude/30**

# Table of contents
- [Overview](#overview)
    + [Made with](#made-with)
    + [Specification](#specification)
    + [Quick run](#quick-run)
- [Installation](#installation)
    + [JAR](#jar)
    + [Installer](#installer)
    + [Requirements](#requirements)
    + [Run](#run)
- [Active development](#active-development)
  * [Roadmap](#roadmap)
  * [Changelog](#changelog)

---

# Overview
Made by:
- **Pietro Beghetto** (account: [**@PietroBeghetto**](https://www.github.com/pietrobeghetto), personal code: **10672945**, e-mail: **pietro.beghetto@mail.polimi.it**)
- **Simone de Donato** (account: [**@SimoDedo**](https://www.github.com/simodedo), personal code: **10677578**, e-mail: **simone.dedonato@mail.polimi.it**)
- **Gregorio Dimaglie** (account: [**@MizuGreg**](https://www.github.com/mizugreg), personal code: **10705277**, e-mail: **gregorio.dimaglie@mail.polimi.it**)

| Feature                  | Progress |
|:-------------------------|:--------:|
| Game rules               |    🟢    |
| Advanced game            |    🟢    |
| AF1: 4 players           |    🟢    |
| AF2: 12 characters       |    🟢    |
| AF3: parallel matches    |    🟢    |
| Controller               |    🟢    |
| Communication protocol   |    🟢    |
| Client & view            |    🟢    |
| CLI                      |    🟢    |
| GUI                      |    🟢    |

🔴 = not present/drafting, 🟡 = in progress/implementing, 🟢 = completed/polishing

**Full class coverage** for the game model.

![image](https://user-images.githubusercontent.com/24454017/177184990-3202efed-3f16-42a2-9df1-21ab4cd0d096.png)

### Made with
- Development kit: [JDK 17](https://www.oracle.com/java/technologies/downloads/)
- IDE: [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Build automation tool: [Maven 3.8.4](https://maven.apache.org/)
- Unit testing: [JUnit 5](https://junit.org/junit5/)
- UML diagrams: [IntelliJ IDEA](https://www.jetbrains.com/idea/) and [Astah UML](https://astah.net/products/astah-uml/)
- Installable packages generator: [jpackage](https://docs.oracle.com/en/java/javase/18/docs/specs/man/jpackage.html)

### Specification
The project includes:
- initial and final **UML** diagrams
- source code of the **implementation**: game rules, networking, CLI, GUI, additional specifications etc., complete with Javadocs
- source code of the **unit tests**
- a cross-platform **JAR** for executing the application as a server or as a CLI-based or GUI-based client
- documentation for the **communication protocol** and for the two **peer reviews** related to this project.

### Quick run
Download the [final JAR](deliverables/JAR_final/Eriantys-v1.0.0.jar), open a terminal and run `java -jar Eriantys-v1.0.0.jar` with parameters `--server`, `--cli` or `--gui`.

**Important note**: terminals that do not support ANSI will not run the CLI properly.

---

# Installation
You can run this application through a universal JAR or install it on your device with a platform-specific installer.

## JAR
You can:
- find the [final JAR](deliverables/JAR_final/Eriantys-v1.0.0.jar) in the deliverables folder, or
- fetch the latest cross-platform JAR from the [releases](https://github.com/SimoDedo/ingsw2022-AM46/releases).

The JAR has been tested on Windows, Ubuntu, MacOS and WSL. Download the JAR and place it in the desired folder.

## Installer
Alternatively, you can install the application on Windows or Debian-based Linux distributions by downloading and executing the installer for your platform from the [releases](https://github.com/SimoDedo/ingsw2022-AM46/releases) (double click on Windows, `sudo dpkg -i` on Debian-based Linux). An icon for the application will be automatically added to your desktop.

Note: if you have WSL, it'll still be added to your Windows Start section. If for some reason you can't find the icon, the executable should be in `/opt/eriantys-am46/bin/Eriantys-AM46`.

The installer was made with [jpackage](https://docs.oracle.com/en/java/javase/18/docs/specs/man/jpackage.html).

## Requirements
The application requires:
- [Java](https://www.java.com/it/) 17 or higher. You can [download](https://www.oracle.com/java/technologies/downloads/#java17) and install version 17 of Java SE or higher. Alternatively you can `sudo apt install openjdk-17-jre` on Linux.
- a graphical interface if you want to use the GUI. On WSL for example, you can `sudo apt install libgtk-3-0` (or `sudo apt install openjfx`) and then `export DISPLAY=:0` inside the shell where you'll run the JAR.

## Run
Double-click on the JAR or `java -jar Eriantys-vX.X.X.jar` in a shell (where X.X.X is the downloaded JAR's version) to start the GUI.
Use the parameters `--server` to start the server, `--cli` to start the CLI, or `--gui`/`--client` to start the GUI.

**Important note**: terminals that do not support ANSI will not run the CLI properly.

[Here](https://asciinema.org/a/ahsKxDUVGFGZ4TVQjqK50JV2p) is a quick demonstration of the CLI running in a WSL shell.
![eriantys-cli-demo](https://user-images.githubusercontent.com/24454017/174342092-f52432a4-c22b-4faf-a2bc-b8e31ef8694c.gif)

---

# Active development
## Roadmap
- **Setup**
  - [x] Create this GitHub repo
  - [x] Set up the structure of README.md

- **Game design**
  - [x] Initial UML draft
    - [x] Game rules diagram
    - [x] MVC diagram
    - [x] Communication protocol diagram
  - [x] Complete UML diagram
  - [x] GUI design
  - [x] Final UML diagram, automatically generated from code
  
- **Implementation**
  - [x] Game rules
  - [x] Board elements and pawns
  - [x] Archipelago
  - [x] Players and teams
  - [x] Expert mode: characters
  - [x] Advanced features
    - [x] AF1: 4-players game
    - [x] AF2: all 12 characters
    - [x] AF3: Parallel matches
  - [x] Unit testing for the model
  - [x] Controller
  - [x] Client
  - [x] Communication protocol
  - [x] CLI
  - [x] GUI

- **Finalization**
  - [x] Polishing
  - [x] Publishing

## Changelog
+ **0.1.0**: initial draft of the UML diagram of the Model
  + **0.1.1**: specifications for various classes, creation of the Pawn class
  + **0.1.2**: draft of Characters implementation using Decorator pattern
  + **0.1.3**: added ProfessorSet, refined main functions of the game, refined island merge function
+ **0.2.0**: heavy rewriting and polishing of the UML Model diagram, Character redefinition
  + **0.2.1**: Game and Character factory, complete check-up
  + **0.2.2**: minor changes and fixes
+ **0.3.0**: started implementation of pawns and base elements, rewrote Characters entirely *(once again)*
+ **0.4.0**: started game model implementation
+ **0.5.0**: rebuilt Game creation
  + **0.5.1**: implemented Characters
  + **0.5.2**: improved testing coverage
+ **0.6.0**: merged game, archipelago, team/player into main 
  + **0.6.1**: 90% model implementation, 70% test coverage
+ **0.7.0**: rebuilt Characters, removed Team in favor of TeamManager, minor fixes
  + **0.7.1**: initial design of the Controller; 95% model implementation, 80% test coverage 
+ **0.8.0**: complete MVC diagram and communication protocol
  + **0.8.1**: complete Controller and Message classes
  + **0.8.2**: complete LobbyServer and AF3
  + **0.8.3**: complete MatchServer
  + **0.8.4**: rebuilt network messages
+ **0.9.0**: created Client class and UI interface 
  + **0.9.1**: build CLI 
  + **0.9.2**: finished Client and CLI
  + **0.9.3**: GUI draft and login/setup screens
  + **0.9.4**: finished GUI
  + **0.9.5**: GUI polishing and bug fixes
  + **0.9.6**: GUI additional features; added all necessary files to the deliverables folder
+ **1.0.0**: final version; rewrote document about the communication protocol.

---

![Second banner](https://user-images.githubusercontent.com/24454017/158022778-42af81b4-8f8f-4718-aa24-aef71a2143a2.jpg "Eriantys end banner")
