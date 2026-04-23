package com.itcodebox.notebooks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.itcodebox.notebooks.entity.ImageRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LeeWyatt
 */
public class ImageRecordUtil {
    private static final Logger LOG = Logger.getInstance(ImageRecordUtil.class);

    private ImageRecordUtil() {
    }

    public static  List<ImageRecord> convertToList(String imageRecords) {
        if (imageRecords == null) {
            return new ArrayList<ImageRecord>();
        }
        try {
            return new ObjectMapper().readValue(imageRecords, new TypeReference<List<ImageRecord>>() {
            });
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to deserialize image records from JSON", e);
        }
        return new ArrayList<ImageRecord>();
    }

    public static String convertToString(List<ImageRecord> list) {
        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialize image records to JSON", e);
        }
        return "";
    }


}
