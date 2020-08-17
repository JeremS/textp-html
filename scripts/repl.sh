#!/usr/bin/env bash

clojure -A:clj:cljs:dev:test:piggie:nrepl -m nrepl.cmdline --middleware "[cider.piggieback/wrap-cljs-repl]"