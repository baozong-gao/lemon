package com.gbz.lemon.datasecurity;

import com.gbz.lemon.util.KeyUtil;


public abstract class DataSecurity {
	
	
	protected KeyUtil keyUtil = new KeyUtil();
	//密钥库文件地址
	protected String keyPath;
	//密钥库密码
	protected String password;
	//私钥别名
	protected String privateAlias;
	//公钥别名
	protected String publicAlias;
	
	public String sign(byte [] data){
		return null;
	};
	
	public  Boolean validate(byte [] signData){
		return null;
	};
	
	public  String nuSign(byte [] signData){
		return null;
	};
	
	public KeyUtil getKeyUtil() {
		return keyUtil;
	}

	public void setKeyUtil(KeyUtil keyUtil) {
		this.keyUtil = keyUtil;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrivateAlias() {
		return privateAlias;
	}

	public void setPrivateAlias(String privateAlias) {
		this.privateAlias = privateAlias;
	}

	public String getPublicAlias() {
		return publicAlias;
	}

	public void setPublicAlias(String publicAlias) {
		this.publicAlias = publicAlias;
	}

	public String getKeyType() {
		return keyUtil.keyType;
	}

	public void setKeyType(String keyType) {
		keyUtil.keyType = keyType;
	}

	
	
	
}
