
package com.paypal.adaptive.api.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.paypal.adaptive.api.responses.PayResponse;
import com.paypal.adaptive.core.APICredential;
import com.paypal.adaptive.core.ActionType;
import com.paypal.adaptive.core.ClientDetails;
import com.paypal.adaptive.core.EndPointUrl;
import com.paypal.adaptive.core.ParameterUtils;
import com.paypal.adaptive.core.PaymentDetails;
import com.paypal.adaptive.core.RequestEnvelope;
import com.paypal.adaptive.core.ServiceEnvironment;
import com.paypal.adaptive.exceptions.AuthorizationRequiredException;
import com.paypal.adaptive.exceptions.InvalidAPICredentialsException;
import com.paypal.adaptive.exceptions.InvalidResponseDataException;
import com.paypal.adaptive.exceptions.MissingAPICredentialsException;
import com.paypal.adaptive.exceptions.MissingParameterException;
import com.paypal.adaptive.exceptions.PayPalErrorException;
import com.paypal.adaptive.exceptions.PaymentExecException;
import com.paypal.adaptive.exceptions.PaymentInCompleteException;
import com.paypal.adaptive.exceptions.RequestFailureException;

/**
 * The request to initiate a Payment. You can use the Pay API operation to make simple payments, chained payments, or parallel
payments; these payments can be explicitly approved, preapproved, or implicitly approved.
 * 
 */
public class PayRequest {

	private static final Logger log = Logger.getLogger(PayRequest.class.getName());
	
    protected ClientDetails clientDetails;
    protected PaymentDetails paymentDetails;
    protected RequestEnvelope requestEnvelope;
    protected ServiceEnvironment env;
    
    
    public PayRequest(String language, ServiceEnvironment env){
    	
    	requestEnvelope = new RequestEnvelope();
    	requestEnvelope.setErrorLanguage(language);
    	this.env = env;
    	paymentDetails = new PaymentDetails(ActionType.PAY);
    	
    }
    
    public PayResponse execute(APICredential credentialObj) 
    throws IOException, MalformedURLException, MissingAPICredentialsException,
    	   InvalidAPICredentialsException, MissingParameterException, 
    	   UnsupportedEncodingException, RequestFailureException,
    	   InvalidResponseDataException, PayPalErrorException, 
    	   PaymentExecException, AuthorizationRequiredException,
    	   PaymentInCompleteException {
    	
    	String responseString = "";
    	
    	/* - VALIDATE REQUIRED PARAMS- */
    	/*
    	 * check for the following things
    	 *  1. API Credentials
    	 *  2. Atleast one receiver has been set
    	 *  3. CurrencyCode is set
    	 */
    	if(credentialObj == null){
    		throw new MissingAPICredentialsException();
    	} else if(credentialObj != null) {
			InvalidAPICredentialsException ex = new InvalidAPICredentialsException();
			if (credentialObj.getAppId() == null
					|| credentialObj.getAppId().length() <= 0) {
				ex.addToMissingCredentials("AppId");
			}
			if (credentialObj.getAPIPassword() == null
					|| credentialObj.getAPIPassword().length() <= 0) {
				ex.addToMissingCredentials("APIPassword");
			}

			if (credentialObj.getAPIUsername() == null
					|| credentialObj.getAPIUsername().length() <= 0) {
				ex.addToMissingCredentials("APIUsername");
			}
			if (credentialObj.getSignature() == null
					|| credentialObj.getSignature().length() <= 0) {
				ex.addToMissingCredentials("Signature");
			}
			if(ex.getMissingCredentials() != null){
				throw ex;
			} else {
				ex = null;
			}
		}
    	
    	if(this.paymentDetails.getReceiverList().size() <= 0 ){
    		throw new MissingParameterException("Receiver");
    	}
    	
    	if(this.paymentDetails.getCurrencyCode() == null){
    		throw new MissingParameterException("CurrencyCode");
    	}
    	
    	/* -END-VALIDATION- */
    	
    	// prepare request parameters
    	StringBuilder postParameters = new StringBuilder();
    	
    	// add request envelope
    	postParameters.append(requestEnvelope.serialize());
    	postParameters.append(ParameterUtils.PARAM_SEP);
    	
    	// add payment details
    	postParameters.append(this.paymentDetails.serialize());
    	
    	// set clientDetails
    	postParameters.append(this.clientDetails.serialize());
    	    	
    	if(log.isLoggable(Level.INFO))
    		log.info("Sending PayRequest with: " + postParameters.toString());
    	
    	// create HTTP Request
    	try {
            URL url = new URL(EndPointUrl.get(this.env) + "Pay");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            // method is always POST
            connection.setRequestMethod("POST");
            // set HTTP headers
            connection.setRequestProperty("X-Paypal-SECURITY-USERID", credentialObj.getAPIUsername());
            connection.setRequestProperty("X-PAYPAL-SECURITY-PASSWORD", credentialObj.getAPIPassword());
            connection.setRequestProperty("X-PAYPAL-SECURITY-SIGNATURE", credentialObj.getSignature());
            connection.setRequestProperty("X-Paypal-Application-Id", credentialObj.getAppId());
            connection.setRequestProperty("X-PAYPAL-REQUEST-DATA-FORMAT", "NV");
            connection.setRequestProperty("X-PAYPAL-RESPONSE-DATA-FORMAT", "NV");
            connection.setRequestProperty("X-PAYPAL-TOOLKIT", "GAE-Toolkit");
            connection.setRequestProperty("X-PAYPAL-REQUEST-SOURCE", "GAE-JAVA_Toolkit");
            
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(postParameters.toString());
            writer.close();
    
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            	String inputLine;
            	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    while ((inputLine = reader.readLine()) != null) {
                    	responseString += inputLine;
                    }
        	    reader.close();
        	    if(responseString.length() <= 0){
        	    	throw new InvalidResponseDataException(responseString);
        	    }
        	    if(log.isLoggable(Level.INFO))
            		log.info("Received PayResponse: " + responseString);
            	
            } else {
                // Server returned HTTP error code.
            	throw new RequestFailureException(connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            // ...
        	throw e;
        } catch (IOException e) {
            // ...
        	throw e;
        }
    	    	
    	// parse response
    	PayResponse response = new PayResponse(responseString);
    	
    	// handle errors
    	return response;
    }
    
	/**
	 * @param clientDetails the clientDetails to set
	 */
	public void setClientDetails(ClientDetails clientDetails) {
		this.clientDetails = clientDetails;
	}
	/**
	 * @return the clientDetails
	 */
	public ClientDetails getClientDetails() {
		return clientDetails;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	
	
	
}