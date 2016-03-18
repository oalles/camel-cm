/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.cm.client;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.camel.component.cm.validation.constraints.E164;

/**
 * Immutable. The message instance provided by the client.
 */
public class SMSMessage {

    /**
     * Required MSISDN. E164 value starting with +. (so, don't need to set the country in parameter if my phone number begins with "+".)
     */
    @E164
    private final String phoneNumber;

    /**
     * Required.
     */
    @NotNull
    private final String message;

    @Size(min = 1, max = 11)
    private final String dynamicFrom;

    /**
     * Restrictions: 1 - 32 alphanumeric characters and reference will not work for demo accounts.
     */
    @Size(min = 1, max = 32)
    private final String idAsString;

    public SMSMessage(final String message, final String phoneNumber) {
        this(null, message, phoneNumber, null);
    }

    public SMSMessage(final String idAsString, final String message, final String phoneNumber, final String dynamicFrom) {
        this.idAsString = idAsString;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.dynamicFrom = dynamicFrom;
    }

    public String getIdAsString() {
        return idAsString;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDynamicFrom() {
        return dynamicFrom;
    }

    @Override
    public String toString() {
        return "SMSMessage [phoneNumber=" + phoneNumber + ", message=" + message + ", dynamicFrom=" + dynamicFrom + ", idAsString=" + idAsString + "]";
    }

}
