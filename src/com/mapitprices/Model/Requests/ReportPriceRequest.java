package com.mapitprices.Model.Requests;

import com.mapitprices.Model.Foursquare.Venue;
import com.mapitprices.Model.Item;

/**
 * Created by IntelliJ IDEA.
 * User: Xatter
 * Date: 9/29/11
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportPriceRequest {
    public Item item;
    public Venue store;
    public Double newprice;
}
