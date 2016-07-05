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
 *\file ScopedMem.h
 */
//------------------------------------------------------------------
// Function       : Template class definition of CScopedMem
// Identification : 1.2
//------------------------------------------------------------------

#ifndef _ScopedMem_H
#define _ScopedMem_H

//-------------------------------------------------------------------
// Definition
//-------------------------------------------------------------------
template <class TYPE>
class CScopedMem
{
	//-------------------------------------------------------------------
	// Member variable
	//-------------------------------------------------------------------
protected :
	TYPE*			m_pMem ;
	unsigned int	m_Count ;

	//-------------------------------------------------------------------
	// Member function
	//-------------------------------------------------------------------
public :
	CScopedMem(unsigned int Count) ;
	CScopedMem() ;
	virtual ~CScopedMem() ;
	bool New(unsigned int Count) ;
	void Delete() ;
	TYPE* Mem() ;
	const TYPE* ConstMem() const;
	TYPE& operator [] (unsigned int Count) ;
	unsigned int Count() ;
} ;


//-------------------------------------------------------------------
// Implementation
//-------------------------------------------------------------------
template <class TYPE>
CScopedMem<TYPE>::CScopedMem(unsigned int Count)
{
	m_pMem = NULL ;
	m_Count = 0 ;

      // Return value is not checked because of no salvation method
      // It is possible to tell afterward because return value from Mem() and Count() respectively becomes NULL and 0 when New() is failed.
	New(Count) ; 
}

template <class TYPE>
CScopedMem<TYPE>::CScopedMem()
{
	m_pMem = NULL ;
	m_Count = 0 ;
}

template <class TYPE>
CScopedMem<TYPE>::~CScopedMem()
{
	Delete() ;
}

template <class TYPE>
bool CScopedMem<TYPE>::New(unsigned int Count)
{
	Delete() ;
	m_pMem = new TYPE[Count] ;
	m_Count = (m_pMem) ? Count : 0 ;
	return (m_pMem) ? true : false ;
}

template <class TYPE>
void CScopedMem<TYPE>::Delete(void)
{
	if(m_pMem){
		delete [] m_pMem ;
		m_pMem = NULL ;
		m_Count = 0 ;
	}
}

template <class TYPE>
TYPE* CScopedMem<TYPE>::Mem()
{
	return m_pMem ;
}

template <class TYPE>
const TYPE* CScopedMem<TYPE>::ConstMem() const
{
	return (const TYPE*)m_pMem ;
}

template <class TYPE>
TYPE& CScopedMem<TYPE>::operator [] (unsigned int Count)
{
	return m_pMem[Count] ;
}

template <class TYPE>
unsigned int CScopedMem<TYPE>::Count()
{
	return m_Count ;
}

#endif // _4F2EB5F5_E9BB_445e_BC9E_188385095B4C_ScopedMem
