package org.apache.camel.component.cm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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

import org.apache.camel.component.cm.client.SMSMessage;
import org.apache.camel.component.cm.client.SMSResponse;
import org.apache.camel.component.cm.exceptions.MessagingException;
import org.apache.camel.component.cm.exceptions.ProviderException;
import org.apache.camel.component.cm.exceptions.XMLConstructionException;
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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import net.freeutils.charset.CCGSMCharset;

public class CMSenderOneMessageImpl implements CMSender {

	private static final Logger LOG = LoggerFactory.getLogger(CMSenderOneMessageImpl.class);

	private String url;
	private UUID productToken;
	private int defaultMaxNumberOfParts;

	private CharsetEncoder encoder = CCGSMCharset.forName("CCGSM").newEncoder();
	private PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();

	public CMSenderOneMessageImpl(String url, UUID productToken, int defaultMaxNumberOfParts) {
		this.url = url;
		this.productToken = productToken;
		this.defaultMaxNumberOfParts = defaultMaxNumberOfParts;
	}

	/**
	 * El producer recibe un body: 1. Validar el BODY. Puede ser un CMMessage o
	 * una collection de CMMessages 2. Extender cada CMMessage a
	 * CMMessageExtended que me permite crear el XML que envio a CM. 3. Crear el
	 * documento XML para el envio. 4. POST del String a la URL configurada.
	 * 
	 */
	public SMSResponse send(Object body) throws MessagingException {

		// TODO: Check https://dashboard.onlinesmsgateway.com/docs for responses

		try {

			SMSMessage smsMessage = (SMSMessage) body;

			// Extend smsMessage to CMMessage
			// This is the instance we will use to build the XML document to be
			// sent
			// to our provider.
			CMMessage cmMessage = new CMMessage();
			cmMessage.setMessage(smsMessage.getMessage());
			cmMessage.setIdAsString(smsMessage.getIdAsString());
			cmMessage.setPhoneNumber(smsMessage.getPhoneNumber());
			cmMessage.setDynamicSender(smsMessage.getDynamicFrom());

			// Unicode and multipart
			this.setUnicodeAndMultipart(cmMessage);

			// TODO: Phone number validation
			// PhoneNumber pn = null; // from cmMessage.getPhoneNumber();
			// PhoneNumberUtil.getInstance().isValidNumber(pn);
			// String telefono = this.getPhoneNumberInE164(phoneNumber);

			// TODO: Message validation.

			// Dynamic FROM validation
			// Maximo 11 caracteres.

			// 1.Construct XML. Throws XMLConstructionException
			String xml = createXml(cmMessage);

			// 2. Try to send to SMS Provider ...Throws ProviderException
			String response = doHttpPost(this.url, xml);
			LOG.debug("Response: " + response);

			// 3. TODO: Build CMResponse en funcion de RESPONSE.
			// Dar estructura al RESPONSE. Analogo a SPRIGN WEB. Status, etc.
			SMSResponse cmResponse = null;
			return cmResponse;
		} catch (RuntimeException e) {
			LOG.error("Imposible enviar sms: ", e);
			throw new MessagingException(e);
		}
	}

	private String createXml(CMMessage message) {

		try {

			// TODO: Arguments Validated en este punto.

			ByteArrayOutputStream xml = new ByteArrayOutputStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			// Get the DocumentBuilder
			DocumentBuilder docBuilder = factory.newDocumentBuilder();

			// Create blank DOM Document
			DOMImplementation impl = docBuilder.getDOMImplementation();
			Document doc = impl.createDocument(null, "MESSAGES", null);

			// ROOT Element es MESSAGES
			Element root = doc.getDocumentElement();

			// Se crea el element AUTHENTICATION
			Element authenticationElement = doc.createElement("AUTHENTICATION");
			Element productTokenElement = doc.createElement("PRODUCTTOKEN");
			authenticationElement.appendChild(productTokenElement);
			Text productTokenValue = doc.createTextNode("" + this.productToken);
			productTokenElement.appendChild(productTokenValue);
			root.appendChild(authenticationElement);

			// Se crea el element MSG
			Element msgElement = doc.createElement("MSG");
			root.appendChild(msgElement);

			// Parejas ELEMENTO-VALORTEXTO
			// <FROM>VALOR</FROM>
			Element fromElement = doc.createElement("FROM");
			fromElement.appendChild(doc.createTextNode(message.getDynamicSender()));
			msgElement.appendChild(fromElement);

			// <BODY>VALOR</BODY>
			Element bodyElement = doc.createElement("BODY");
			bodyElement.appendChild(doc.createTextNode(message.getMessage()));
			msgElement.appendChild(bodyElement);

			// <TO>VALOR</TO>
			Element toElement = doc.createElement("TO");
			toElement.appendChild(doc.createTextNode(message.getPhoneNumber()));
			msgElement.appendChild(toElement);

			// <DCS>VALOR</DCS> - Si es UNICODE - messageOut.isGSM338Enc
			// false
			if (message.isUnicode()) {
				Element dcsElement = doc.createElement("DCS");
				dcsElement.appendChild(doc.createTextNode("8"));
				msgElement.appendChild(dcsElement);
			}

			// <REFERENCE>VALOR</REFERENCE> -Es MI ID de mensajes(12 bytes)
			// como string - Limite 32 caracteres.
			String id = message.getIdAsString();
			if (id != null && !id.isEmpty()) {
				Element refElement = doc.createElement("REFERENCE");
				refElement.appendChild(doc.createTextNode("" + message.getIdAsString()));
				msgElement.appendChild(refElement);
			}

			// <MINIMUMNUMBEROFMESSAGEPARTS>1</MINIMUMNUMBEROFMESSAGEPARTS>
			// <MAXIMUMNUMBEROFMESSAGEPARTS>8</MAXIMUMNUMBEROFMESSAGEPARTS>
			if (message.isMultipart()) {
				Element minMessagePartsElement = doc.createElement("MINIMUMNUMBEROFMESSAGEPARTS");
				minMessagePartsElement.appendChild(doc.createTextNode("1"));
				msgElement.appendChild(minMessagePartsElement);

				Element maxMessagePartsElement = doc.createElement("MAXIMUMNUMBEROFMESSAGEPARTS");
				maxMessagePartsElement.appendChild(doc.createTextNode(Integer.toString(message.getMultiparts())));
				msgElement.appendChild(maxMessagePartsElement);
			}

			// Creatate XML as String
			Transformer aTransformer = TransformerFactory.newInstance().newTransformer();
			aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Source src = new DOMSource(doc);
			Result dest = new StreamResult(xml);
			aTransformer.transform(src, dest);
			return xml.toString();
		} catch (TransformerException e) {
			LOG.error("Imposible construir XML para SMS: ", e);
			throw new XMLConstructionException(e);
		} catch (ParserConfigurationException e) {
			LOG.error("Imposible construir XML para SMS: ", e);
			throw new XMLConstructionException(e);
		}
	}

	private String doHttpPost(String urlString, String requestString) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(urlString);
		post.setEntity(new StringEntity(requestString, Charset.forName("UTF-8")));

		try {

			HttpResponse response = client.execute(post);

			LOG.debug("Response Code : {}", response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			// TODO: Añadir el procesado de la respuesta.
			// Aqui deberia construir mi estructura SMSResponse . CMResponse
			// deberia ser la respuesta que me da CM.
			return result.toString();
		} catch (IOException io) {
			throw new ProviderException(io);
		}
	}

	private boolean isGsm0338Encodeable(String message) {
		return encoder.canEncode(message);
	}

	private void setUnicodeAndMultipart(CMMessage message) {
		// Set UNICODE and MULTIPART
		String msg = message.getMessage();
		if (isGsm0338Encodeable(msg)) {
			// Not Unicode is Multipart?
			if (msg.length() > CMConstants.MAX_GSM_MESSAGE_LENGTH) {

				// Multiparts. 153 caracteres max per part
				int parts = msg.length() % CMConstants.MAX_GSM_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

				message.setMultiparts((parts > this.defaultMaxNumberOfParts) ? this.defaultMaxNumberOfParts : parts);
			} // Otherwise multipart = 1
		} else {
			// Unicode Message
			message.setUnicode(true);

			if (msg.length() > CMConstants.MAX_UNICODE_MESSAGE_LENGTH) {

				// Multiparts. 67 caracteres max per part
				int parts = msg.length() % CMConstants.MAX_UNICODE_MESSAGE_LENGTH_PER_PART_IF_MULTIPART;

				message.setMultiparts((parts > this.defaultMaxNumberOfParts) ? this.defaultMaxNumberOfParts : parts);
			} // Otherwise multipart = 1
		}
	}

	// public String getPhoneNumberInE164(String telefono) {
	// PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
	// try {
	// PhoneNumber pn = pnu.parse(telefono, "ES");
	// return pnu.format(pn, PhoneNumberFormat.E164);
	// } catch (NumberParseException e) {
	// throw new RuntimeException(e);
	// }
	// }
}
