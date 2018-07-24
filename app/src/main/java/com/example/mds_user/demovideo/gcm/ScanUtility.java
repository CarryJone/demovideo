package com.example.mds_user.demovideo.gcm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanUtility {
	
	public static final String regEx = "[ _`~!@#$%^&*()=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——|{}【】‘；：”“’。，、？]|\n|\r|\t";
	
	public static String encrypt(String str){
    	if ((str!=null) && !str.equals("")) {
    		str = AESHelper.encrypt(str); 
    	} 
		return AESHelper.decrypt(str);		
	}
	
	public static InputStream encrypt(InputStream is){
		String str = getString(is);
		String encrypted = AESHelper.encrypt(str);
		String decrypted = AESHelper.decrypt(encrypted);
		InputStream out = null;
		try {
			out = new ByteArrayInputStream(decrypted.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;		
	}
	
    public static String filterSqlInjection(String sql) {
    	if(sql == null || sql.equals("")){
    		return sql;
    	}
    	String regex = "'|%|;|-|--|like|and|or|not|use|insert|delete|update|select|count|group|union" +
                "|create|drop|truncate|alter|grant|execute|exec|xp_cmdshell|call|declare|source|sql";     	
    	return sql.replaceAll("(?i)"+regex, "");  //(?i)不區分大小寫替換      	      
    }
	
    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
    
    /**
     * 過濾特殊字符
     * @param str
     * @return
     */
    public static String filterSpecialChar(String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();  
    }
    
	private static String getString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		return result;
	}
}
