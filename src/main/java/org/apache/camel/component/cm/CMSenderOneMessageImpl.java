package org.apache.camel.component.cm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.component.cm.exceptions.MessagingException;
import org.apache.camel.component.cm.exceptions.ProviderException;
import org.apache.camel.component.cm.exceptions.XMLConstructionException;
import org.apache.camel.component.cm.exceptions.cmresponse.CMResponseException;
import org.apache.camel.component.cm.exceptions.cmresponse.InsufficientBalanceException;
import org.apache.camel.component.cm.exceptions.cmresponse.InvalidProductTokenException;
import org.apache.camel.component.cm.exceptions.cmresponse.NoAccountFoundForProductTokenException;
import org.apache.camel.component.cm.exceptions.cmresponse.UnknownErrorException;
import org.apache.camel.component.cm.exceptions.cmresponse.UnroutableMessageException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class CMSenderOneMessageImpl implements CMSender {

    private static final Logger LOG = LoggerFactory
            .getLogger(CMSenderOneMessageImpl.class);

    private String url;
    private UUID productToken;

    public CMSenderOneMessageImpl(String url, UUID productToken) {
        this.url = url;
        this.productToken = productToken;
    }

    /**
     * Sends a previously validated SMSMessage to CM endpoint
     *
     */
    public void send(CMMessage cmMessage) throws MessagingException {

        // TODO: Check https://dashboard.onlinesmsgateway.com/docs for responses

        try {

            // 1.Construct XML. Throws XMLConstructionException
            String xml = createXml(cmMessage);

            // 2. Try to send to SMS Provider ...Throws ProviderException
            doHttpPost(url, xml);
        } catch (RuntimeException e) {
            LOG.error("Failed to send SMS: {}", cmMessage, e);
            throw new MessagingException(e);
        }
    }

    private String createXml(CMMessage message) {

        try {

            // TODO: Arguments Validated en este punto.

            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true);

            // Get the DocumentBuilder
            DocumentBuilder docBuilder = factory.newDocumentBuilder();

            // Create blank DOM Document
            DOMImplementation impl = docBuilder.getDOMImplementation();
            Document doc = impl.createDocument(null, "MESSAGES", null);

            // ROOT Element es MESSAGES
            Element root = doc.getDocumentElement();

            // AUTHENTICATION element
            Element authenticationElement = doc.createElement("AUTHENTICATION");
            Element productTokenElement = doc.createElement("PRODUCTTOKEN");
            authenticationElement.appendChild(productTokenElement);
            Text productTokenValue = doc.createTextNode("" + productToken);
            productTokenElement.appendChild(productTokenValue);
            root.appendChild(authenticationElement);

            // MSG Element
            Element msgElement = doc.createElement("MSG");
            root.appendChild(msgElement);

            // <FROM>VALUE</FROM>
            Element fromElement = doc.createElement("FROM");
            fromElement.appendChild(
                    doc.createTextNode(message.getDynamicSender()));
            msgElement.appendChild(fromElement);

            // <BODY>VALUE</BODY>
            Element bodyElement = doc.createElement("BODY");
            bodyElement.appendChild(doc.createTextNode(message.getMessage()));
            msgElement.appendChild(bodyElement);

            // <TO>VALUE</TO>
            Element toElement = doc.createElement("TO");
            toElement.appendChild(doc.createTextNode(message.getPhoneNumber()));
            msgElement.appendChild(toElement);

            // <DCS>VALUE</DCS> - if UNICODE - messageOut.isGSM338Enc
            // false
            if (message.isUnicode()) {
                Element dcsElement = doc.createElement("DCS");
                dcsElement.appendChild(doc.createTextNode("8"));
                msgElement.appendChild(dcsElement);
            }

            // <REFERENCE>VALUE</REFERENCE> -Alfanum
            String id = message.getIdAsString();
            if (id != null && !id.isEmpty()) {
                Element refElement = doc.createElement("REFERENCE");
                refElement.appendChild(
                        doc.createTextNode("" + message.getIdAsString()));
                msgElement.appendChild(refElement);
            }

            // <MINIMUMNUMBEROFMESSAGEPARTS>1</MINIMUMNUMBEROFMESSAGEPARTS>
            // <MAXIMUMNUMBEROFMESSAGEPARTS>8</MAXIMUMNUMBEROFMESSAGEPARTS>
            if (message.isMultipart()) {
                Element minMessagePartsElement = doc
                        .createElement("MINIMUMNUMBEROFMESSAGEPARTS");
                minMessagePartsElement.appendChild(doc.createTextNode("1"));
                msgElement.appendChild(minMessagePartsElement);

                Element maxMessagePartsElement = doc
                        .createElement("MAXIMUMNUMBEROFMESSAGEPARTS");
                maxMessagePartsElement.appendChild(doc.createTextNode(
                        Integer.toString(message.getMultiparts())));
                msgElement.appendChild(maxMessagePartsElement);
            }

            // Creatate XML as String
            Transformer aTransformer = TransformerFactory.newInstance()
                    .newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source src = new DOMSource(doc);
            Result dest = new StreamResult(xml);
            aTransformer.transform(src, dest);
            return xml.toString();
        } catch (TransformerException e) {
            LOG.error("Cant serialize CMMessage {}: ", message, e);
            throw new XMLConstructionException(e);
        } catch (ParserConfigurationException e) {
            LOG.error("Cant serialize CMMessage {}: ", message, e);
            throw new XMLConstructionException(e);
        }
    }

    private void doHttpPost(String urlString, String requestString) {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlString);
        post.setEntity(
                new StringEntity(requestString, Charset.forName("UTF-8")));

        try {

            HttpResponse response = client.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();

            LOG.debug("Response Code : {}", statusCode);

            if (statusCode == 400) {
                throw new ProviderException(
                        "CM Component and CM API show some kind of inconsistency. CM is complaining about not using a post method for the request. And this component only uses POST requests. What happens?");
            }

            if (statusCode != 200) {
                throw new ProviderException(
                        "CM Component and CM API show some kind of inconsistency. The component expects the status code to be 200 or 400. New api released? ");
            }

            // So we have 200 status code...

            // The response type is 'text/plain' and contains the actual
            // result of the request processing.

            // We obtaing the result text
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = null;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            // ... and process it

            line = result.toString();
            if (!line.isEmpty()) {

                // Line is not empty = error
                LOG.debug("Result of the request processing: FAILED\n{}", line);

                // The response text contains the error description. We will
                // throw a custom exception for each.

                if (line.contains(CMConstants.ERROR_UNKNOWN)) {
                    throw new UnknownErrorException();
                } else if (line.contains(CMConstants.ERROR_NO_ACCOUNT)) {
                    throw new NoAccountFoundForProductTokenException();
                } else if (line
                        .contains(CMConstants.ERROR_INSUFICIENT_BALANCE)) {
                    throw new InsufficientBalanceException();
                } else if (line
                        .contains(CMConstants.ERROR_UNROUTABLE_MESSAGE)) {
                    throw new UnroutableMessageException();
                } else if (line
                        .contains(CMConstants.ERROR_INVALID_PRODUCT_TOKEN)) {
                    throw new InvalidProductTokenException();
                } else {

                    // SO FAR i would expect other kind of ERROR.

                    // MSISDN correctness and message validity is client
                    // responsibility
                    throw new CMResponseException(
                            "CHECK ME. I am not expecting this. ");
                }
            }

            // Ok. Line is EMPTY - successfully submitted
            LOG.debug(
                    "Result of the request processing: Successfully submited");
        } catch (IOException io) {
            throw new ProviderException(io);
        }
    }
}
