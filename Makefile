shell := bash

fmt:
	lein cljfmt fix

kibit:
	lein kibit --replace --interactive

ancient:
	lein ancient upgrade :interactive :no-tests

