package convert;

import types.Media;

public interface Converter<T> {
    public T convert(T media, Media to_type);
}
