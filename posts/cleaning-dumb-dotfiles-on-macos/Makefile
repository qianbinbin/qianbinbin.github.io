NAME = clean-mac-dumbs
VERSION := $(shell ./$(NAME) --version | head -n 1 | awk '{print $$2}')

PREFIX ?= /usr/local

BINDIR = $(PREFIX)/bin
MANDIR = $(PREFIX)/share/man/man1
BASHDIR = $(PREFIX)/share/bash-completion/completions
ZSHDIR = $(PREFIX)/share/zsh/site-functions
FISHDIR = $(PREFIX)/share/fish/vendor_completions.d

DESTDIR ?=

.PHONY: clean install uninstall man completion dist help

all: man completion

man:
	help2man --no-info -n "clean dumb dotfiles on macOS" -o $(NAME).1 ./$(NAME)
	perl -0777 -pi -e 's/\.PP\nHome page: /\.SH HOME PAGE\n/g' $(NAME).1

completion:
	mkdir -p completion
	crazy-complete --option-stacking=False --input-type=yaml -o completion/$(NAME) bash ./$(NAME).yaml
	crazy-complete --option-stacking=False --input-type=yaml -o completion/$(NAME).fish fish ./$(NAME).yaml
	crazy-complete --option-stacking=False --input-type=yaml -o completion/_$(NAME) zsh ./$(NAME).yaml

install: man completion
	install -d $(DESTDIR)$(BINDIR)
	install -m755 $(NAME) $(DESTDIR)$(BINDIR)/$(NAME)
	install -d $(DESTDIR)$(MANDIR)
	install -m644 $(NAME).1 $(DESTDIR)$(MANDIR)/$(NAME).1
	install -d $(DESTDIR)$(BASHDIR)
	install -m644 completion/$(NAME) $(DESTDIR)$(BASHDIR)/$(NAME)
	install -d $(DESTDIR)$(ZSHDIR)
	install -m644 completion/_$(NAME) $(DESTDIR)$(ZSHDIR)/_$(NAME)
	install -d $(DESTDIR)$(FISHDIR)
	install -m644 completion/$(NAME).fish $(DESTDIR)$(FISHDIR)/$(NAME).fish

uninstall:
	rm -f $(DESTDIR)$(BINDIR)/$(NAME)
	rm -f $(DESTDIR)$(MANDIR)/$(NAME).1
	rm -f $(DESTDIR)$(BASHDIR)/$(NAME)
	rm -f $(DESTDIR)$(ZSHDIR)/_$(NAME)
	rm -f $(DESTDIR)$(FISHDIR)/$(NAME).fish

clean:
	rm -f $(NAME).1
	rm -rf completion

dist: man completion
	tar czf $(NAME)-$(VERSION).tar.gz \
		$(NAME) $(NAME).yaml $(NAME).1 completion Makefile LICENSE

help:
	@echo "Targets:"
	@echo "  all          Generate man page and shell completion"
	@echo "  man          Generate man page"
	@echo "  completion   Generate shell completion"
	@echo "  install      Install program"
	@echo "  uninstall    Remove program"
	@echo "  clean        Remove build files"
	@echo "  dist         Create tarball"
