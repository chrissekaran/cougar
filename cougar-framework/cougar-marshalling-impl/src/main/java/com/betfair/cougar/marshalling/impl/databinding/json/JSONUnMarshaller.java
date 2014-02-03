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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import com.betfair.cougar.api.fault.FaultCode;
import com.betfair.cougar.core.api.client.EnumWrapper;
import com.betfair.cougar.core.api.exception.CougarException;
import com.betfair.cougar.core.api.fault.CougarFault;
import com.betfair.cougar.core.api.fault.FaultDetail;
import com.betfair.cougar.core.api.transcription.ParameterType;
import com.betfair.cougar.marshalling.api.databinding.FaultUnMarshaller;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.betfair.cougar.core.api.exception.CougarServiceException;
import com.betfair.cougar.core.api.exception.CougarValidationException;
import com.betfair.cougar.core.api.exception.ServerFaultCode;

import com.betfair.cougar.marshalling.api.databinding.UnMarshaller;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

public class JSONUnMarshaller implements UnMarshaller, FaultUnMarshaller {

	private final ObjectMapper objectMapper;
	
	public JSONUnMarshaller(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public String getFormat() {
		return "json";
	}

	@Override
	public Object unmarshall(InputStream inputStream, Class<?> clazz, String encoding) {
		try {
			Reader reader = new BufferedReader(new InputStreamReader(inputStream,encoding));
			return objectMapper.readValue(reader, clazz);
		} catch (JsonProcessingException e) {
//            if (e.getCause() instanceof CougarException) {
//                throw (CougarException) e.getCause();
//            }
			throw new CougarValidationException(ServerFaultCode.JSONDeserialisationParseFailure, e);
		} catch (IOException e) {
			throw new CougarServiceException(ServerFaultCode.JSONDeserialisationParseFailure, "Failed to unmarshall object", e);
		}
	}

    private static final ParameterType STRING_PARAM_TYPE = new ParameterType(String.class, null);

    public Object unmarshall(InputStream inputStream, ParameterType parameterType, String encoding) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream,encoding));
            if (parameterType.getImplementationClass().equals(EnumWrapper.class)) {
                String value = objectMapper.readValue(reader, buildJavaType(STRING_PARAM_TYPE));
                return new EnumWrapper(parameterType.getComponentTypes()[0].getImplementationClass(), value);
            }
            else {
                return objectMapper.readValue(reader, buildJavaType(parameterType));
            }
        } catch (JsonProcessingException e) {
            throw new CougarServiceException(ServerFaultCode.JSONDeserialisationParseFailure, "Failed to unmarshall object", e);
        } catch (IOException e) {
            throw new CougarServiceException(ServerFaultCode.JSONDeserialisationParseFailure, "Failed to unmarshall object", e);
        }

    }

    private JavaType buildJavaType(ParameterType paramType) {
		return paramType.transform(new ParameterType.TransformingVisitor<JavaType>() {
			@Override
			public JavaType transformMapType(JavaType keyType, JavaType valueType) {
				return TypeFactory.mapType(HashMap.class, keyType, valueType);
			}
			@Override
			public JavaType transformListType(JavaType elemType) {
				return TypeFactory.collectionType(ArrayList.class, elemType);
			}
			@Override
			public JavaType transformSetType(JavaType elemType) {
				return TypeFactory.collectionType(HashSet.class, elemType);
			}
			@Override
			public JavaType transformType(ParameterType.Type type, Class implementationClass) {
				return TypeFactory.fastSimpleType(implementationClass);
			}
		});

	}


    @Override
    public CougarFault unMarshallFault(InputStream inputStream, String encoding) {
        final HashMap<String,Object> faultMap = (HashMap<String,Object>) unmarshall(inputStream, HashMap.class, encoding);

        final String faultString = (String)faultMap.get("faultstring");
        final FaultCode faultCode = FaultCode.valueOf((String)faultMap.get("faultcode"));


        final HashMap<String, Object> detailMap = (HashMap<String, Object>)faultMap.get("detail");
        String exceptionName = (String)detailMap.get("exceptionname");

        List<String[]> faultParams = Collections.emptyList();
        if (exceptionName != null) {
            faultParams = new ArrayList<String[]>();
            Map<String, Object> paramMap = (Map<String, Object>) detailMap.get(exceptionName);

            for(Map.Entry e:paramMap.entrySet()){
                String[] nvpair=new String[] { (String)e.getKey(), (String)e.getValue().toString() };
                faultParams.add(nvpair);
            }
        }

        final FaultDetail fd=new FaultDetail(faultString, faultParams);

        return new CougarFault() {
            @Override
            public String getErrorCode() {
                return faultString;
            }

            @Override
            public FaultCode getFaultCode() {
                return faultCode;
            }

            @Override
            public FaultDetail getDetail() {
                return fd;
            }
        };
    }
}
