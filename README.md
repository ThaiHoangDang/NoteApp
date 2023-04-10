<h1 align="center">
  <br>
  <img src="logo.png" alt="Sticky Note" width="200"></a>
  <br>
  Sticky Note
  <br>
</h1>

<h4 align="center">A minimal note-taking application built on top of <a href="https://kotlinlang.org/" target="_blank">Kotlin</a></h4>

<p align="center">
  <a href="#goal">Goal</a> •
  <a href="#features">Key Features</a> •
  <a href="#installation">Installation</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#contribution">Contribution</a> •
  <a href="#releases">Releases</a> •
  <a href="#license">License</a>
</p>

![screenshot](interface.png)

## Goal
**Sticky Note** is a simple and easy-to-use note-taking application with many features supported. This is a project from team 112 of CS 346: Application development.

## Features
#### Main features include:
- A top-level menu bar that lists major functions, and indicates the hotkeys for each feature (e.g. File, Edit, View menu and submenus).
- Toolbars that let the user control settings and modes that apply to the application e.g. a Bold button that can be used to embolden text, and reflects the state of selected text.
- Minimize/maximize buttons function as expected.
- Undo-redo support for actions in the user interface.
- Cut-copy-paste text.
- Create, edit, delete note
- Display a list of locally and remotely saved notes
- Rich text support for the note body (e.g., font style, size, face, and highlight support)
- Support for bullet points

#### Additional features:
- Notes explorer window
- Multiple note windows
- Remote database synchronization
- Dark/Light Theme support
- Image Support
- Command Line Support

## Microservices:
This notes application makes use of the microservices architecture for storing notes and images
- [Notes Microservice](https://git.uwaterloo.ca/a32menon/notes-app-backend)
- [Image Microservice](https://git.uwaterloo.ca/a32menon/notes-app-image)

## Installation
- To install Sticky Notes, simply head over to Software Releases and unzip one of our many releases (Sprint 4 being the latest)
- Once the file has been installed and unzipped, the app can be executed by going to the `bin/` directory and running the `./app` command
## Technologies

This project uses the following technologies:

- [Kotlin](https://kotlinlang.org/) as the main language.
- [GitLab](https://about.gitlab.com/) for project tracking.
- [JavaFX](https://openjfx.io/) for the graphical user-interface.
- [SQLite](https://www.sqlite.org/index.html) for managing local storage.
- [Spring](https://spring.io/) for creating the web service.
- [Amazon Elastic Cloud Compute](https://aws.amazon.com/) for hosting the Spring web service.
- [JUnit](https://junit.org/junit5/) for testing.
- [Gradle](https://gradle.org/) for project builds.

## Contribution

- [Abhay Menon](https://git.uwaterloo.ca/a32menon): @a32menon
- [Inseo Kim](https://git.uwaterloo.ca/i32kim): @i32kim
- [Hoang Dang](https://git.uwaterloo.ca/h22dang): @h22dang
- [Guransh Khurana](https://git.uwaterloo.ca/g3khuran): @g3khuran
- [Anshul Ruhil](https://git.uwaterloo.ca/aruhil): @aruhil

## Releases

> **Version 1.0.0**: Infrastructure & GUI (released Feb 17, 2023)
> * [release-notes (md)](release-notes/1.0.0.md)
> * installer (Window, MacOS)

>**Version 2.0.0**: Data Persistance (released Mar 10, 2023)
> * [release-notes (md)](release-notes/2.0.0.md)
> * installer (Window, MacOS) 

> **Version 3.0.0**: Infrastructure & GUI (released Mar 24, 2023)
> * [release-notes (md)](release-notes/3.0.0.md)
> * installer (Window, MacOS)

>**Version 4.0.0**: Wrapup (released Apr 10, 2023)
> * [release-notes (md)](release-notes/4.0.0.md)
> * installer (Window, MacOS)

## License
- [MIT License](LICENSE)
---
