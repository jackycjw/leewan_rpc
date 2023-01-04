package com.leewan.rpc.share.databind.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.leewan.rpc.share.databind.ResponseDataBinder;
import org.objenesis.strategy.StdInstantiatorStrategy;

public abstract class AbstractKryoDataBinder {

    private ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<>(){
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    protected Kryo getKryo(){
        return kryoThreadLocal.get();
    }
}
