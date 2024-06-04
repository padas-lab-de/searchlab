SHELL := /bin/bash
.PHONY : clean
.PRECIOUS: %.class

IndexTrec : QryEvalExtensions.jar IndexTrec.class

org/apache/lucene/analysis/en/% : %
	mkdir --parents $(@D)
	cp $< $@

org/apache/lucene/benchmark/byTask/feeds/% : %
	mkdir --parents $(@D)
	cp $< $@ 

QryEvalExtensions.jar : \
	  org/apache/lucene/analysis/en/EnglishAnalyzerConfigurable.class \
	  org/apache/lucene/benchmark/byTask/feeds/TrecClueWebParser.class
	touch QryEvalExtensions.jar
	jar -uf QryEvalExtensions.jar $^

	#  Insert utility subclasses into the jar file.
	for class in $^; do \
	    file=$$(basename "$$class" ".class") ; \
	    subclass=$$file\$$\*.class ; \
	    for sub in $$subclass; do \
		path=$$(dirname $$class) ; \
		cp $$sub $$path ; \
		jar -uf QryEvalExtensions.jar $$path/$$sub ; \
	    done ; \
	    exit ; \
        done

org/*$$*.class : 
	echo dummy rule

%.class : %.java
ifeq ($(OS),Windows_NT)
	# assume windows
	javac -Xlint -cp ".;lucene-9.8.0/*" -g $<
else
	# assume Linux
	javac -Xlint -cp ".:lucene-9.8.0/*" -g $<
endif

clean :
	rm -rf org
