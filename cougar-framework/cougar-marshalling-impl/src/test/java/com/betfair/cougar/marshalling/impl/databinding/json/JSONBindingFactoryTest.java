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

package com.betfair.cougar.marshalling.impl.databinding.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.type.JavaType;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class JSONBindingFactoryTest {

    @Test
	public void testJSONBindingFactory() {
		JSONBindingFactory factory = new JSONBindingFactory();
		assertNotNull(factory.getMarshaller());
		assertNotNull(factory.getUnMarshaller());
	}

    @Test(expected = IllegalArgumentException.class)
    public void testByteConversion() {
        ObjectMapper mapper = JSONBindingFactory.createBaseObjectMapper();
        JsonNode paramValue = new IntNode(128);
        JavaType javaType = SimpleType.construct(byte.class);
        mapper.convertValue(paramValue, javaType);
    }

}
