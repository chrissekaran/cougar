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

package com.betfair.cougar.netutil.nio.connected;

import com.betfair.cougar.core.api.transcription.Parameter;
import com.betfair.cougar.core.api.transcription.ParameterType;
import com.betfair.cougar.core.api.transcription.TranscribableParams;
import com.betfair.cougar.core.api.transcription.TranscriptionInput;
import com.betfair.cougar.core.api.transcription.TranscriptionOutput;

import java.util.Set;

/**
 *
 */
public class SetScalar extends AbstractUpdateAction {

    private int id;
    private Object value;
    private transient com.betfair.platform.virtualheap.updates.SetScalar heapRepresentation;

    // used in transcription, change the ordering at your peril!
    // add new fields at the end!
    private static Parameter[] parameters =  new Parameter[] {
        new Parameter("id", ParameterType.create(int.class), true),
            // this should be reasonably unbrittle as the only types that will go in here will be primitive
        new Parameter("value", ParameterType.create(Object.class), true)
    };

    // only used for transcription
    public SetScalar() {
    }

    public SetScalar(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public SetScalar(com.betfair.platform.virtualheap.updates.SetScalar u) {
        this(u.getId(), u.getValue());
        this.heapRepresentation = u;
    }

    @Override
    public void transcribe(TranscriptionOutput out, Set<TranscribableParams> params, boolean client) throws Exception {
        out.writeObject(id, parameters[0], client);
        out.writeObject(value, parameters[1], client);
        // NOTE: add new fields at the end
    }

    @Override
    public void transcribe(TranscriptionInput in, Set<TranscribableParams> params, boolean client) throws Exception {
        id = (Integer) in.readObject(parameters[0], client);
        value = in.readObject(parameters[1], client);
        // NOTE: add new fields at the end
    }

    @Override
    public Parameter[] getParameters() {
        return parameters;
    }

    @Override
    public com.betfair.platform.virtualheap.updates.Update getHeapRepresentation() {
        if (heapRepresentation == null) {
            heapRepresentation = new com.betfair.platform.virtualheap.updates.SetScalar(id, value);
        }
        return heapRepresentation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetScalar setScalar = (SetScalar) o;

        if (id != setScalar.id) return false;
        if (value != null ? !value.equals(setScalar.value) : setScalar.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SetScalar{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
