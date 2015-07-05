package com.farukon.resource.service;

import com.farukon.resource.model.Resource;

import java.util.List;

/**
 * Created by jamesyan on 7/4/15.
 */
public interface ResourceService {
    public List<Resource> fetchUrl(String url) throws Exception;
}

