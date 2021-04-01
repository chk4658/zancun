package com.touchspring.ailge.dto;

import lombok.Data;

@Data
public class PagingDTO {
    private Integer totalNum;//总条数
    private Integer totalPage;//总页数
    private Integer pageSize;//每页条数
    private Integer pageIndex;//当前页码
    private Integer queryIndex;//当前页从第几条开始查

    public static PagingDTO pagination(Integer totalNum,Integer pageSize,Integer pageIndex){
        PagingDTO page = new PagingDTO();
        page.setTotalNum(totalNum);
        Integer totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        page.setTotalPage(totalPage);
        page.setPageIndex(pageIndex + 1);
        page.setPageSize(pageSize);
        page.setQueryIndex(pageSize * pageIndex);
        return page;
    }

}
