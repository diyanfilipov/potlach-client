package com.android.potlach.cloud.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This is an example of an HTTP client that does not properly
 * validate SSL certificates that are used for HTTPS. You should
 * NEVER use a client like this in a production application. Self-signed
 * certificates are ususally only OK for testing purposes, such as
 * this use case. 
 * 
 * @author jules
 *
 */
public class UnsafeHttpsClient {
	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		 
		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);
		 
			TrustManager tm = new X509TrustManager() {
			
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}
			 
			public void checkServerTrusted(X509Certificate[] chain, String authType) {}
			 
			public X509Certificate[] getAcceptedIssuers() {
				return null;
				}
			};
			 
			sslContext.init(null, new TrustManager[] { tm }, null);
		}
		 
		public MySSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
			super(null);
			sslContext = context;
		}
		 
		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}
		 
		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
			}
		}
	
	public static HttpClient createUnsafeClient() {
		try {
			HttpClient client = new DefaultHttpClient();
			client = sslClient(client);
			 
			// Execute HTTP Post Request
//			HttpGet post = new HttpGet(new URI("https://google.com"));
//			HttpResponse result = client.execute(post);
//			Log.v("test", EntityUtils.toString(result.getEntity()));
			
//			KeyStore trusted = KeyStore.getInstance("BKS");
//			SSLContextBuilder builder = new SSLContextBuilder();
//			builder.loadTrustMaterial(null, new AllowAllHostnameVerifier());
//			
//			SSLConnectionSocketFactory sslsf = new SSLSocketFactory(
//					builder.build());
//			CloseableHttpClient httpclient = HttpClients.custom()
//					.setSSLSocketFactory(sslsf).build();
			return client;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static HttpClient sslClient(HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }
 
	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }
 
	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 8443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
	    }
	}
}
