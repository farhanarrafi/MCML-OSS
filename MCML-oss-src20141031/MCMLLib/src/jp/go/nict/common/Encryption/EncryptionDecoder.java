package jp.go.nict.common.Encryption;

/**
 * @author Kimura Noriyuki
 * @version 2.10
 * @since 2011/08/20
 */
public class EncryptionDecoder extends EncryptionBase{

	/**
	 * コンストラクタ
	 * @param sEncodeKey　エンコードキー（数字のみ）
	 */
	public EncryptionDecoder(String sEncodeKey){
		super(sEncodeKey);
	}
	
	/**
	 * 文字列を暗号化から元に戻す
	 * @param sOriginalString 暗号化された文字列
	 * @return　暗号化を解いた文字列
	 */
	public String DecodeString(String sOriginalString){
		InitCursol();
		String sRetVal = new String();

		String sWords[] = new String[sOriginalString.length()];

		for(int i=0;i<sWords.length;i++){
				sWords[i] = sOriginalString.substring(i,i+1);
				sWords[i] = DecodeWord(sWords[i]);
				sRetVal += sWords[i];
		}
			
		return sRetVal;
	}
	
	/**
	 * 暗号化された文字を解読する
	 * @param sWord　暗号化された文字
	 * @return　解読された文字
	 */
	private String DecodeWord(String sWord){
		String sRetVal = null;

		int iEncodeNumber = PARAM_TABLE.get(sWord) - getEncodeKey();

		sRetVal = getWord(iEncodeNumber);
		
		return sRetVal;
	}
	
	
	/**
	 * 暗号化を解読するアプリ（テスト用）
	 * @param args  第一引数：暗号化元文字列　第二引数：エンコードキー
	 */
	public static void main(String[] args) {
		EncryptionDecoder Decoder = new EncryptionDecoder(args[1]);
		String Result = Decoder.DecodeString(args[0]);
		System.out.println(Result);
	}
		
}
