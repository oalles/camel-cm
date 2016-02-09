package org.apache.camel.component.cm;

import org.apache.camel.component.cm.client.SMSResponse;
import org.apache.camel.component.cm.exceptions.MessagingException;

/**
 * Servicio que me permite interactuar con el API de CM.
 * @author Omar
 *
 */
public interface CMSender {

	/**
	 * El producer recibe un body: 1. Validar el BODY. Puede ser un CMMessage o
	 * una collection de CMMessages 2. Extender cada CMMessage a
	 * CMMessageExtended que me permite crear el XML que envio a CM. 3. Crear el
	 * documento XML para el envio. 4. POST del String a la URL configurada.
	 * 
	 */
	SMSResponse send(Object body) throws MessagingException;

}
