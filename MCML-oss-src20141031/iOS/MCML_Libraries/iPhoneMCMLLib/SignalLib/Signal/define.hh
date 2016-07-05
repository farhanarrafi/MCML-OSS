// Copyright 2013, NICT
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of NICT nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
/**
 *\file: define.hh
 *                                �ϐ���`
 * Date: 2006/9/30
 */


#ifndef ATRNS_DEFINE_HH
#define ATRNS_DEFINE_HH

#include "SignalCommon.h"


// �������� (FB)

#define ATRNS_SMPFREQ    (16000.0)  //       �T���v�����O���g��
#define ATRNS_HIGHPASS    (8000.0)  //           �n�C�p�X���g��
#define ATRNS_LOWPASS        (0.0)  //           ���[�p�X���g��
#define ATRNS_FFTLEN         (512)  //               FFT ���͕�
#define ATRNS_WINDOW         (320)  //                 ���͑���
#define ATRNS_SHIFT          (160)  //         �t���[���V�t�g��
#define ATRNS_PREEMP        (0.98)  //     �v���G���t�@�V�X�W��

// �G���}�� (MMSE)

#define ATRNS_VECLEN          (24)  //         �t�B���^�o���N��
#define ATRNS_MIXNUM         (512)  //        ���� GMM �̍�����
// #define ATRNS_NBEST           (10)  //          NBest ���z��
#define ATRNS_CODESIZE        (16)  //   ���z�R�[�h�u�b�N�T�C�Y

#define DEFAULT_NBEST         (10)  //             NBest ���z��
#define ATRNS_NBEST (gNsVadParam.NBest)	//         NBest ���z��

// �G������ (PF)

#define DEFAULT_INITNUM         (10)  // �����G������p�T���v����
#define DEFAULT_INTERVAL        (16)  //     �G���d�􉹐�����Ԋu
#define DEFAULT_SMPINT          (20) //         �T���v�����O�Ԋu
#define DEFAULT_SMPNUM           (5)  //           �p�[�e�B�N����
#define DEFAULT_DRVVAR      (0.0005) //             �G�����z�ϓ�
#define DEFAULT_SYSNUM           (4)  //               �V�X�e����
#define DEFAULT_MAXFGT         (0.2)  //             �ő�Y�p�W��
#define DEFAULT_MAXSCALE       (2.0)  //     �ő�X�P�[�����O�W��
#define DEFAULT_MAXAVELEN       (20)  //               �ő啽�ϒ�
#define DEFAULT_SENSFACT       (0.1)  //                 �q���W��

#define ATRNS_INITNUM   (gNsVadParam.NsInitNum)	  // �����G������p�T���v����
#define ATRNS_INTERVAL	(gNsVadParam.NsInterval)  //     �G���d�􉹐�����Ԋu
#define ATRNS_SMPINT	(gNsVadParam.SamplingInterval)  //   �T���v�����O�Ԋu
#define ATRNS_SMPNUM	(gNsVadParam.ParticleNum)  //          �p�[�e�B�N����
#define ATRNS_SYSNUM	(gNsVadParam.SystemNum)    //              �V�X�e����
#define ATRNS_MAXAVELEN	(gNsVadParam.MaxAverageLength)  //         �ő啽�ϒ�

#define ATRNS_BUFSIZE	(gNsVadParam.BufferSize)		// �o�̓o�b�t�@�T�C�Y
#define DEFAULT_BUFSIZE	 		(5000) //                  �o�̓o�b�t�@�T�C�Y

#ifndef __DSR_FLOAT_DEBUG__
// �^�p��
#define ATRNS_DRVVAR	(gNsVadParam.DRVVAR)	// �G�����z�ϓ�
#define ATRNS_MAXFGT	(gNsVadParam.MaxForget)	// �ő�Y�p�W��
#define ATRNS_MAXSCALE	(gNsVadParam.MaxScale)	// �ő�X�P�[�����O�W��
#define ATRNS_SENSFACT	(gNsVadParam.SensFact)	// �q���W��
#else
// float�̊ۂߌ덷���z�������f�o�b�O��
#define ATRNS_DRVVAR	DEFAULT_DRVVAR		// �G�����z�ϓ�
#define ATRNS_MAXFGT	DEFAULT_MAXFGT		// �ő�Y�p�W��
#define ATRNS_MAXSCALE	DEFAULT_MAXSCALE	// �ő�X�P�[�����O�W��
#define ATRNS_SENSFACT	DEFAULT_SENSFACT	// �q���W��
#endif

// ���b��Ԍ��o (VAD)

#define DEFAULT_ALPHA          (2.0)  //   �G���p���[�ɑ΂���{��
#define DEFAULT_TRIGGER          (3)  //           ������Ԕ��蒷
#define DEFAULT_STARTING         (7)  //   �J�n�J��グ�t���[����
#define DEFAULT_ENDING          (35)  //       �I���x���t���[����

#define ATRNS_ALPHA	(gNsVadParam.Alpha)  //   �G���p���[�ɑ΂���{��
#define ATRNS_TRIGGER	(gNsVadParam.Trigger)  //           ������Ԕ��蒷
#define ATRNS_STARTING	(gNsVadParam.Starting)  //   �J�n�J��グ�t���[����
#define ATRNS_ENDING	(gNsVadParam.Ending)  //       �I���x���t���[����


struct NS_VAD_PARAM {
	iPhoneSignal::NOISE_SUPPRESS_TYPE	NoiseSuppressType ;
	bool				VAD ;
	bool				ParticleFilter ;
	unsigned short		NBest ;
	unsigned short		NsInitNum ;
	unsigned short		NsInterval ;
	unsigned short		SamplingInterval ;
	unsigned short		ParticleNum ;
	float				DRVVAR ;
	unsigned short		SystemNum ;
	float				MaxForget ;
	float				MaxScale ;
	unsigned short		MaxAverageLength ;
	float				SensFact ;
	//float				WaveThreshold ;
	//float				SpecThreshold ;
	//float				MelThreshold ;
	unsigned short		BufferSize ;
	float				Alpha ;
	unsigned short		Trigger ;
	unsigned short		Starting;
	unsigned short		Ending ;
	//unsigned short		HangOver ;
	//unsigned short		SangOver ;
	//unsigned short		VangOver ;
	//unsigned short		HTrigger ;
	//unsigned short		VTrigger ;
	//iPhoneSignal::LANGUAGE_TYPE	LanguageType ;
} ;

#endif
