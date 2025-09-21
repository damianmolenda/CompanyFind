package com.example.companyfind.gus;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
public class SoapResponse {

    @Element(name = "Body")
    public Body body;

    @Root(name = "Body", strict = false)
    public static class Body {
        @Element(name = "StatusSesjiResponse", required = false)
        public StatusSesjiResponse statusSesjiResponse;
    }

    @Root(name = "StatusSesjiResponse", strict = false)
    @Namespace(reference = "http://CIS/BIR/PUBL/2014/07")
    public static class StatusSesjiResponse {
        @Element(name = "StatusSesjiResult", required = false)
        public String result;
    }
}

