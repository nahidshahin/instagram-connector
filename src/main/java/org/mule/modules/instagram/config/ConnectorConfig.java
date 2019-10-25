package org.mule.modules.instagram.config;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.MuleContext;
import org.mule.api.annotations.*;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.context.MuleContextAware;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;



@ConnectionManagement(friendlyName = "Configuration")
public class ConnectorConfig implements MuleContextAware {
    /**
     * Proxy host
     */
    @Configurable
    @Optional
    @Placement(group = "Proxy settings", tab = "Proxy")
    private String proxyHost;

    /**
     * Proxy port
     */
    @Configurable
    @Default("-1")
    @Placement(group = "Proxy settings", tab = "Proxy")
    private int proxyPort;

    /**
     * Proxy username
     */
    @Configurable
    @Optional
    @Placement(group = "Proxy settings", tab = "Proxy")
    private String proxyUsername;

    /**
     * Proxy password
     */
    @Configurable
    @Optional
    @Placement(group = "Proxy settings", tab = "Proxy")
    @Password
    private String proxyPassword;

	private Instagram4j instagram;
    private String connectionId;

	@Override
	public void setMuleContext(MuleContext context) {
		//MuleHttpClient.setMuleContext(context);
	}

	/**
     * Connects to Instagram
     *
     * @param userName    The userName used by this application
     * @param password    The password used by this application
     * @throws org.mule.api.ConnectionException when the connection fails
     */
    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String userName, @Password String password) throws ConnectionException {
        try {
			if (proxyPort > 0) {
				HttpHost proxy = new HttpHost(this.proxyHost, this.proxyPort, "http");
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(new AuthScope(AuthScope.ANY),
								((org.apache.http.auth.Credentials)new UsernamePasswordCredentials(this.proxyUsername, this.proxyUsername)));
				instagram = Instagram4j.builder().username(userName).password(password).proxy(proxy).credentialsProvider(credentialsProvider).build();
			} else {
				instagram = Instagram4j.builder().username(userName).password(password).build();
			}
			instagram.setup();
			instagram.login();
        } catch (Exception te) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, null, "Bad credentials", te);
        }
        this.connectionId = userName + "-" + password;
    }

    @Disconnect
    public void disconnect() {
        instagram = null;
    }

    @ValidateConnection
    public boolean validateConnection() {
        return instagram != null;
	}
	
	@ConnectionIdentifier
    public String getConnectionIdentifier() {
        return this.connectionId;
    }
    public Instagram4j getInstagram() {
        return instagram;
	}
	
	public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

}