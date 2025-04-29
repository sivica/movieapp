# Movie app - Android Technical Assignment

## Overview/Description

This Android application allows users to browse movies using The Movie Database (TMDB) API. It serves as a technical assignment submission, demonstrating proficiency in modern Android development practices.

Key features include:
* An infinitely scrolling list of currently popular movies.
* A detailed screen displaying information about a selected movie (overview, rating, genres).
* A search screen to find movies by title.

## Setup and Installation

Follow these steps to get the project running:

### 1. Clone the Repository

## 2. Add TMDB API Credentials

This application requires API credentials from The Movie Database (TMDB) to function.

### Obtain Credentials:

* Go to [https://www.themoviedb.org/](https://www.themoviedb.org/).

### Add Token to `local.properties`:

1.  Open the `local.properties` file.
2.  Add the following line, replacing `YOUR_API_KEY` with the actual api key you copied:

    ```properties
    tmdb_api_key=""YOUR_API_KEY"
    ```

**Important:** The `local.properties` file is included in the project's `.gitignore` by default to prevent accidentally committing your api key.