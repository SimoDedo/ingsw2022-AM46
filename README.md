# Eriantys
### Final project for the Software Engineering course at PoliMi AY 2022, curated by group AM46
![Eriantys banner](https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_slider.jpg "Eriantys title banner")

Online implementation of the tabletop game [Eriantys](https://craniointernational.com/products/eriantys/) (Italian version [here](https://www.craniocreations.it/prodotto/eriantys/)), produced by [Cranio Creations](https://craniointernational.com/) (Italian website [here](https://www.craniocreations.it/)). Written in Java with the help of various tools.

# Table of contents
- [Overview](#overview)
    + [Made with](#made-with)
    + [Specification](#specification)
- [The project](#the-project)
    + [Structure](#structure)
    + [Game rules](#game-rules)
    + [Network](#network)
- [Active development](#active-development)
  * [Roadmap](#roadmap)
  * [Changelog](#changelog)

---

# Overview
Made by:
- **Pietro Beghetto** (account: [**@PietroBeghetto**](https://www.github.com/pietrobeghetto), personal code: **10672945**, e-mail: **pietro.beghetto@mail.polimi.it**)
- **Simone de Donato** (account: [**@SimoDedo**](https://www.github.com/simodedo), personal code: **10677578**, e-mail: **simone.dedonato@mail.polimi.it**)
- **Gregorio Dimaglie** (account: [**@MizuGreg**](https://www.github.com/mizugreg), personal code: **10705277**, e-mail: **gregorio.dimaglie@mail.polimi.it**)

| Feature                   | Progress |
|:--------------------------|:--------:|
| Game rules                |    🟢    |
| Advanced game             |    🟢    |
| AF1: 4 players            |    🟢    |
| AF2: 12 characters        |    🟢    |
| AF3: parallel matches     |    🟢    |
| Controller & virtual view |    🟡    |
| Communication protocol    |    🟡    |
| Client & view             |    🟡    |
| CLI                       |    🔴    |
| GUI                       |    🔴    |
🔴 = not present/drafting, 🟡 = in progress/implementing, 🟢 = completed/polishing

### Made with
- Development kit: [JDK 17](https://www.oracle.com/java/technologies/downloads/)
- IDE: [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Build automation tool: [Maven 3.8.4](https://maven.apache.org/)
- Unit testing: [JUnit 5](https://junit.org/junit5/)
### Specification
The project will include:
- initial and final **UML** diagrams
- source code of the **implementation**: game rules, networking, CLI, GUI, additional specifications etc.
- source code of the **unit tests**.

# The project
### Structure
*WIP*
### Game rules
*WIP*
### Network
*WIP*

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
  - [ ] Complete UML diagram
  - [ ] Final UML diagram, automatically generated from code
  - [ ] GUI design
  
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
  - [ ] Virtual view
  - [ ] View
  - [ ] Communication protocol
  - [ ] CLI
  - [ ] GUI

- **Finalization**
  - [ ] Polishing
  - [ ] Publishing

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
  + **0.8.2**: complete ServerHandler and AF3
---

![Second banner](https://user-images.githubusercontent.com/24454017/158022778-42af81b4-8f8f-4718-aa24-aef71a2143a2.jpg "Eriantys end banner")
