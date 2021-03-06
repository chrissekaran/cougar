/*
 * Copyright 2013, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_IDL_QueryParam_SimpleType_ENUM_blank.xls;
package com.betfair.cougar.tests.updatedcomponenttests.standardvalidation.rest;

import com.betfair.testing.utils.cougar.assertions.AssertionUtils;
import com.betfair.testing.utils.cougar.beans.HttpCallBean;
import com.betfair.testing.utils.cougar.beans.HttpResponseBean;
import com.betfair.testing.utils.cougar.enums.CougarMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.cougar.manager.AccessLogRequirement;
import com.betfair.testing.utils.cougar.manager.CougarManager;
import com.betfair.testing.utils.cougar.misc.XMLHelpers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Cougar returns the correct fault, when a REST request passes a blank SimpleTypeENUM Query parameter
 */
public class RestIDLQueryParamSimpleTypeENUMunrecognizedTest {
    @Test(dataProvider = "ValidationFailureLocation")
    public void doTest(String location) throws Exception {
        // Create the HttpCallBean
        CougarManager cougarManager1 = CougarManager.getInstance();
        HttpCallBean httpCallBeanBaseline = cougarManager1.getNewHttpCallBean();
        CougarManager cougarManagerBaseline = cougarManager1;
        // Get the cougar logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "cougarBaseline");

        httpCallBeanBaseline.setVersion("v2");
        // Set up the Http Call Bean to make the request
        CougarManager cougarManager2 = CougarManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = cougarManager2.getNewHttpCallBean("87.248.113.14");
        cougarManager2 = cougarManager2;

        cougarManager2.setCougarFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

        getNewHttpCallBean2.setOperationName("enumSimpleOperation");

        getNewHttpCallBean2.setServiceName("baseline", "cougarBaseline");

        getNewHttpCallBean2.setVersion("v2");
        // Set the parameters, setting the SimpleTypeENUM Query parameter to be blank
        Map map3 = new HashMap();
        map3.put("HeaderParam",location.equals("header")?"UNRECOGNIZED_VALUE":"FOOBAR");
        getNewHttpCallBean2.setHeaderParams(map3);

        Map map4 = new HashMap();
        map4.put("queryParam",location.equals("query")?"UNRECOGNIZED_VALUE":"BAR");
        getNewHttpCallBean2.setQueryParams(map4);

        getNewHttpCallBean2.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<message><bodyParameter>"+(location.equals("body")?"UNRECOGNIZED_VALUE":"FOO")+"</bodyParameter></message>").getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp11 = new Timestamp(System.currentTimeMillis());
        // Make the 4 REST calls to the operation
        cougarManager2.makeRestCougarHTTPCalls(getNewHttpCallBean2);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers6 = new XMLHelpers();
        Document jsonResponse = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<CougarMessageProtocolRequestTypeEnum, Object> jsonRestTypes = cougarManager2.convertResponseToRestTypes(jsonResponse, getNewHttpCallBean2);
        Document xmlResponse = xMLHelpers6.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Convert the expected response to REST types for comparison with actual responses
        Map<CougarMessageProtocolRequestTypeEnum, Object> xmlRestTypes = cougarManager2.convertResponseToRestTypes(xmlResponse, getNewHttpCallBean2);
        // Check the 4 responses are as expected (Bad Request)
        HttpResponseBean response7 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.cougar.enums.CougarMessageProtocolResponseTypeEnum.RESTXMLXML);
        AssertionUtils.multiAssertEquals(xmlRestTypes.get(CougarMessageProtocolRequestTypeEnum.RESTXML), response7.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response7.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response7.getHttpStatusText());

        HttpResponseBean response8 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.cougar.enums.CougarMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(jsonRestTypes.get(CougarMessageProtocolRequestTypeEnum.RESTJSON), response8.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response8.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response8.getHttpStatusText());

        HttpResponseBean response9 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.cougar.enums.CougarMessageProtocolResponseTypeEnum.RESTXMLJSON);
        AssertionUtils.multiAssertEquals(xmlRestTypes.get(CougarMessageProtocolRequestTypeEnum.RESTJSON), response9.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response9.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response9.getHttpStatusText());

        HttpResponseBean response10 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.cougar.enums.CougarMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(jsonRestTypes.get(CougarMessageProtocolRequestTypeEnum.RESTXML), response10.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response10.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response10.getHttpStatusText());

        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        CougarManager cougarManager13 = CougarManager.getInstance();
        cougarManager13.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp11, new AccessLogRequirement("87.248.113.14", "/cougarBaseline/v2/enumSimpleOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/cougarBaseline/v2/enumSimpleOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/cougarBaseline/v2/enumSimpleOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/cougarBaseline/v2/enumSimpleOperation", "BadRequest") );
    }
    @DataProvider(name = "ValidationFailureLocation")
    public Object[][] versions() {
        return new Object[][]{
                {"body"},
                {"header"},
                {"query"}
        };
    }

}
