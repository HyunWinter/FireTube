# Firetube
CST 407 - Android Mobile Development (2020 Summer)

## About the App

Firetube provides a simple, visual way to organize Youtube Playlists. Log in to your Google account and start searching for your favorite recipes, music, and game videos the way you want.

## Table of Contents

- [Screenshots](#screenshots)
- [Features](#features)
- [Libraries](#libraries)
- [Limitations](#limitations)
- [Copyright](#copyright)

## Screenshots

| **Welcome Page** | **Choose Account** | **Navigation View** |
| :---: | :---: | :---: |
| ![Welcome Page](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/01_welcome_page.png) | ![Choose Account](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/02_google_auth.png) | ![Navigation View](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/03_navigation_menu.png)

| **Playlists** | **Playlist Videos** | **Uploads** |
| :---: | :---: | :---: |
| ![Playlist](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/04_phone_playlists.png) | ![Playlist Video](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/05_phone_playlists_item.png) | ![Uploads](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/06_phone_videos.png)

| **Youtube Player** | **Settings** | **Dark Theme** |
| :---: | :---: | :---: |
| ![Youtube Player](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/07_phone_youtube_player.png) | ![Settings](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/08_phone_settings.png) | ![Dark Theme](https://raw.githubusercontent.com/HyunWinter/Firetube/master/screenshots/09_dark_theme.png)

| **Tablet Portrait** | **Tablet Landscape** |
| :---: | :---: |
| ![Tablet Portrait](https://github.com/HyunWinter/Firetube/blob/master/screenshots/10_tablet_portrait.png) | ![Tablet Landscape](https://github.com/HyunWinter/Firetube/blob/master/screenshots/11_tablet_landscape.png)

## Features
- [x] Authentication
  - [x] Firebase Authentication
  - [x] Google Log In
  - [x] Shared Credential with Youtube
- [x] Youtube Uploads
  - [ ] SQLite Local DB
  - [ ] Pagination
  - [x] Adaptable Tablet View
  - [x] Search
  - [x] Refresh
- [x] Youtube Playlists
  - [x] SQLite Local DB
  - [ ] Pagination
  - [x] Adaptable Tablet View
  - [x] Search
  - [x] Refresh
  - [x] Sort Ascending
  - [x] Sort Descending
- [x] Youtube Playlist Videos
  - [ ] SQLite Local DB
  - [ ] Pagination
  - [x] Adaptable Tablet View
  - [ ] Video Details & Comments
  - [x] Search
  - [x] Refresh
  - [x] Sort Ascending
  - [x] Sort Descending
- [x] Watch Videos
  - [x] In-App Player
  - [x] Youtube Player
- [x] Settings Page
  - [x] Logout
  - [x] Light, Dark, System Themes
  - [ ] Localization Support
  - [x] Playlist Search Tag
  - [x] Watch Videos in Youtube App

## Limitations
- <a href="https://developers.google.com/youtube/v3/getting-started" target="_blank">Youtube Data API</a>
  - Queries have limited support operations
  - Default quota allocation of 10,000 units per day
  - Playlist() and PlaylistItems() don't support pre-ordering queries
  - Search() by order returns inaccurate results
  
- <a href="https://developers.google.com/youtube/android/player" target="_blank">YouTube Player API </a>
  - Hasn't been updated since <a href="https://developers.google.com/youtube/android/player/revision_history#release_notes_10_14_2015" target="_blank">October 2015</a> (excluding terms)
  - YouTubePlayerView class needs to be extended from YouTubeBaseActivity
  - YouTubePlayerSupportFragment() can't be casted to AndroidX Fragment()

## Libraries
The libraries and tools used include:
- <a href="https://firebase.google.com/docs/auth" target="_blank">Firebase Authentication</a>
- <a href="https://www.sqlite.org/index.html" target="_blank">SQLite</a>
- <a href="https://developers.google.com/youtube/v3/getting-started" target="_blank">Youtube Data API v3</a>
- <a href="https://github.com/PRNDcompany/YouTubePlayerView" target="_blank">Youtube Player View</a> by <a href="https://github.com/PRNDcompany" target="_blank">PRND</a> 

## Copyright

Copyright 2020 © <a href="https://github.com/HyunWinter" target="_blank">Hyun Winter</a>. All rights reserved.
