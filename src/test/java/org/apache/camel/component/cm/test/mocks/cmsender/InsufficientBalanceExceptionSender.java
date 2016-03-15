package org.apache.camel.component.cm.test.mocks.cmsender;

import org.apache.camel.component.cm.CMMessage;
import org.apache.camel.component.cm.CMSender;
import org.apache.camel.component.cm.exceptions.MessagingException;
import org.apache.camel.component.cm.exceptions.cmresponse.InsufficientBalanceException;

public class InsufficientBalanceExceptionSender implements CMSender {

    @Override
    public void send(CMMessage cmMessage) throws MessagingException {
        throw new InsufficientBalanceException();
    }

}
