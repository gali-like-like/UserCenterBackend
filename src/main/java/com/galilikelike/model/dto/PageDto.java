package com.galilikelike.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageDto {
    @NotNull(message = "当前页不能为空")
    @Min(value = 1,message = "必须从第一页起步")
    private Integer current;

    @NotNull(message = "分页大小不能为空")
    @Min(value = 10,message = "分页大小不能小于10页")
    private Integer pageSize;
}
