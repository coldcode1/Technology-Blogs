package com.github.paicoding.forum.core.cache.cacheserializer;

import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;
import java.util.List;

public class FurySerializer<T> implements CacheSerializer<T> {
    private static ThreadLocalFury fury;

    public FurySerializer(Class clazz) {
        fury = new ThreadLocalFury(classLoader ->{
            Fury f = Fury.builder().requireClassRegistration(false).withClassLoader(classLoader).build();
            f.register(clazz);
            return f;
        });
    }

    public FurySerializer(List<Class> classList) {
        fury = new ThreadLocalFury(classLoader ->{
            Fury f = Fury.builder().requireClassRegistration(false).withClassLoader(classLoader).build();
            classList.forEach(f::register);
            return f;
        });
    }

    @Override
    public void serialize(T o, ByteBuffer byteBuffer) {
        byte[] serializedBytes = fury.serialize(o);
        // 将序列化后的字节数组写入 ByteBuffer
        byteBuffer.put(serializedBytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] serializedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(serializedBytes);
        // 反序列化字节数组
        Object deserialize = fury.deserialize(serializedBytes);
        return (T)deserialize ;
    }

    @Override
    public int serializedSize(T o) {
        byte[] serializedBytes = fury.serialize(o);
        return serializedBytes.length;
    }
}
