#include "jp_go_nict_speex4j_SpeexEncoder.h"
#include "jp_go_nict_speex4j_SpeexDecoder.h"
#include <speex/speex.h>
#include <string.h>
#include <iostream>

struct JSpeexState {
	void*		state;
	SpeexBits*	bits;
};

static int frame_size;	// # samples (1 sample 16 bit)

//
//	Decoder
//

JNIEXPORT jbyteArray JNICALL
Java_jp_go_nict_speex4j_SpeexDecoder_createx(JNIEnv *env, jobject obj)
{
	JSpeexState ss;
	ss.bits = new SpeexBits;
	ss.state = speex_decoder_init(&speex_wb_mode);

	memset(ss.bits, 0, sizeof(SpeexBits));
	speex_bits_init(ss.bits);
	speex_decoder_ctl(ss.state, SPEEX_GET_FRAME_SIZE, &frame_size);

	jbyteArray jba = env->NewByteArray(sizeof(JSpeexState));
	env->SetByteArrayRegion(jba, 0, sizeof(JSpeexState), (jbyte*)&ss);

	return jba;
}


JNIEXPORT jint JNICALL
Java_jp_go_nict_speex4j_SpeexDecoder_get_1frame_1size(JNIEnv *env, jobject obj)
{
	return frame_size * sizeof(spx_int16_t);
}


JNIEXPORT void JNICALL
Java_jp_go_nict_speex4j_SpeexDecoder_destroyx(JNIEnv *env, jobject obj, jbyteArray state)
{
	JSpeexState ss;
	env->GetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	speex_bits_destroy(ss.bits);
	speex_decoder_destroy(ss.state);
	delete ss.bits;
}


JNIEXPORT jbyteArray JNICALL
Java_jp_go_nict_speex4j_SpeexDecoder_decodex(JNIEnv *env, jobject obj, 
	jbyteArray state, jbyteArray head, jbyteArray rest, jint start, jint nframes)
{
	if (nframes == 0)
		return env->NewByteArray(0);

	JSpeexState ss;

	// speex state in Java --> in C++
	env->GetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	spx_int16_t raw[frame_size * nframes];
	spx_int16_t* rp = raw;
	int fr = 0;

	if (head != NULL) {
		jbyte* encdata = env->GetByteArrayElements(head, NULL);
		int nb = encdata[0];
		speex_bits_read_from(ss.bits, (char*)encdata+1, nb);
		speex_decode_int(ss.state, ss.bits, rp);
		rp += frame_size;
		fr = 1;

		env->ReleaseByteArrayElements(head, encdata, JNI_ABORT);
	}

	jbyte* encdata = env->GetByteArrayElements(rest, NULL);
	jbyte* enc = encdata + start;

	for ( ; fr < nframes; fr++) {
		int nb = enc[0];
		speex_bits_read_from(ss.bits, (char*)enc+1, nb);
		speex_decode_int(ss.state, ss.bits, rp);
		rp += frame_size;
		enc += nb+1;
	}

	// speex state in C++ --> in Java
	env->SetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	env->ReleaseByteArrayElements(rest, encdata, JNI_ABORT);

	int sz = nframes * frame_size * sizeof(spx_int16_t);
	jbyteArray jba = env->NewByteArray(sz);
	env->SetByteArrayRegion(jba, 0, sz, (jbyte*)raw);

	return jba;
}


//
//	Encoder
//

JNIEXPORT jbyteArray JNICALL
Java_jp_go_nict_speex4j_SpeexEncoder_createx(JNIEnv *env, jobject obj, jint quality, jint complexity, jint vbr)
{
	JSpeexState ss;
	ss.bits = new SpeexBits;
	ss.state = speex_encoder_init(&speex_wb_mode);

	memset(ss.bits, 0, sizeof(SpeexBits));
	speex_bits_init(ss.bits);
	speex_encoder_ctl(ss.state, SPEEX_GET_FRAME_SIZE, &frame_size);

	speex_encoder_ctl(ss.state, SPEEX_SET_QUALITY, &quality);
	speex_encoder_ctl(ss.state, SPEEX_SET_COMPLEXITY, &complexity);
	speex_encoder_ctl(ss.state, SPEEX_SET_VBR, &vbr);

	jbyteArray jba = env->NewByteArray(sizeof(JSpeexState));
	env->SetByteArrayRegion(jba, 0, sizeof(JSpeexState), (jbyte*)&ss);

	return jba;
}


JNIEXPORT jint JNICALL
Java_jp_go_nict_speex4j_SpeexEncoder_get_1frame_1size(JNIEnv *env, jobject obj)
{
	return frame_size * sizeof(spx_int16_t);
}



JNIEXPORT void JNICALL
Java_jp_go_nict_speex4j_SpeexEncoder_destroyx(JNIEnv *env, jobject obj, jbyteArray state)
{
	JSpeexState ss;
	env->GetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	speex_bits_destroy(ss.bits);
	speex_encoder_destroy(ss.state);
	delete ss.bits;
}


#define MAX_WB_BYTES	128

JNIEXPORT jbyteArray JNICALL
Java_jp_go_nict_speex4j_SpeexEncoder_encodex(JNIEnv *env, jobject obj,
	jbyteArray state, jbyteArray head, jbyteArray rest, jint start, jint nframes)
{
	if (nframes == 0)
		return env->NewByteArray(0);

	JSpeexState ss;

	// speex state in Java --> in C++
	env->GetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	char encdata[nframes * MAX_WB_BYTES];
	char* enc = encdata;
	int sz = 0;
	int fr = 0;

	int comp = 0;

	if (head != NULL) {
		jbyte* raw = env->GetByteArrayElements(head, NULL);
		speex_bits_reset(ss.bits);
		speex_encode_int(ss.state, (spx_int16_t*)raw, ss.bits);
		int nb = speex_bits_write(ss.bits, enc+1, MAX_WB_BYTES);
		comp += nb+1;
		enc[0] = nb;
		enc += nb+1;
		sz = nb+1;
		fr = 1;
		env->ReleaseByteArrayElements(head, raw, JNI_ABORT);
	}

	jbyte* raw = env->GetByteArrayElements(rest, NULL);
	spx_int16_t* rp = (spx_int16_t*)raw;

	for ( ; fr < nframes; fr++) {
		speex_bits_reset(ss.bits);
		speex_encode_int(ss.state, rp, ss.bits);
		int nb = speex_bits_write(ss.bits, enc+1, MAX_WB_BYTES);
		comp += nb+1;
		enc[0] = nb;
		enc += nb+1;
		sz += nb+1;
		rp += frame_size;
	}

	// speex state in C++ --> in Java
	env->SetByteArrayRegion(state, 0, sizeof(JSpeexState), (jbyte*)&ss);

	env->ReleaseByteArrayElements(rest, raw, JNI_ABORT);

	jbyteArray jba = env->NewByteArray(sz);
	env->SetByteArrayRegion(jba, 0, sz, (jbyte*)encdata);

	return jba;
}

