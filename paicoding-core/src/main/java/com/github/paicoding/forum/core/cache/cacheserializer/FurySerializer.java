package com.github.paicoding.forum.core.cache.cacheserializer;

import org.apache.fury.Fury;
import org.apache.fury.ThreadLocalFury;
import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;

public class FurySerializer<T> implements CacheSerializer<T> {
    private static ThreadLocalFury threadLocalFury = new ThreadLocalFury(classLoader ->
            Fury.builder().requireClassRegistration(false).withClassLoader(classLoader).build());

    public FurySerializer(Class<T> clazz) {
        threadLocalFury.register(clazz);
    }

    @Override
    public void serialize(T o, ByteBuffer byteBuffer) {
        byte[] serializedBytes = threadLocalFury.serialize(o);
        // 将序列化后的字节数组写入 ByteBuffer
        byteBuffer.put(serializedBytes);
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        byte[] serializedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(serializedBytes);
        // 反序列化字节数组
        Object deserialize = threadLocalFury.deserialize(serializedBytes);
        return (T)deserialize ;
    }

    @Override
    public int serializedSize(T o) {
        byte[] serializedBytes = threadLocalFury.serialize(o);
        return serializedBytes.length;
    }
}
