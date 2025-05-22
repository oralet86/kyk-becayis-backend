package com.sazark.kykbecayis.misc;

// A is Entity, B is DTO
public interface Mapper<A, B> {
    B toDTO(A a);

    A toEntity(B b);
}
