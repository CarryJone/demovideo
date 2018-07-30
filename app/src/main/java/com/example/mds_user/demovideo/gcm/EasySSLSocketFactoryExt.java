package com.example.mds_user.demovideo.gcm;


import com.example.mds_user.demovideo.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import com.example.mds_user.demovideo.gcm.TraceUtility.TraceType;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import mds.approval.lib.EasySSLSocketFactory;
import mds.approval.lib.EasyX509TrustManager;

public class EasySSLSocketFactoryExt extends EasySSLSocketFactory {
	
	private static android.content.Context mContext;
	
	public EasySSLSocketFactoryExt(android.content.Context mContext){
		this.mContext=mContext;
	}
	
	public SSLContext createEasySSLContext() throws IOException {
		try {

			List<InputStream> caInputList = new ArrayList<InputStream>();

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = null;

			caInput = new BufferedInputStream(mContext.getResources().openRawResource(R.raw.wistronssl_cer01));
			caInputList.add(caInput);
			addCertificateEntry(cf,caInputList);

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { new EasyX509TrustManager(
					null) }, null);
			return context;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private static void addCertificateEntry(CertificateFactory cf, List<InputStream> caInputList) throws Exception {
		
		KeyStore keyStore = null;
		String keyStoreType = KeyStore.getDefaultType();
		keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		for(int i=0;i<caInputList.size();i++){
			Certificate ca;
			InputStream caInput=null;
			try {
				caInput=(InputStream)caInputList.get(i);
				ca = cf.generateCertificate(caInput);
				// Create a KeyStore containing our trusted CAs
				keyStore.setCertificateEntry("ca"+i, ca);
			}catch(Exception e) {
				TraceUtility.trace(TraceType.error,"EasySSLSocketFactoryExt.addCertificateEntry()",e.toString());
				throw e;
			}finally {
					caInput.close();
				}
			}

	}
	
}
