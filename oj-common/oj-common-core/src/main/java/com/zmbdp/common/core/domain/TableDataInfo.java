package com.zmbdp.common.core.domain;

import com.zmbdp.common.core.enums.ResultCode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格分页数据对象
 */
@Data
public class TableDataInfo {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 表格数据对象
     */
    public TableDataInfo() {
    }

    public static TableDataInfo empty() {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultCode.SUCCESS.getCode());
        rspData.setRows(new ArrayList<>());
        rspData.setMsg(ResultCode.SUCCESS.getMsg());
        rspData.setTotal(0);
        return rspData;
    }
    public static TableDataInfo success(List<?> list,int total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(ResultCode.SUCCESS.getCode());
        rspData.setRows(list);
        rspData.setMsg(ResultCode.SUCCESS.getMsg());
        rspData.setTotal(total);
        return rspData;
    }

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }
}