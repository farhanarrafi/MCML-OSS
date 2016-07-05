//-------------------------------------------------------------------
// Ver.1.1
// 2011/07/13
//-------------------------------------------------------------------

#include "ClientComCtrl.h"

namespace mcml{
// Constructor
CClientComCtrl::CClientComCtrl()
{
}

// Destructor
CClientComCtrl::~CClientComCtrl()
{
}

// request
int CClientComCtrl::request(const std::string& url, const std::string& xmlData, CResponseData& resData)
{
	std::vector<std::string > binaryDataList;

	// call request
	return request(url, xmlData, binaryDataList, resData);
}

int CClientComCtrl::request(const std::string& url, const std::string& xmlData, const std::string& binaryData, CResponseData& resData)
{
	// set binaryDataList
	std::vector<std::string > binaryDataList;
	if (!binaryData.empty()) {
		binaryDataList.push_back(binaryData);
	}

	// call request
	return request(url, xmlData, binaryDataList, resData);
}

int CClientComCtrl::request(const std::string& url, const std::string& xmlData, const std::vector<std::string >& binaryDataList, CResponseData& resData)
{
	// set cookie
	CComCtrl::setSendCookie(CComCtrl::getRecvCookie());

	// send and recv
	return CComCtrl::sendrecv(url, xmlData, binaryDataList, resData);
}


int CClientComCtrl::request(const std::string& url, CResponseData& resData)
{
	std::string xmlData;
	std::vector<std::string > binaryDataList;

	// call request
	return request(url, xmlData, binaryDataList, resData);
}

int CClientComCtrl::requestBinary(const std::string& url, const std::string& binaryData, CResponseData& resData)
{
	std::string xmlData;
	std::vector<std::string > binaryDataList;
	if (!binaryData.empty()) {
		binaryDataList.push_back(binaryData);
	}

	// call request
	return request(url, xmlData, binaryDataList, resData);
}

int CClientComCtrl::requestBinary(const std::string& url, const std::vector<std::string >& binaryDataList, CResponseData& resData)
{
	std::string xmlData;

	// call request
	return request(url, xmlData, binaryDataList, resData);
}
} // namespace mcml

