JAVA_HONE=/usr/slt/jdk1.6.0
JAVA_BIN=$(JAVA_HOME)/bin
JAVAC=$(JAVA_BIN)/javac
JAVAH=$(JAVA_BIN)/javah

SRC_DIR=jp/go/nict/speex4j

JCLASS= jp.go.nict.speex4j.SpeexEncoder \
	jp.go.nict.speex4j.SpeexDecoder

JNI_H=  jp_go_nict_speex4j_SpeexEncoder.h \
	jp_go_nict_speex4j_SpeexDecoder.h

CFLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I./include -fPIC -m32

all: libspeex4j.so

speex4j.o: speex4j.cpp
	$(CXX) $(CFLAGS) -c $< -o $@

libspeex4j.so: jdone speex4j.o 
	$(CXX) -m32 -shared -o $@ speex4j.o libspeex/*.o

jdone: 
	$(JAVAC) $(SRC_DIR)/*.java
	$(JAVAH) -jni $(JCLASS)
	echo done > jdone

test:
	$(JAVAC) EncodeTest.java

clean:
	rm lispeex/*.o speex4j.o jdone

tar:
	tar zcf speex4j.tgz $(SRC_DIR)/*.java libspeex/{*.[ch],Makefile,COPYING,AUTHORS} include speex4j.cpp Makefile README

