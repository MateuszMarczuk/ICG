package pl.edu.wat.icg.common.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.auth.AuthenticationException;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import pl.edu.wat.icg.common.file.ICGFileDto;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmuletRestClient extends RestClient {

    private HttpHost ssoTarget;
    private URL ssoURL;

    private String clientId;
    private String clientPassword;
    private String username;
    private String password;

    public AmuletRestClient(String wsUrl, String ssoUrl, String clientId, String clientPassword, String username, String password) throws MalformedURLException, GeneralSecurityException {
        super(wsUrl);
        this.ssoURL = new URL(ssoUrl);
        this.ssoTarget = new HttpHost(ssoURL.getHost(), ssoURL.getPort());
        this.clientId = clientId;
        this.clientPassword = clientPassword;
        this.username = username;
        this.password = password;
    }

    public HttpEntity sendIcgFile(ICGFileDto fileDto) throws IOException, HttpException {
        return post("/icgfile", fileDto);
    }

    @Override
    protected Map<String,Object> acquireToken() throws IOException, AuthenticationException {
        HttpPost request = new HttpPost(ssoURL.getPath() + "/oauth/token");

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("scope", "rere"));
        request.setEntity(new UrlEncodedFormEntity(params));

        UsernamePasswordCredentials creds  = new UsernamePasswordCredentials(clientId, clientPassword);
        request.addHeader(new BasicScheme().authenticate(creds, request, null));

        try(CloseableHttpResponse response = client.execute(ssoTarget, request)) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return new ObjectMapper().readValue(responseString, new TypeReference<HashMap<String, Object>>() {});
            } else {
                return null;
            }
        }
    }

    public static AmuletRestClient fromConfiguration() throws MalformedURLException, GeneralSecurityException {
        return new AmuletRestClient("http://10.0.2.2:8071", "http://10.0.2.2:8081/auth",
                "NieznajomyUzytkownika", "TajneHaslo409356",
                "krasnal", "krasnal23");
    }
}

