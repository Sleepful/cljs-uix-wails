# Wails + ClojureScript starter

Similar to: https://github.com/Sleepful/cljs-uix-electron

This one uses [Wails](https://wails.io/) instead of Electron.

ClojureScript is configured with:

- Uix (React)
- Reitit routing (with Hash routes to work properly within the local webview)
- Reframe from the Uix starter

Run with `wails dev` on the root directory.

Requires installations for:

- Npm
- Clojure
- Go

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
