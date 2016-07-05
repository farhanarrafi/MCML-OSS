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
 * @file ComCtrl.h
 * @brief Communication control class declaration
 */

#ifndef __COMCTRL_H__
#define __COMCTRL_H__

#include <vector>
#include <string>
#include "ComData.h"

namespace mcml {
    // Class
	class CComCtrl
	{
		// Member value
    private:
    std::string m_SendCookie;
    std::string m_RecvCookie;
		unsigned long m_TimeoutCounterThreshold;
		bool m_IsUsingSeifSignedCertification;
		unsigned int m_currentTimeoutCounter;
		// Member method
    public:
        /**
         * @brief Constructor
         */
		CComCtrl();
        /**
         * @brief Destructor
         */
		~CComCtrl();
        /**
         * @brief Communication timeout value setting
         * @param timeoutMilliSeconds Request timeout value (milliseconds) [in]
         */
		void setTimeout(const unsigned long& timeoutMilliSeconds);
        /**
         * @brief Self-signed certificate usage setting
         */
		void setUsingSelfSignedCertification(void);
		virtual void forceTimout();
        
    protected:
		int sendrecv(const std::string& url, const std::string& xmlData, const std::vector<std::string>& binaryDataList, CComData& comData);
        
    std::string& getRecvCookie();
		void setSendCookie(const std::string& cookie);
	};
} // namespace mcml

#endif // __COMCTRL_H__
