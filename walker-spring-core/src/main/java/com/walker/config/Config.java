package com.walker.config;

import com.walker.mode.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Config extends com.walker.service.Config {

    public static Pageable turnTo(Page page) {
        String order = page.getOrder();
        String[] orders = order.split(" ");

        /**
         * 如何构造多排序条件问题
         */
        Sort sort = null;
        if (orders[0].length() > 0) {
            String key = "\\Q" + orders[0] + "\\E";
            if (orders.length > 1 && orders[1].equalsIgnoreCase("DESC")) {
//                sort = new Sort(Sort.Direction.DESC, key);
//                sort = new Sort( new Sort.Order(Sort.Direction.DESC, key).ignoreCase() );
            } else {
//                sort = new Sort(Sort.Direction.ASC, key);
//                sort = new Sort( new Sort.Order(Sort.Direction.ASC, key).ignoreCase() );
            }
        }
        //jpa分页从0开始
        Pageable pageable =
                sort == null
                        ? PageRequest.of(page.getNowpage() - 1, page.getShownum())
                        : PageRequest.of(page.getNowpage() - 1, page.getShownum(), sort);
        return pageable;
    }


}
