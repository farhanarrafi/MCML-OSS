package jp.go.nict.common.Encryption;

import java.util.Hashtable;

/**
 * @author Kimura Noriyuki
 * @version 2.10
 * @since 2011/08/20
 */
public class EncryptionBase {
	protected int mEncodeKey[] = null; 
	protected int mCursol =0;
	protected Hashtable<String,Integer> PARAM_TABLE = new Hashtable<String,Integer>();

	
	private String PARAM_LIST[] = {
		"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
		"0","1","2","3","4","5","6","7","8","9",
		";","/","?",":","@","&","=","+","$",",","#",
		"-","_",".","!","~","*","'","(",")"," "
		};
	
	
	/**
	 * PARAM_TABLEの作成
	 * 引数からエンコードキーの作成
	 * @param sEncodeKey　エンコードキー（数字のみ）
	 */
	public EncryptionBase(String sEncodeKey){
		for(int i=0;i<PARAM_LIST.length;i++){
			PARAM_TABLE.put(PARAM_LIST[i], new Integer(i));
		}
		mEncodeKey = ConvertKeyString2KeyInt(sEncodeKey);
		if(mEncodeKey==null){
			System.exit(1);
		}		
	}
	
	/**
	 * エンコードキーの文字列を数字列に変換する
	 * @param sEncodeKey　エンコードキー（数字のみ）
	 * @return　エンコードキーの数字配列
	 */
	private int[] ConvertKeyString2KeyInt(String sEncodeKey){
		String sKeys[] = new String[sEncodeKey.length()];
		int[] RetVal = new int[sKeys.length];

		try{
			for(int i=0;i<sKeys.length;i++){
				sKeys[i] = sEncodeKey.substring(i,i+1);
				RetVal[i] = Integer.parseInt(sKeys[i]);
			}
		}
		catch(Exception E){
			E.printStackTrace();
			return null;
		}
		return RetVal;
	}	
	

	/**
	 * PARAM_LISTのサイズを取得
	 * @return　PARAM_LISTのサイズ
	 */
	protected int getParamListLength(){
		return PARAM_LIST.length;
	}
	
	/**
	 * 指定した番号のPARAM_LISTを取得
	 * @param iNumber　番号指定
	 * @return　PARAM_LISTの文字
	 */
	protected String getWord(int iNumber){
		if(iNumber >= getParamListLength()){
			iNumber -= getParamListLength();
		}
		else if(iNumber <0){
			iNumber += getParamListLength();
		}
				
		return PARAM_LIST[iNumber];
	}
	
	/**
	 * エンコードキーの取得
	 * @return
	 */
	protected int getEncodeKey(){
		int iRetVal = mEncodeKey[mCursol];
		mCursol++;
		if(mCursol==mEncodeKey.length){
			mCursol =0;
		}
		return iRetVal;
	}
	
	protected void InitCursol(){
		mCursol = 0;
	}
		
}
