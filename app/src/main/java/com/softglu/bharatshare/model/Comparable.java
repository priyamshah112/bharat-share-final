package com.softglu.bharatshare.model;

public interface Comparable
{
    boolean comparisonSupported();

    String getComparableName();

    long getComparableDate();

    long getComparableSize();
}
