package com.ftpix.mmath.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Created by gz on 25-Sep-16.
 */
public interface MmathDao<T> {


    public T insert(T object);
    public T insert(String url) throws IOException, ParseException;

    public Optional<T> get(String id);
    public Optional<T> getByUrl(String url);

    public List<T> getAll();



    public T update(T object);

    public boolean delete(T object);
    public boolean delete(String id);
    public boolean deleteByUrl(String url);
}
