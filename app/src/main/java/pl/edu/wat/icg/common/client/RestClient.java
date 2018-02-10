package pl.edu.wat.icg.common.client;


import java.io.IOException;

import cz.msebera.android.httpclient.HttpHost;

import com.fasterxml.jackson.databind.ObjectMapper;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.auth.AuthenticationException;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustSelfSignedStrategy;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.ssl.SSLContextBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Map;

public abstract class RestClient {

    CloseableHttpClient client;
    private URL url;
    private HttpHost target;
    private Map<String, Object> accessToken;

    private static final int TIMEOUT = 5;

    RestClient(String wsUrl) throws MalformedURLException, GeneralSecurityException {
        url = new URL(wsUrl);
        target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

        //POłączenie, tworzy klienta który będzie sam nie uwierzytelniał. i timeout.
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(builder.build());

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT * 1000)
                .setConnectionRequestTimeout(TIMEOUT * 1000)
                .setSocketTimeout(TIMEOUT * 1000).build();

        client = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLSocketFactory(sslFactory).build();
    }

        //metoda post, która pobiera token jesli jest potrzebny a następnie wysyła plik.
    <T> HttpEntity post(String path, T body) throws IOException, HttpException {
        acquireTokenIfNeeded();

        HttpPost request = new HttpPost(url.getPath() + path);
        addTokenToRequestHeader(request);
        if (body != null) {
            ObjectMapper mapper = new ObjectMapper();
            StringEntity entity = new StringEntity(mapper.writeValueAsString(body), StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            request.setEntity(entity);
        }

        try (CloseableHttpResponse response = client.execute(target, request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                accessToken = null;
                return post(path, body);
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException(response.getStatusLine().getReasonPhrase());
            }
            return response.getEntity();
        }
    }

    private void addTokenToRequestHeader(HttpRequestBase request) {
        request.addHeader("Authorization", String.format("Bearer %s", accessToken.get("access_token")));
    }

    private void acquireTokenIfNeeded() throws IOException, AuthenticationException {
        if (accessToken == null) {
            accessToken = acquireToken();
        }
    }

    protected abstract Map<String, Object> acquireToken() throws IOException, AuthenticationException;
}
