package com.find.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 发送Https请求工具类
 * Created by maqiang on 2015/1/26.
 */
public class HttpsUtils {

    public static final MyHostnameVerifier HOST_VERIFIER = new MyHostnameVerifier();
    public static final TrustManager[] TRUST_MANAGERS = new TrustManager[]{new MyTrustManager()};

    public static final String BASE_URL = "https://10.96.101.44:8443/Find";
    public static final String HTTP_BASE_URL="http://10.96.101.44:8080/Find";
    public static final String POSTSERVLET = "/PostServlet";

    private HttpsUtils() {
    }

    //发送https Get
    public static InputStream doGet(final String url, final Map<String, String> params) throws Exception {
        FutureTask<InputStream> futureTask = new FutureTask<>(new Callable<InputStream>() {
            @Override
            public InputStream call() throws Exception {
                String finalUrl=url;
                if(params!=null) {
                    //参数拼串
                    StringBuilder sb = new StringBuilder();
                    sb.append("?");
                    for (String key : params.keySet()) {
                        sb.append(key);
                        sb.append("=");
                        sb.append(params.get(key));
                        sb.append("&");
                    }
                    finalUrl= url + sb.toString().substring(0, sb.toString().length() - 1);
                }
                HttpClient httpClient=getNewHttpClient();
                HttpGet get=new HttpGet(finalUrl);
                HttpResponse response=httpClient.execute(get);
                if(response.getStatusLine().getStatusCode()==200) {
                    return response.getEntity().getContent();
                }
                return null;
            }
        });
        new Thread(futureTask).start();
        return futureTask.get();
    }

//    public static String doPost(final String url, final Map<String, String> params) throws Exception {
//        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                StringBuilder sb = new StringBuilder();
//                for (Map.Entry<String, String> set : params.entrySet()) {
//                    sb.append(set.getKey());
//                    sb.append("=");
//                    sb.append(set.getValue());
//                    sb.append("&");
//                }
//                String entity = sb.substring(0, sb.length() - 1);
//                HttpsURLConnection conn = (HttpsURLConnection) (new URL(url)).openConnection();
//                SSLContext sc = SSLContext.getInstance("TLS");
//                sc.init(null, TRUST_MANAGERS, new SecureRandom());
//                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//                HttpsURLConnection.setDefaultHostnameVerifier(HOST_VERIFIER);
//
//
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Charset", "utf-8");
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
////                conn.setRequestProperty("Connection","Keep-Alive");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                conn.setUseCaches(false);
//
//                //写入参数
//                PrintWriter pw = new PrintWriter(conn.getOutputStream());
//                pw.write(URLEncoder.encode(entity, "utf-8"));
//                pw.flush();
//                pw.close();
////                conn.connect();
//                if (conn.getResponseCode() == 200) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    return URLEncoder.encode(br.readLine(), "utf-8");
//                } else {
//                    return null;
//                }
//            }
//        });
//        new Thread(futureTask).start();
//        return futureTask.get();
//    }

    public static String doPost(final String url,final Map<String,String> params) throws Exception {
        FutureTask<String> futureTask=new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpClient httpClient=getNewHttpClient();
                HttpPost httpPost=new HttpPost(url);
                if(params!=null) {
                    List<NameValuePair> list=new ArrayList<>();
                    for(String key:params.keySet()) {
                        list.add(new BasicNameValuePair(key,params.get(key)));
                    }
                    httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
                }
                HttpResponse response=httpClient.execute(httpPost);
                if(response.getStatusLine().getStatusCode()==200) {
                    return EntityUtils.toString(response.getEntity(),"utf-8");
                }
                return null;
            }
        });
        new Thread(futureTask).start();
        return futureTask.get();
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore trustStore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(trustStore);

            TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[]{tm}, null);
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
}

class MyHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        // TODO Auto-generated method stub
        return true;
    }

}

class MyTrustManager implements X509TrustManager {


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)

            throws CertificateException {
        // TODO Auto-generated method stub
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }


}
