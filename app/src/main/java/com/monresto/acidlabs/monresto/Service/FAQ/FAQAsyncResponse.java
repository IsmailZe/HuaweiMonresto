package com.monresto.acidlabs.monresto.Service.FAQ;

import com.monresto.acidlabs.monresto.Model.FAQ;

import java.util.ArrayList;

public interface FAQAsyncResponse {
    void processFinish(ArrayList<FAQ> FAQList);
}
