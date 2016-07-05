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
 * @file Lock.h
 * @brief Lock.h
 */
#pragma once

#include <pthread.h>

namespace mcml {

class CLock {
	private :
		pthread_mutex_t m_Lock ;
		pthread_mutexattr_t m_Attr ;
	public :
	CLock()
	{
		//pthread_mutex_t recMutex ;
		pthread_mutexattr_init(&m_Attr) ;
		pthread_mutexattr_settype(&m_Attr,PTHREAD_MUTEX_RECURSIVE) ;
		//pthread_mutex_init(&recMutex,&m_Attr) ;
		//m_Lock = recMutex ;
		pthread_mutex_init(&m_Lock,&m_Attr) ;
 	}   
 	virtual ~CLock(){} ;
	void Lock(){ pthread_mutex_lock(&m_Lock) ; }
	void Unlock(){ pthread_mutex_unlock(&m_Lock) ; }
 	pthread_mutex_t& Handle(){ return m_Lock ; } 
} ; 

class CScopedLock {
private:
	CLock& m_Lock ;
public:
	CScopedLock(CLock& Lock) : m_Lock(Lock) { m_Lock.Lock(); }
	virtual ~CScopedLock(){ m_Lock.Unlock(); }
};

#define	LOCKING_THIS_SCOPE(L) \
	CScopedLock lockingThisScope(L) ;

} // namespace mcml

