# Wails + ClojureScript starter

Similar to: https://github.com/Sleepful/cljs-uix-electron

This one uses [Wails](https://wails.io/) instead of Electron.

ClojureScript is configured with:

- Uix (React)
- Reitit routing (with Hash routes to work properly within the local webview)
- Reframe from the Uix starter

Requires installations for:

- Npm
- Go

To run use two separate commands. One for running wails and another one for running the CLJS server.

## Frontend

First terminal command:
```
cd frontend
npm i
npm run dev # starts shadowcljs watch server
```

## Wails server

Second terminal command, it will serve files from the first watch server

```
wails dev
```

# Alternative way of running the dev command 

It is possible to use a single command instead of two, but it presents some issues where live reloading might not work until the browser is refreshed.

To use a single command one would add to `wails.json`:

```
  "frontend:install": "npm install",
  "frontend:build": "npm run build",
  "frontend:dev:watcher": "npm run dev",
```

Run with `wails dev` on the root directory.

I would recommend against this as of the time of writing.

# README

## About

This is the official Wails Vanilla template.

You can configure the project by editing `wails.json`. More information about the project settings can be found
here: https://wails.io/docs/reference/project-config

## Live Development

To run in live development mode, run `wails dev` in the project directory. This will run a Vite development
server that will provide very fast hot reload of your frontend changes. If you want to develop in a browser
and have access to your Go methods, there is also a dev server that runs on http://localhost:34115. Connect
to this in your browser, and you can call your Go code from devtools.

## Building

To build a redistributable, production mode package, use `wails build`.
