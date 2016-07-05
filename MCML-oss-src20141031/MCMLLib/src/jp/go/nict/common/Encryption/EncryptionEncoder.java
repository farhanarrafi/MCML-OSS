package jp.go.nict.common.Encryption;


/**
 * @author Kimura Noriyuki
 * @version 2.10
 * @since 2011/08/20
 */
public class EncryptionEncoder extends EncryptionBase{

	/**
	 * コンストラクタ
	 * @param sEncodeKey　エンコードキー（数字のみ）
	 */
	public EncryptionEncoder(String sEncodeKey){
		super(sEncodeKey);
	}
	

	/**
	 * 文字列を暗号化する
	 * @param sOriginalString 暗号化元文字列
	 * @return 暗号化後文字列
	 */
	private String EncodeString(String sOriginalString){
		InitCursol();

		String sRetVal = new String();

		String sWords[] = new String[sOriginalString.length()];

		for(int i=0;i<sWords.length;i++){
				sWords[i] = sOriginalString.substring(i,i+1);
				sWords[i] = EncodeWord(sWords[i]);
				sRetVal += sWords[i];
		}
			
		return sRetVal;
	}
	
	/**
	 * 文字を暗号化する
	 * @param sWord 暗号化する文字
	 * @return 暗号化後文字
	 */
	private String EncodeWord(String sWord){
		String sRetVal = null;

		int iEncodeNumber = PARAM_TABLE.get(sWord) + getEncodeKey();

		sRetVal = getWord(iEncodeNumber);
		
		return sRetVal;
	}
	
	
	/**
	 * 暗号化実行用
	 * @param args 第一引数：暗号化元文字列　第二引数：エンコードキー
	 */
	public static void main(String[] args) {
		EncryptionEncoder Encoder = new EncryptionEncoder(args[1]);
		String Result = Encoder.EncodeString(args[0]);
		System.out.println(Result);
	}
	
}
