package com.sdk.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import com.sdk.utils.LoggerUtil;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Date;



public class HttpClient {

	
	private static CloseableHttpClient client = null;
	private static HttpClient  _instance=new HttpClient();
	private static PoolingHttpClientConnectionManager connManager = null;
	public static String getSend(String url){
		return getSend(url, "UTF-8", "");
	}
	static {  
		LoggerUtil.info("HttpClient初始化");
		try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null,
                    new TrustManager[] { new X509TrustManager() {
                         
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
 
                        public void checkClientTrusted(
                                X509Certificate[] certs, String authType) {
                        }
 
                        public void checkServerTrusted(
                                X509Certificate[] certs, String authType) {
                        }
                    }}, null);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();
             
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            client = HttpClients.custom().setConnectionManager(connManager).build();
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints)
                .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
        } catch (KeyManagementException e) {
            LoggerUtil.error("KeyManagementException"+e);
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.error("NoSuchAlgorithmException"+e);
        }
	}
	synchronized public static HttpClient getInstance(){
		return _instance;
	}

	 public static String postRestData(String url, String xmlData,Date curDate)  {
			LoggerUtil.info("请求rest地址:"+url);
			LoggerUtil.info("请求rest内容:"+xmlData);
			String result="";
			CloseableHttpClient client = null;
			String encode="UTF-8";
			try{
				if (isHttpsUrl(url)) {
					client = registerSSL(getHost(url), "TLS", 8883, "https");
				} else {
					client = HttpClients.createDefault();
				}
				
				RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(30000).build();
				HttpPost post = new HttpPost(url);
				post.setConfig(requestConfig);
			
				post.setHeader("Accept", "application/xml");
				post.setHeader("Content-Type", "application/xml;charset="+encode);
//				post.setHeader("Content-Length",String.valueOf(xmlData.getBytes().length)) ;

				HttpEntity httpEntity = new ByteArrayEntity(xmlData.getBytes("UTF-8"));
				post.setEntity(httpEntity);

				HttpResponse response = client.execute(post);
				HttpEntity entity = response.getEntity();
				Integer status =response.getStatusLine().getStatusCode();
	            StringBuffer sb = new StringBuffer();   
	            InputStreamReader iReader = null;  
	            InputStream inputStream = entity.getContent();   
	            iReader = new InputStreamReader(inputStream,encode);   
	            BufferedReader reader = new BufferedReader(iReader);   
	            String line = null;   
	            
	            while ((line = reader.readLine()) != null) {   
	            sb.append(line + "\r\n");   
	            }
	            iReader.close(); 
	            result=sb.toString();			
				LoggerUtil.debug("rest返回结果 :"+result+",status:"+status);
			}catch(Exception e){
				LoggerUtil.error("HttpClientConnect请求rest地址错误,"+e.getCause());
	        	e.printStackTrace();
			}finally{
				if (client != null ) {
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
	            }
			}
			return result;
		}

	public static String postRestData(String url, String xmlData,String accountId,Date curDate)  {
//		LoggerUtil.info("请求rest地址:"+url);
//		LoggerUtil.info("请求rest内容:"+xmlData);
		String result="";
		CloseableHttpClient client = null;
		String encode="UTF-8";
		try{

			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
//			client =  new DefaultHttpClient(new ThreadSafeClientConnManager());
//			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000); //连接时间30s
//			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,60000);    //数据传输60s
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
			HttpPost post = new HttpPost(url);
			post.setConfig(requestConfig);
//			post.removeHeaders("Content-Length");		        
			post.setHeader("Accept", "application/xml");
			post.setHeader("Content-Type", "application/xml;charset="+encode);
//			post.setHeader("Content-Length",String.valueOf(xmlData.getBytes().length)) ;
//			HttpEntity requestBody = new InputStreamEntity(new ByteArrayInputStream(xmlData.getBytes(encode)), -1);
//	        post.setEntity(requestBody);
//			byte[] requestByte =xmlData.getBytes("UTF-8");
			HttpEntity httpEntity = new ByteArrayEntity(xmlData.getBytes("UTF-8"));
			post.setEntity(httpEntity);

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			Integer status =response.getStatusLine().getStatusCode();
            StringBuffer sb = new StringBuffer();   
            InputStreamReader iReader = null;  
            InputStream inputStream = entity.getContent();   
            iReader = new InputStreamReader(inputStream,encode);   
            BufferedReader reader = new BufferedReader(iReader);   
            String line = null;   
            
            while ((line = reader.readLine()) != null) {   
            sb.append(line + "\r\n");   
            }
            iReader.close(); 
            result=sb.toString();			
			LoggerUtil.debug("rest返回结果 :"+result+",status:"+status);
		}catch(Exception e){
			LoggerUtil.error("HttpClient请求rest地址错误,"+e.getCause());
        	e.printStackTrace();
		}finally{
			if (client != null ) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
            }
		}
		return result;
	}

	static CloseableHttpClient registerSSL(String hostname, String protocol, int port, String scheme)
			throws NoSuchAlgorithmException, KeyManagementException {
//		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建SSL上下文实例
		SSLContext ctx = SSLContext.getInstance(protocol);
		// 服务端证书验证
		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}
			public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				if (chain == null || chain.length == 0)
					throw new IllegalArgumentException("null or zero-length certificate chain");
				if (authType == null || authType.length() == 0)
					throw new IllegalArgumentException("null or zero-length authentication type");

				boolean br = false;
				Principal principal = null;
				for (X509Certificate x509Certificate : chain) {
					principal = x509Certificate.getSubjectX500Principal();
					LoggerUtil.debug("服务器证书信息：" + principal.getName());
					if (principal != null) {
						br = true;
						return;
					}
				}
				if (!br) {
					LoggerUtil.error("服务端证书验证失败！");
				}
			}
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		// 初始化SSL上下文
		ctx.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());
//		// 创建SSL连接
//		SSLSocketFactory socketFactory = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		Scheme sch = new Scheme(scheme, port, socketFactory);
//		// 注册SSL连接
//		httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}
	private static boolean isHttpsUrl(String url) {
		return (null != url) && (url.length() > 7) && url.substring(0, 8).equalsIgnoreCase("https://");
	}
	public static String getHost(String url) {
		int index = url.indexOf("//");
		String host = url.substring(index + 2);
		index = host.indexOf("/");
		if (index > 0) {
			host = host.substring(0, index);
		}
		LoggerUtil.debug("host:"+host);
		return host;
	}
	
	
	
	
	

	public static String postDataJson(String url, String jsonData) {
		LoggerUtil.info("请求地址:" + url +"发送内容:"+jsonData);
		String result = "";
		CloseableHttpClient client = null;
		String encode = "UTF-8";
		try {
			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
			HttpPost post = new HttpPost(url);
			post.setConfig(requestConfig);
			// post.removeHeaders("Content-Length");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json;charset=" + encode);
			// post.setHeader("Content-Length",String.valueOf(xmlData.getBytes().length)) ;
			HttpEntity httpEntity = new ByteArrayEntity(jsonData.getBytes("UTF-8"));
			post.setEntity(httpEntity);

			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			Integer status =response.getStatusLine().getStatusCode();
			
			StringBuffer sb = new StringBuffer();
			InputStreamReader iReader = null;
			InputStream inputStream = entity.getContent();
			iReader = new InputStreamReader(inputStream, encode);
			BufferedReader reader = new BufferedReader(iReader);
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			iReader.close();
			result = sb.toString();
			LoggerUtil.info("响应状态码："+status+",result:"+result);
		} catch (Exception e) {
			LoggerUtil.error("HttpClient请求第三方地址错误," + e);
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	public static String postResponse(String url, String encode, String content) {
		LoggerUtil.debug("向" + url + "发起请求");
		String result = "";
		CloseableHttpClient client = null;
		try {
			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
			HttpResponse response = null;
			HttpEntity entity = null;
			HttpPost httppost = new HttpPost(url); // 引号中的参数是：servlet的地址

			RequestConfig requestConfig = RequestConfig.custom()
//					.setSocketTimeout(60000).setConnectTimeout(6000).build();
					.setSocketTimeout(60000).//数据传输时间
					setConnectTimeout(6000).build();//连接时间
			httppost.setConfig(requestConfig);
			// 返回服务器响应
//			StringEntity reqEntity = new StringEntity(content, encode);
//			reqEntity.setContentType("text/plain;charset=" + encode);
//			reqEntity.setContentEncoding(encode);
//			httppost.setEntity(reqEntity);
//			httppost.setHeader("Accept", "text/plain");
//			httppost.setHeader("Content-Type", "text/plain;charset="+encode);
			StringEntity reqEntity = new StringEntity(content, encode);
			reqEntity.setContentType("application/xml;charset=" + encode);
			reqEntity.setContentEncoding(encode);
			httppost.setEntity(reqEntity);
			httppost.setHeader("Accept", "application/xml");
			httppost.setHeader("Content-Type", "application/xml;charset="+encode);

			// 将参数传入post方法中
			response = client.execute(httppost); // 执行
			entity = response.getEntity();
			Integer status =response.getStatusLine().getStatusCode();
			StringBuffer sb = new StringBuffer();
			InputStreamReader iReader = null;
			InputStream inputStream = entity.getContent();
			iReader = new InputStreamReader(inputStream, encode);
			BufferedReader reader = new BufferedReader(iReader);
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			iReader.close();
			result = sb.toString();
			LoggerUtil.info("响应状态码："+status+",result:"+result);
		} catch (Exception e) {
			e.fillInStackTrace();
			String exec = "HttpClientConnect请求url："+url+",content:"+content+" ,异常信息" + e.getMessage();
			LoggerUtil.error("请求执行异常：" + exec);
			return exec;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}







	public static String postResponseSet(String url, String encode, String content,int timeLong) {
		LoggerUtil.debug("向" + url + "发起请求");
		String result = "";
		CloseableHttpClient client = null;
		try {
			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
			HttpResponse response = null;
			HttpEntity entity = null;
			HttpPost httppost = new HttpPost(url); // 引号中的参数是：servlet的地址

			RequestConfig requestConfig = RequestConfig.custom()
//					.setSocketTimeout(60000).setConnectTimeout(6000).build();
					.setSocketTimeout(60000).//数据传输时间
					setConnectTimeout(1000*timeLong).build();//连接时间
			httppost.setConfig(requestConfig);
			// 返回服务器响应
			StringEntity reqEntity = new StringEntity(content, encode);
			reqEntity.setContentType("application/xml;charset=" + encode);
			reqEntity.setContentEncoding(encode);
			httppost.setEntity(reqEntity);
			httppost.setHeader("Accept", "application/xml");
			httppost.setHeader("Content-Type", "application/xml;charset="+encode);

			// 将参数传入post方法中
			response = client.execute(httppost); // 执行
			entity = response.getEntity();
			Integer status =response.getStatusLine().getStatusCode();

			StringBuffer sb = new StringBuffer();
			InputStreamReader iReader = null;
			InputStream inputStream = entity.getContent();
			iReader = new InputStreamReader(inputStream, encode);
			BufferedReader reader = new BufferedReader(iReader);
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			iReader.close();
			result = sb.toString();
			LoggerUtil.info("响应状态码："+status+",result:"+result);
		} catch (Exception e) {
			e.fillInStackTrace();
			String exec = "HttpClientConnect请求url："+url+",异常信息" + e.getMessage();
			LoggerUtil.error("请求执行异常：" + exec);
			return exec;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}



	public static String getResponse(String url, String encode, String content) {
		LoggerUtil.debug("向" + url + "发起请求");
		String result = "";
		CloseableHttpClient client = null;
		try {
			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
			HttpResponse response = null;
			HttpEntity entity = null;
			HttpGet httpget = new HttpGet(url);

			RequestConfig requestConfig = RequestConfig.custom()
//					.setSocketTimeout(60000).setConnectTimeout(6000).build();
					.setSocketTimeout(20000).//数据传输时间
					setConnectTimeout(6000).build();//连接时间
			httpget.setConfig(requestConfig);

//			httpget.setHeader("Content-Type", "text/plain;charset=" + encode);
//			httpget.setHeader("Accept", "text/plain");
		 	response = client.execute(httpget);
	        System.out.println("StatusCode -> " + response.getStatusLine().getStatusCode());

	        entity = response.getEntity();
	        String jsonStr = EntityUtils.toString(entity,encode);//, "utf-8");

	        httpget.releaseConnection();

	        result = jsonStr;

		} catch (Exception e) {
			LoggerUtil.error("HttpClientConnect请求url：" + url + ",异常" + e.getMessage());
			result = "HttpClientConnect请求url：" + url + ",异常" + e.getMessage();
			return result;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	public static String getSend(String url, String encode, String content) {
		LoggerUtil.info("向" + url + "发起请求");
		String result = "";
		CloseableHttpClient client = null;
		try {
			if (isHttpsUrl(url)) {
				client = registerSSL(getHost(url), "TLS", 8883, "https");
			} else {
				client = HttpClients.createDefault();
			}
			HttpResponse response = null;
			HttpEntity entity = null;
			HttpGet httpget = new HttpGet(url);

			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(20000).//数据传输时间
					setConnectTimeout(6000).build();//连接时间
			httpget.setConfig(requestConfig);
		 	response = client.execute(httpget);
		 	LoggerUtil.info("StatusCode -> " + response.getStatusLine().getStatusCode());
	        entity = response.getEntity();
	        String jsonStr = EntityUtils.toString(entity,encode);//, "utf-8");
	        httpget.releaseConnection();
	        result = jsonStr;
		} catch (Exception e) {
			LoggerUtil.error("HttpClientConnect请求url：" + url + ",异常" + e.getMessage());
			result = "请求url：" + url + ",异常" + e.getMessage();
			return result;
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}



	public static HttpResult postSend(String url, String encode, String content,int sockeTimeout,int connectTimeout,int connectReqTimeOut,String method,String action,String authorization) {
		LoggerUtil.info("向" + url + "发起请求");
		Long b = System.currentTimeMillis();
		HttpPost post = new HttpPost(url);
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(sockeTimeout) //请求获取数据的超时时间，单位毫秒
                    .setConnectTimeout(connectTimeout) // 设置连接超时时间，单位毫秒。
                    .setConnectionRequestTimeout(connectReqTimeOut) //设置从connect Manager获取Connection 超时时间，单位毫秒
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            StringEntity reqEntity = new StringEntity(content, encode);
			reqEntity.setContentType("application/xml;charset=" + encode);
			reqEntity.setContentEncoding(encode);
			post.setEntity(reqEntity);
			if(StringUtils.isNotEmpty(action))
			{
				post.setHeader("Action", action);
			}
			if(StringUtils.isNotEmpty(authorization))
			{
				post.setHeader("Authorization", authorization);
			}
			post.setHeader("Accept", "application/"+method);
//			post.setHeader("Content-type", "application/json");
			post.setHeader("Content-Type", "application/"+method+";charset="+encode);
            CloseableHttpResponse response = client.execute(post);
            Integer status =response.getStatusLine().getStatusCode();
            if(status==200){
            	HttpEntity entity = response.getEntity();
            	try{
            		String result = EntityUtils.toString(entity, encode);
					return new HttpResult(200,result,b);
            	}finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
			}else{
				LoggerUtil.info("请求失败状态码："+status);
				return new HttpResult(status,response.getStatusLine().getReasonPhrase(),b);
			}
        }catch (Exception e) {
        	e.printStackTrace();
			LoggerUtil.error("请求执行异常：" + e.getMessage());
			return new HttpResult(-1,e.getMessage(),b);
        }  finally {
            post.releaseConnection();
        }
	}


	 public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
	        URL url = new URL(urlStr);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	                //设置超时间为3秒
	        conn.setConnectTimeout(3*1000);
	        //防止屏蔽程序抓取而返回403错误
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

	        //得到输入流
	        InputStream inputStream = conn.getInputStream();
	        //获取自己数组
	        byte[] getData = readInputStream(inputStream);

	        //文件保存位置
	        File saveDir = new File(savePath);
	        if(!saveDir.exists()){
	            saveDir.mkdir();
	        }
	        File file = new File(saveDir+File.separator+fileName);
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write(getData);
	        if(fos!=null){
	            fos.close();
	        }
	        if(inputStream!=null){
	            inputStream.close();
	        }


	        System.out.println("info:"+url+" download success");

	    }

	    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
	        byte[] buffer = new byte[1024];    
	        int len = 0;    
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
	        while((len = inputStream.read(buffer)) != -1) {    
	            bos.write(buffer, 0, len);    
	        }    
	        bos.close();    
	        return bos.toByteArray();    
	    }    

}
