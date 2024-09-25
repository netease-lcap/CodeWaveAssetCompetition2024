package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class AuthItemDto  {
    public List<String> personIds;

    public List<String> orgIds;

    public List<ResourceInfoParam> resourceInfos;

    public String queryType;

    public List<Integer> personStatus;

    public List<Integer> cardStatus;

    public List<Integer> faceStatus;

    public ConfigTimeParam configTime;

    public List<SortObjectParam> sortObjects;

    public List<String> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<String> personIds) {
        this.personIds = personIds;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public List<ResourceInfoParam> getResourceInfos() {
        return resourceInfos;
    }

    public void setResourceInfos(List<ResourceInfoParam> resourceInfos) {
        this.resourceInfos = resourceInfos;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public List<Integer> getPersonStatus() {
        return personStatus;
    }

    public void setPersonStatus(List<Integer> personStatus) {
        this.personStatus = personStatus;
    }

    public List<Integer> getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(List<Integer> cardStatus) {
        this.cardStatus = cardStatus;
    }

    public List<Integer> getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(List<Integer> faceStatus) {
        this.faceStatus = faceStatus;
    }

    public ConfigTimeParam getConfigTime() {
        return configTime;
    }

    public void setConfigTime(ConfigTimeParam configTime) {
        this.configTime = configTime;
    }

    public List<SortObjectParam> getSortObjects() {
        return sortObjects;
    }

    public void setSortObjects(List<SortObjectParam> sortObjects) {
        this.sortObjects = sortObjects;
    }
}