package convert;

import types.MediaType;

public interface Converter<T> {
    public T convert(T media, MediaType to_type);
}
