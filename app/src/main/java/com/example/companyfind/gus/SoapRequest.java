package com.example.companyfind.gus;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/")
public class SoapRequest {

    @Element(name = "Body")
    public Body body;

    public SoapRequest(String methodName) {
        this.body = new Body(methodName);
    }

    @Root(name = "Body", strict = false)
    public static class Body {
        @Element(name = "StatusSesji", required = false)
        @Namespace(reference = "http://CIS/BIR/PUBL/2014/07")
        public String method;

        public Body(String methodName) {
            this.method = ""; // no params needed for StatusSesji
        }
    }
}
