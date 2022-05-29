# ![Logo](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-client/blob/ReadMe-media/images/logo/war_elephant_purple.png) SoPra FS22 - Hannibal Server ![Logo](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-client/blob/ReadMe-media/images/logo/war_elephant_purple_left.png)

Hannibal is a turn-based strategy game in which players can play against each other over the web. Users can create 1vs1 public or private games without registration. However, it is possible to register to keep records of the games played and compete with other users to be at the top of the leaderboard.


## Table of content

- [Technologies](#technologies)
- [High-level components](#high-level-components)
- [External Dependencies](#external-dependencies)
- [Getting started](#getting-started)
    - [Prerequisites and Installation](#prerequisites-and-installation)
    - [Build](#build)
    - [Run](#run)
    - [Test](#test)
    - [Development Mode](#development mode)
    - [Debugging](#debugging)
    - [Deployment](#deployment)

- [Illustrations](#illustrations)
- [Roadmap](#roadmap)
- [Authors](#authors)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Links](#links)

## Technologies

<img src="https://user-images.githubusercontent.com/91155454/170843203-151000ab-db93-4750-b4f4-ba4060a23d53.png" width="16" height="16" /> [**Java**](https://java.com/)		

<img src="https://user-images.githubusercontent.com/91155454/170842503-3a531289-1afc-4b9c-87c1-cc120d9229ce.svg" style='visibility:hidden;' width="16" height="16" /> [**REST**](https://en.wikipedia.org/wiki/Representational_state_transfer) 	

<img src="https://user-images.githubusercontent.com/91155454/170843632-39007803-3026-4e48-bb78-93836a3ea771.png" style='visibility:hidden;' width="16" height="16" /> [**WebSocket**](https://en.wikipedia.org/wiki/WebSocket) 	

<img src="https://github.com/get-icon/geticon/blob/master/icons/heroku-icon.svg" width="16" height="16" /> [**Heroku**](https://www.heroku.com/)		

<img src="https://github.com/get-icon/geticon/blob/master/icons/github-icon.svg" width="16" height="16" /> [**GitHub**](https://github.com/)	

## High-level components

The high-level architecture follows the [Repository-Service Pattern](https://tom-collings.medium.com/controller-service-repository-16e29a4684e5).

## External Dependencies	

<img src="https://user-images.githubusercontent.com/91155454/170843438-4e721d42-5d97-4126-9739-ce049d0d8701.png" style='visibility:hidden;' width="16" height="16" /> [**Spring**](https://spring.io/) 	

<img src="https://user-images.githubusercontent.com/91155454/170843395-534f90bd-793d-477d-8626-4d8015c6041a.png" style='visibility:hidden;' width="16" height="16" /> [**Hibernate**](https://hibernate.org/) 		

## Getting started
<p>
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.
</p>

### Prerequisites and Installation
Follow the instruction guide for the [Hannibal server](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-server) and [Hannibal client](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-client).

Get the server
```bash
git clone https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-server.git
```
and open the project with an IDE of your choice.

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

### Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

This launches the test runner in an interactive watch mode.
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### Deployment
After each commit to the master branch, automatic Github Actions get executed which deploy our application to [Heroku](https://www.heroku.com/).

## Roadmap

<img src="https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-client/blob/ReadMe-media/images/Illustrations/Hannibalroadmap.png" width="25%"/>

## Authors

* **Luis Torrejón Machado**  - [luis-tm](https://github.com/luis-tm)
* **Paul Luley**  - [paolovic](https://github.com/paolovic)
* **Maria Korobeynikova** - [mkorob](https://github.com/mkorob)
* **Hilal Çomak** - [hilalcomak](https://github.com/hilalcomak)
* **Alessio Brazerol** - [apple00juice](https://github.com/apple00juice)

## License
This project is licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html), which guarantees end users the freedoms to run, study, share and modify the software.

## Acknowledgments
* This project is based on the [SoPra FS22 - Server Template](https://github.com/HASEL-UZH/sopra-fs22-template-server)
* Thanks to **Melih Catal** - [melihcatal](https://github.com/melihcatal) who supported us as a Tutor and Scrum Master during this project.

## Links
* [Hannibal Client Website](https://sopra-fs22-group-16-client.herokuapp.com/)
* [Hannibal Server Website](https://sopra-fs22-group-16-server.herokuapp.com/)
* [SonarCloud](https://sonarcloud.io/organizations/sopra-fs22-group-16/projects)
* [Issue tracker](https://github.com/sopra-fs22-group-16/sopra-fs22-group-16-client/issues)
