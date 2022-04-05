# Eriantys
### Final project for the Software Engineering course at PoliMi AY 2022, curated by group AM46
![Eriantys banner](https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_slider.jpg "Eriantys title banner")

Online implementation of the tabletop game [Eriantys](https://craniointernational.com/products/eriantys/) (Italian version [here](https://www.craniocreations.it/prodotto/eriantys/)), produced by [Cranio Creations](https://craniointernational.com/) (Italian website [here](https://www.craniocreations.it/)). Written in Java with the help of various tools.

**Made by**: Pietro Beghetto, Simone de Donato, Gregorio Dimaglie.

# Table of contents
- [Intro](#intro)
    + [Made with](#made-with)
    + [Specification](#specification)
- [The project](#the-project)
    + [Structure](#structure)
    + [Game rules](#game-rules)
    + [Network](#network)
- [Active development](#active-development)
  * [Roadmap](#roadmap)
  * [Changelog](#changelog)
- [Miscellaneous](#miscellaneous)
  * [Contacts](#contacts)
  * [License](#license)

---

# Intro
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
  - [x] ~~Create this GitHub repo~~
  - [x] ~~Set up the structure of README.md~~


- **Game design**
  - [ ] Initial UML draft
    - [x] ~~Game rules diagram~~
    - [ ] MVC diagram
    - [ ] Communication protocol diagram
  - [ ] Complete UML diagram
  - [ ] Final UML diagram, automatically generated from code
  - [ ] GUI design


- **Implementation**
  - [x] ~~Game rules~~
  - [x] ~~Board elements and pawns~~
  - [x] ~~Archipelago~~
  - [ ] Players and teams
  - [ ] Expert mode: characters
  - [ ] Advanced features
    - [ ] AF1: 4-players game
    - [ ] AF2: all 12 characters
    - [ ] AF3: *WIP*
  - [ ] Unit testing
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
---

# Miscellaneous
## Contacts
- Pietro Beghetto
  - Personal code: 10672945
  - Email: pietro.beghetto@mail.polimi.it
- Simone de Donato:
  - Personal code: 10677578
  - Email: simone.dedonato@mail.polimi.it
- Gregorio Dimaglie:
  - Personal code: 10705277
  - Email: gregorio.dimaglie@mail.polimi.it

## License
This repository is **proprietary** and may not be used, forked or otherwise exploited by anyone, except the previously appointed supervisors of the project to which this repository pertains.

![Second banner](https://user-images.githubusercontent.com/24454017/158022778-42af81b4-8f8f-4718-aa24-aef71a2143a2.jpg "Eriantys end banner")
